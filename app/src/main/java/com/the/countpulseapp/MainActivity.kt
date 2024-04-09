package com.the.countpulseapp

import android.Manifest.permission.BODY_SENSORS
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.the.countpulseapp.data.Constants.NAME_PREF
import com.the.countpulseapp.data.Constants.USER_SENSOR_DEF
import com.the.countpulseapp.data.Constants.USER_SENSOR_NO_USE
import com.the.countpulseapp.data.Constants.USER_SENSOR_USE
import com.the.countpulseapp.data.UserInfoPulse
import com.the.countpulseapp.databinding.ActivityMainBinding
import com.the.countpulseapp.db.Db
import com.the.countpulseapp.utils.ActivityMainHelper
import com.the.countpulseapp.utils.Adapters.UserInfoPulseAdapter
import com.the.countpulseapp.utils.UserPulseDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private var isMenuVisible = false
    private var isSensorMenuVisible = false
    private var isDateMenuVisible = false
    private var isTimeMenuVisible = false
    private var showAllItems = false
    private lateinit var activityMainHelper: ActivityMainHelper
    private lateinit var preferences: SharedPreferences
    private lateinit var adapter: UserInfoPulseAdapter
    private lateinit var userPulseDb: UserPulseDb
    private lateinit var db: Db
    private val REQUEST_BODY_SENSORS_PERMISSION = 123
    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
                val heartRate = event.values[0]

                Log.d(TAG, "heartRate: $heartRate")

                activityMainBinding.clAddNewPulseInfo.tvShowHeartRate.text =
                    heartRate.toInt().toString()

                if (heartRate.toInt() != 0)
                    sensorManager.unregisterListener(this)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        CoroutineScope(Dispatchers.Main).launch {
            initAllComponents()
        }

        activityMainBinding.clShowUserHeartSensor.btnNoSensor.setOnClickListener {
            userPulseDb.userUseSensor(USER_SENSOR_NO_USE)
            activityMainHelper.hideSensorMenu()
            isSensorMenuVisible = false
        }

        activityMainBinding.clShowUserHeartSensor.btnOkSensor.setOnClickListener {
            if (requestSensorPermission()) {
                heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)!!
                userPulseDb.userUseSensor(USER_SENSOR_USE)
            }
            activityMainHelper.hideSensorMenu()
            isSensorMenuVisible = false
        }

        activityMainBinding.clAddNewPulseInfo.btnSaveInfoFromUser.setOnClickListener {
            Thread {
                val valuePulse = if (userPulseDb.whatStateWithSensor() == USER_SENSOR_USE)
                    activityMainBinding.clAddNewPulseInfo.tvShowHeartRate.text.toString().toInt()
                else
                    activityMainBinding.clAddNewPulseInfo.pulsePicker.value

                db.getDao().insertUserInfoPulse(
                    UserInfoPulse(
                        null,
                        activityMainBinding.clAddNewPulseInfo.systolicPicker.value,
                        activityMainBinding.clAddNewPulseInfo.diastolicPicker.value,
                        valuePulse,
                        activityMainBinding.clAddNewPulseInfo.tvCurrentDateSmall.text.toString(),
                        activityMainBinding.clAddNewPulseInfo.tvCurrentTimeSmall.text.toString()
                    )
                )
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.txt_success_save),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.start()
            activityMainHelper.hideAddMenu()
            isMenuVisible = false
            unregisterHeartSensor()
        }

        activityMainBinding.flBtnAddNewRate.setOnClickListener {
            if (!isMenuVisible) {
                if (activityMainHelper.isSensorAvailable(sensorManager) &&
                    userPulseDb.whatStateWithSensor() == USER_SENSOR_DEF) {
                    isSensorMenuVisible = true
                    activityMainHelper.showUseHeartSensor()
                } else if (userPulseDb.whatStateWithSensor() == USER_SENSOR_NO_USE) {
                    activityMainHelper.showAddMenu(false)
                    isMenuVisible = true
                } else if (userPulseDb.whatStateWithSensor() == USER_SENSOR_USE) {
                    activityMainHelper.showAddMenu(true)
                    if (heartRateSensor == null)
                        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)!!
                    registerHeartSensor()
                    isMenuVisible = true
                } else {
                    activityMainHelper.showAddMenu(false)
                    isMenuVisible = true
                }

            } else {
                hideMenuWhatVisible()
            }
        }

        activityMainBinding.blurView.setOnClickListener {
            hideMenuWhatVisible()
        }

        activityMainBinding.clChooseTime.btnCancelChooseTime.setOnClickListener {
            hideMenuWhatVisible()
        }

        activityMainBinding.clChooseCalendarDay.btnCancelChooseDate.setOnClickListener {
            hideMenuWhatVisible()
        }

        activityMainBinding.llWithHistory.setOnClickListener {
            showAllItems = !showAllItems
            adapter.setShowAllItems(showAllItems)
        }

        activityMainBinding.tvHistory.setOnClickListener {
            showAllItems = !showAllItems
            adapter.setShowAllItems(showAllItems)
        }

        activityMainBinding.imgBtnHistory.setOnClickListener {
            showAllItems = !showAllItems
            adapter.setShowAllItems(showAllItems)
        }

        activityMainBinding.clChooseTime.btnConfirmChooseTime.setOnClickListener {
            activityMainHelper.setTimeInTv(
                activityMainBinding.clChooseTime.timePicker.hour,
                activityMainBinding.clChooseTime.timePicker.minute
            )
            isTimeMenuVisible = false
        }

        activityMainBinding.clChooseCalendarDay.btnConfirmChooseDate.setOnClickListener {
            activityMainHelper.setDateInTv(
                activityMainBinding.clChooseCalendarDay.datePicker.dayOfMonth,
                activityMainBinding.clChooseCalendarDay.datePicker.month,
                activityMainBinding.clChooseCalendarDay.datePicker.year
            )
            isDateMenuVisible = false
        }

        activityMainBinding.clAddNewPulseInfo.llWithTime.setOnClickListener {
            isDateMenuVisible = true
            activityMainHelper.showChooseDateMenu()
        }

        activityMainBinding.clAddNewPulseInfo.imgBtnCalendarSmall.setOnClickListener {
            isDateMenuVisible = true
            activityMainHelper.showChooseDateMenu()
        }

        activityMainBinding.clAddNewPulseInfo.tvCurrentDateSmall.setOnClickListener {
            isDateMenuVisible = true
            activityMainHelper.showChooseDateMenu()
        }

        activityMainBinding.clAddNewPulseInfo.llWithTime.setOnClickListener {
            isTimeMenuVisible = true
            activityMainHelper.showChooseTimeMenu()
        }

        activityMainBinding.clAddNewPulseInfo.imgBtnClockSmall.setOnClickListener {
            isTimeMenuVisible = true
            activityMainHelper.showChooseTimeMenu()
        }

        activityMainBinding.clAddNewPulseInfo.tvCurrentTimeSmall.setOnClickListener {
            isTimeMenuVisible = true
            activityMainHelper.showChooseTimeMenu()
        }
    }

    @Override
    override fun onBackPressed() {
        if (isDateMenuVisible) {
            activityMainHelper.hideDateMenu()
            isDateMenuVisible = false
        } else if (isTimeMenuVisible) {
            activityMainHelper.hideTimeMenu()
            isTimeMenuVisible = false
        } else if (isMenuVisible) {
            activityMainHelper.hideAddMenu()
            activityMainBinding.flBtnAddNewRate.visibility = VISIBLE
            isMenuVisible = false
        } else if (isSensorMenuVisible) {
            activityMainHelper.hideSensorMenu()
            isSensorMenuVisible = false
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        unregisterHeartSensor()
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this, BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)!!
            userPulseDb.userUseSensor(USER_SENSOR_USE)
        }
    }

    private fun initAllComponents() = with(activityMainBinding) {
        activityMainHelper = ActivityMainHelper(this@MainActivity)
        preferences = getSharedPreferences(NAME_PREF, MODE_PRIVATE)
        userPulseDb = UserPulseDb(preferences)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        db = Db.getDb(this@MainActivity)

        adapter = UserInfoPulseAdapter(emptyList(), showAllItems)
        rvWithUserInfo.layoutManager = LinearLayoutManager(this@MainActivity)
        rvWithUserInfo.adapter = adapter

        clAddNewPulseInfo.systolicPicker.minValue = 0
        clAddNewPulseInfo.systolicPicker.maxValue = 200
        clAddNewPulseInfo.systolicPicker.value = 100

        clAddNewPulseInfo.diastolicPicker.minValue = 0
        clAddNewPulseInfo.diastolicPicker.maxValue = 200
        clAddNewPulseInfo.diastolicPicker.value = 100

        clAddNewPulseInfo.pulsePicker.minValue = 0
        clAddNewPulseInfo.pulsePicker.maxValue = 150
        clAddNewPulseInfo.pulsePicker.value = 70

        db.getDao().getUserInfoPulse().asLiveData().observe(this@MainActivity) {
            adapter.updateData(it.reversed())
        }
    }

    private fun hideMenuWhatVisible() {
        if (isDateMenuVisible) {
            activityMainHelper.hideDateMenu()
            isDateMenuVisible = false
        } else if (isTimeMenuVisible) {
            activityMainHelper.hideTimeMenu()
            isTimeMenuVisible = false
        } else if (isMenuVisible) {
            activityMainHelper.hideAddMenu()
            activityMainBinding.flBtnAddNewRate.visibility = VISIBLE
            isMenuVisible = false
        } else if (isSensorMenuVisible) {
            activityMainHelper.hideSensorMenu()
            isSensorMenuVisible = false
        }
    }

    private fun requestSensorPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this@MainActivity, BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(BODY_SENSORS),
                REQUEST_BODY_SENSORS_PERMISSION
            )
            false
        } else {
            true
        }
    }

    private fun unregisterHeartSensor() {
        if (userPulseDb.whatStateWithSensor() == USER_SENSOR_USE)
            sensorManager.unregisterListener(sensorEventListener)
    }

    private fun registerHeartSensor() {
        if (userPulseDb.whatStateWithSensor() == USER_SENSOR_USE)
            sensorManager.registerListener(
                sensorEventListener,
                heartRateSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
    }
}
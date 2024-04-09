package com.the.countpulseapp.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.TimePicker
import com.the.countpulseapp.R
import com.the.countpulseapp.data.Constants
import java.text.SimpleDateFormat
import java.util.Calendar

class ActivityMainHelper(private val activity: Activity) {
    private var blurView = activity.findViewById<View>(R.id.blurView)
    private val clShowUserHeartSensor = activity.findViewById<View>(R.id.clShowUserHeartSensor)
    private val clAddNewPulseInfo = activity.findViewById<View>(R.id.clAddNewPulseInfo)
    private val clChooseCalendarDay = activity.findViewById<View>(R.id.clChooseCalendarDay)
    private val clChooseTime = activity.findViewById<View>(R.id.clChooseTime)
    private val clMain = activity.findViewById<View>(R.id.clMain)
    private val tvCurrentTimeSmall = activity.findViewById<TextView>(R.id.tvCurrentTimeSmall)
    private val tvCurrentDateSmall = activity.findViewById<TextView>(R.id.tvCurrentDateSmall)
    private val timePicker = activity.findViewById<TimePicker>(R.id.timePicker)
    private val pulsePicker = activity.findViewById<NumberPicker>(R.id.pulsePicker)
    private val tvShowHeartRate = activity.findViewById<TextView>(R.id.tvShowHeartRate)

    fun setDateInTv(dayOfMonth: Int, month: Int, year: Int) {
        val date = "$dayOfMonth.${month + 1}.$year"
        val parts = date.split(".")
        val userDay = if (parts[0].length == 1) "0${parts[0]}" else parts[0]
        val userMonth = if (parts[1].length == 1) "0${parts[1]}" else parts[1]
        val userYear = parts[2]

        tvCurrentDateSmall.text = "$userDay.$userMonth.$userYear"
        hideDateMenu()
    }

    fun setTimeInTv(hours: Int, minutes: Int) {
        val time = "$hours:$minutes"
        val parts = time.split(":")
        val userHours = if (parts[0].length == 1) "0${parts[0]}" else parts[0]
        val userMinutes = if (parts[1].length == 1) "0${parts[1]}" else parts[1]

        tvCurrentTimeSmall.text = "$userHours:$userMinutes"
        hideTimeMenu()
    }

    fun showAddMenu(isSensorUse:Boolean) {
        if (isSensorUse){
            pulsePicker.visibility = GONE
            tvShowHeartRate.visibility = VISIBLE
        } else {
            pulsePicker.visibility =  VISIBLE
            tvShowHeartRate.visibility = GONE
        }
        val currentTime = Calendar.getInstance().time
        val timeFormat = SimpleDateFormat("HH:mm")
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        tvCurrentTimeSmall.text = timeFormat.format(currentTime)
        tvCurrentDateSmall.text = dateFormat.format(currentTime)
        timePicker.setIs24HourView(true)
        val screenHeight = clAddNewPulseInfo.resources.displayMetrics.heightPixels
        val slideAnimationShow = TranslateAnimation(0f, 0f, screenHeight.toFloat(), 0f)
        val alphaAnimationShow = AlphaAnimation(0f, 1f)
        val animationSetShow = AnimationSet(true)
        animationSetShow.addAnimation(slideAnimationShow)
        animationSetShow.addAnimation(alphaAnimationShow)
        animationSetShow.duration = 1000

        animationSetShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                clAddNewPulseInfo.isClickable = false
                clAddNewPulseInfo.visibility = VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {
                clAddNewPulseInfo.isClickable = true
                blurView.visibility = VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        clAddNewPulseInfo.startAnimation(animationSetShow)
    }

    fun hideAddMenu() {
        val screenHeight = clMain.resources.displayMetrics.heightPixels
        val slideAnimationHide = TranslateAnimation(0f, 0f, 0f, screenHeight.toFloat())
        val alphaAnimationHide = AlphaAnimation(1f, 0f)
        val animationSetHide = AnimationSet(true)
        animationSetHide.addAnimation(slideAnimationHide)
        animationSetHide.addAnimation(alphaAnimationHide)
        animationSetHide.duration = 1000

        animationSetHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                clAddNewPulseInfo.isClickable = false
                clAddNewPulseInfo.visibility = GONE
            }

            override fun onAnimationEnd(animation: Animation?) {
                clAddNewPulseInfo.visibility = GONE
                blurView.visibility = GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        clAddNewPulseInfo.startAnimation(animationSetHide)
    }

    fun showUseHeartSensor() {
        val screenHeight = clShowUserHeartSensor.resources.displayMetrics.heightPixels
        val slideAnimationShow = TranslateAnimation(0f, 0f, screenHeight.toFloat(), 0f)
        val alphaAnimationShow = AlphaAnimation(0f, 1f)
        val animationSetShow = AnimationSet(true)
        animationSetShow.addAnimation(slideAnimationShow)
        animationSetShow.addAnimation(alphaAnimationShow)
        animationSetShow.duration = 1000

        animationSetShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                clShowUserHeartSensor.isClickable = false
                clShowUserHeartSensor.visibility = VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {
                clShowUserHeartSensor.isClickable = true
                blurView.visibility = VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        clShowUserHeartSensor.startAnimation(animationSetShow)
    }

    fun hideSensorMenu() {
        val screenHeight = clMain.resources.displayMetrics.heightPixels
        val slideAnimationHide = TranslateAnimation(0f, 0f, 0f, screenHeight.toFloat())
        val alphaAnimationHide = AlphaAnimation(1f, 0f)
        val animationSetHide = AnimationSet(true)
        animationSetHide.addAnimation(slideAnimationHide)
        animationSetHide.addAnimation(alphaAnimationHide)
        animationSetHide.duration = 1000

        animationSetHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                clShowUserHeartSensor.isClickable = false
                clShowUserHeartSensor.visibility = GONE
            }

            override fun onAnimationEnd(animation: Animation?) {
                clShowUserHeartSensor.visibility = GONE
                blurView.visibility = GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        clShowUserHeartSensor.startAnimation(animationSetHide)
    }

    fun showChooseDateMenu() {
        val screenHeight = clChooseCalendarDay.resources.displayMetrics.heightPixels
        val slideAnimationShow = TranslateAnimation(0f, 0f, screenHeight.toFloat(), 0f)
        val alphaAnimationShow = AlphaAnimation(0f, 1f)
        val animationSetShow = AnimationSet(true)
        animationSetShow.addAnimation(slideAnimationShow)
        animationSetShow.addAnimation(alphaAnimationShow)
        animationSetShow.duration = 1000

        animationSetShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                clChooseCalendarDay.isClickable = false
                clChooseCalendarDay.visibility = VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {
                clChooseCalendarDay.isClickable = true
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        clChooseCalendarDay.startAnimation(animationSetShow)
    }

    fun hideDateMenu() {
        val screenHeight = clMain.resources.displayMetrics.heightPixels
        val slideAnimationHide = TranslateAnimation(0f, 0f, 0f, screenHeight.toFloat())
        val alphaAnimationHide = AlphaAnimation(1f, 0f)
        val animationSetHide = AnimationSet(true)
        animationSetHide.addAnimation(slideAnimationHide)
        animationSetHide.addAnimation(alphaAnimationHide)
        animationSetHide.duration = 1000

        animationSetHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                clChooseCalendarDay.isClickable = false
                clChooseCalendarDay.visibility = GONE
            }

            override fun onAnimationEnd(animation: Animation?) {
                clChooseCalendarDay.visibility = GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        clChooseCalendarDay.startAnimation(animationSetHide)
    }

    fun showChooseTimeMenu() {
        val screenHeight = clChooseTime.resources.displayMetrics.heightPixels
        val slideAnimationShow = TranslateAnimation(0f, 0f, screenHeight.toFloat(), 0f)
        val alphaAnimationShow = AlphaAnimation(0f, 1f)
        val animationSetShow = AnimationSet(true)
        animationSetShow.addAnimation(slideAnimationShow)
        animationSetShow.addAnimation(alphaAnimationShow)
        animationSetShow.duration = 1000

        animationSetShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                clChooseTime.isClickable = false
                clChooseTime.visibility = VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {
                clChooseTime.isClickable = true
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        clChooseTime.startAnimation(animationSetShow)
    }

    fun hideTimeMenu() {
        val screenHeight = clMain.resources.displayMetrics.heightPixels
        val slideAnimationHide = TranslateAnimation(0f, 0f, 0f, screenHeight.toFloat())
        val alphaAnimationHide = AlphaAnimation(1f, 0f)
        val animationSetHide = AnimationSet(true)
        animationSetHide.addAnimation(slideAnimationHide)
        animationSetHide.addAnimation(alphaAnimationHide)
        animationSetHide.duration = 1000

        animationSetHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                clChooseTime.isClickable = false
                clChooseTime.visibility = GONE
            }

            override fun onAnimationEnd(animation: Animation?) {
                clChooseTime.visibility = GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        clChooseTime.startAnimation(animationSetHide)
    }

    fun isSensorAvailable(sensorManager:SensorManager): Boolean {
        val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        return heartRateSensor != null
    }
}
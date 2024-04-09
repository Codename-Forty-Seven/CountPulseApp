package com.the.countpulseapp.utils.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.the.countpulseapp.R
import com.the.countpulseapp.data.UserInfoPulse

class UserInfoPulseAdapter(var userInfoPulseList: List<UserInfoPulse>, private var showAllItems: Boolean) : RecyclerView.Adapter<UserInfoPulseAdapter.ViewHolder>() {

    fun setShowAllItems(showAllItems: Boolean) {
        this.showAllItems = showAllItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userInfoPulse = userInfoPulseList[position]
        holder.bind(userInfoPulse)
    }

    override fun getItemCount(): Int {
        return if (showAllItems) userInfoPulseList.size else minOf(userInfoPulseList.size, 3)
    }

    fun updateData(newData: List<UserInfoPulse>) {
        userInfoPulseList = newData
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPulse: TextView = itemView.findViewById(R.id.tvPulseItem)
        private val tvSystolic: TextView = itemView.findViewById(R.id.tvSystolic)
        private val tvDiastolic: TextView = itemView.findViewById(R.id.tvDiastolic)
        private val tvDateAndTime: TextView = itemView.findViewById(R.id.tvTimeAndDate)

        fun bind(userInfoPulse: UserInfoPulse) {
            tvPulse.text = userInfoPulse.pulse.toString()
            tvSystolic.text = userInfoPulse.systolic.toString()
            tvDiastolic.text = userInfoPulse.diastolic.toString()
            tvDateAndTime.text = "${userInfoPulse.dateToSet}, ${userInfoPulse.timeToSet}"
        }
    }
}
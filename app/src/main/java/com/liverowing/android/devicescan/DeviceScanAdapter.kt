package com.liverowing.android.devicescan

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import kotlinx.android.synthetic.main.fragment_device_scan_item.view.*

class DeviceScanAdapter(private val items: List<BluetoothDeviceAndStatus>, private val onClick: (View, BluetoothDeviceAndStatus) -> Unit) : RecyclerView.Adapter<DeviceScanAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_device_scan_item))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onClick)
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: BluetoothDeviceAndStatus, onClick: (View, BluetoothDeviceAndStatus) -> Unit) = with(itemView) {
            fragment_device_scan_name.text = item.device.name
            fragment_device_scan_status.text = item.status.toString()
            setOnClickListener { onClick(itemView, item) }
        }
    }
}
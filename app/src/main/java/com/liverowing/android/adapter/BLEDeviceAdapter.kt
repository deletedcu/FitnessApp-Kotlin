package com.liverowing.android.adapter

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.R
import com.liverowing.android.inflate
import kotlinx.android.synthetic.main.fragment_device_row.view.*

/**
 * Created by henrikmalmberg on 2017-11-02.
 */
class BLEDeviceAdapter(private val items: List<BLEDevice>, private val listener: (BLEDevice) -> Unit) : RecyclerView.Adapter<BLEDeviceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_device_row))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: BLEDevice, listener: (BLEDevice) -> Unit) = with(itemView) {
            f_device_scan_row_name.text = "${item.device.name} (${item.device.address})"
            f_device_scan_row_status.text = "${item.rssi}dBm"
            setOnClickListener { listener(item) }
        }
    }

    data class BLEDevice(val device: BluetoothDevice, var rssi: Int)
}
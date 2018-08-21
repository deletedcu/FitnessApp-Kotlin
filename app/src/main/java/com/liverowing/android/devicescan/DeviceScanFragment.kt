package com.liverowing.android.devicescan

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.MvpLceViewStateFragment
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.RetainingLceViewState
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.extensions.isPermissionGranted
import kotlinx.android.synthetic.main.fragment_device_scan.*


class DeviceScanFragment : MvpLceViewStateFragment<SwipeRefreshLayout, List<BluetoothDeviceAndStatus>, DeviceScanView, DeviceScanPresenter>(), DeviceScanView, SwipeRefreshLayout.OnRefreshListener {
    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val REQUEST_FINE_LOCATION = 2
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewDividerItemDecoration: DividerItemDecoration

    private val dataSet = mutableListOf<BluetoothDeviceAndStatus>()

    override fun createPresenter() = DeviceScanPresenter(activity!!.applicationContext)

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_device_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setupToolbar(f_device_scan_toolbar)

        viewManager = LinearLayoutManager(activity)
        viewDividerItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        viewAdapter = DeviceScanAdapter(dataSet) { _, device ->
            if (device.status != BluetoothDeviceAndStatus.Companion.Status.Disconnected) {
                presenter.disconnectFromDevice(device.device)
            } else {
                presenter.connectToDevice(device.device)
            }
        }

        contentView.setOnRefreshListener(this@DeviceScanFragment)
        recyclerView = f_device_scan_recyclerview.apply {
            setHasFixedSize(true)
            addItemDecoration(viewDividerItemDecoration)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stopScanning()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            presenter.loadDevices(false)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_FINE_LOCATION && isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            presenter.loadDevices(false)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun requestBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }

    override fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION)
    }

    override fun deviceConnected(name: String) {
        Toast.makeText(activity, "$name connected.", Toast.LENGTH_SHORT).show()
        findNavController(view!!).navigateUp()
    }

    override fun loadData(pullToRefresh: Boolean) {
        presenter.loadDevices(pullToRefresh)
    }

    override fun onRefresh() {
        loadData(true)
    }

    override fun setData(data: List<BluetoothDeviceAndStatus>) {
        dataSet.clear()
        dataSet.addAll(data)
        viewAdapter.notifyDataSetChanged()
    }

    override fun showContent() {
        super.showContent()
        contentView.isRefreshing = false
    }

    override fun showError(e: Throwable?, pullToRefresh: Boolean) {
        super.showError(e, pullToRefresh)
        contentView.isRefreshing = false
    }

    override fun showLoading(pullToRefresh: Boolean) {
        super.showLoading(pullToRefresh)
        contentView.isRefreshing = pullToRefresh
    }

    override fun createViewState(): LceViewState<List<BluetoothDeviceAndStatus>, DeviceScanView> = RetainingLceViewState()

    override fun getData(): List<BluetoothDeviceAndStatus> {
        return dataSet
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        return "There was an error listing bluetooth devices:\n\n${e?.message}"
    }
}

package com.jeluchu.zebraprinter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import android.widget.ListView
import android.content.IntentFilter
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.printer.PrinterLanguage
import com.zebra.sdk.printer.ZebraPrinter
import com.zebra.sdk.printer.ZebraPrinterFactory
import java.io.IOException


class MainActivity2 : AppCompatActivity() {

    private lateinit var listView: ListView
    private val mDeviceList = ArrayList<String>()
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothDeviceAddress = mutableListOf<String>()
    val content = "^XA\n" +
            "^FX Second section with recipient address and permit information.\n" +
            "^CFA,30\n" +
            "^FO50,200^FDJohn Doe^FS\n" +
            "^FO50,340^FD100 Main Street^FS\n" +
            "^FO50,380^FDSpringfield TN 39021^FS\n" +
            "^FO50,420^FDUnited States (USA)^FS\n" +
            "^CFA,15\n" +
            "^FO600,300^GB150,150,3^FS\n" +
            "^FO638,340^FDPermit^FS\n" +
            "^FO638,390^FD123456^FS\n" +
            "^XZ"

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent
                        .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                mDeviceList.add(device.name + "\n" + device.address)
                bluetoothDeviceAddress.add(device.address)
                Log.i("BT", device.name + "\n" + device.address)
                listView.adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_list_item_1, mDeviceList
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothAdapter?.startDiscovery()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)

        listView.setOnItemClickListener { _, _, position, _ ->
            print(bDeviceAddress = bluetoothDeviceAddress[position], content = content)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    private fun print(bDeviceAddress: String, content: String): ZebraPrinter? {
        var printer: ZebraPrinter? = null
        val printerConnection = BluetoothConnection(bDeviceAddress)
        try {
            printerConnection.open()
            if (printer == null) {
                printer = ZebraPrinterFactory.getInstance(PrinterLanguage.CPCL, printerConnection)
            }
            sendToPrint(printer!!, content)
            printerConnection.close()
        } catch (e: ConnectionException) {
            Log.d("ERROR - ", e.message)
        } finally {

        }
        return printer
    }

    private fun sendToPrint(printer: ZebraPrinter, content: String) {
        try {
            val filepath = getFileStreamPath("TEMP.LBL")
            createFile("TEMP.LBL", content)
            printer.sendFileContents(filepath.absolutePath)
        } catch (e1: ConnectionException) {
            Log.d("ERROR - ", "Error sending file to printer")
        } catch (e: IOException) {
            Log.d("ERROR - ", "Error creating file")
        }

    }

    @Throws(IOException::class)
    private fun createFile(fileName: String, content: String) {
        val os = this.openFileOutput(fileName, Context.MODE_APPEND)
        os.write(content.toByteArray())
        os.flush()
        os.close()
    }
}

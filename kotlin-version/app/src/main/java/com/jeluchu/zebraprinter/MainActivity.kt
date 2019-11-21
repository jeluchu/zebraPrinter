package com.jeluchu.zebraprinter

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zebra.android.comm.BluetoothPrinterConnection
import com.zebra.android.comm.ZebraPrinterConnectionException
import com.zebra.android.printer.PrinterLanguage
import com.zebra.android.printer.ZebraPrinter
import com.zebra.android.printer.ZebraPrinterFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var printerConnection: BluetoothPrinterConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPrint.setOnClickListener {
            try { connect() }
            catch (ex: Exception) { ex.printStackTrace() }
        }

    }

    private fun connect() {

        printerConnection = null
        printerConnection = BluetoothPrinterConnection("AC:3F:A4:1C:23:69")

        try { printerConnection!!.open() } catch (e: ZebraPrinterConnectionException) { disconnect() }

        val printer: ZebraPrinter

        if (printerConnection!!.isConnected) {
            try {
                printer = ZebraPrinterFactory.getInstance(PrinterLanguage.ZPL, printerConnection)
                printer.graphicsUtil.printImage(BitmapFactory.decodeResource(this.resources, R.drawable.ticket), 0, 0, 550, 2030, false)
            } catch (e: ZebraPrinterConnectionException) { disconnect() }
        }
    }

    private fun disconnect() {
        try { if (printerConnection != null) printerConnection!!.close() }
        catch (e: ZebraPrinterConnectionException) { e.message }
    }
}

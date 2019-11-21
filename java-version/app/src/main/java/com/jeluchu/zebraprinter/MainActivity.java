package com.jeluchu.zebraprinter;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;

public class MainActivity extends AppCompatActivity {

    BluetoothPrinterConnection printerConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try { connect(); }
                catch (Exception ex) { ex.printStackTrace(); }
            }
        });

    }

    public void connect() {

        printerConnection = null;
        printerConnection = new BluetoothPrinterConnection("AC:3F:A4:1C:23:69");

        try { printerConnection.open(); }
        catch (ZebraPrinterConnectionException e) { disconnect(); }

        ZebraPrinter printer;

        if (printerConnection.isConnected()) {
            try {
                printer = ZebraPrinterFactory.getInstance(PrinterLanguage.ZPL, printerConnection);
                printer.getGraphicsUtil().printImage(BitmapFactory.decodeResource(this.getResources(), R.drawable.ticket), 0, 0, 550, 2030, false);
            } catch (ZebraPrinterConnectionException e) { disconnect(); }
        }

    }

    public void disconnect() {
        try { if (printerConnection != null) { printerConnection.close(); } }
        catch (ZebraPrinterConnectionException e) { e.getMessage(); }
    }

}

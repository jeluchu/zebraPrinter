package com.jeluchu.zebraprinter;

import android.content.Context;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;


public class ZebraPrinterUtils {

    private static final int LINE_LENGHT_18 = 65;
    private static final int LINE_LENGHT_14 = 93;
    private static final String TAG = "ZebraPrinterUtils";
    private static BluetoothPrinterConnection printerConnection;

    public static ZebraPrinter connectPrinterZPL(String macAddress, Context context) {
        printerConnection = new BluetoothPrinterConnection(macAddress);
        try {
            printerConnection.open();
        } catch (ZebraPrinterConnectionException e) {
            disconnectPrinter(context);
        }

        ZebraPrinter printer = null;

        if (printerConnection != null && printerConnection.isConnected()) {
            try {
                printer = ZebraPrinterFactory.getInstance(PrinterLanguage.ZPL, printerConnection);
                String setLanguage = "\r\n! U1 setvar \"device.languages\" \"zpl\"\r\n";
                printerConnection.write(setLanguage.getBytes());
            } catch (ZebraPrinterConnectionException e) {
                printer = null;
                disconnectPrinter(context);
            }
        }
        return printer;
    }

    public static void disconnectPrinter(Context context) {
        try {
            if (printerConnection != null) {
                printerConnection.close();
                printerConnection = null;
            }
        } catch (ZebraPrinterConnectionException ignored) { }
    }

}
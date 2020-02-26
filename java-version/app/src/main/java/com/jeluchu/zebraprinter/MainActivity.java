package com.jeluchu.zebraprinter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
<<<<<<< Updated upstream
import android.util.Log;
=======
import android.os.Looper;
import android.preference.PreferenceManager;
>>>>>>> Stashed changes
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
<<<<<<< Updated upstream
import com.zebra.android.printer.GraphicsUtil;
=======
import com.zebra.android.discovery.BluetoothDiscoverer;
>>>>>>> Stashed changes
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;


<<<<<<< Updated upstream
=======
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

>>>>>>> Stashed changes
public class MainActivity extends AppCompatActivity {

    private ListView lstvw;
    private ArrayAdapter<String> aAdapter;
    private BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothPrinterConnection printerConnection;
<<<<<<< Updated upstream
    private GraphicsUtil graphicsUtil;
=======
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();

>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*TiffConverter.ConverterOptions options = new TiffConverter.ConverterOptions();
        options.throwExceptions = false; //Set to true if you want use java exception mechanism;
        options.availableMemory = 128 * 1024 * 1024; //Available 128Mb for work;
        options.compressionScheme = CompressionScheme.LZW; //compression scheme for tiff
        options.appendTiff = false;//If set to true - will be created one more tiff directory, otherwise file will be overwritten
        TiffConverter.convertToTiff(getResources().getDrawable(R.drawable.ticket).toString(), getFilesDir()+"/out.tif", options, progressListener);*/

        findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try { connect(); }
                catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    /*IProgressListener progressListener = new IProgressListener() {
        @Override
        public void reportProgress(long processedPixels, long totalPixels) {
            Log.v("Progress reporter", String.format("Processed %d pixels from %d", processedPixels, totalPixels));
        }
    };*/

    public void connect() {

        printerConnection = null;
        printerConnection = new BluetoothPrinterConnection("AC:3F:A4:1C:23:69");

        try { printerConnection.open(); }
        catch (ZebraPrinterConnectionException e) { disconnect(); }

        ZebraPrinter printer;

        if (printerConnection.isConnected()) {
            try {

                printer = ZebraPrinterFactory.getInstance(PrinterLanguage.ZPL, printerConnection);
               // printer.getGraphicsUtil().printImage(BitmapFactory.decodeResource(this.getResources(), R.drawable.ticket), 0, 0, 550, 2030, false);
                String textito ="^XA" +
                        "^FO220,115^FD1000 Shipping Lane^FS" +
                        "^FO220,155^FDShelbyville TN 38102^FS" +
                        "^FO220,195^FDUnited States (USA)^FS" +
                        "^FO50,250^GB700,1,3^FS" +
                        "^FX Second section with recipient address and permit information." +
                        "^CFA,30" +
                        "^FO50,300^FDJohn Doe^FS" +
                        "^FO50,340^FD100 Main Street^FS" +
                        "^FO50,380^FDSpringfield TN 39021^FS" +
                        "^FO50,420^FDUnited States (USA)^FS" +
                        "^CFA,15" +
                        "^FO600,300^GB150,150,3^FS" +
                        "^FO638,340^FDPermit^FS" +
                        "^FO638,390^FD123456^FS" +
                        "^FO50,500^GB700,1,3^FS" +
                        "^FX Third section with barcode." +
                        "^BY5,2,270" +
                        "^FO100,550^BC^FD12345678^FS" +
                        "^FX Fourth section (the two boxes on the bottom)." +
                        "^FO50,900^GB700,250,3^FS" +
                        "^FO400,900^GB1,250,3^FS" +
                        "^CF0,40" +
                        "^FO100,960^FDCtr. X34B-1^FS" +
                        "^FO100,1010^FDREF1 F00B47^FS" +
                        "^FO100,1060^FDREF2 BL4H8^FS" +
                        "^CF0,190" +
                        "^FO470,955^FDCA^FS" +
                        "^XZ" ;

                printer.getGraphicsUtil().printImage(textito,550, 2030);
            } catch (ZebraPrinterConnectionException e) { disconnect(); }
        }

    }

    public void disconnect() {
        try { if (printerConnection != null) { printerConnection.close(); } }
        catch (ZebraPrinterConnectionException e) { e.getMessage(); }
    }

}

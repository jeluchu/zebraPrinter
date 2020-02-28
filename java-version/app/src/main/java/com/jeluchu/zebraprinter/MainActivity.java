package com.jeluchu.zebraprinter;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.GraphicsUtil;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;

import org.beyka.tiffbitmapfactory.CompressionScheme;
import org.beyka.tiffbitmapfactory.IProgressListener;
import org.beyka.tiffbitmapfactory.TiffConverter;


public class MainActivity extends AppCompatActivity {

    BluetoothPrinterConnection printerConnection;
    private GraphicsUtil graphicsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TiffConverter.ConverterOptions options = new TiffConverter.ConverterOptions();
        options.throwExceptions = false; //Set to true if you want use java exception mechanism;
        options.availableMemory = 128 * 1024 * 1024; //Available 128Mb for work;
        options.compressionScheme = CompressionScheme.LZW; //compression scheme for tiff
        options.appendTiff = false;//If set to true - will be created one more tiff directory, otherwise file will be overwritten
        TiffConverter.convertToTiff(getResources().getDrawable(R.drawable.ticket).toString(), getFilesDir()+"/out.tif", options, progressListener);

        findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try { connect(); }
                catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    IProgressListener progressListener = new IProgressListener() {
        @Override
        public void reportProgress(long processedPixels, long totalPixels) {
            Log.v("Progress reporter", String.format("Processed %d pixels from %d", processedPixels, totalPixels));
        }
    };

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
                String textito ="^XA" ;
                textito+="^ FO50,50 ^ XGE: "+ getFilesDir()+"/out.tif ^ FS" ;
                /*textito+="^FO220,155^FDShelbyville TN 38102^FS" ;
                textito+="^FO220,195^FDUnited States (USA)^FS" ;
                textito+="^FO50,250^GB700,1,3^FS" ;
                textito+="^FX Second section with recipient address and permit information." ;
                textito+="^CFA,30" ;
                textito+="^FO50,300^FDJohn Doe^FS" ;
                textito+="^FO50,340^FD100 Main Street^FS" ;
                textito+="^FO50,380^FDSpringfield TN 39021^FS" ;
                textito+="^FO50,420^FDUnited States (USA)^FS" ;
                textito+="^CFA,15" ;
                textito+="^FO600,300^GB150,150,3^FS" ;
                textito+="^FO638,340^FDPermit^FS" ;
                textito+="^FO638,390^FD123456^FS" ;
                textito+="^FO50,500^GB700,1,3^FS" ;
                textito+="^FX Third section with barcode." ;
                textito+="^BY5,2,270" ;
                textito+="^FO100,550^BC^FD12345678^FS" ;
                textito+="^FX Fourth section (the two boxes on the bottom)." ;
                textito+="^FO50,900^GB700,250,3^FS" ;
                textito+="^FO400,900^GB1,250,3^FS" ;
                textito+="^CF0,40" ;
                textito+="^FO100,960^FDCtr. X34B-1^FS" ;
                textito+="^FO100,1010^FDREF1 F00B47^FS" ;
                textito+="^FO100,1060^FDREF2 BL4H8^FS" ;
                textito+="^CF0,190" ;
                textito+="^FO470,955^FDCA^FS" ;
                textito+="^FO220,115^FD1000 Shipping Lane^FS" ;
                textito+="^FO220,155^FDShelbyville TN 38102^FS" ;
                textito+="^FO220,195^FDUnited States (USA)^FS" ;
                textito+="^FO50,250^GB700,1,3^FS" ;
                textito+="^FX Second section with recipient address and permit information." ;
                textito+="^CFA,30" ;
                textito+="^FO50,300^FDJohn Doe^FS" ;
                textito+="^FO50,340^FD100 Main Street^FS" ;
                textito+="^FO50,380^FDSpringfield TN 39021^FS" ;
                textito+="^FO50,420^FDUnited States (USA)^FS" ;
                textito+="^CFA,15" ;
                textito+="^FO600,300^GB150,150,3^FS" ;
                textito+="^FO638,340^FDPermit^FS" ;
                textito+="^FO638,390^FD123456^FS" ;
                textito+="^FO50,500^GB700,1,3^FS" ;
                textito+="^FX Third section with barcode." ;
                textito+="^BY5,2,270" ;
                textito+="^FO100,550^BC^FD12345678^FS" ;
                textito+="^FX Fourth section (the two boxes on the bottom)." ;
                textito+="^FO50,900^GB700,250,3^FS" ;
                textito+="^FO400,900^GB1,250,3^FS" ;
                textito+="^CF0,40" ;
                textito+="^FO100,960^FDCtr. X34B-1^FS" ;
                textito+="^FO100,1010^FDREF1 F00B47^FS" ;
                textito+="^FO100,1060^FDREF2 BL4H8^FS" ;
                textito+="^CF0,190" ;
                textito+="^FO470,955^FDCA^FS" ;
                textito+="^FO220,115^FD1000 Shipping Lane^FS" ;
                textito+="^FO220,155^FDShelbyville TN 38102^FS" ;
                textito+="^FO220,195^FDUnited States (USA)^FS" ;
                textito+="^FO50,250^GB700,1,3^FS" ;
                textito+="^FX Second section with recipient address and permit information." ;
                textito+="^CFA,30" ;
                textito+="^FO50,300^FDJohn Doe^FS" ;
                textito+="^FO50,340^FD100 Main Street^FS" ;
                textito+="^FO50,380^FDSpringfield TN 39021^FS" ;
                textito+="^FO50,420^FDUnited States (USA)^FS" ;
                textito+="^CFA,15" ;
                textito+="^FO600,300^GB150,150,3^FS" ;
                textito+="^FO638,340^FDPermit^FS" ;
                textito+="^FO638,390^FD123456^FS" ;
                textito+="^FO50,500^GB700,1,3^FS" ;
                textito+="^FX Third section with barcode." ;
                textito+="^BY5,2,270" ;
                textito+="^FO100,550^BC^FD12345678^FS" ;
                textito+="^FX Fourth section (the two boxes on the bottom)." ;
                textito+="^FO50,900^GB700,250,3^FS" ;
                textito+="^FO400,900^GB1,250,3^FS" ;
                textito+="^CF0,40" ;
                textito+="^FO100,960^FDCtr. X34B-1^FS" ;
                textito+="^FO100,1010^FDREF1 F00B47^FS" ;
                textito+="^FO100,1060^FDREF2 BL4H8^FS" ;
                textito+="^CF0,190" ;
                textito+="^FO470,955^FDCA^FS" ;
                textito+="^FO220,115^FD1000 Shipping Lane^FS" ;
                textito+="^FO220,155^FDShelbyville TN 38102^FS" ;
                textito+="^FO220,195^FDUnited States (USA)^FS" ;
                textito+="^FO50,250^GB700,1,3^FS" ;
                textito+="^FX Second section with recipient address and permit information." ;
                textito+="^CFA,30" ;
                textito+="^FO50,300^FDJohn Doe^FS" ;
                textito+="^FO50,340^FD100 Main Street^FS" ;
                textito+="^FO50,380^FDSpringfield TN 39021^FS" ;
                textito+="^FO50,420^FDUnited States (USA)^FS" ;
                textito+="^CFA,15" ;
                textito+="^FO600,300^GB150,150,3^FS" ;
                textito+="^FO638,340^FDPermit^FS" ;
                textito+="^FO638,390^FD123456^FS" ;
                textito+="^FO50,500^GB700,1,3^FS" ;
                textito+="^FX Third section with barcode." ;
                textito+="^BY5,2,270" ;
                textito+="^FO100,550^BC^FD12345678^FS" ;
                textito+="^FX Fourth section (the two boxes on the bottom)." ;
                textito+="^FO50,900^GB700,250,3^FS" ;
                textito+="^FO400,900^GB1,250,3^FS" ;
                textito+="^CF0,40" ;
                textito+="^FO100,960^FDCtr. X34B-1^FS" ;
                textito+="^FO100,1010^FDREF1 F00B47^FS" ;
                textito+="^FO100,1060^FDREF2 BL4H8^FS" ;
                textito+="^CF0,190" ;
                textito+="^FO470,955^FDCA^FS" ;
                textito+="^FO220,115^FD1000 Shipping Lane^FS" ;
                textito+="^FO220,155^FDShelbyville TN 38102^FS" ;
                textito+="^FO220,195^FDUnited States (USA)^FS" ;
                textito+="^FO50,250^GB700,1,3^FS" ;
                textito+="^FX Second section with recipient address and permit information." ;
                textito+="^CFA,30" ;
                textito+="^FO50,300^FDJohn Doe^FS" ;
                textito+="^FO50,340^FD100 Main Street^FS" ;
                textito+="^FO50,380^FDSpringfield TN 39021^FS" ;
                textito+="^FO50,420^FDUnited States (USA)^FS" ;
                textito+="^CFA,15" ;
                textito+="^FO600,300^GB150,150,3^FS" ;
                textito+="^FO638,340^FDPermit^FS" ;
                textito+="^FO638,390^FD123456^FS" ;
                textito+="^FO50,500^GB700,1,3^FS" ;
                textito+="^FX Third section with barcode." ;
                textito+="^BY5,2,270" ;
                textito+="^FO100,550^BC^FD12345678^FS" ;
                textito+="^FX Fourth section (the two boxes on the bottom)." ;
                textito+="^FO50,900^GB700,250,3^FS" ;
                textito+="^FO400,900^GB1,250,3^FS" ;
                textito+="^CF0,40" ;
                textito+="^FO100,960^FDCtr. X34B-1^FS" ;
                textito+="^FO100,1010^FDREF1 F00B47^FS" ;
                textito+="^FO100,1060^FDREF2 BL4H8^FS" ;
                textito+="^CF0,190" ;
                textito+="^FO470,955^FDCA^FS" ;*/
                textito+="^XZ" ;

            printerConnection.write(textito.getBytes());


            } catch (ZebraPrinterConnectionException e) { disconnect(); }
        }

    }

    public void disconnect() {
        try { if (printerConnection != null) { printerConnection.close(); } }
        catch (ZebraPrinterConnectionException e) { e.getMessage(); }
    }

}

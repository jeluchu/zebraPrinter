package com.jeluchu.zebraprinter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.graphics.internal.CompressedBitmapOutputStreamCpcl;
import com.zebra.android.graphics.internal.DitheredImageProvider;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraIllegalArgumentException;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;
import com.zebra.android.printer.ZebraPrinterLanguageUnknownException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    ZebraPrinter printer;
    String macAddress;
    BluetoothPrinterConnection printerConnection;
    BluetoothConnector connector;
    ParcelUuid[] uuids;
    ArrayList<UUID> uuid;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    TextView tvPrinterName;
    EditText etText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CREATE OBJECT OF CONTROLS
        Button btnConnect = findViewById(R.id.btnConnect);
        Button btnDisconnect = findViewById(R.id.btnDisconnect);
        Button btnPrint = findViewById(R.id.btnPrint);

        etText = findViewById(R.id.etText);
        tvPrinterName = findViewById(R.id.tvPrinterName);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //findBluetoothDevice();
                    //openBluetoothPrinter();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //disconnectBT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //printData();
                    imprimirBoleta();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

   /* void findBluetoothDevice() {

        try {

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapter == null) {
                tvPrinterName.setText("No Bluetooth Adapter device found");
            }

            if (bluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, 0);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if (pairedDevice.size() > 0) {
                for (BluetoothDevice pairedDev : pairedDevice) {

                    if (pairedDev.getName().equals("XXXXJ143200473")) {
                        bluetoothDevice = pairedDev;
                        macAddress = pairedDev.getAddress();
                        tvPrinterName.setText("Bluetooth Printer Attached: " + pairedDev.getName());
                        break;
                    }
                }
            }

            tvPrinterName.setText("Bluetooth Printer Attached");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void openBluetoothPrinter() {

        try {

            UUID uuidString = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidString);

            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            beginListendData();

        } catch (Exception ex) {

        }
    }

    void beginListendData() {

        try {

            final Handler handler = new Handler();
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int byteAvailable = inputStream.available();
                            if (byteAvailable > 0) {
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for (int i = 0; i < byteAvailable; i++) {
                                    byte b = packetByte[i];
                                    if (b == delimiter) {
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedByte, 0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte, "US-ASCII");
                                        readBufferPosition = 0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvPrinterName.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }

                            }
                        } catch (Exception ex) {
                            stopWorker = true;
                        }
                    }

                }
            });

            thread.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    void printData() {
        try {

            String msg = etText.getText().toString();
            //String commands = "^XA\n\r^MNM\n\r^FO050,50\n\r^B8N,100,Y,N\n\r^FD1234567\n\r^FS\n\r^PQ3\n\r^XZ"; // TO PRINT BAR CODE
            String zplData = "^XA^FO20,20^A0N,25,25^FD" + msg + "^FS^XZ";

            String example = "^XA^PMN\n" +
                    "^DFR:SAMPLE.GRF^FS\n" +
                    "^FO20,30^GB750,1100,4^FS\n" +
                    "^FO20,30^GB750,200,4^FS\n" +
                    "^FO20,30^GB750,400,4^FS\n" +
                    "^FO20,30^GB750,700,4^FS\n" +
                    "^FO20,226^GB325,204,4^FS\n" +
                    "^FO30,40^ADN,36,20^FDShip to:^FS\n" +
                    "^FO30,260^ADN,18,10^FDPart number #^FS\n" +
                    "^FO360,260^ADN,18,10^FDDescription:^FS\n" +
                    "^FO30,750^ADN,36,20^FDFrom:^FS\n" +
                    "^FO150,125^ADN,36,20^FN1^FS (ship to)\n" +
                    "^FO60,330^ADN,36,20^FN2^FS(part num)\n" +
                    "^FO400,330^ADN,36,20^FN3^FS(description)\n" +
                    "^FO70,480^BY4^B3N,,200^FN4^FS(barcode)\n" +
                    "^FO150,800^ADN,36,20^FN5^FS (from)\n" +
                    "^XZ\n" +
                    "^XA\n" +
                    "^XFR:SAMPLE.GRF\n" +
                    "^FN1^FDAcme Printing^FS\n" +
                    "^FN2^FD14042^FS\n" +
                    "^FN3^FDScrew^FS\n" +
                    "^FN4^FD12345678^FS\n" +
                    "^FN5^FDMacks Fabricating^FS\n" +
                    "^XZ";
            //printerConnection.open();
            StringBuilder toPrint = new StringBuilder();


            outputStream.write(zplData.getBytes());
            outputStream.write(example.getBytes());

            tvPrinterName.setText("Printting Text...");

            disconnectBT();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void disconnectBT() {
        try {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            tvPrinterName.setText("Printer Disconnected");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } */

    public void imprimirBoleta()
    {
        Log.d("Imprimir", "Boleta");
        Toast.makeText(this,"Imprimir Boleta", Toast.LENGTH_SHORT).show();
        doConnectionTest();
        /*new Thread(new Runnable() {
         public void run() {
         Looper.prepare();
         doConnectionTest();
         Looper.loop();
         Looper.myLooper().quit();
         }
         }).start();*/
    }

    private void doConnectionTest()
    {
        Log.d("Do", "Connection");
        ZebraPrinter printer = connect();
        if (printer != null) {
            Log.d("Imprimir", "label");
            sendTestLabel();
        } else {
            Log.d("Imprimir", "Null");
            disconnect();
        }
    }

    private void sendTestLabel()
    {
        try
        {
            //String zpl = "^XA^FO100, ^XGR:LOGO.GRF^FS";


            /*String imprimir = "! 0 200 200 1200 1\r\n"
                    + "BOX 100 10 700 200 8\r\n"
                    + "CENTER\r\n"
                    + "T 4 0 0 30 R.U.T.: 99.513.400-4\r\n"
                    + "T 4 0 0 80 BOLETA ELECTRONICA\r\n"
                    + "T 4 0 0 130 N 1\r\n"
                    + "T 0 3 0 205 S.I.I-SANTIAGO ORIENTE\r\n"
                    + "LEFT\n\r"
                    + "T 0 3 10 250 N CLIENTE: " + 545455 + "\r\n"
                    + "T 0 3 10 275 Fecha de emision: 05 jun 2017\r\n"
                    + "L 10 300 810 300 1\r\n"
                    + "T 0 3 10 310 Sr. (a) " + "Soy un nombre" + "\r\n"
                    + "T 0 3 10 335 Direccion de Envio: " + "Soy una dirección" + "\r\n"
                    + "T 0 3 330 360 TALCA\r\n"
                    + "T 0 3 10 385 Observaciones de reparto: \r\n"
                    + "T 0 3 10 410 Ruta: " + 4545 + "| Var.Corresp: RMAN\r\n"
                    + "L 10 435 810 435 1\r\n"
                    + "CENTER\r\n"
                    + "T 0 3 0 460 Detalle de mi cuenta\r\n"
                    + "LEFT\n\r"
                    + "T 0 3 10 495 Servicio Electrico\r\n"
                    + "T 0 3 10 520 Administracion del servicio\r\n"
                    + "T 0 3 10 545 (cargo fijo)\r\n"
                    + "T 0 3 650 520 $      847\r\n"
                    + "T 0 3 10 575 Electricidad Consumida\r\n"
                    + "T 0 3 10 600 (cargo por energia base) (150 kWh)\r\n"
                    + "T 0 3 650 575 $   19.270\r\n"
                    + "T 0 3 10 630 Transporte de la electricidad\r\n"
                    + "T 0 3 10 655 (cargo unico uso sistema troncal)\r\n"
                    + "T 0 3 650 630 $      221\r\n"
                    + "T 0 3 10 685 Arriendo de medidor\r\n"
                    + "T 0 3 650 685 $      426\r\n"
                    + "T 0 3 10 720 Otros Cargos\r\n"
                    + "T 0 3 10 745 Ajuste para facilitar el pago\r\n"
                    + "T 0 3 10 770 en efectivo, mes anterior\r\n"
                    + "T 0 3 650 745 $       48\r\n"
                    + "T 0 3 10 800 Ajuste para facilitar el pago\r\n"
                    + "T 0 3 10 825 en efectivo, mes actual\r\n"
                    + "T 0 3 650 800 $      -12\r\n"
                    + "T 0 3 10 875 Monto afecto a impuesto\r\n"
                    + "T 0 3 650 875 $   20.764\r\n"
                    + "T 0 3 10 905 Monto exento a impuesto\r\n"
                    + "T 0 3 650 905 $        0\r\n"
                    + "T 0 3 10 935 Total Boleta\r\n"
                    + "T 0 3 650 935 $   20.764\r\n"
                    + "T 0 3 10 965 Saldo anterior\r\n"
                    + "T 0 3 650 965 $        0\r\n"
                    + "T 4 0 10 995 Total a pagar\r\n"
                    + "T 4 0 650 995 $20.800\r\n"
                    + "B PDF-417 100 1050 XD 5 YD 30 C 3 S 2\r\n"
                    + "PDF DATA\r\n"
                    + "codigo prueba\r\n"
                    + "ENDPDF\r\n"
                    + "PRINT \r\n"; */

            /*String zpl;
            zpl = "^XA\r\n";
            zpl += "^FO100,^XGR:LOGO.GRF^FS\r\n";
            zpl += "^CF0,25\r\n";
            zpl += "^FO30,^FB500,1,0,C^FD ^FS^XZ\r\n";


            printerConnection.write(zpl.getBytes());

             */
            //setStatus("Sending Data", Color.BLUE);
            //DemoSleeper.sleep(1500);
            if (printerConnection instanceof BluetoothPrinterConnection) {
                String friendlyName = ((BluetoothPrinterConnection) printerConnection).getFriendlyName();
                //setStatus(friendlyName, Color.MAGENTA);
                //DemoSleeper.sleep(500);
            }
        } finally {
            disconnect();
            Intent resultIntent = new Intent();
            //setResult(Activity.RESULT_OK, resultIntent);
            this.getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * Metodo encargado de conectarse a la impresora
     * @return instancia de la impresora.
     */
    public ZebraPrinter connect()
    {

        //setStatus("Connecting...", Color.YELLOW);
        printerConnection = null;
        printerConnection = new BluetoothPrinterConnection("AC:3F:A4:1C:23:69");

        //SettingsHelper.saveBluetoothAddress(this, getMacAddressFieldText());
        try {
            printerConnection.open();
            //setStatus("Connected", Color.GREEN);
        }   catch (ZebraPrinterConnectionException e) {
            e.printStackTrace();
            //setStatus("Comm Error! Disconnecting", Color.RED);
            //DemoSleeper.sleep(1000);
            Log.d("Error", "conection.open");
            disconnect();
        }

        ZebraPrinter printer = null;

        if (printerConnection.isConnected()) {
            try {
                printer = ZebraPrinterFactory.getInstance(PrinterLanguage.ZPL, printerConnection);

                //printer.getGraphicsUtil().storeImage("R:LOGO.GRF", BitmapFactory.decodeResource(this.getResources(), R.drawable.logo_repsol_black), 576, 3875);

                //BitmapFactory.decodeFile(file.getAbsolutePath())

                //and you can pass that to the printer via

                //getGraphicsUtil().printImage(pathOnPrinter, bitmap, [x], [y])

                printerConnection.write("! UTILITIES\r\nIN-MILLIMETERS\r\nSETFF 10 2\r\nPRINT\r\n".getBytes());
                //zp.getGraphicsUtil().printImage(bmp,0,0,100,100,false);
                printer.getGraphicsUtil().printImage(BitmapFactory.decodeResource(this.getResources(), R.drawable.chinochuke), -1,0,400,2000,false);
                //printer.getGraphicsUtil().printImage(BitmapFactory.decodeResource(this.getResources(), R.drawable.chinochuke), 0,0,350,100,false);


               /* Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo_repsol_black);
                int widthOfImageInBytes = (bitmap.getWidth() + 7) / 8;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(("! 0 200 200 "
                        + bitmap.getHeight()
                        + " 1\r\nCG "
                        + widthOfImageInBytes
                        + " "
                        + bitmap.getHeight()
                        + " "
                        + 0
                        + " "
                        + 0
                        + " ").getBytes());
                printerConnection.write(baos.toByteArray());
                OutputStream compressedBitmapOutputStreamCpcl = new CompressedBitmapOutputStreamCpcl(printerConnection);
                DitheredImageProvider.getDitheredImage(bitmap, compressedBitmapOutputStreamCpcl);
                compressedBitmapOutputStreamCpcl.close();
                printerConnection.write("\r\nFORM\r\nPRINT\r\n".getBytes());*/

                //printer.printImage(new ZebraImageAndroid(bitmap), x, y, width, height, false);

                //setStatus("Determining Printer Language", Color.YELLOW);
                //PrinterLanguage pl = printer.getPrinterControlLanguage();
                //setStatus("Printer Language " + pl, Color.BLUE);
            } catch (ZebraPrinterConnectionException e) {
                //setStatus("Unknown Printer Language", Color.RED);
                printer = null;
                //DemoSleeper.sleep(1000);
                Log.d("Error", "conection exception");
                disconnect();
            }
        }

        return printer;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Cierra la coneccion con la impresora
     */
    public void disconnect()
    {
        try
        {
            //setStatus("Disconnecting", Color.RED);
            if (printerConnection != null)
            {
                printerConnection.close();
            }
            //setStatus("Not Connected", Color.RED);
        }
        catch (ZebraPrinterConnectionException e)
        {
            //setStatus("COMM Error! Disconnected", Color.RED);
        }
        finally
        {
            //enableTestButton(true);
        }
    }

}

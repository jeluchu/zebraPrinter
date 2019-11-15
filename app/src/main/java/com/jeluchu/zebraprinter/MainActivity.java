package com.jeluchu.zebraprinter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.printer.ZebraPrinter;

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
                    findBluetoothDevice();
                    openBluetoothPrinter();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    disconnectBT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    printData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    void findBluetoothDevice() {

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
    }

}

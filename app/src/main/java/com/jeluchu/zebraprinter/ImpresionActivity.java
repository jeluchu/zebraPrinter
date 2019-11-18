package com.jeluchu.zebraprinter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;
import com.zebra.android.printer.ZebraPrinterLanguageUnknownException;


public class ImpresionActivity extends Fragment
{
    private ZebraPrinterConnection printerConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        Toast.makeText(this.getContext(), "Impresion" , Toast.LENGTH_SHORT).show();
        this.imprimirBoleta();
    }


    public void imprimirBoleta()
    {
        Log.d("Imprimir", "Boleta");
        Toast.makeText(this.getContext(),"Imprimir Boleta", Toast.LENGTH_SHORT).show();
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
            String imprimir = "! 0 200 200 1200 1\r\n"
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
                    + "PRINT \r\n";

            printerConnection.write(imprimir.getBytes());

            //setStatus("Sending Data", Color.BLUE);
            //DemoSleeper.sleep(1500);
            if (printerConnection instanceof BluetoothPrinterConnection) {
                String friendlyName = ((BluetoothPrinterConnection) printerConnection).getFriendlyName();
                //setStatus(friendlyName, Color.MAGENTA);
                //DemoSleeper.sleep(500);
            }
        } catch (ZebraPrinterConnectionException e) {
            //setStatus(e.getMessage(), Color.RED);
        } finally {
            disconnect();
            Intent resultIntent = new Intent();
            //setResult(Activity.RESULT_OK, resultIntent);
            this.getActivity().getSupportFragmentManager().popBackStack();
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
        printerConnection = new BluetoothPrinterConnection("dgxdgxdgx");

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
                printer = ZebraPrinterFactory.getInstance(printerConnection);
                //setStatus("Determining Printer Language", Color.YELLOW);
                PrinterLanguage pl = printer.getPrinterControlLanguage();
                //setStatus("Printer Language " + pl, Color.BLUE);
            } catch (ZebraPrinterConnectionException e) {
                //setStatus("Unknown Printer Language", Color.RED);
                printer = null;
                //DemoSleeper.sleep(1000);
                Log.d("Error", "conection exception");
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                //setStatus("Unknown Printer Language", Color.RED);
                printer = null;
                //DemoSleeper.sleep(1000);
                Log.d("Error", "printerlenguage");
                disconnect();
            }
        }

        return printer;
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

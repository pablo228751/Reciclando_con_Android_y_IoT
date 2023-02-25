package com.example.luces;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button encender,apagar,desconectar;
    Button btn_0, btn_30, btn_60, btn_90, btn_120, btn_150, btn_180,btn_monitoreo;
    TextView IdBufferin;
    Handler bluetoothIn;
    final int handlerState=0;
    private BluetoothAdapter btAdapter=null;
    private BluetoothSocket btSocket=null;
    private StringBuilder DataStringIN= new StringBuilder();
    private ConnectedThread MyConexionBT;
    private  static  final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private  static  String address= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        encender =(Button) findViewById(R.id.encender);
        apagar =(Button) findViewById(R.id.apagar);
        desconectar =(Button) findViewById(R.id.desconectar);
        btn_0 =(Button) findViewById(R.id.btn_0);
        btn_30 =(Button) findViewById(R.id.btn_30);
        btn_60 =(Button) findViewById(R.id.btn_60);
        btn_90 =(Button) findViewById(R.id.btn_90);
        btn_120 =(Button) findViewById(R.id.btn_120);
        btn_150 =(Button) findViewById(R.id.btn_150);
        btn_180 =(Button) findViewById(R.id.btn_180);
        btn_monitoreo =(Button) findViewById(R.id.btn_monitoreo);
        IdBufferin =(TextView) findViewById(R.id.IdBufferin);
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        IdBufferin.setText("Estado: " + dataInPrint);//<-<- PARTE A MODIFICAR >->->
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();

        // Configuracion onClick listeners para los botones
        // para indicar que se realizara cuando se detecte
        // el evento de Click
        encender.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MyConexionBT.write("OUT:A");
            }
        });

        apagar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("OUT:B");
            }
        });
        btn_0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("OUT:C");
            }
        });
        btn_30.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("OUT:D");
            }
        });
        btn_60.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("OUT:E");
            }
        });
        btn_90.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("OUT:F");
            }
        });
        btn_120.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("OUT:G");
            }
        });
        btn_150.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("OUT:H");
            }
        });
        btn_180.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("OUT:I");
            }
        });
        btn_monitoreo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("OUT:J");
                Intent i = new Intent(MainActivity.this, monitorizcion.class);//<-<- PARTE A MODIFICAR >->->
                startActivity(i);
            }
        });

        desconectar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (btSocket!=null)
                {
                    try {btSocket.close();}
                    catch (IOException e)
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();;}
                }
                finish();
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(dispositivo_bt.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
    }

    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión Falló", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}

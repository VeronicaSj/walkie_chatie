package com.example.walkie_chatie.controler;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walkie_chatie.connection.Client;
import com.example.walkie_chatie.R;
import com.example.walkie_chatie.connection.Server;
import com.example.walkie_chatie.model.Mensage;

/**
 * Clase que controla la primera vista que encontramos al abrir la aplicacion. Desde esta clase
 * establecemos una primera comunicacion entre distintos dispositivos.
 */

public class LobbyActivity extends AppCompatActivity {

    public static String myIp = "";

    private TextView txtMyIP;
    private EditText editNick;
    private EditText editReceptorIp;
    private Button btnUpdateIp;
    private Button btnStart;

    private Server server;
    private Client client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        //asociacion de la vista y su parte programatica
        txtMyIP = (TextView) findViewById(R.id.txtMyIP);
        editNick = (EditText) findViewById(R.id.editNick);
        editReceptorIp = (EditText) findViewById(R.id.editReceptorIp);
        btnStart = (Button) findViewById(R.id.buttonStartTalking);
        btnUpdateIp = (Button) findViewById(R.id.btnUpdateIp);

        //obtenemos la ip del usuario y mostramos los textos relacionados con la ip
        myIp = getMyIp();//obtenemos la ip del user
        txtMyIP.setText("Mi ip actual es: " + myIp);//mostramos la ip del user
        editReceptorIp.setText(myIp.substring(0, myIp.lastIndexOf(".") + 1));//facilitamos el rellenado de la ip

        //bloque relativo al funcionamiento de la conexion
        client = new Client();
        server = new Server(this);
        startServer();

        //funcionamiento del boton de comenzar a chatear
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si mi ip esta bien, procedemos a la validacion de los datos introducidos
                if(!myIp.equals("error")){
                    //si los datos introducidos son validos:
                    //1. mandamos un mensaje de aviso al receptor
                    //2. iniciamos el chat y le pasamos los datos
                    if(validateData(editNick.getText().toString(), editReceptorIp.getText().toString())){
                        //1. mandamos un mensaje de aviso al receptor
                        Mensage msg = new Mensage("USUARIO CONECTADO", myIp);//creamos el mensaje
                        client.sendMessage(msg, editReceptorIp.getText().toString());//lo mandamos

                        //2. iniciamos el chat y le pasamos los datos
                        Intent i = new Intent(LobbyActivity.this, ChatActivity.class);
                        i.putExtra("nombre", editNick.getText().toString());
                        i.putExtra("ip", editReceptorIp.getText().toString());
                        startActivity(i);
                    }else{
                        //si los datos introucidos no fueran validos, informamos al usuario
                        createAlertDialog("ERROR", "Los datos introducidos son incorrectos").show();
                    }
                }else{ //si mi ip no está bien, mandamos un mensaje de error
                    createAlertDialog("ERROR", "No se encuentra la ip de este dispositivo").show();
                }
            }
        });

        //funcionamiento del boton de actualizar la ip del user
        btnUpdateIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myIp = getMyIp() ;//obtenemos la ip del user
                txtMyIP.setText("Mi ip actual es: " + myIp);//mostramos
            }
        });
    }


    //____________________________________________________________________________________________

    //Métodos auxiliares

    /**
     * funcion para obtener la ip del propio dipositivo
     * @return ip del propio dipositivo
     */
    private String getMyIp(){
        String ip="";
        try{
            Context context = this.getApplicationContext();//obtenemos el contexto
            //con el contexto obtenemos el wifiManager
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());//preguntamos la ip
        }catch(Exception ex){
            ip= "error";//si algo sale mal, retornamos un codigo de error
        }
        return ip;
    }

    /**
     * funcion para parametrizar un AlertDialog
     * @param titulo titulo del AlertDialog
     * @param mensaje mensaje del AlertDialog
     * @return un AlertDialog
     */
    private AlertDialog createAlertDialog(String titulo, String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(LobbyActivity.this);
        builder.setMessage(mensaje).setTitle(titulo);

        //no hay boton de confirmacion, se pulsa fuera de la notificacion para cerrar

        return builder.create();
    }

    /**
     * funcion para validar los datos de conexion dados por el usuario
     * @param Nick nick del usuario ajeno
     * @param Ip ip del usuario ajeno
     * @return true si los parametros son validos. false en caso contrario
     */
    private boolean validateData(String Nick,String Ip){
        boolean res = false;
        if(Nick != null && Ip != null){//Ningun dato puede ser null
            //las ip validas tienen entre 7 y 15 caracteres
            if(Nick.length()>0 && Nick.length() <= 15 && Ip.length() >= 7 && Ip.length() <= 15) {
                res = true;
            }
        }
        return res;
    }

    /**
     * funcion para evitar la repeticion de codigo al poner a funcioner el servidor
     */
    private void startServer() {
        //iniciamos el bucle del servidor
        //el Runnable que le pasamos es el codigo que queremos ejecutar en la vista
        server.startServer(new Runnable() {
            @Override
            public void run() {
                try{
                    Mensage msg = server.getMsg();//obtenemos el mensaje recibido
                    //notificamos al usuario que alguen quiere conectarse con el
                    //facilitamos la ip por si el usuario estuviera interesado
                    createAlertDialog("Alguien quiere contactar", "IP: "+msg.getMsg()).show();
                }catch(Exception ex){}
            }
        });
    }


    //____________________________________________________________________________________________

    //metodos que evitan errores al cambiar de pantalla, al cerrar o al volver a esta pantalla

    @Override
    protected void onPause() {
        super.onPause();
        if(server != null){
            server.killServer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(server != null){
            server.killServer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(server != null){
            startServer();
        }
    }
}
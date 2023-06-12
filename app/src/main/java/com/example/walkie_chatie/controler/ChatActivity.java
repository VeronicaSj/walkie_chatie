package com.example.walkie_chatie.controler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walkie_chatie.connection.Client;
import com.example.walkie_chatie.connection.Connection;
import com.example.walkie_chatie.connection.Server;
import com.example.walkie_chatie.model.Mensage;
import com.example.walkie_chatie.R;
import com.example.walkie_chatie.adapter.RecyclerAdapter;

import java.util.ArrayList;

/**
 * Clase que controla la vista en la que se chatea. Desde esta clase enviamos y recibimos mensajes.
 */
public class ChatActivity extends AppCompatActivity {

    private ImageButton btnSend;
    private EditText txtMensaje;
    private RecyclerView recyclerView;
    private RecyclerAdapter recAdapter;

    private Server server;
    private Client client;
    private ArrayList<Mensage> msgList = new ArrayList<>();

    private String receptorIp;//necesario para mandar los mensajes
    private String receptorNick;//necesario como elemeto visual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //asociacion de la vista y su parte programatica
        btnSend = findViewById(R.id.btnSend);
        txtMensaje = findViewById(R.id.editSend);
        recyclerView = findViewById(R.id.recyclerView);

        //recogemos los datos del usuario para usarlos en esta pantalla
        Intent intent = getIntent();
        receptorIp=intent.getStringExtra("ip");
        receptorNick=intent.getStringExtra("nombre");

        //aprovechamos la barrita de arriba para poner el nombre del receptor de mensajes
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(receptorNick);
        }

        // bolque relativo al funcionamiento del recycler view
        recAdapter = new RecyclerAdapter(msgList);//inicializamos el adapter con la lista de mensajes
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//los estarán uno debajo del otro
        recyclerView.setAdapter(recAdapter);//damos el adapter al recView
        recyclerView.setLayoutManager(layoutManager);//damos la distribución de elementos

        //bloque relativo al funcionamiento de la conexion
        client = new Client();
        server = new Server(this);
        startServer();

        //funcionamiento del boton de enviar mensaje
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si hay algo para enviar, procedemos al funcionamiento
                if(txtMensaje.getText().toString().length()>0) {
                    Mensage msg = new Mensage("Yo", txtMensaje.getText().toString());//creamos el mensaje
                    client.sendMessage(msg, receptorIp);//mandamos el mensaje al receptor
                    msgList.add(msg);//añadimos mensaje
                    recAdapter.notifyDataSetChanged();//actualizamos recView
                    txtMensaje.setText("");//vaciamos el editText
                    recyclerView.scrollToPosition(recAdapter.getItemCount() - 1);//vamos a ver el mensaje recien mandado
                }
            }
        });
    }


    //____________________________________________________________________________________________

    //Métodos auxiliares

    /**
     * funcion para evitar la repeticion de codigo al poner a funcioner el servidor
     */
    protected void startServer(){
        //iniciamos el bucle del servidor
        //el Runnable que le pasamos es el codigo que queremos ejecutar en la vista
        server.startServer(new Runnable() {
            @Override
            public void run() {
                try {
                    Mensage msg = server.getMsg();//obtenemos el mensaje recibido en el server
                    //los msgs llegan con name="yo" o con name="USUARIO CONECTADO"
                    if(msg.getName().equals("USUARIO CONECTADO")){//si el mensaje es una notificacion de usuario conectado
                        msg.setName("USUARIO CONECTADO");//dejamos el nombre "USUARIO CONECTADO"
                    }else{//si llegan con cualquier otro name
                        msg.setName(receptorNick);//les ponemos el nombre que el user haya elegido
                    }
                    msgList.add(msg);//añadimos a la lista
                    recAdapter.notifyDataSetChanged();//actualizamos recView

                    /*aqui podria bajar el recview para ver el mensaje recibido
                    con la funcion ->  recyclerView.scrollToPosition(recAdapter.getItemCount() - 1)
                    pero eso podria ser incomodo si el usuario quisiera leer algún mensaje antiguo y
                    le llegara un nuevo mensaje mientras intenta leer el antiguo */
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
        startServer();
    }
}
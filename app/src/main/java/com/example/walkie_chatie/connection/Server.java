package com.example.walkie_chatie.connection;

import android.content.Context;
import android.os.Handler;

import com.example.walkie_chatie.model.Mensage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase que implementa Connection. Gestiona el funcionamiento de un servidor que recibe mensajes
 * de un cliente
 */
public class Server implements Connection{

    private final Handler handler;//necesario para ejecutar cambios en la vista desde esta clase
    private Mensage msg;
    private boolean serverIsOpen;
    private ServerSocket serverSocket;

    public Server(Context context){
        this.handler = new Handler(context.getMainLooper()); //le pasamos donde se realizarian los cambios
    }

    /**
     * funcion que ayuda a comunicar el mensaje recibido con la vista
     * @return mensaje que recibe el servidor
     */
    public Mensage getMsg(){
        return this.msg;
    }

    /**
     * funcion que inicia un bucle a la espera de que un servidor reciba mensages
     * @param runnable codigo que queremos ejecutar pero que afecta a la vista
     */
    public void startServer(Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverIsOpen = true;//notificamos que el servidor esta a la espera
                    serverSocket = new ServerSocket(PORT);//abrimos el servidor
                    while(serverIsOpen) {
                        Socket socket = serverSocket.accept();//esperamos a una conexion de un cliente
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()); //abrimos un flujo de datos
                        msg = (Mensage) ois.readObject();//recibimos el mensaje del cliente
                        handler.post(runnable); //ejecutamos el codigo que afecta a la vista
                        ois.close();//cerramos el flujo
                        socket.close();//cerramos el socket cliente
                    }

                } catch (IOException ex) {} catch (ClassNotFoundException ex) {}
            }
        }).start();//este codigo debe ser lanzado en un hilo secundario
    }

    /**
     * funcion para parar el funcionamiento del servidor
     */
    public void killServer(){
        this.serverIsOpen = false;
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException ex) {}
    }
}

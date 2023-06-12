package com.example.walkie_chatie.connection;

import com.example.walkie_chatie.model.Mensage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Clase que implementa Connection. Gestiona el funcionamiento de un cliente que manda mensajes a un
 * servidor
 */
public class Client implements Connection{

    /**
     * funcion que manda un mensaje a un servidor
     * @param msg mensaje que se va a mandar
     * @param ipOther ip del servidor a donde se va a mandar el mensaje
     */
    public void sendMessage(Mensage msg, String ipOther){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(ipOther, PORT);//inicializamos el cliente con los datos de conexion
                    if (socket.isBound()) {//si nos hemos conectado, procedemos al intercambio de info
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());//abrimos un flujo de datos
                        oos.writeObject(msg);//mandamos el mensaje
                        oos.close();//cerramos el flujo
                        socket.close();//cerramos el cliente
                    }
                } catch (IOException ex) {}
            }}).start();//este codigo debe ser lanzado en un hilo secundario
    }
}

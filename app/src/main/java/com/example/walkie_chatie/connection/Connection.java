package com.example.walkie_chatie.connection;


import android.content.Context;
import android.os.Handler;

import com.example.walkie_chatie.model.Mensage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Interface que guarda el puerto de conexion entre cliente y servidor
 */
public interface Connection {
    public static final int PORT = 3940;
}

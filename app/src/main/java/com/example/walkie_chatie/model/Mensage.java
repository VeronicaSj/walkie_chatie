package com.example.walkie_chatie.model;

import java.io.Serializable;

/**
 * clase sencilla que da forma a los mensajes
 */
public class Mensage implements Serializable {
    private String name;
    private String msg;

    public Mensage(String nombre, String mensaje) {
        this.name = nombre;
        this.msg = mensaje;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public void setName(String name) {
        this.name = name;
    }
}

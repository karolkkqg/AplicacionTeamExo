package com.example.aplicacionteamexo.utilidades;

import android.content.SharedPreferences;

import android.content.Context;


public class Configuracion {
    private static final String IP_POR_DEFECTO = "192.168.233.88";
    private static final String CLAVE_IP = "ip_servidor";
    private static final String PREF_NAME = "configuracion";

    public static String obtenerIP(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(CLAVE_IP, IP_POR_DEFECTO);
    }
}

package com.example.aplicacionteamexo.data.modelo;

public class PublicacionInfo {
    private int identificador;
    private String titulo;
    private String contenido;
    private String estado;
    private int usuarioId;
    private String fechaCreacion;

    public int getIdentificador() {
        return identificador;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public String getEstado() {
        return estado;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }
}

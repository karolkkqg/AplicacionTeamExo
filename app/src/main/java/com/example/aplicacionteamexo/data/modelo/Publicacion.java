package com.example.aplicacionteamexo.data.modelo;

public class Publicacion {
    private String titulo;
    private String contenido;
    private int usuarioId;
    private String estado = "Borrador";

    public Publicacion(String titulo, String contenido, int usuarioId) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getEstado() {
        return estado;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

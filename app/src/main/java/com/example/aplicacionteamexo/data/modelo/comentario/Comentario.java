package com.example.aplicacionteamexo.data.modelo.comentario;

public class Comentario {
    private int comentarioId;
    private int publicacionId;
    private int usuarioId;
    private String texto;
    private String fecha;

    // Getters y Setters
    public int getComentarioId() { return comentarioId; }
    public void setComentarioId(int comentarioId) { this.comentarioId = comentarioId; }

    public int getPublicacionId() { return publicacionId; }
    public void setPublicacionId(int publicacionId) { this.publicacionId = publicacionId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}


package com.example.aplicacionteamexo.data.modelo.comentario;

public class ComentarioRegistro {
    private int publicacionId;
    private int usuarioId;
    private String texto;

    // Constructor
    public ComentarioRegistro(int publicacionId, int usuarioId, String texto) {
        this.publicacionId = publicacionId;
        this.usuarioId = usuarioId;
        this.texto = texto;
    }

    // Getters y Setters
    public int getPublicacionId() { return publicacionId; }
    public void setPublicacionId(int publicacionId) { this.publicacionId = publicacionId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}


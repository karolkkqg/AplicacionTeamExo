package com.example.aplicacionteamexo.data.modelo.comentario;

public class ComentarioRegistro {
    private int comentarioId;
    private int publicacionId;
    private int usuarioId;
    private String texto;

    public ComentarioRegistro(int comentarioId, int publicacionId, int usuarioId, String texto) {
        this.comentarioId = comentarioId;
        this.publicacionId = publicacionId;
        this.usuarioId = usuarioId;
        this.texto = texto;
    }

    // --- Getters ---

    public int getComentarioId() {
        return comentarioId;
    }

    public int getPublicacionId() {
        return publicacionId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getTexto() {
        return texto;
    }

    // --- Setters ---

    public void setComentarioId(int comentarioId) {
        this.comentarioId = comentarioId;
    }

    public void setPublicacionId(int publicacionId) {
        this.publicacionId = publicacionId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

}

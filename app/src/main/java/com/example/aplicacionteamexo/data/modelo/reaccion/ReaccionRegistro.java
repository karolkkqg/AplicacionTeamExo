package com.example.aplicacionteamexo.data.modelo.reaccion;

public class ReaccionRegistro {
    public String tipo;
    public int publicacionId;
    public int usuarioId;
    public String nombreUsuario;

    public ReaccionRegistro(String tipo, int publicacionId, int usuarioId, String nombreUsuario) {
        this.tipo = tipo;
        this.publicacionId = publicacionId;
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
    }


    public String getTipo() {
        return tipo;
    }

    public int getPublicacionId() {
        return publicacionId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    // --- Setters ---

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setPublicacionId(int publicacionId) {
        this.publicacionId = publicacionId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}

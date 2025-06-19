package com.example.aplicacionteamexo.data.modelo.publicacion;

public class EliminarPublicacionRequest {
    private String rol;
    private int usuarioId;

    public EliminarPublicacionRequest(String rol, int usuarioId) {
        this.rol = rol;
        this.usuarioId = usuarioId;
    }

    public String getRol() {
        return rol;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
}

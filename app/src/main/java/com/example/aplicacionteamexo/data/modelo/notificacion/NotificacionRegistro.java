package com.example.aplicacionteamexo.data.modelo.notificacion;

public class NotificacionRegistro {
    private int usuarioId;
    private String tipo;
    private String mensaje;

    public NotificacionRegistro(int usuarioId, String tipo, String mensaje) {
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.mensaje = mensaje;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

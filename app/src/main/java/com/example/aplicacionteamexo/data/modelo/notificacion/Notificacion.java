package com.example.aplicacionteamexo.data.modelo.notificacion;

public class Notificacion {
    private int notificacionId;
    private int usuarioId;
    private String tipo;
    private String mensaje;
    private boolean leida;
    private String fecha;

    // Getters y Setters

    public int getNotificacionId() {
        return notificacionId;
    }

    public void setNotificacionId(int notificacionId) {
        this.notificacionId = notificacionId;
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

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}

package com.example.aplicacionteamexo.data.modelo.reaccion;

public class Reaccion {
    private int reaccionId;
    private String tipo;
    private int publicacionId;
    private int usuarioId;
    private String fecha;

    public int getReaccionId() { return reaccionId; }
    public void setReaccionId(int reaccionId) { this.reaccionId = reaccionId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getPublicacionId() { return publicacionId; }
    public void setPublicacionId(int publicacionId) { this.publicacionId = publicacionId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}

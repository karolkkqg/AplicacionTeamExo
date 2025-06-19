package com.example.aplicacionteamexo.data.modelo.recurso;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Recurso {

    @SerializedName("identificador")
    private int identificador;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("formato")
    private int formato;

    @SerializedName("tamano")
    private int tamano;

    @SerializedName("URL") // ✅ Mapea el campo "URL" del JSON
    private String url;

    @SerializedName("usuarioId")
    private int usuarioId;

    @SerializedName("resolucion")
    private Integer resolucion;

    @SerializedName("duracion")
    private Integer duracion;

    @SerializedName("fechaCreacion")
    private Date fechaCreacion;

    // Constructor
    public Recurso(int identificador, String tipo, int formato, int tamano, String url, int usuarioId,
                   Integer resolucion, Integer duracion, Date fechaCreacion) {
        this.identificador = identificador;
        this.tipo = tipo;
        this.formato = formato;
        this.tamano = tamano;
        this.url = url;
        this.usuarioId = usuarioId;
        this.resolucion = resolucion;
        this.duracion = duracion;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public int getIdentificador() { return identificador; }
    public void setIdentificador(int identificador) { this.identificador = identificador; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getFormato() { return formato; }
    public void setFormato(int formato) { this.formato = formato; }

    public int getTamano() { return tamano; }
    public void setTamano(int tamano) { this.tamano = tamano; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public Integer getResolucion() { return resolucion; }
    public void setResolucion(Integer resolucion) { this.resolucion = resolucion; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}

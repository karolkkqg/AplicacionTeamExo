package com.example.aplicacionteamexo;

public class Post {
    private int identificador;
    private String titulo;
    private String estado;
    private String contenido;
    private int usuarioId;
    private String fechaCreacion; // o Date si lo deseas con conversión

    // Constructor
    public Post(int identificador, String titulo, String estado, String contenido, int usuarioId, String fechaCreacion) {
        this.identificador = identificador;
        this.titulo = titulo;
        this.estado = estado;
        this.contenido = contenido;
        this.usuarioId = usuarioId;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y setters
    public int getIdentificador() {
        return identificador;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getEstado() {
        return estado;
    }

    public String getContenido() {
        return contenido;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }
}

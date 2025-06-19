package com.example.aplicacionteamexo.data.modelo.publicacion;

import com.example.aplicacionteamexo.data.modelo.recurso.Recurso;

public class PublicacionConRecurso {
    private int identificador;
    private String titulo;
    private String estado;
    private String contenido;
    private int usuarioId;
    private String fechaCreacion;
    private Recurso recurso;


    // Getters y Setters
    public int getIdentificador() { return identificador; }
    public void setIdentificador(int identificador) { this.identificador = identificador; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Recurso getRecurso() { return recurso; }
    public void setRecurso(Recurso recurso) { this.recurso = recurso; }

    public Publicacion toPublicacion() {
        Publicacion p = new Publicacion(
                this.identificador,
                this.titulo,
                this.estado,
                this.contenido,
                this.usuarioId,
                this.fechaCreacion
        );
        return p;
    }
}

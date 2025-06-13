package com.example.aplicacionteamexo.data.modelo.publicacion;

public class PublicacionRegistro {
    private String titulo;
    private String contenido;
    private String estado;
    private int usuarioId;

    private int recursoId;

    public PublicacionRegistro(String titulo, String contenido, String estado, int usuarioId, int recursoId) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.estado = estado;
        this.usuarioId = usuarioId;
        this.recursoId = recursoId;
    }

    // Getter para titulo
    public String getTitulo() {
        return titulo;
    }

    // Setter para titulo
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // Getter para contenido
    public String getContenido() {
        return contenido;
    }

    // Setter para contenido
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    // Getter para estado
    public String getEstado() {
        return estado;
    }

    // Setter para estado
    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Getter para usuarioId
    public int getUsuarioId() {
        return usuarioId;
    }

    // Setter para usuarioId
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getRecursoId() {
        return recursoId;
    }

    // Setter para 'recursoId'
    public void setRecursoId(int recursoId) {
        this.recursoId = recursoId;
    }
}

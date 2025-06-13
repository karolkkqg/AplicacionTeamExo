package com.example.aplicacionteamexo.data.modelo.publicacion;

import com.example.aplicacionteamexo.data.modelo.recurso.Recurso;

public class PublicacionConRecurso {
    private int identificador;
    private String titulo;
    private String contenido;
    private String estado;
    private int usuarioId;
    private String fechaCreacion;
    private Integer recursoId;  // Puede ser null
    private Recurso recurso;    // Objeto recurso

    public int getIdentificador() {
        return identificador;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public String getEstado() {
        return estado;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public Integer getRecursoId() {
        return recursoId;
    }

    public Recurso getRecurso() {
        return recurso;
    }
}

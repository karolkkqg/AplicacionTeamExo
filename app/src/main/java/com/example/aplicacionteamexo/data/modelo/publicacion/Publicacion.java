package com.example.aplicacionteamexo.data.modelo.publicacion;

import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRespuesta;

import java.util.List;

public class Publicacion {
    private int identificador;
    private String titulo;
    private String estado;
    private String contenido;
    private int usuarioId;
    private String fechaCreacion; // o Date si lo deseas con conversión

    private List<ReaccionRespuesta> reacciones; // debes inicializarlo cuando traes las publicaciones
    private int reaccionIdUsuarioActual; // si el usuario ya reaccionó
    private String tipoReaccionUsuarioActual;


    private String comentariosTexto;

    // Constructor
    public Publicacion(int identificador, String titulo, String estado, String contenido, int usuarioId, String fechaCreacion) {
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
    public void setIdentificador(int identificador) { this.identificador = identificador; }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) { this.estado = estado; }

    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public int getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Reacciones relacionadas
    public List<ReaccionRespuesta> getReacciones() {
        return reacciones;
    }

    public void setReacciones(List<ReaccionRespuesta> reacciones) {
        this.reacciones = reacciones;
    }

    public int getReaccionIdUsuarioActual() {
        return reaccionIdUsuarioActual;
    }

    public void setReaccionIdUsuarioActual(int reaccionIdUsuarioActual) {
        this.reaccionIdUsuarioActual = reaccionIdUsuarioActual;
    }

    public String getTipoReaccionUsuarioActual() {
        return tipoReaccionUsuarioActual;
    }

    public void setTipoReaccionUsuarioActual(String tipoReaccionUsuarioActual) {
        this.tipoReaccionUsuarioActual = tipoReaccionUsuarioActual;
    }

    public String getComentariosTexto() {
        return comentariosTexto;
    }

    public void setComentariosTexto(String comentariosTexto) {
        this.comentariosTexto = comentariosTexto;
    }

    public void agregarComentarioTexto(String nuevoComentario) {
        if (comentariosTexto.isEmpty()) {
            comentariosTexto = nuevoComentario;
        } else {
            comentariosTexto += "\n" + nuevoComentario;
        }
    }

}

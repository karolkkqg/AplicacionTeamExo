package com.example.aplicacionteamexo.data.modelo.recurso;

public class RecursoRegistro {
    private String tipo;
    private int formato;
    private int tamano;
    private int usuarioId;
    private Integer resolucion;
    private Integer duracion;
    private byte[] archivo;

    // Constructor, getters y setters
    public RecursoRegistro(String tipo, int formato, int tamano, int usuarioId,
                           Integer resolucion, Integer duracion, byte[] archivo) {
        this.tipo = tipo;
        this.formato = formato;
        this.tamano = tamano;
        this.usuarioId = usuarioId;
        this.resolucion = resolucion;
        this.duracion = duracion;
        this.archivo = archivo;
    }

    // ... getters y setters
}

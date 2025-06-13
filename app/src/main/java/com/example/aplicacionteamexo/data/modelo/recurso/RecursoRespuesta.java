package com.example.aplicacionteamexo.data.modelo.recurso;

public class RecursoRespuesta {
    private boolean exito;
    private String mensaje;
    private int recursoId;

    // Constructor, getters y setters
    public RecursoRespuesta(boolean exito, String mensaje, int recursoId) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.recursoId = recursoId;
    }

    public boolean isExito() {
        return exito;
    }

    // Setter para 'exito'
    public void setExito(boolean exito) {
        this.exito = exito;
    }

    // Getter para 'mensaje'
    public String getMensaje() {
        return mensaje;
    }

    // Setter para 'mensaje'
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    // Getter para 'recursoId'
    public int getRecursoId() {
        return recursoId;
    }

    // Setter para 'recursoId'
    public void setRecursoId(int recursoId) {
        this.recursoId = recursoId;
    }
}

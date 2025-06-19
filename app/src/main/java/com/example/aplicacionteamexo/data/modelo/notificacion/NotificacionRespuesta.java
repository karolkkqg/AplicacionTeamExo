package com.example.aplicacionteamexo.data.modelo.notificacion;

import java.util.List;

public class NotificacionRespuesta {
    private int total;
    private int paginas;
    private int paginaActual;
    private List<Notificacion> resultados;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public void setPaginaActual(int paginaActual) {
        this.paginaActual = paginaActual;
    }

    public List<Notificacion> getResultados() {
        return resultados;
    }

    public void setResultados(List<Notificacion> resultados) {
        this.resultados = resultados;
    }
}

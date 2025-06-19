package com.example.aplicacionteamexo.data.modelo.publicacion;

import java.util.List;

public class PublicacionBusquedaRespuesta {
    private int total;
    private int paginas;
    private int paginaActual;
    private List<PublicacionConRecurso> resultados;

    // Getters y Setters
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getPaginas() { return paginas; }
    public void setPaginas(int paginas) { this.paginas = paginas; }

    public int getPaginaActual() { return paginaActual; }
    public void setPaginaActual(int paginaActual) { this.paginaActual = paginaActual; }

    public List<PublicacionConRecurso> getResultados() { return resultados; }
    public void setResultados(List<PublicacionConRecurso> resultados) { this.resultados = resultados; }
}

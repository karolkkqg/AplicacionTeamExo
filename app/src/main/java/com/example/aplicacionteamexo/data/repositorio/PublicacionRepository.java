package com.example.aplicacionteamexo.data.repositorio;

import com.example.aplicacionteamexo.data.api.PublicacionAPI;
import com.example.aplicacionteamexo.data.modelo.publicacion.EliminarPublicacionRequest;
import com.example.aplicacionteamexo.data.modelo.publicacion.Publicacion;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionBusquedaRespuesta;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionConRecurso;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionRegistro;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionRespuesta;
import com.example.aplicacionteamexo.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class PublicacionRepository {
    private PublicacionAPI api;

    public PublicacionRepository() {
        api = RetrofitClient.getInstance().create(PublicacionAPI.class);
    }

    public Call<PublicacionRespuesta> crearPublicacion(PublicacionRegistro nuevaPublicacion) {
        return api.crearPublicacion(nuevaPublicacion);
    }

    public Call<PublicacionBusquedaRespuesta> buscarPublicaciones(
            String query,
            String categorias,
            String tipoRecurso,
            int limit,
            int page
    ) {
        // Estado "Publicado" se fuerza desde el repositorio
        return api.buscarPublicaciones(query, categorias, tipoRecurso, "Publicado", limit, page);
    }

    public Call<List<PublicacionConRecurso>> obtenerPublicacionesPorUsuario(int usuarioId) {
        return api.obtenerPublicacionesPorUsuarioConRecursos(usuarioId);
    }

    public Call<Void> eliminarPublicacion(int publicacionId, String rol, int usuarioId) {
        EliminarPublicacionRequest request = new EliminarPublicacionRequest(rol, usuarioId);
        return api.eliminarPublicacion(publicacionId, request);
    }

    public Call<List<PublicacionConRecurso>> obtenerPublicacionesConRecursos() {
        return api.obtenerPublicacionesConRecursos();
    }
}



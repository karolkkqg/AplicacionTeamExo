package com.example.aplicacionteamexo.data.repositorio;

import com.example.aplicacionteamexo.data.api.PublicacionAPI;
import com.example.aplicacionteamexo.data.modelo.publicacion.Publicacion;
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
        return api.crearPublicacion(nuevaPublicacion); // <- corregido
    }

    public Call<List<Publicacion>> buscarPublicaciones(String query, String estado, int limit, int page) {
        return api.buscarPublicaciones(query, estado, limit, page); // <- usa los parámetros definidos en la API
    }

    public Call<List<Publicacion>> obtenerPorUsuario(int usuarioId) {
        return api.obtenerPorUsuario(usuarioId);
    }

    public Call<Void> eliminarPublicacion(int publicacionId) {
        return api.eliminarPublicacion(publicacionId); // <- corregido
    }

    public Call<List<PublicacionConRecurso>> obtenerPublicacionesConRecursos(Integer usuarioId) {
        return api.obtenerPublicacionesConRecursos(usuarioId);
    }

}


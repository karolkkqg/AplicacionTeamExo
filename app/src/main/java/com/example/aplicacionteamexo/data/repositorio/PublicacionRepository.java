package com.example.aplicacionteamexo.data.repositorio;

import com.example.aplicacionteamexo.data.api.PublicacionAPI;
import com.example.aplicacionteamexo.data.modelo.Publicacion;
import com.example.aplicacionteamexo.data.modelo.PublicacionRespuesta;
import com.example.aplicacionteamexo.data.network.RetrofitClient;

import retrofit2.Call;

public class PublicacionRepository {
    private final PublicacionAPI api;

    public PublicacionRepository() {
        api = RetrofitClient.getInstance().create(PublicacionAPI.class);
    }

    public Call<PublicacionRespuesta> crearPublicacion(Publicacion publicacion) {
        return api.crearPublicacion(publicacion);
    }
}

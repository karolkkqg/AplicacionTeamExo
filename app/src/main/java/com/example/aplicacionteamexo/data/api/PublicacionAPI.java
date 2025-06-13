package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.Publicacion;
import com.example.aplicacionteamexo.data.modelo.PublicacionRespuesta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PublicacionAPI {
    @POST("api/publicaciones")
    Call<PublicacionRespuesta> crearPublicacion(@Body Publicacion publicacion);
}

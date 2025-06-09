package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.UsuarioRegistro;
import com.example.aplicacionteamexo.data.modelo.UsuarioRespuesta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioAPI {
    @POST("/api/usuarios")
    Call<UsuarioRespuesta> registrar(@Body UsuarioRegistro nuevoUsuario);
}

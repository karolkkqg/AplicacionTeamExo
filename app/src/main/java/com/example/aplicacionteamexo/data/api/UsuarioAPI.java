package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.ContrasenaRequest;
import com.example.aplicacionteamexo.data.modelo.Usuario;
import com.example.aplicacionteamexo.data.modelo.UsuarioRegistro;
import com.example.aplicacionteamexo.data.modelo.UsuarioRespuesta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface UsuarioAPI {
    @POST("/api/usuarios")
    Call<UsuarioRespuesta> registrar(@Body UsuarioRegistro nuevoUsuario);
    @GET("api/usuarios/perfil")
    Call<UsuarioRespuesta> obtenerPerfil(@Header("Authorization") String token);
    @PUT("api/usuarios/{usuarioId}")
    Call<UsuarioRespuesta> actualizarUsuario(
            @Header("Authorization") String token,
            @Path("usuarioId") int usuarioId,
            @Body Usuario usuario
    );
    @PUT("api/usuarios/{usuarioId}/contrasena")
    Call<UsuarioRespuesta> actualizarContrasena(
            @Header("Authorization") String token,
            @Path("usuarioId") int usuarioId,
            @Body ContrasenaRequest body
    );
}

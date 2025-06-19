package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.ContrasenaRequest;
import com.example.aplicacionteamexo.data.modelo.Usuario;
import com.example.aplicacionteamexo.data.modelo.UsuarioRegistro;
import com.example.aplicacionteamexo.data.modelo.UsuarioRespuesta;
import com.example.aplicacionteamexo.data.modelo.UsuarioRespuestaBusqueda;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Header;
import retrofit2.http.DELETE;

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

    @DELETE("api/usuarios/{usuarioId}")
    Call<UsuarioRespuesta> eliminarUsuario(
            @Header("Authorization") String token,
            @Path("usuarioId") int usuarioId
    );

    @GET("api/usuarios/{usuarioId}")
    Call<UsuarioRespuestaBusqueda> obtenerUsuarioPorId(@Path("usuarioId") int usuarioId);

}

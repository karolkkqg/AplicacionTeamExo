package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.comentario.Comentario;
import com.example.aplicacionteamexo.data.modelo.comentario.ComentarioRegistro;

import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ComentarioAPI {

    // Crear un nuevo comentario
    @POST("/api/comentarios")
    Call<Comentario> crearComentario(@Body ComentarioRegistro solicitud);

    // Obtener todos los comentarios de una publicación
    @GET("/api/comentarios/publicacion/{id}")
    Call<List<Comentario>> obtenerComentariosPorPublicacion(@Path("id") int publicacionId);

    // Actualizar un comentario por su ID
    @PUT("/api/comentarios/{comentarioId}")
    Call<JsonObject> actualizarComentario(@Path("comentarioId") int comentarioId, @Body Comentario comentario);

    // Eliminar un comentario por su ID
    @DELETE("/api/comentarios/{comentarioId}")
    Call<Comentario> eliminarComentario(@Path("comentarioId") int comentarioId);
}

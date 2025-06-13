package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.comentario.Comentario;
import com.example.aplicacionteamexo.data.modelo.comentario.ComentarioRegistro;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ComentarioAPI {
    @POST("api/comentarios")
    Call<Comentario> crearComentario(@Body ComentarioRegistro solicitud);

    @GET("api/comentarios/publicacion/{id}")
    Call<List<Comentario>> obtenerComentariosPorPublicacion(@Path("id") int publicacionId);

    @PUT("api/comentarios/{comentarioId}")
    Call<Comentario> actualizarComentario(@Path("comentarioId") int comentarioId, @Body Map<String, String> texto);

    @DELETE("api/comentarios/{comentarioId}")
    Call<Void> eliminarComentario(@Path("comentarioId") int comentarioId);
}

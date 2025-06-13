package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionConRecurso;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionRegistro;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionRespuesta;
import com.example.aplicacionteamexo.data.modelo.publicacion.Publicacion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PublicacionAPI {

    // Buscar publicaciones (lista, no solo una)
    @GET("/api/publicaciones")
    Call<List<Publicacion>> buscarPublicaciones(
            @Query("query") String query,
            @Query("estado") String estado,
            @Query("limit") int limit,
            @Query("page") int page
    );

    @GET("/api/publicaciones/usuario/{usuarioId}")
    Call<List<Publicacion>> obtenerPorUsuario(@Path("usuarioId") int usuarioId);

    @GET("/api/publicaciones/con-recursos")
    Call<List<PublicacionConRecurso>> obtenerPublicacionesConRecursos(
            @Query("usuarioId") Integer usuarioId
    );

    // Crear una publicación
    @POST("/api/publicaciones")
    Call<PublicacionRespuesta> crearPublicacion(@Body PublicacionRegistro publicacionRegistro);

    // Eliminar una publicación por ID
    @DELETE("/api/publicaciones/{identificador}")
    Call<Void> eliminarPublicacion(@Path("identificador") int id);
}

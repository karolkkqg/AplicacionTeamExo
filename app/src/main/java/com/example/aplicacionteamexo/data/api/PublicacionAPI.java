package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.publicacion.EliminarPublicacionRequest;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionBusquedaRespuesta;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionConRecurso;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionRegistro;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionRespuesta;
import com.example.aplicacionteamexo.data.modelo.publicacion.Publicacion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PublicacionAPI {

    @POST("/api/publicaciones")
    Call<PublicacionRespuesta> crearPublicacion(
            @Body PublicacionRegistro publicacionRegistro
    );

    @GET("/api/publicaciones/buscar")
    Call<PublicacionBusquedaRespuesta> buscarPublicaciones(
            @Query("query") String query,
            @Query("categorias") String categorias,
            @Query("tipoRecurso") String tipoRecurso,// Añadido para coincidir con el backend
            @Query("estado") String estado,
            @Query("limit") int limit,
            @Query("page") int page
    );

    @GET("/api/publicaciones/usuario/{usuarioId}")
    Call<List<Publicacion>> obtenerPorUsuario(
            @Path("usuarioId") int usuarioId,
            @Query("estado") String estado, // Parámetros opcionales del backend
            @Query("limit") Integer limit,
            @Query("page") Integer page
    );

    @GET("/api/publicaciones/usuario/{id}")
    Call<List<PublicacionConRecurso>> obtenerPublicacionesPorUsuarioConRecursos(
            @Path("id") int usuarioId
    );

    @GET("/api/publicaciones/con-recursos")
    Call<List<PublicacionConRecurso>> obtenerPublicacionesConRecursos();

    // Añadir parámetro de rol para la eliminación
    @HTTP(method = "DELETE", path = "api/publicaciones/{publicacionId}", hasBody = true)
    Call<Void> eliminarPublicacion(
            @Path("publicacionId") int publicacionId,
            @Body EliminarPublicacionRequest request
    );


}

package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.notificacion.NotificacionRespuesta;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificacionAPI {

    // Obtener notificaciones paginadas de un usuario
    @GET("/api/notificaciones/usuario/{usuarioId}")
    Call<NotificacionRespuesta> obtenerNotificacionesUsuario(
            @Path("usuarioId") int usuarioId,
            @Query("leida") Boolean leida,
            @Query("limit") int limit,
            @Query("page") int page
    );

    // Marcar una notificación como leída
    @PATCH("/api/notificaciones/{id}/marcar-leida")
    Call<Void> marcarComoLeida(@Path("id") int id);

    // Eliminar una notificación
    @DELETE("/api/notificaciones/{id}")
    Call<Void> eliminarNotificacion(@Path("id") int id);
}

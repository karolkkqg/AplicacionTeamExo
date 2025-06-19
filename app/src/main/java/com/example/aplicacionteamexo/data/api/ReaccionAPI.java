package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.reaccion.Reaccion;
import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRegistro;
import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRespuesta;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReaccionAPI {
    @POST("/api/reacciones")
    Call<ReaccionRespuesta> crearReaccion(@Body ReaccionRegistro request);

    @PUT("/api/reacciones/{reaccionId}")
    Call<ReaccionRespuesta> actualizarReaccion(@Path("reaccionId") int reaccionId, @Body Map<String, String> body);

    @DELETE("/api/reacciones/{reaccionId}")
    Call<ReaccionRespuesta> eliminarReaccion(@Path("reaccionId") int reaccionId);

    @GET("/api/reacciones/publicacion/{id}")
    Call<List<Reaccion>> obtenerReaccionesPorPublicacion(@Path("id") int publicacionId);
    @GET("/api/reacciones/buscar")
    Call<ReaccionRespuesta> obtenerReaccionId(@Query("reaccionId") int reaccionId);
}

package com.example.aplicacionteamexo.data.api;

import com.example.aplicacionteamexo.data.modelo.recurso.Recurso;
import com.example.aplicacionteamexo.data.modelo.recurso.RecursoActualizacion;
import com.example.aplicacionteamexo.data.modelo.recurso.RecursoRegistro;
import com.example.aplicacionteamexo.data.modelo.recurso.RecursoRespuesta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecursoAPI {

    // Crear recurso (POST /api/recursos)
    @POST("recursos")
    Call<RecursoRespuesta> crearRecurso(@Body RecursoRegistro registro);

    // Obtener recurso por ID (GET /api/recursos/{id})
    @GET("recursos/{id}")
    Call<Recurso> obtenerRecursoPorId(@Path("id") int id);

    // Obtener recursos por usuario (GET /api/recursos/usuario/{usuarioId})
    @GET("recursos/usuario/{usuarioId}")
    Call<List<Recurso>> obtenerRecursosPorUsuario(@Path("usuarioId") int usuarioId);

    // Obtener recursos por tipo (GET /api/recursos/tipo/{tipo})
    @GET("recursos/tipo/{tipo}")
    Call<List<Recurso>> obtenerRecursosPorTipo(@Path("tipo") String tipo);

    // Buscar recursos con filtros (GET /api/recursos/buscar)
    @GET("recursos/buscar")
    Call<List<Recurso>> buscarRecursos(
            @Query("tipo") String tipo,
            @Query("tamano") Integer tamano,
            @Query("formato") Integer formato,
            @Query("usuarioId") Integer usuarioId
    );

    // Actualizar recurso (PUT /api/recursos/{id})
    @PUT("recursos/{id}")
    Call<Recurso> actualizarRecurso(
            @Path("id") int id,
            @Body RecursoActualizacion actualizacion
    );

    // Eliminar recurso (DELETE /api/recursos/{id})
    @DELETE("recursos/{id}")
    Call<Void> eliminarRecurso(@Path("id") int id);
}

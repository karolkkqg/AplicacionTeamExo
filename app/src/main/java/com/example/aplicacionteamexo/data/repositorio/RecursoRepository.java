package com.example.aplicacionteamexo.data.repositorio;

import com.example.aplicacionteamexo.data.api.RecursoAPI;
import com.example.aplicacionteamexo.data.modelo.recurso.Recurso;
import com.example.aplicacionteamexo.data.modelo.recurso.RecursoActualizacion;
import com.example.aplicacionteamexo.data.modelo.recurso.RecursoRegistro;
import com.example.aplicacionteamexo.data.modelo.recurso.RecursoRespuesta;
import com.example.aplicacionteamexo.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class RecursoRepository {
    private final RecursoAPI api;

    public RecursoRepository() {
        this.api = RetrofitClient.getInstance().create(RecursoAPI.class);
    }

    // Crear recurso
    public void crearRecurso(RecursoRegistro registro, OnRecursoCreadoListener listener) {
        api.crearRecurso(registro).enqueue(new Callback<RecursoRespuesta>() {
            @Override
            public void onResponse(Call<RecursoRespuesta> call, Response<RecursoRespuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error al crear recurso");
                }
            }

            @Override
            public void onFailure(Call<RecursoRespuesta> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    // Obtener recurso por ID
    public void obtenerRecursoPorId(int id, OnRecursoObtenidoListener listener) {
        api.obtenerRecursoPorId(id).enqueue(new Callback<Recurso>() {
            @Override
            public void onResponse(Call<Recurso> call, Response<Recurso> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onRecursoObtenido(response.body());
                } else {
                    listener.onError("Recurso no encontrado");
                }
            }

            @Override
            public void onFailure(Call<Recurso> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    // Obtener recursos por usuario
    public void obtenerRecursosPorUsuario(int usuarioId, OnRecursosListadosListener listener) {
        api.obtenerRecursosPorUsuario(usuarioId).enqueue(new Callback<List<Recurso>>() {
            @Override
            public void onResponse(Call<List<Recurso>> call, Response<List<Recurso>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onRecursosListados(response.body());
                } else {
                    listener.onError("Error al obtener recursos");
                }
            }

            @Override
            public void onFailure(Call<List<Recurso>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    // Buscar recursos con filtros
    public void buscarRecursos(String tipo, Integer tamano, Integer formato, Integer usuarioId,
                               OnRecursosListadosListener listener) {
        api.buscarRecursos(tipo, tamano, formato, usuarioId).enqueue(new Callback<List<Recurso>>() {
            @Override
            public void onResponse(Call<List<Recurso>> call, Response<List<Recurso>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onRecursosListados(response.body());
                } else {
                    listener.onError("No se encontraron recursos");
                }
            }

            @Override
            public void onFailure(Call<List<Recurso>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    // Actualizar recurso
    public void actualizarRecurso(int id, RecursoActualizacion actualizacion,
                                  OnRecursoActualizadoListener listener) {
        api.actualizarRecurso(id, actualizacion).enqueue(new Callback<Recurso>() {
            @Override
            public void onResponse(Call<Recurso> call, Response<Recurso> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onRecursoActualizado(response.body());
                } else {
                    listener.onError("Error al actualizar");
                }
            }

            @Override
            public void onFailure(Call<Recurso> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    // Eliminar recurso
    public void eliminarRecurso(int id, OnRecursoEliminadoListener listener) {
        api.eliminarRecurso(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onRecursoEliminado();
                } else {
                    listener.onError("Error al eliminar");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    // Interfaces de callback
    public interface OnRecursoCreadoListener {
        void onSuccess(RecursoRespuesta respuesta);
        void onError(String mensaje);
    }

    public interface OnRecursoObtenidoListener {
        void onRecursoObtenido(Recurso recurso);
        void onError(String mensaje);
    }

    public interface OnRecursosListadosListener {
        void onRecursosListados(List<Recurso> recursos);
        void onError(String mensaje);
    }

    public interface OnRecursoActualizadoListener {
        void onRecursoActualizado(Recurso recurso);
        void onError(String mensaje);
    }

    public interface OnRecursoEliminadoListener {
        void onRecursoEliminado();
        void onError(String mensaje);
    }
}

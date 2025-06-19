package com.example.aplicacionteamexo.data.repositorio;

import com.example.aplicacionteamexo.data.api.ComentarioAPI;
import com.example.aplicacionteamexo.data.modelo.comentario.Comentario;
import com.example.aplicacionteamexo.data.modelo.comentario.ComentarioRegistro;
import com.example.aplicacionteamexo.data.network.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComentarioRepository {
    private ComentarioAPI api;

    public ComentarioRepository() {
        api = RetrofitClient.getInstance().create(ComentarioAPI.class);
    }

    public void crearComentario(ComentarioRegistro solicitud, Callback<Comentario> callback) {
        Call<Comentario> call = api.crearComentario(solicitud);
        call.enqueue(callback);
    }

    public void obtenerComentariosPorPublicacion(int publicacionId, Callback<List<Comentario>> callback) {
        Call<List<Comentario>> call = api.obtenerComentariosPorPublicacion(publicacionId);
        call.enqueue(callback);
    }

    public void actualizarComentario(int comentarioId, String nuevoTexto, Callback<Comentario> callback) {
        Comentario comentario = new Comentario();
        comentario.setTexto(nuevoTexto);  // Solo mandamos el campo necesario

        Call<JsonObject> call = api.actualizarComentario(comentarioId, comentario);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject json = response.body().getAsJsonObject("comentario");

                    if (json != null) {
                        Comentario comentarioActualizado = new Gson().fromJson(json, Comentario.class);
                        callback.onResponse(null, Response.success(comentarioActualizado));
                    } else {
                        callback.onFailure(null, new Throwable("Comentario nulo"));
                    }
                } else {
                    callback.onFailure(null, new Throwable("Respuesta no exitosa"));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }


    public void eliminarComentario(int comentarioId, Callback<Comentario> callback) {
        Call<Comentario> call = api.eliminarComentario(comentarioId);
        call.enqueue(callback);
    }
}

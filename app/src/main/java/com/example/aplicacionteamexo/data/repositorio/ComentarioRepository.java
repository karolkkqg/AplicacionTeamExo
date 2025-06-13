package com.example.aplicacionteamexo.data.repositorio;

import com.example.aplicacionteamexo.data.api.ComentarioAPI;
import com.example.aplicacionteamexo.data.modelo.comentario.Comentario;
import com.example.aplicacionteamexo.data.modelo.comentario.ComentarioRegistro;
import com.example.aplicacionteamexo.data.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

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
        Map<String, String> body = new HashMap<>();
        body.put("texto", nuevoTexto);
        Call<Comentario> call = api.actualizarComentario(comentarioId, body);
        call.enqueue(callback);
    }

    public void eliminarComentario(int comentarioId, Callback<Void> callback) {
        Call<Void> call = api.eliminarComentario(comentarioId);
        call.enqueue(callback);
    }
}

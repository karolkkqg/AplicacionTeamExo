package com.example.aplicacionteamexo.data.repositorio;

import com.example.aplicacionteamexo.data.api.ReaccionAPI;
import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRegistro;
import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRespuesta;
import com.example.aplicacionteamexo.data.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Callback;

public class ReaccionRepository {
    private ReaccionAPI reaccionApi;

    public ReaccionRepository() {
        reaccionApi = RetrofitClient.getInstance().create(ReaccionAPI.class);
    }

    public void crearReaccion(ReaccionRegistro req, Callback<ReaccionRespuesta> callback) {
        reaccionApi.crearReaccion(req).enqueue(callback);
    }

    public void actualizarReaccion(int reaccionId, String nuevoTipo, Callback<ReaccionRespuesta> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("tipo", nuevoTipo);
        reaccionApi.actualizarReaccion(reaccionId, body).enqueue(callback);
    }

    public void eliminarReaccion(int reaccionId, Callback<Void> callback) {
        reaccionApi.eliminarReaccion(reaccionId).enqueue(callback);
    }

    public void obtenerReaccionesPorPublicacion(int publicacionId, Callback<List<ReaccionRespuesta>> callback) {
        reaccionApi.obtenerReaccionesPorPublicacion(publicacionId).enqueue(callback);
    }
}

package com.example.aplicacionteamexo.data.repositorio;


import com.example.aplicacionteamexo.data.api.ReaccionAPI;
import com.example.aplicacionteamexo.data.modelo.reaccion.Reaccion;
import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRegistro;
import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRespuesta;
import com.example.aplicacionteamexo.data.network.RetrofitClient;

import java.util.List;
import java.util.Map;
import retrofit2.Call;

public class ReaccionRepository {

    private final ReaccionAPI api;

    public ReaccionRepository() {
        api = RetrofitClient.getInstance().create(ReaccionAPI.class);
    }

    public Call<ReaccionRespuesta> crearReaccion(ReaccionRegistro registro) {
        return api.crearReaccion(registro);
    }

    public Call<ReaccionRespuesta> actualizarReaccion(int reaccionId, Map<String, String> body) {
        return api.actualizarReaccion(reaccionId, body);
    }

    public Call<ReaccionRespuesta> eliminarReaccion(int reaccionId) {
        return api.eliminarReaccion(reaccionId);
    }

    public Call<List<Reaccion>> obtenerReaccionesPorPublicacion(int publicacionId) {
        return api.obtenerReaccionesPorPublicacion(publicacionId);
    }

    public Call<ReaccionRespuesta> obtenerReaccionPorId(int reaccionId) {
        return api.obtenerReaccionId(reaccionId);
    }
}
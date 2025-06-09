package com.example.aplicacionteamexo.data.repositorio;

import com.example.aplicacionteamexo.data.api.UsuarioAPI;
import com.example.aplicacionteamexo.data.modelo.UsuarioRegistro;
import com.example.aplicacionteamexo.data.modelo.UsuarioRespuesta;
import com.example.aplicacionteamexo.data.network.RetrofitClient;

import retrofit2.Call;

public class UsuarioRepository {
    private UsuarioAPI api;

    public UsuarioRepository() {
        api = RetrofitClient.getInstance().create(UsuarioAPI.class);
    }

    public Call<UsuarioRespuesta> registrar(UsuarioRegistro nuevoUsuario) {
        return api.registrar(nuevoUsuario);
    }
}

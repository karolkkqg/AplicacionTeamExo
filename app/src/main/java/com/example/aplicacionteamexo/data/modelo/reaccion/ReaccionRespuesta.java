package com.example.aplicacionteamexo.data.modelo.reaccion;

public class ReaccionRespuesta {
    public int reaccionId;
    public String tipo;
    public int publicacionId;
    public int usuarioId;
    public String nombreUsuario;

    public ReaccionRespuesta(int reaccionId, String tipo, int publicacionId, int usuarioId, String nombreUsuario) {
        this.reaccionId = reaccionId;
        this.tipo = tipo;
        this.publicacionId = publicacionId;
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
    }
}

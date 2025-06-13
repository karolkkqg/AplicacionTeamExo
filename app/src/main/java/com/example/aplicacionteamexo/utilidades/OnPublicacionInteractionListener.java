package com.example.aplicacionteamexo.utilidades;

import com.example.aplicacionteamexo.data.modelo.publicacion.Publicacion;

public interface OnPublicacionInteractionListener {
    void onEliminarClick(Publicacion publicacion); // Para el botón eliminar
    void onLikeClick(Publicacion publicacion); // Para like
    void onReaccionClick(Publicacion publicacion, String tipoReaccion);
    void onComentarioSubmit(Publicacion publicacion, String comentario); // Al enviar comentario
    void onPublicacionClick(Publicacion publicacion); // Al hacer clic en la publicación
}

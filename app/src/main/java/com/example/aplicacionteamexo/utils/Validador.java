package com.example.aplicacionteamexo.utils;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

public class Validador {

    public static String validarNombre(String texto, String nombreCampo) {
        if (TextUtils.isEmpty(texto)) {
            return nombreCampo + " no puede estar vacío.";
        }
        if (!texto.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(\\s[A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$")) {
            return nombreCampo + " solo debe contener letras y un espacio entre palabras.";
        }
        return null;
    }

    public static String validarNombreDeUsuario(String texto) {
        if (TextUtils.isEmpty(texto)) {
            return "El nombre de usuario no puede estar vacío.";
        }
        if (!texto.matches("^[a-zA-Z0-9_]{4,20}$")) {
            return "El nombre de usuario solo debe contener letras, números y guiones bajos (4-20 caracteres).";
        }
        return null;
    }

    public static String validarCorreo(String correo) {
        if (TextUtils.isEmpty(correo)) {
            return "El correo no puede estar vacío.";
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            return "Correo electrónico inválido.";
        }
        return null;
    }

    public static String validarPassword(String pass) {
        if (TextUtils.isEmpty(pass)) {
            return "La contraseña no puede estar vacía.";
        }
        if (pass.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres.";
        }
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{8,}$";
        if (!pass.matches(regex)) {
            return "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un símbolo.";
        }
        return null;
    }

    public static String validarTitulo(String titulo) {
        if (TextUtils.isEmpty(titulo)) {
            return "El título no puede estar vacío.";
        }
        if (titulo.length() > 100) {
            return "El título no puede tener más de 100 caracteres.";
        }
        if (titulo.matches(".*[<>\"'{}].*")) {
            return "El título contiene caracteres no permitidos.";
        }
        return null;
    }

    public static String validarLongitud(String texto, String nombreCampo, int min, int max) {
        if (texto.length() < min) {
            return nombreCampo + " debe tener al menos " + min + " caracteres.";
        }
        if (texto.length() > max) {
            return nombreCampo + " no debe tener más de " + max + " caracteres.";
        }
        return null;
    }
}
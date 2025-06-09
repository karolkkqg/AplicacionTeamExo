package com.example.aplicacionteamexo.data.modelo;

public class UsuarioRegistro {
    public String nombreUsuario; // si no se usa, puedes eliminarlo
    public String nombre;
    public String apellidos;
    public String correo;
    public String contrasena;
    public String rol;
    public String claveRol; // ← este campo FALTABA

    public UsuarioRegistro(String nombreUsuario, String nombre, String apellidos, String correo,
                           String password, String rol) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.contrasena = password;
        this.rol = rol;
        this.nombreUsuario = nombreUsuario; // ← ya no sobreescribe rol
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return contrasena; }
    public void setPassword(String password) { this.contrasena = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getClaveRol() { return claveRol; }
    public void setClaveRol(String claveRol) { this.claveRol = claveRol; }
}

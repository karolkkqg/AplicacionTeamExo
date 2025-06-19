package com.example.aplicacionteamexo;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicacionteamexo.data.api.UsuarioAPI;
import com.example.aplicacionteamexo.data.modelo.ContrasenaRequest;
import com.example.aplicacionteamexo.data.modelo.Usuario;
import com.example.aplicacionteamexo.data.modelo.UsuarioRespuesta;
import com.example.aplicacionteamexo.data.network.RetrofitClient;
import com.example.aplicacionteamexo.utils.Validador;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaDetallePerfil extends AppCompatActivity {

    private boolean mostrarCampoContrasena = false;

    private EditText etNombreUsuarioDetalle, etNombreDetalle, etApellidosDetalle, etCorreoDetalle;
    private TextView tvRolDetalle;

    private int usuarioIdGuardado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_detalle_perfil);

        EditText etContrasena = findViewById(R.id.etContrasena);
        Button btnGuardarContrasena = findViewById(R.id.btnGuardarContrasena);
        Button btnMostrarEditarContrasena = findViewById(R.id.btnMostrarEditarContrasena);

        etNombreUsuarioDetalle = findViewById(R.id.etNombreUsuario);
        etNombreDetalle = findViewById(R.id.etNombre);
        etApellidosDetalle = findViewById(R.id.etApellidos);
        etCorreoDetalle = findViewById(R.id.etCorreo);
        tvRolDetalle = findViewById(R.id.tvRol);

        Button btnBorrarCuenta = findViewById(R.id.btnBorrarCuenta);
        btnBorrarCuenta.setOnClickListener(v -> mostrarDialogoConfirmacion());

        etContrasena.setVisibility(View.GONE);
        btnGuardarContrasena.setVisibility(View.GONE);

        btnMostrarEditarContrasena.setOnClickListener(v -> {
            mostrarCampoContrasena = !mostrarCampoContrasena;

            if (mostrarCampoContrasena) {
                etContrasena.setVisibility(View.VISIBLE);
                btnGuardarContrasena.setVisibility(View.VISIBLE);
                btnMostrarEditarContrasena.setText("Cancelar edición");
            } else {
                etContrasena.setVisibility(View.GONE);
                btnGuardarContrasena.setVisibility(View.GONE);
                btnMostrarEditarContrasena.setText("Editar contraseña");
            }
        });

        String token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);
        if (token != null) {
            cargarPerfil("Bearer " + token);
        } else {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show();
        }

        Button btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnEditarPerfil.setOnClickListener(v -> actualizarPerfil());

        btnGuardarContrasena.setOnClickListener(v -> actualizarContrasena());

        EditText etPassword = findViewById(R.id.etContrasena);
        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_candado_abierto, 0);
                    } else {
                        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_candado_cerrado, 0);
                    }

                    etPassword.setSelection(etPassword.length());
                    return true;
                }
            }
            return false;
        });
    }

    private void cargarPerfil(String token) {
        UsuarioAPI api = RetrofitClient.getInstance().create(UsuarioAPI.class);
        Call<UsuarioRespuesta> call = api.obtenerPerfil(token);

        call.enqueue(new Callback<UsuarioRespuesta>() {
            @Override
            public void onResponse(Call<UsuarioRespuesta> call, Response<UsuarioRespuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario u = response.body().usuario;
                    usuarioIdGuardado = u.usuarioId;

                    etNombreUsuarioDetalle.setText(u.nombreUsuario);
                    etNombreDetalle.setText(u.nombre);
                    etApellidosDetalle.setText(u.apellidos);
                    etCorreoDetalle.setText(u.correo);
                    tvRolDetalle.setText("Rol: "+ u.rol);
                } else if (response.code() == 401) {
                    redirigirAlLogin();
                } else {
                    Toast.makeText(PantallaDetallePerfil.this, "Ocurrió un error con el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioRespuesta> call, Throwable t) {
                boolean estaConectado = false;

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    estaConectado = networkInfo != null && networkInfo.isConnected();
                }

                if (estaConectado) {
                    Toast.makeText(PantallaDetallePerfil.this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PantallaDetallePerfil.this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void actualizarPerfil() {
        String token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombreUsuario = etNombreUsuarioDetalle.getText().toString().trim();
        String nombre = etNombreDetalle.getText().toString().trim();
        String apellidos = etApellidosDetalle.getText().toString().trim();
        String correo = etCorreoDetalle.getText().toString().trim();

        StringBuilder errores = new StringBuilder();

        String errNombre = Validador.validarNombre(nombre, "Nombre");
        if (errNombre != null) errores.append("- ").append(errNombre).append("\n");

        String errApellidos = Validador.validarNombre(apellidos, "Apellidos");
        if (errApellidos != null) errores.append("- ").append(errApellidos).append("\n");

        String errUsuario = Validador.validarNombreDeUsuario(nombreUsuario);
        if (errUsuario != null) errores.append("- ").append(errUsuario).append("\n");

        String errCorreo = Validador.validarCorreo(correo);
        if (errCorreo != null) errores.append("- ").append(errCorreo).append("\n");

        if (errores.length() > 0) {
            Toast.makeText(getApplicationContext(), errores.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.usuarioId = usuarioIdGuardado;
        usuarioActualizado.nombreUsuario = nombreUsuario;
        usuarioActualizado.nombre = nombre;
        usuarioActualizado.apellidos = apellidos;
        usuarioActualizado.correo = correo;

        UsuarioAPI api = RetrofitClient.getInstance().create(UsuarioAPI.class);

        Call<UsuarioRespuesta> call = api.actualizarUsuario("Bearer " + token, usuarioActualizado.usuarioId, usuarioActualizado);

        call.enqueue(new Callback<UsuarioRespuesta>() {
            @Override
            public void onResponse(Call<UsuarioRespuesta> call, Response<UsuarioRespuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PantallaDetallePerfil.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    redirigirAlLogin();
                } else {
                    String mensaje;
                    switch (response.code()) {
                        case 400:
                            mensaje = "El correo ya está registrado a otro usuario";
                            break;
                        case 500:
                            mensaje = "Ha ocurrido un error con el servidor. Puede reportarlo";
                            break;
                        default:
                            mensaje = "Error desconocido. Código: " + response.code();
                            break;
                    }
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioRespuesta> call, Throwable t) {
                boolean estaConectado = false;

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    estaConectado = networkInfo != null && networkInfo.isConnected();
                }

                if (estaConectado) {
                    Toast.makeText(PantallaDetallePerfil.this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PantallaDetallePerfil.this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void actualizarContrasena() {
        String token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText etContrasena = findViewById(R.id.etContrasena);
        String nuevaContrasena = etContrasena.getText().toString().trim();

        StringBuilder errores = new StringBuilder();

        String errPass = Validador.validarPassword(nuevaContrasena);
        if (errPass != null) errores.append("- ").append(errPass).append("\n");

        if (errores.length() > 0) {
            Toast.makeText(getApplicationContext(), errores.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        ContrasenaRequest request = new ContrasenaRequest(nuevaContrasena);

        UsuarioAPI api = RetrofitClient.getInstance().create(UsuarioAPI.class);
        Call<UsuarioRespuesta> call = api.actualizarContrasena("Bearer " + token, usuarioIdGuardado, request);

        call.enqueue(new Callback<UsuarioRespuesta>() {
            @Override
            public void onResponse(Call<UsuarioRespuesta> call, Response<UsuarioRespuesta> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PantallaDetallePerfil.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                    etContrasena.setText("");
                    etContrasena.setVisibility(View.GONE);
                    Button btnGuardarContrasena = findViewById(R.id.btnGuardarContrasena);
                    btnGuardarContrasena.setVisibility(View.GONE);
                } else {
                    Toast.makeText(PantallaDetallePerfil.this, "Error al actualizar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioRespuesta> call, Throwable t) {
                boolean estaConectado = false;

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    estaConectado = networkInfo != null && networkInfo.isConnected();
                }

                if (estaConectado) {
                    Toast.makeText(PantallaDetallePerfil.this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PantallaDetallePerfil.this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void redirigirAlLogin() {
        Toast.makeText(PantallaDetallePerfil.this, "Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
        getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(PantallaDetallePerfil.this, ActividadLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarDialogoConfirmacion() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar cuenta")
                .setMessage("¿Estás segura de que quieres borrar tu cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> eliminarCuenta())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarCuenta() {
        String token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioAPI api = RetrofitClient.getInstance().create(UsuarioAPI.class);
        Call<UsuarioRespuesta> call = api.eliminarUsuario("Bearer " + token, usuarioIdGuardado);

        call.enqueue(new Callback<UsuarioRespuesta>() {
            @Override
            public void onResponse(Call<UsuarioRespuesta> call, Response<UsuarioRespuesta> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PantallaDetallePerfil.this, "Cuenta eliminada correctamente", Toast.LENGTH_LONG).show();

                    getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
                    Intent intent = new Intent(PantallaDetallePerfil.this, ActividadLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PantallaDetallePerfil.this, "No se pudo eliminar la cuenta. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioRespuesta> call, Throwable t) {
                boolean estaConectado = false;

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    estaConectado = networkInfo != null && networkInfo.isConnected();
                }

                if (estaConectado) {
                    Toast.makeText(PantallaDetallePerfil.this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PantallaDetallePerfil.this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

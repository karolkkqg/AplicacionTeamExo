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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Request;  // ✅ ESTA ES LA CORRECTA

import com.example.aplicacionteamexo.grpc.LoginRequest;
import com.example.aplicacionteamexo.grpc.LoginResponse;
import com.example.aplicacionteamexo.grpc.UsuarioServiceGrpc;
import com.example.aplicacionteamexo.utilidades.Configuracion;
import com.example.aplicacionteamexo.utils.Validador;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ActividadLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        probarConexionServidor();
        EdgeToEdge.enable(this);
        setContentView(R.layout.actividad_login);

        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        Button btnRegistrarse = findViewById(R.id.btnRegistrarse);
        ProgressBar barraDeProgreso = findViewById(R.id.barraProgreso);

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

        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(ActividadLogin.this, actividadRegistro.class);
            startActivity(intent);
        });

        btnIniciarSesion.setOnClickListener(v -> {
            barraDeProgreso.setVisibility(View.VISIBLE);
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etPassword.getText().toString();

            StringBuilder errores = new StringBuilder();

            String errCorreo = Validador.validarCorreo(correo);
            if (errCorreo != null) errores.append("- ").append(errCorreo).append("\n");

            String errPass = Validador.validarPassword(contrasena);
            if (errPass != null) errores.append("- ").append(errPass).append("\n");

            if (errores.length() > 0) {
                barraDeProgreso.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), errores.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            new Thread(() -> {
                try {
                    String ip = Configuracion.obtenerIP(getApplicationContext());
                    ManagedChannel channel = ManagedChannelBuilder
                            .forAddress(ip, 50051)
                            .usePlaintext()
                            .build();

                    UsuarioServiceGrpc.UsuarioServiceBlockingStub stub = UsuarioServiceGrpc.newBlockingStub(channel);

                    LoginRequest request = LoginRequest.newBuilder()
                            .setCorreo(correo)
                            .setContrasena(contrasena)
                            .build();

                    LoginResponse response = stub.login(request);

                    runOnUiThread(() -> {
                        if (response.getExito()) {
                            String token = response.getToken();
                            String mensaje = response.getMensaje();

                            getSharedPreferences("auth", MODE_PRIVATE)
                                    .edit()
                                    .putString("token", token)
                                    .apply();

                            Toast.makeText(this, "Inicio de sesión exitoso: " + mensaje, Toast.LENGTH_SHORT).show();

                            String nombreUsuario = response.getNombreUsuario();

                            getSharedPreferences("auth", MODE_PRIVATE)
                                    .edit()
                                    .putString("nombreUsuario", nombreUsuario)
                                    .apply();

                            String rol = response.getRol();
                            int usuarioId = response.getUsuarioId();

                            getSharedPreferences("auth", MODE_PRIVATE)
                                    .edit()
                                    .putString("token", token)
                                    .putString("nombreUsuario", nombreUsuario)
                                    .putString("rol", rol)
                                    .putInt("usuarioId", usuarioId)
                                    .apply();

                            Intent intent = new Intent(ActividadLogin.this, PantallaPrincipal.class);
                            startActivity(intent);
                            finish();

                        } else {
                            String mensajeError = response.getMensaje();
                            if (mensajeError != null && !mensajeError.isEmpty()) {
                                Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();
                                barraDeProgreso.setVisibility(View.GONE);
                            } else {
                                boolean estaConectado = false;

                                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                                if (connectivityManager != null) {
                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                                    estaConectado = networkInfo != null && networkInfo.isConnected();
                                }

                                if (estaConectado) {
                                    Toast.makeText(this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    channel.shutdown();

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        barraDeProgreso.setVisibility(View.GONE);

                        boolean estaConectado = false;

                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                        if (connectivityManager != null) {
                            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                            estaConectado = networkInfo != null && networkInfo.isConnected();
                        }

                        if (estaConectado) {
                            Toast.makeText(this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        });
    }

    public void probarConexionServidor() {
        new Thread(() -> {
            String ip = Configuracion.obtenerIP(getApplicationContext());
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://192.168.233.88:3000/api/usuarios")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    runOnUiThread(() -> Toast.makeText(this, "Servidor OK. Datos: " + body.length(), Toast.LENGTH_LONG).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Código HTTP: " + response.code(), Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}

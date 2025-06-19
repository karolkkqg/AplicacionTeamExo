package com.example.aplicacionteamexo;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aplicacionteamexo.data.api.UsuarioAPI;
import com.example.aplicacionteamexo.data.modelo.UsuarioRegistro;
import com.example.aplicacionteamexo.data.modelo.UsuarioRespuesta;
import com.example.aplicacionteamexo.data.network.RetrofitClient;
import com.example.aplicacionteamexo.utils.Validador;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class actividadRegistro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_registro);

        Spinner spinnerRol = findViewById(R.id.spinnerRol);
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etNombreDeUsuario = findViewById(R.id.etNombreDeUsuario);
        EditText etApellidos = findViewById(R.id.etApellidos);
        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etPassModerador = findViewById(R.id.etPassModerador);
        Button btnRegistrarse = findViewById(R.id.btnRegistrarse);
        LinearLayout contenedorPassModerador = findViewById(R.id.contenedorPassModerador);
        TextView tvLabelModerador = findViewById(R.id.tvLabelModerador);

        String[] roles = {"Fan", "Moderador", "Administrador"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapter);

        spinnerRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String rolSeleccionado = parent.getItemAtPosition(position).toString();

                if (rolSeleccionado.equals("Moderador")) {
                    contenedorPassModerador.setVisibility(View.VISIBLE);
                    tvLabelModerador.setText("Contraseña de moderador:");
                } else if (rolSeleccionado.equals("Administrador")) {
                    contenedorPassModerador.setVisibility(View.VISIBLE);
                    tvLabelModerador.setText("Clave de administrador:");
                } else {
                    contenedorPassModerador.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                contenedorPassModerador.setVisibility(View.GONE);
            }
        });

        EditText etPassword = findViewById(R.id.etPassword);
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

        ImageButton btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(actividadRegistro.this, ActividadLogin.class);
            startActivity(intent);
            finish();
        });

        btnRegistrarse.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String rol = spinnerRol.getSelectedItem().toString();
            String nombreUsuario = etNombreDeUsuario.getText().toString().trim();
            String claveRol = etPassModerador.getText().toString().trim();

            StringBuilder errores = new StringBuilder();

            String errNombre = Validador.validarNombre(nombre, "Nombre");
            if (errNombre != null) errores.append("- ").append(errNombre).append("\n");

            String errApellidos = Validador.validarNombre(apellidos, "Apellidos");
            if (errApellidos != null) errores.append("- ").append(errApellidos).append("\n");

            String errUsuario = Validador.validarNombreDeUsuario(nombreUsuario);
            if (errUsuario != null) errores.append("- ").append(errUsuario).append("\n");

            String errCorreo = Validador.validarCorreo(correo);
            if (errCorreo != null) errores.append("- ").append(errCorreo).append("\n");

            String errPass = Validador.validarPassword(password);
            if (errPass != null) errores.append("- ").append(errPass).append("\n");

            if (rol.equals("Moderador")) {
                if (claveRol.isEmpty()) {
                    errores.append("- La clave del rol no puede estar vacía.\n");
                } else if (!claveRol.equals("uwo193d")) {
                    errores.append("- Clave incorrecta para el rol seleccionado.\n");
                }
            }

            if (rol.equals("Admnistrador")) {
                if (claveRol.isEmpty()) {
                    errores.append("- La clave del rol no puede estar vacía.\n");
                } else if (!claveRol.equals("29dmao2")) {
                    errores.append("- Clave incorrecta para el rol seleccionado.\n");
                }
            }

            if (errores.length() > 0) {
                Toast.makeText(getApplicationContext(), errores.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            UsuarioRegistro nuevoUsuario = new UsuarioRegistro(
                    nombreUsuario, nombre, apellidos, correo, password, rol
            );

            UsuarioAPI api = RetrofitClient.getInstance().create(UsuarioAPI.class);
            Call<UsuarioRespuesta> call = api.registrar(nuevoUsuario);

            call.enqueue(new Callback<UsuarioRespuesta>() {
                @Override
                public void onResponse(Call<UsuarioRespuesta> call, Response<UsuarioRespuesta> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_LONG).show();
                        limpiarCampos(
                                etNombre,
                                etNombreDeUsuario,
                                etApellidos,
                                etCorreo,
                                etPassword,
                                etPassModerador
                        );
                    } else {
                        int code = response.code();
                        String mensaje;

                        switch (code) {
                            case 400:
                                mensaje = "El correo ya está registrado. Ingrese otro.";
                                break;
                            case 500:
                                mensaje = "Ha ocurdido un error con el servidor. Puede reportarlo";
                                break;
                            default:
                                mensaje = "Error desconocido. Código: " + code;
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
                        Toast.makeText(actividadRegistro.this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(actividadRegistro.this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
    private void limpiarCampos(EditText... campos) {
        for (EditText campo : campos) {
            campo.setText("");
        }
    }
}

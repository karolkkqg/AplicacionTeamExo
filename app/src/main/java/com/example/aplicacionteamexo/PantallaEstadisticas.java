package com.example.aplicacionteamexo;

import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import estadistica.Estadistica;
import estadistica.EstadisticasServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class PantallaEstadisticas extends AppCompatActivity {
    private TextView txtTotalPublicaciones, txtDiaMasActivo, txtNotificaciones,
            txtTopLikes, txtTopComentarios, txtUsuarioTopPublicaciones,
            txtUsuarioTopReacciones, txtUsuarioTopComentarios;

    private TableLayout tablaRecursos;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_estadisticas);

        txtTotalPublicaciones = findViewById(R.id.txtTotalPublicaciones);
        txtDiaMasActivo = findViewById(R.id.txtDiaMasActivo);
        txtNotificaciones = findViewById(R.id.txtNotificaciones);
        txtTopLikes = findViewById(R.id.txtTopLikes);
        txtTopComentarios = findViewById(R.id.txtTopComentarios);
        txtUsuarioTopPublicaciones = findViewById(R.id.txtUsuarioTopPublicaciones);
        txtUsuarioTopReacciones = findViewById(R.id.txtUsuarioTopReacciones);
        txtUsuarioTopComentarios = findViewById(R.id.txtUsuarioTopComentarios);
        tablaRecursos = findViewById(R.id.tablaRecursos);
        pieChart = findViewById(R.id.miPieChart);

        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("192.168.0.157", 50055)
                .usePlaintext()
                .build();

        EstadisticasServiceGrpc.EstadisticasServiceBlockingStub stub =
                EstadisticasServiceGrpc.newBlockingStub(channel);

        new Thread(() -> {
            try {
                Estadistica.EstadisticasResponse response = stub.obtenerEstadisticas(
                        Estadistica.EstadisticasRequest.newBuilder().build()
                );

                runOnUiThread(() -> {
                    txtTotalPublicaciones.setText("Total publicaciones: " + response.getTotalPublicaciones());
                    txtDiaMasActivo.setText("Día con más publicaciones: " + response.getDiaConMasPublicaciones());
                    txtNotificaciones.setText("Notificaciones pendientes: " + response.getNotificacionesPendientes());

                    txtTopLikes.setText("ID: " + response.getTopLikes().getPublicacionId() + ", Total: " + response.getTopLikes().getTotal());
                    txtTopComentarios.setText("ID: " + response.getTopComentarios().getPublicacionId() + ", Total: " + response.getTopComentarios().getTotal());

                    txtUsuarioTopPublicaciones.setText("ID: " + response.getUsuarioTopPublicaciones());
                    txtUsuarioTopReacciones.setText("ID: " + response.getUsuarioTopReacciones());
                    txtUsuarioTopComentarios.setText("ID: " + response.getUsuarioTopComentarios());

                    tablaRecursos.removeAllViews();
                    List<PieEntry> entries = new ArrayList<>();

                    for (Estadistica.ConteoPorTipo tipo : response.getRecursosPorTipoList()) {
                        TableRow row = new TableRow(this);
                        TextView tipoTxt = new TextView(this);
                        TextView totalTxt = new TextView(this);
                        tipoTxt.setText(tipo.getTipo());
                        totalTxt.setText(String.valueOf(tipo.getTotal()));
                        row.addView(tipoTxt);
                        row.addView(totalTxt);
                        tablaRecursos.addView(row);

                        entries.add(new PieEntry(tipo.getTotal(), tipo.getTipo()));
                    }

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    PieData data = new PieData(dataSet);
                    pieChart.setData(data);
                    pieChart.invalidate();

                });
            } catch (Exception e) {
                Log.e("PantallaEstadisticas", "Error al cargar estadísticas", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al cargar estadísticas", Toast.LENGTH_SHORT).show();
                });
            } finally {
                channel.shutdown();
            }
        }).start();
    }

}
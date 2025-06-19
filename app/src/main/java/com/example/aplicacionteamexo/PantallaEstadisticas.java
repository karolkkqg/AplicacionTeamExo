package com.example.aplicacionteamexo;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import estadistica.Estadistica;
import estadistica.EstadisticasServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class PantallaEstadisticas extends AppCompatActivity {
    private TextView txtTotalPublicaciones, txtDiaMasActivo, txtNotificaciones,
            txtTopLikes, txtTopComentarios, txtUsuarioTopPublicaciones,
            txtUsuarioTopReacciones, txtUsuarioTopComentarios, txtTituloTopLikes,
            txtTituloTopComentarios;
    StringBuilder contenido = new StringBuilder();
    private TableLayout tablaRecursos;
    private Estadistica.EstadisticasResponse ultimaRespuesta;
    private BarChart barChart;

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
        barChart = findViewById(R.id.barChart);
        txtTituloTopLikes = findViewById(R.id.txtTituloTopLikes);
        txtTituloTopComentarios = findViewById(R.id.txtTituloTopComentarios);

        Button btnDescargar = findViewById(R.id.btnDescargarEstadisticas);
        cargarEstadisticas();
        btnDescargar.setOnClickListener(v -> {
            if (ultimaRespuesta != null) {
                generarArchivoEstadisticasPDF(ultimaRespuesta);
            } else {
                Toast.makeText(this, "Aún no se han cargado las estadísticas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarEstadisticas() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("192.168.0.109", 50055)
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
                    ultimaRespuesta = response;
                    contenido.setLength(0);
                    txtTotalPublicaciones.setText("Total publicaciones: " + response.getTotalPublicaciones());
                    txtDiaMasActivo.setText("Día con más publicaciones: " + response.getDiaConMasPublicaciones());
                    txtNotificaciones.setText("Notificaciones pendientes: " + response.getNotificacionesPendientes());

                    txtTopLikes.setText("ID: " + response.getTopLikes().getPublicacionId() + ", Total: " + response.getTopLikes().getTotal());
                    txtTituloTopLikes.setText("Título: " + response.getTopLikes().getTitulo());
                    txtTituloTopComentarios.setText("Título: " + response.getTopComentarios().getTitulo());
                    txtTopComentarios.setText("ID: " + response.getTopComentarios().getPublicacionId() + ", Total: " + response.getTopComentarios().getTotal());

                    Estadistica.UsuarioTop usuarioPub = response.getUsuarioTopPublicaciones();
                    txtUsuarioTopPublicaciones.setText("👤 Publicaciones:\nNombre: " + usuarioPub.getNombre() + "\nID: " + usuarioPub.getUsuarioId());
                    Estadistica.UsuarioTop usuarioReac = response.getUsuarioTopReacciones();
                    txtUsuarioTopReacciones.setText("💬 Reacciones:\nNombre: " + usuarioReac.getNombre() + "\nID: " + usuarioReac.getUsuarioId());
                    Estadistica.UsuarioTop usuarioCom = response.getUsuarioTopComentarios();
                    txtUsuarioTopComentarios.setText("📝 Comentarios:\nNombre: " + usuarioCom.getNombre() + "\nID: " + usuarioCom.getUsuarioId());

                    tablaRecursos.removeAllViews();
                    List<BarEntry> barEntries = new ArrayList<>();
                    List<String> etiquetas = new ArrayList<>();
                    int index = 0;
                    AtomicInteger totalRecursos = new AtomicInteger(0);

                    for (Estadistica.ConteoPorTipo tipo : response.getRecursosPorTipoList()) {
                        TableRow row = new TableRow(this);
                        TextView tipoTxt = new TextView(this);
                        TextView totalTxt = new TextView(this);

                        tipoTxt.setText(tipo.getTipo());
                        tipoTxt.setTextColor(Color.BLACK);

                        totalTxt.setText(String.valueOf(tipo.getTotal()));
                        totalTxt.setTextColor(Color.BLACK);

                        row.addView(tipoTxt);
                        row.addView(totalTxt);
                        tablaRecursos.addView(row);

                        barEntries.add(new BarEntry(index, tipo.getTotal()));
                        etiquetas.add(tipo.getTipo());
                        totalRecursos.addAndGet(tipo.getTotal());
                        index++;
                    }

                    BarDataSet dataSet = new BarDataSet(barEntries, "Recursos por tipo");
                    dataSet.setColor(Color.parseColor("#53005D"));
                    dataSet.setValueTextColor(Color.BLACK);
                    dataSet.setValueTextSize(12f);

                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getBarLabel(BarEntry barEntry) {
                            float porcentaje = (barEntry.getY() / totalRecursos.get()) * 100;
                            return String.format("%.1f%%", porcentaje);
                        }
                    });

                    BarData barData = new BarData(dataSet);
                    barChart.setData(barData);
                    barChart.getDescription().setEnabled(false);

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas));
                    xAxis.setGranularity(1f);
                    xAxis.setGranularityEnabled(true);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);

                    barChart.getAxisRight().setEnabled(false);
                    barChart.invalidate();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaActual = sdf.format(new Date());

                    contenido.append("📊 ESTADÍSTICAS EXO – ").append(fechaActual).append("\n\n");
                    contenido.append("Total de publicaciones: ").append(response.getTotalPublicaciones()).append("\n");
                    contenido.append("Día más activo: ").append(response.getDiaConMasPublicaciones())
                            .append(" (").append(response.getPublicacionesEnEseDia()).append(" publicaciones)\n");
                    contenido.append("Notificaciones pendientes: ").append(response.getNotificacionesPendientes()).append("\n\n");

                    contenido.append("🔝 Publicación con más likes:\n");
                    contenido.append("ID: ").append(response.getTopLikes().getPublicacionId())
                            .append(" | Título: ").append(response.getTopLikes().getTitulo())
                            .append(" | Total: ").append(response.getTopLikes().getTotal()).append("\n");

                    contenido.append("🔝 Publicación con más comentarios:\n");
                    contenido.append("ID: ").append(response.getTopComentarios().getPublicacionId())
                            .append(" | Título: ").append(response.getTopComentarios().getTitulo())
                            .append(" | Total: ").append(response.getTopComentarios().getTotal()).append("\n\n");

                    contenido.append("👥 Usuarios destacados:\n");
                    contenido.append("Más publicaciones: ").append(response.getUsuarioTopPublicaciones().getNombre()).append("\n");
                    contenido.append("Más reacciones: ").append(response.getUsuarioTopReacciones().getNombre()).append("\n");
                    contenido.append("Más comentarios: ").append(response.getUsuarioTopComentarios().getNombre()).append("\n\n");

                    contenido.append("📂 Recursos por tipo:\n");
                    for (Estadistica.ConteoPorTipo tipo : response.getRecursosPorTipoList()) {
                        contenido.append("- ").append(tipo.getTipo()).append(": ").append(tipo.getTotal()).append("\n");
                    }

                });
            } catch (Exception e) {
                runOnUiThread(() -> {
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
            } finally {
                channel.shutdown();
            }
        }).start();
    }

    private void generarArchivoEstadisticasPDF(Estadistica.EstadisticasResponse response) {
        try {
            String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "estadisticas_appExo_" + fecha + ".pdf";
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, fileName);

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("ESTADÍSTICAS EXO", titleFont));
            document.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()), textFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Total publicaciones: " + response.getTotalPublicaciones(), textFont));
            document.add(new Paragraph("Día más activo: " + response.getDiaConMasPublicaciones() + " (" + response.getPublicacionesEnEseDia() + ")", textFont));
            document.add(new Paragraph("Notificaciones pendientes: " + response.getNotificacionesPendientes(), textFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Publicación con más likes:", sectionFont));
            document.add(new Paragraph("ID: " + response.getTopLikes().getPublicacionId(), textFont));
            document.add(new Paragraph("Título: " + response.getTopLikes().getTitulo(), textFont));
            document.add(new Paragraph("Total: " + response.getTopLikes().getTotal(), textFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Publicación con más comentarios:", sectionFont));
            document.add(new Paragraph("ID: " + response.getTopComentarios().getPublicacionId(), textFont));
            document.add(new Paragraph("Título: " + response.getTopComentarios().getTitulo(), textFont));
            document.add(new Paragraph("Total: " + response.getTopComentarios().getTotal(), textFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Usuarios destacados:", sectionFont));
            document.add(new Paragraph("Más publicaciones: " + response.getUsuarioTopPublicaciones().getNombre(), textFont));
            document.add(new Paragraph("Más reacciones: " + response.getUsuarioTopReacciones().getNombre(), textFont));
            document.add(new Paragraph("Más comentarios: " + response.getUsuarioTopComentarios().getNombre(), textFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Recursos por tipo:", sectionFont));
            PdfPTable table = new PdfPTable(2);
            table.addCell(new PdfPCell(new Paragraph("Tipo", sectionFont)));
            table.addCell(new PdfPCell(new Paragraph("Total", sectionFont)));
            for (Estadistica.ConteoPorTipo tipo : response.getRecursosPorTipoList()) {
                table.addCell(new Paragraph(tipo.getTipo(), textFont));
                table.addCell(new Paragraph(String.valueOf(tipo.getTotal()), textFont));
            }
            document.add(table);

            document.close();

            Toast.makeText(this, "PDF guardado en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException | DocumentException e) {
            Toast.makeText(this, "Error al guardar el PDF", Toast.LENGTH_SHORT).show();
        }
    }

}
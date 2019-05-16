package mx.upcrapbaba.sms.views.inicio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.adaptadores.spinners.Asignaturas_General_Adapter;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.api.Service.SMSService;
import mx.upcrapbaba.sms.extras.Alert_Dialog;
import mx.upcrapbaba.sms.models.Alumno;
import mx.upcrapbaba.sms.models.Asignatura;
import mx.upcrapbaba.sms.models.Grupo;
import mx.upcrapbaba.sms.models.User;
import mx.upcrapbaba.sms.sqlite.DBHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Estadisticas extends AppCompatActivity implements BottomNavigation.OnMenuItemSelectionListener {

    private User user_data;
    private AnyChartView chartPromedio, chartPorcentaje;
    private Spinner spAsignaturas, spGrupos;
    private List<Asignatura> asignaturas = new LinkedList<>();
    private Asignatura asignatura_seleccionada;
    private List<Grupo> grupos = new LinkedList<>();
    private List<String> nombre_grupos = new LinkedList<>();
    private Grupo grupo_seleccionado;
    private List<Alumno> alumnos;
    private ToggleButton tgType;
    private LinearLayout layPromedio, layPorcentaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        SMSService sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);
        String token = "Bearer " + new DBHelper(this).getData_Usuario().get(1);
        String id_usuario = new DBHelper(this).getData_Usuario().get(0);

        BottomNavigation nav_bar = findViewById(R.id.bottom_nav_bar);

        nav_bar.setDefaultSelectedIndex(1);

        nav_bar.setMenuItemSelectionListener(this);

        chartPromedio = findViewById(R.id.chartPromedio);
        chartPorcentaje = findViewById(R.id.chartPorcentaje);

        spAsignaturas = findViewById(R.id.spAsignaturas_Estadisticas);
        spGrupos = findViewById(R.id.spGrupos_Estadisticas);
        tgType = findViewById(R.id.tgEstadisticas);
        layPorcentaje = findViewById(R.id.lay_Porcentaje);
        layPromedio = findViewById(R.id.lay_Promedio);

        Toolbar toolbar = findViewById(R.id.ToolBar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_Estadisticas);
        } else {
            System.out.println("Ha ocurrido un error al inicializar la barra de titulo");
            Alert_Dialog.showErrorMessage(this);
        }

        getUserInfo(sms_service, token, id_usuario);

        tgType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                layPromedio.setVisibility(View.VISIBLE);
                layPorcentaje.setVisibility(View.GONE);
            } else {
                layPromedio.setVisibility(View.GONE);
                layPorcentaje.setVisibility(View.VISIBLE);
            }
        });

    }

    private void getUserInfo(SMSService sms_service, String token, String id_usuario) {
        sms_service.getUserInfo(token, id_usuario).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user_data = response.body();
                    asignaturas = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());

                    if (asignaturas.isEmpty()) {
                        spAsignaturas.setEnabled(false);
                    } else {
                        spAsignaturas.setAdapter(new Asignaturas_General_Adapter(Estadisticas.this, R.layout.asignatura_item, asignaturas));
                        setOnSelectedListener();
                    }

                } else {
                    System.out.println(response.errorBody());
                    Alert_Dialog.showErrorMessage(Estadisticas.this);
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Alert_Dialog.showErrorMessage(Estadisticas.this);
                System.out.println(t.toString());
            }
        });

    }

    private void setOnSelectedListener() {
        spAsignaturas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                asignatura_seleccionada = asignaturas.get(position);
                grupos = new Gson().fromJson(asignatura_seleccionada.getGrupos(), new TypeToken<List<Grupo>>() {
                }.getType());
                nombre_grupos.clear();

                spGrupos.setAdapter(new ArrayAdapter<>(Estadisticas.this, R.layout.custom_spinner, new ArrayList<>()));
                for (Grupo grupo : grupos) {
                    nombre_grupos.add(grupo.getNombre_grupo());
                }

                if (grupos.isEmpty()) {
                    spGrupos.setEnabled(false);
                } else {
                    spGrupos.setEnabled(true);
                    spGrupos.setAdapter(new ArrayAdapter<>(Estadisticas.this, R.layout.custom_spinner, nombre_grupos));
                    setGrupo();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setGrupo() {
        spGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                grupo_seleccionado = grupos.get(position);

                alumnos = new Gson().fromJson(grupo_seleccionado.getAlumnos(), new TypeToken<List<Alumno>>() {
                }.getType());

                if (!alumnos.isEmpty()) {
                    //Cargo los datos
                    int cant_aprobados = 0, cant_reprobados = 0, cant_np = 0;
                    double promedio_aprobados = 0.0, promedio_reprobados = 0.0, promedio_general = 0.0;

                    for (Alumno alumno : alumnos) {
                        if (!alumno.getPromedio().equals("NP")) {
                            double promedio = Double.parseDouble(alumno.getPromedio());
                            if (promedio >= 6.0) {
                                cant_aprobados++;
                                promedio_aprobados += promedio;
                            } else {
                                cant_reprobados++;
                                promedio_reprobados += promedio;
                            }
                        } else {
                            cant_np++;
                        }

                    }

                    double aprobados_final = (cant_aprobados * 100) / alumnos.size();
                    double reprobados_final = (cant_reprobados * 100) / alumnos.size();
                    double np_final = (cant_np * 100) / alumnos.size();

                    double promedio_final_aprobados = promedio_aprobados / alumnos.size();
                    double promedio_final_reprobados = promedio_reprobados / alumnos.size();
                    double promedio_final_general = (promedio_aprobados + promedio_final_reprobados) / alumnos.size();

                    loadDataPorcentajeCharts(aprobados_final, reprobados_final, np_final);

                    loadDataPromedioCharts(promedio_final_aprobados, promedio_final_reprobados, promedio_final_general);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void loadDataPorcentajeCharts(double aprobados, double reprobados, double np) {

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Aprobados", aprobados));
        data.add(new ValueDataEntry("Reprobados", reprobados));
        data.add(new ValueDataEntry("NP", np));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(10d)
                .format("{%Value}{groupsSeparator: }%");

        cartesian.animation(true);

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }%");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        chartPorcentaje.setChart(cartesian);


    }

    private void loadDataPromedioCharts(double aprobados, double reprobados, double general) {

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Aprobados", aprobados));
        data.add(new ValueDataEntry("Reprobados", reprobados));
        data.add(new ValueDataEntry("General", general));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(10d)
                .format("{%Value}{groupsSeparator: }%");

        cartesian.animation(true);

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }%");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        chartPromedio.setChart(cartesian);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Estadisticas.this, Inicio.class));
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        Estadisticas.this.finish();
    }

    @Override
    public void onMenuItemReselect(int i, int i1, boolean b) {
        Estadisticas.this.recreate();
    }

    @Override
    public void onMenuItemSelect(int i, int i1, boolean b) {
        switch (i1) {
            case 0:
                startActivity(new Intent(Estadisticas.this, Inicio.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                this.finish();
                break;
            case 2:
                startActivity(new Intent(Estadisticas.this, Information.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                this.finish();
                break;
        }
    }
}

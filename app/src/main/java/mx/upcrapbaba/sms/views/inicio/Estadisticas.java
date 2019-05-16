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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
    private BarChart chartPromedio, chartPorcentaje;
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
                    System.out.println("Ha ocurrido un error al obtener los datos " + response.errorBody());
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


                    double promedio_final_aprobados = 0.0;
                    double promedio_final_reprobados = 0.0;
                    double promedio_final_general = 0.0;

                    if (promedio_aprobados > 0) {
                        promedio_final_aprobados = promedio_aprobados / cant_aprobados;
                    } else {
                        promedio_final_aprobados = 0.0;
                    }
                    if (promedio_reprobados > 0) {
                        promedio_final_reprobados = promedio_reprobados / cant_reprobados;
                    }

                    promedio_final_general = (promedio_final_aprobados + promedio_final_reprobados) / alumnos.size();

                    setDataPorcentaje(aprobados_final, reprobados_final, np_final);

                    setDataPromedio(promedio_final_aprobados, promedio_final_reprobados, promedio_final_general);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setDataPromedio(double promedio_final_aprobados, double promedio_final_reprobados, double promedio_final_general) {
        ArrayList<BarEntry> aprobados = new ArrayList<>();
        ArrayList<BarEntry> reprobados = new ArrayList<>();
        ArrayList<BarEntry> general = new ArrayList<>();

        aprobados.add(new BarEntry(0, (float) promedio_final_aprobados));
        reprobados.add(new BarEntry(1, (float) promedio_final_reprobados));
        general.add(new BarEntry(2, (float) promedio_final_general));

        BarDataSet dataSetAprobados = new BarDataSet(aprobados, "Aprobados");
        BarDataSet dataSetReprobados = new BarDataSet(reprobados, "Reprobados");
        BarDataSet dataSetGeneral = new BarDataSet(general, "General");

        dataSetAprobados.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSetReprobados.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSetGeneral.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData();
        barData.addDataSet(dataSetAprobados);
        barData.addDataSet(dataSetReprobados);
        barData.addDataSet(dataSetGeneral);


        chartPromedio.animateY(3000);

        chartPromedio.setData(barData);
        chartPromedio.invalidate();

    }

    private void setDataPorcentaje(double aprobados_final, double reprobados_final, double np_final) {
        ArrayList<BarEntry> aprobados = new ArrayList<>();
        ArrayList<BarEntry> reprobados = new ArrayList<>();
        ArrayList<BarEntry> np = new ArrayList<>();

        aprobados.add(new BarEntry(0, (float) aprobados_final));
        reprobados.add(new BarEntry(1, (float) reprobados_final));
        np.add(new BarEntry(2, (float) np_final));

        BarDataSet dataSetAprobados = new BarDataSet(aprobados, "Aprobados");
        BarDataSet dataSetReprobados = new BarDataSet(reprobados, "Reprobados");
        BarDataSet dataSetNP = new BarDataSet(np, "NP");

        dataSetAprobados.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSetReprobados.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSetNP.setColors(ColorTemplate.PASTEL_COLORS);

        BarData barData = new BarData();
        barData.addDataSet(dataSetAprobados);
        barData.addDataSet(dataSetReprobados);
        barData.addDataSet(dataSetNP);

        chartPorcentaje.animateY(3000);

        chartPorcentaje.setData(barData);
        chartPorcentaje.invalidate();

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

package mx.upcrapbaba.sms.views.personalizacion;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import mx.upcrapbaba.sms.R;
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

public class Add_Edit_Alumnos extends AppCompatActivity {
    private User user_data;
    private Asignatura asignatura_seleccionada;
    private Grupo grupo_seleccionado;
    private Alumno alumno_seleccionado;
    private List<Asignatura> asignaturas_original = new LinkedList<>();
    private List<Grupo> grupos_original = new LinkedList<>();
    private List<Alumno> alumnos_general = new LinkedList<>(), alumnos_lineal_general = new LinkedList<>();
    private List<String> nombre_alumno = new LinkedList<>();
    private SMSService sms_service;
    private String SELECCIONADO = "", token = "", id_usuario = "";
    private LinearLayout layGrupos_Inscrito;
    private Spinner spAlumno;
    private EditText etMatricula_Alumno, etNombre_Alumno, etApellidos_Alumno;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add_edit_alumnos);

        sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);
        token = "Bearer " + new DBHelper(this).getData_Usuario().get(1);
        id_usuario = new DBHelper(this).getData_Usuario().get(0);
        spAlumno = findViewById(R.id.spAlumnos_Edit);
        etMatricula_Alumno = findViewById(R.id.etMatricula_Alumno);
        etNombre_Alumno = findViewById(R.id.etNombre_Alumno);
        etApellidos_Alumno = findViewById(R.id.etApellidos_Alumno);
        btnGuardar = findViewById(R.id.btnGuardar_Alumno);

        Toolbar toolbar = findViewById(R.id.ToolBar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            if (getIntent().getStringExtra("SELECCIONADO") != null) {
                SELECCIONADO = getIntent().getStringExtra("SELECCIONADO");
                getSupportActionBar().setTitle(getResources().getString(R.string.barTitle, SELECCIONADO));
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            System.out.println("Ha ocurrido un error al inicializar la barra de titulo");
            Alert_Dialog.showErrorMessage(Add_Edit_Alumnos.this);
        }

        sms_service.getUserInfo(token, id_usuario).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user_data = response.body();
                    asignaturas_original = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());

                    for (Asignatura asignaturas : asignaturas_original) {
                        List<Grupo> grupos_asignatura = new Gson().fromJson(asignaturas.getGrupos(), new TypeToken<List<Grupo>>() {
                        }.getType());
                        for (Grupo grupo : grupos_asignatura) {
                            List<Alumno> alumnos_grupo = new Gson().fromJson(grupo.getAlumnos(), new TypeToken<List<Alumno>>() {
                            }.getType());
                            alumnos_general.addAll(alumnos_grupo);
                            grupos_original.add(grupo);
                        }

                    }

                    List<Alumno> list_duplicate = new ArrayList<>(alumnos_general);

                    HashSet<Alumno> alumnos_lineal = new HashSet<>(list_duplicate);

                    alumnos_lineal_general.clear();

                    alumnos_lineal_general.addAll(alumnos_lineal);

                    for (Alumno alumno : alumnos_lineal) {
                        nombre_alumno.add(String.format("%s - %s", alumno.getMatricula_alumno(), alumno.getNombre_alumno()));
                    }

                    nombre_alumno.add("Añadir nuevo alumno");

                    if (!alumnos_lineal.isEmpty()) {
                        spAlumno.setAdapter(new ArrayAdapter<>(Add_Edit_Alumnos.this, R.layout.custom_spinner, nombre_alumno));
                        spAlumno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position < alumnos_lineal.size()) {
                                    alumno_seleccionado = alumnos_lineal_general.get(position);
                                    etMatricula_Alumno.setText(alumno_seleccionado.getMatricula_alumno());
                                    etNombre_Alumno.setText(alumno_seleccionado.getNombre_alumno());
                                    etApellidos_Alumno.setText(alumno_seleccionado.getApellidos());
                                } else {
                                    alumno_seleccionado = null;
                                    etMatricula_Alumno.getText().clear();
                                    etNombre_Alumno.getText().clear();
                                    etApellidos_Alumno.getText().clear();
                                }
                                btnGuardar.setOnClickListener(v -> saveChanges());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {

                    }



                } else {
                    //TODO HANDLE
                }

            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                //TODO HANDLE
            }
        });


    }

    private void saveChanges() {
        if (validateFields()) {
            if (alumno_seleccionado != null) {
                //Actualizar datos
            } else {
                //Crear un nuevo alumno
                JsonObject data_alumno = new JsonObject();

                data_alumno.addProperty("matricula_alumno", etMatricula_Alumno.getText().toString().trim());
                data_alumno.addProperty("nombre_alumno", etNombre_Alumno.getText().toString().trim());
                data_alumno.addProperty("apellidos", etApellidos_Alumno.getText().toString().trim());

                sms_service.add_alumno(data_alumno, token).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            //Alert_Dialog.showMessage(Add_Edit_Alumnos.this, getString(R.string));
                            Add_Edit_Alumnos.this.recreate();
                        } else {
                            Toasty.warning(Add_Edit_Alumnos.this, "Ha ocurrido un error").show();
                            System.out.println("Ha ocurrido un error al crear el alumno " + response.errorBody());
                        }

                    }

                    @Override
                    public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                        Toasty.warning(Add_Edit_Alumnos.this, "Ha ocurrido un error").show();
                        System.out.println("Ha ocurrido un error en la request para crear el alumno " + t.getMessage());
                    }
                });

            }
        } else {
            Toasty.warning(Add_Edit_Alumnos.this, getString(R.string.fields_error)).show();
        }
    }

    private boolean validateFields() {
        return (!etNombre_Alumno.getText().toString().trim().isEmpty() ||
                !etMatricula_Alumno.getText().toString().trim().isEmpty() ||
                !etApellidos_Alumno.getText().toString().trim().isEmpty());
    }

}

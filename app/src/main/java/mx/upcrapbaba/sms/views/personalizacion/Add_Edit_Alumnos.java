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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import kotlin.Unit;
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
                Alumno alumno_add = new Alumno();
                alumno_add.setNombre_alumno(etNombre_Alumno.getText().toString());
                alumno_add.setMatricula_alumno(etMatricula_Alumno.getText().toString());
                alumno_add.setApellidos(etApellidos_Alumno.getText().toString());
                alumno_add.setImagen_alumno("profile_images/profile_holder.png");
                alumno_add.setCalificaciones(alumno_seleccionado.getCalificaciones());

                for (int i = 0; i < asignaturas_original.size(); i++) {
                    Asignatura asignatura_actual = asignaturas_original.get(i);
                    ArrayList<Grupo> grupos_in_asign = new Gson().fromJson(asignaturas_original.get(i).getGrupos(), new TypeToken<ArrayList<Grupo>>() {
                    }.getType());
                    for (int j = 0; j < grupos_in_asign.size(); j++) {
                        Grupo grupo_actual = grupos_in_asign.get(j);
                        ArrayList<Alumno> alumnos = new Gson().fromJson(grupos_in_asign.get(j).getAlumnos(), new TypeToken<ArrayList<Alumno>>() {
                        }.getType());
                        for (int k = 0; k < alumnos.size(); k++) {
                            Alumno alumno_actual = alumnos.get(k);
                            if (alumno_actual.getMatricula_alumno().equals(alumno_seleccionado.getMatricula_alumno())) {
                                alumnos.remove(alumno_actual);
                                alumnos.add(alumno_add);
                                k--;
                                grupos_in_asign.remove(grupo_actual);
                                grupo_actual.setAlumnos((JsonArray) new Gson().toJsonTree(alumnos, new TypeToken<List<Alumno>>() {
                                }.getType()));
                                grupos_in_asign.add(grupo_actual);
                                j--;
                                asignaturas_original.remove(asignatura_actual);
                                JsonArray grupos_nuevos = (JsonArray) new Gson().toJsonTree(grupos_in_asign, new TypeToken<List<Grupo>>() {
                                }.getType());
                                asignatura_actual.setGrupos(grupos_nuevos);
                                asignaturas_original.add(asignatura_actual);
                                i--;
                            }
                        }
                    }
                }

                JsonObject user_update = new JsonObject();

                JsonArray asignaturas_array = (JsonArray) new Gson().toJsonTree(asignaturas_original,
                        new TypeToken<List<Asignatura>>() {
                        }.getType());

                user_update.add("materias", asignaturas_array);

                sms_service.update_data(user_update, token, id_usuario).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Alert_Dialog.showWarnMessage(Add_Edit_Alumnos.this, "¡Correcto!", "Se han actualizado los alumnos")
                                    .positiveButton(R.string.aceptar, null, materialDialog -> {
                                        Add_Edit_Alumnos.this.recreate();
                                        return Unit.INSTANCE;
                                    }).show();

                        } else {
                            Toasty.warning(Add_Edit_Alumnos.this, "Ha ocurrido un error al actualizar los alumnos").show();
                            System.out.println("Error al actualizar los datos del usuario " + response.errorBody());
                        }

                    }

                    @Override
                    public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                        Toasty.warning(Add_Edit_Alumnos.this, "Ha ocurrido un error en la request para actualizar los datos").show();
                        System.out.println("Error al actualizar los datos del usuario " + t.getMessage());
                    }
                });

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

package mx.upcrapbaba.sms.views.personalizacion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import kotlin.Unit;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.adaptadores.listviews.AlumnosOnline_EditGrupo_Adapter;
import mx.upcrapbaba.sms.adaptadores.listviews.Alumnos_EditGrupo_Adapter;
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

public class Add_Edit_Grupos extends AppCompatActivity implements Alumnos_EditGrupo_Adapter.AlumnoListener, AlumnosOnline_EditGrupo_Adapter.AlumnoOnlineListener {

    private String SELECCIONADO = "", token = "", id_usuario = "";
    private User user_data;
    private List<Asignatura> asignaturas_original = new LinkedList<>(), asignaturas_repeated = new LinkedList<>();
    private ArrayDeque<Asignatura> asignaturas_temporal = new ArrayDeque<>();
    private List<Grupo> grupos_original = new LinkedList<>();
    private List<Alumno> alumnos_grupo = new LinkedList<>(), alumnos_to_remove = new LinkedList<>(), alumnos_to_add = new LinkedList<>();
    private ArrayDeque<String> grupos_for_spinner = new ArrayDeque<>();
    private EditText etNombre_Grupo;
    private Spinner spGrupos, spAsignaturas;
    private Button btnEliminar_Alumnos, btnEdit_Criterios, btnSeleccionar_Grupo, btnClosePopup;
    private ListView lstAlumnos_Inscritos, lstAlumnos_Online;
    private Grupo grupo_seleccionado = null;
    private TextView txtAsignaturas;
    private Asignatura asignatura_seleccionada;
    private SMSService sms_service;
    private View popUpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add_edit_grupos);

        sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);
        token = "Bearer " + new DBHelper(this).getData_Usuario().get(1);
        id_usuario = new DBHelper(this).getData_Usuario().get(0);


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
            Alert_Dialog.showErrorMessage(Add_Edit_Grupos.this);
        }

        etNombre_Grupo = findViewById(R.id.etGrupo_Nombre);
        spGrupos = findViewById(R.id.spGrupos_Edit);
        Button btnSave = findViewById(R.id.btnGuardar);
        btnEliminar_Alumnos = findViewById(R.id.btnEliminar_Alumnos);
        btnSeleccionar_Grupo = findViewById(R.id.btnSelect_Grupo_Edit);
        lstAlumnos_Inscritos = findViewById(R.id.lstAlumnos_Inscritos);
        lstAlumnos_Online = findViewById(R.id.lstAlumnos_Online);
        btnEdit_Criterios = findViewById(R.id.btnEditar_Criterios);
        spAsignaturas = findViewById(R.id.spAsignatura_to_select);
        txtAsignaturas = findViewById(R.id.txtAsignatura_Label);
        popUpView = findViewById(R.id.popUpView);

        sms_service.getUserInfo(token, id_usuario).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user_data = response.body();

                    asignaturas_original = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());


                    for (int i = 0; i < asignaturas_original.size(); i++) {
                        List<Grupo> grupo_asignatura = new Gson()
                                .fromJson(asignaturas_original.get(i).getGrupos(),
                                        new TypeToken<List<Grupo>>() {
                                        }.getType());

                        for (int j = 0; j < grupo_asignatura.size(); j++) {
                            grupos_original.add(grupo_asignatura.get(j));
                            grupos_for_spinner.add(String.format("%s - %s", grupo_asignatura.get(j).getNombre_grupo(), asignaturas_original.get(i).getNombre_materia()));
                            asignaturas_repeated.add(asignaturas_original.get(i));
                        }
                    }

                    grupos_for_spinner.add("Crear grupo");

                    spGrupos.setAdapter(new ArrayAdapter<>(Add_Edit_Grupos.this, R.layout.custom_spinner, grupos_for_spinner.toArray()));

                    btnSeleccionar_Grupo.setOnClickListener(v -> {
                        if (spGrupos.getSelectedItemPosition() < grupos_original.size()) {
                            txtAsignaturas.setVisibility(View.GONE);
                            spAsignaturas.setVisibility(View.GONE);
                            btnEdit_Criterios.setEnabled(true);
                            btnEliminar_Alumnos.setEnabled(true);
                            editCriterios();
                            grupo_seleccionado = grupos_original.get(spGrupos.getSelectedItemPosition());
                            asignatura_seleccionada = asignaturas_repeated.get(spGrupos.getSelectedItemPosition());
                            loadDataSpinner(grupo_seleccionado);
                        } else {
                            btnEliminar_Alumnos.setEnabled(false);
                            btnEdit_Criterios.setEnabled(false);
                            etNombre_Grupo.getText().clear();
                            txtAsignaturas.setVisibility(View.VISIBLE);
                            spAsignaturas.setVisibility(View.VISIBLE);
                            spAsignaturas.setAdapter(new Asignaturas_General_Adapter(Add_Edit_Grupos.this, R.layout.asignatura_item, asignaturas_original));
                            grupo_seleccionado = null;
                            asignatura_seleccionada = asignaturas_original.get(spAsignaturas.getSelectedItemPosition());
                            spAsignaturas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    asignatura_seleccionada = asignaturas_original.get(position);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    });

                } else {
                    Alert_Dialog.showWarnMessage(Add_Edit_Grupos.this, getString(R.string.header_warning), getString(R.string.request_error))
                            .positiveButton(R.string.aceptar, null, materialDialog -> {
                                startActivity(new Intent(Add_Edit_Grupos.this, Add_Edit_Grupos.class));
                                Add_Edit_Grupos.this.finish();
                                return Unit.INSTANCE;
                            }).show();
                    System.out.println("Error al obtener la informacion de usuario del servidor \n" + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Alert_Dialog.showWarnMessage(Add_Edit_Grupos.this, getString(R.string.header_warning), getString(R.string.request_error))
                        .positiveButton(R.string.aceptar, null, materialDialog -> {
                            startActivity(new Intent(Add_Edit_Grupos.this, Add_Edit_Grupos.class));
                            Add_Edit_Grupos.this.finish();
                            return Unit.INSTANCE;
                        }).show();
                System.out.println("Error en la request para obtener la informacion de usuario del servidor \n" + t.getMessage());
            }
        });

        sms_service.getAllAlumnos(token).enqueue(new Callback<List<Alumno>>() {
            @Override
            public void onResponse(@NotNull Call<List<Alumno>> call, @NotNull Response<List<Alumno>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lstAlumnos_Online.setAdapter(new AlumnosOnline_EditGrupo_Adapter(Add_Edit_Grupos.this, response.body(), Add_Edit_Grupos.this));
                } else {
                    Alert_Dialog.showWarnMessage(Add_Edit_Grupos.this, getString(R.string.header_warning), getString(R.string.request_error))
                            .positiveButton(R.string.aceptar, null, materialDialog -> {
                                startActivity(new Intent(Add_Edit_Grupos.this, Add_Edit_Grupos.class));
                                Add_Edit_Grupos.this.finish();
                                return Unit.INSTANCE;
                            }).show();
                    System.out.println("Error al obtener los alumnos del servidor \n" + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Alumno>> call, @NotNull Throwable t) {
                Alert_Dialog.showWarnMessage(Add_Edit_Grupos.this, getString(R.string.header_warning), getString(R.string.request_error))
                        .positiveButton(R.string.aceptar, null, materialDialog -> {
                            startActivity(new Intent(Add_Edit_Grupos.this, Add_Edit_Grupos.class));
                            Add_Edit_Grupos.this.finish();
                            return Unit.INSTANCE;
                        }).show();
                System.out.println("Error en la request para obtener los alumnos del servidor \n" + t.getMessage());
            }
        });

        btnSave.setOnClickListener(v -> {
            if (grupo_seleccionado != null) {
                actualizar_grupo();
            } else {
                add_new_grupo();
            }

        });

        btnEliminar_Alumnos.setOnClickListener(v -> DeleteAlumnos());

    }

    private void actualizar_grupo() {
        if (!etNombre_Grupo.getText().toString().isEmpty()) {
            //ArrayDeque<Grupo> grupos_temporal = new ArrayDeque<>();
            ArrayDeque<Asignatura> asignaturas_temporal = new ArrayDeque<>(asignaturas_original);
            List<Grupo> grupos_asignatura = new LinkedList<>(new Gson().fromJson(asignatura_seleccionada.getGrupos(), new TypeToken<List<Grupo>>() {
            }.getType()));

            for (int i = 0; i < grupos_asignatura.size(); i++) {
                if (grupo_seleccionado.getNombre_grupo().equals(grupos_asignatura.get(i).getNombre_grupo())) {
                    grupos_asignatura.remove(i);
                    break;
                }
            }

            asignaturas_temporal.remove(asignatura_seleccionada);

            grupos_original.clear();
            asignaturas_original.clear();

            grupo_seleccionado.setNombre_grupo(etNombre_Grupo.getText().toString());
            if (!alumnos_to_add.isEmpty()) {
                JsonArray alumnos_original = grupo_seleccionado.getAlumnos();
                JsonArray alumnos_updated = (JsonArray) new Gson().toJsonTree(alumnos_to_add, new TypeToken<List<Alumno>>() {
                }.getType());
                alumnos_original.add(alumnos_updated);
                grupo_seleccionado.setAlumnos(alumnos_updated);

            }

            if (!alumnos_to_remove.isEmpty()) {
                List<Alumno> alumnos_original = new Gson().fromJson(grupo_seleccionado.getAlumnos(), new TypeToken<List<Alumno>>() {
                }.getType());

                alumnos_original.removeAll(alumnos_to_remove);

                grupo_seleccionado.setAlumnos((JsonArray) new Gson().toJsonTree(alumnos_original, new TypeToken<List<Alumno>>() {
                }.getType()));

            }

            grupos_asignatura.add(grupo_seleccionado);

            grupos_original.addAll(grupos_asignatura);

            asignatura_seleccionada.setGrupos((JsonArray) new Gson().toJsonTree(grupos_original, new TypeToken<List<Grupo>>() {
            }.getType()));

            asignaturas_temporal.add(asignatura_seleccionada);

            asignaturas_original.addAll(asignaturas_temporal);

            JsonObject user_update = new JsonObject();

            JsonArray asignaturas_new = (JsonArray) new Gson().toJsonTree(asignaturas_original, new TypeToken<List<Asignatura>>() {
            }.getType());

            user_update.add("materias", asignaturas_new);

            sms_service.update_data(user_update, token, id_usuario).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Alert_Dialog.showWarnMessage(Add_Edit_Grupos.this, "¡Actualizacion correcta", "Se ha actualizado correctamente la informacion de usuario")
                                .positiveButton(R.string.aceptar, null, materialDialog -> {
                                    Add_Edit_Grupos.this.recreate();
                                    return Unit.INSTANCE;
                                }).show();
                    } else {
                        Toasty.error(Add_Edit_Grupos.this, "Ha ocurrido un error en la request").show();
                        System.out.println("Error al actualizar los valores " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toasty.error(Add_Edit_Grupos.this, "Ha ocurrido un error en la request").show();
                    System.out.println("Error en la request " + t.getMessage());
                }
            });


        } else {
            Toasty.warning(Add_Edit_Grupos.this, getString(R.string.fields_error)).show();
        }
    }

    private void DeleteAlumnos() {
        Alert_Dialog.showWarnMessage(Add_Edit_Grupos.this, "¡Advertencia!", "¿Esta seguro de querer eliminar a los alumnos seleccionados?")
                .negativeButton(R.string.cancelar, null, materialDialog -> Unit.INSTANCE)
                .positiveButton(R.string.aceptar, null, materialDialog -> {
                    alumnos_grupo.removeAll(alumnos_to_remove);
                    if (!alumnos_grupo.isEmpty()) {
                        lstAlumnos_Inscritos.setAdapter(new Alumnos_EditGrupo_Adapter(Add_Edit_Grupos.this, alumnos_grupo, Add_Edit_Grupos.this));
                    } else {
                        lstAlumnos_Inscritos.setAdapter(new ArrayAdapter<>(Add_Edit_Grupos.this, android.R.layout.simple_list_item_1, new ArrayList<>()));
                    }
                    return Unit.INSTANCE;
                }).show();
    }

    private void editCriterios() {
        btnClosePopup = popUpView.findViewById(R.id.btnClose);

        btnEdit_Criterios.setOnClickListener(v -> popUpView.setVisibility(View.VISIBLE));
        btnClosePopup.setOnClickListener(v -> popUpView.setVisibility(View.GONE));
    }

    private void loadDataSpinner(Grupo grupo_seleccionado) {
        etNombre_Grupo.setText(grupo_seleccionado.getNombre_grupo());
        alumnos_grupo = new Gson().fromJson(grupo_seleccionado.getAlumnos(), new TypeToken<List<Alumno>>() {
        }.getType());
        if (!alumnos_grupo.isEmpty()) {
            lstAlumnos_Inscritos.setAdapter(new Alumnos_EditGrupo_Adapter(Add_Edit_Grupos.this, alumnos_grupo, Add_Edit_Grupos.this));
        } else {
            lstAlumnos_Inscritos.setAdapter(new ArrayAdapter<>(Add_Edit_Grupos.this, android.R.layout.simple_list_item_1, new ArrayList<>()));
        }


    }

    private void add_new_grupo() {
        if (!etNombre_Grupo.getText().toString().isEmpty()) {
            asignaturas_temporal.remove(asignatura_seleccionada);
            Grupo grupo_nuevo = new Grupo();
            JsonArray alumnos_add = (JsonArray) new Gson().toJsonTree(alumnos_to_add, new TypeToken<List<Alumno>>() {
            }.getType());
            grupo_nuevo.setNombre_grupo(etNombre_Grupo.getText().toString());

            JsonObject grupo = new JsonObject();
            grupo.addProperty("nombre_grupo", etNombre_Grupo.getText().toString());

            sms_service.add_grupo(grupo, token).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject user = new JsonObject();
                        grupo_nuevo.setAlumnos(alumnos_add);

                        JsonArray grupos_asignatura = asignatura_seleccionada.getGrupos();

                        grupos_asignatura.add(new Gson().toJsonTree(grupo_nuevo, new TypeToken<Grupo>() {
                        }.getType()));

                        asignatura_seleccionada.setGrupos(grupos_asignatura);

                        asignaturas_temporal.add(asignatura_seleccionada);

                        asignaturas_original.clear();

                        asignaturas_original.addAll(asignaturas_temporal);

                        JsonArray asignaturas_new = (JsonArray) new Gson().toJsonTree(asignaturas_original, new TypeToken<List<Asignatura>>() {
                        }.getType());

                        user.add("materias", asignaturas_new);

                        sms_service.update_data(user, token, id_usuario).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Alert_Dialog.showWarnMessage(Add_Edit_Grupos.this, "¡Correcto!", "Se ha creado el grupo")
                                            .positiveButton(R.string.aceptar, null, materialDialog -> {
                                                Add_Edit_Grupos.this.recreate();
                                                return Unit.INSTANCE;
                                            }).show();
                                } else {
                                    Toasty.info(Add_Edit_Grupos.this, "Ha ocurrido un error al añadir el grupo").show();
                                    System.out.println(response.errorBody());
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                                Toasty.info(Add_Edit_Grupos.this, "Ha ocurrido un error en la request").show();
                                System.out.println(t.getMessage());
                            }
                        });
                    } else {
                        Toasty.info(Add_Edit_Grupos.this, "Ha ocurrido un error al añadir el grupo").show();
                        System.out.println(response.errorBody());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                    Toasty.info(Add_Edit_Grupos.this, "Ha ocurrido un error en la request").show();
                    System.out.println(t.getMessage());
                }
            });

        } else {
            Toasty.warning(Add_Edit_Grupos.this, getString(R.string.warn_nombre_grupo)).show();
        }
    }

    @Override
    public void OnAlumnoSelected(Alumno alumno_seleccionado) {
        alumnos_to_remove.add(alumno_seleccionado);
    }

    @Override
    public void OnAlumnoDeselected(Alumno alumno_seleccionado) {
        alumnos_to_remove.remove(alumno_seleccionado);
    }

    @Override
    public void OnAlumnoOnlineSelected(Alumno alumno_seleccionado) {
        alumnos_to_add.add(alumno_seleccionado);
    }

    @Override
    public void OnAlumnoOnlineDeselected(Alumno alumno_seleccionado) {
        alumnos_to_add.remove(alumno_seleccionado);
    }


}

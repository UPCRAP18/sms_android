package mx.upcrapbaba.sms.views.personalizacion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import kotlin.Unit;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.api.Service.SMSService;
import mx.upcrapbaba.sms.extras.Alert_Dialog;
import mx.upcrapbaba.sms.models.Asignatura;
import mx.upcrapbaba.sms.models.Grupo;
import mx.upcrapbaba.sms.models.User;
import mx.upcrapbaba.sms.sqlite.DBHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Add_Edit_Grupos extends AppCompatActivity {

    private SMSService sms_service;
    private String SELECCIONADO = "", token = "";
    private User user_data;
    private List<Asignatura> asignaturas_original = new LinkedList<>();
    private ArrayDeque<Asignatura> asignaturas_temporal = new ArrayDeque<>();
    private List<Grupo> grupos_original = new LinkedList<>();
    private ArrayDeque<Grupo> grupos_edited = new ArrayDeque<>();
    private ArrayDeque<String> grupos_for_spinner = new ArrayDeque<>();
    private EditText etNombre_Grupo;
    private Spinner spGrupos;
    private Button btnSave, btnEliminar_Grupo, btnEliminar_Alumnos, btnEdit_Criterios, btnSeleccionar_Grupo;
    private ListView lstAlumnos_Inscritos, lstAlumnos_Online;
    private Grupo grupo_seleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add_edit_grupos);

        sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);
        token = "Bearer " + new DBHelper(this).getData_Usuario().get(1);
        String id_usuario = new DBHelper(this).getData_Usuario().get(0);

        if (getIntent().getExtras() != null) {
            SELECCIONADO = getIntent().getStringExtra("SELECCIONADO");
        } else {
            Alert_Dialog.showWarnMessage(Add_Edit_Grupos.this, getString(R.string.header_warning), getString(R.string.request_error))
                    .positiveButton(R.string.aceptar, null, materialDialog -> {
                        startActivity(new Intent(Add_Edit_Grupos.this, Add_Edit_Asignaturas.class));
                        Add_Edit_Grupos.this.finish();
                        return Unit.INSTANCE;
                    }).show();

        }

        Toolbar toolbar = findViewById(R.id.ToolBar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.barTitle, SELECCIONADO));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } else {
            System.out.println("Ha ocurrido un error al inicializar la barra de titulo");
            Alert_Dialog.showErrorMessage(Add_Edit_Grupos.this);
        }

        etNombre_Grupo = findViewById(R.id.etGrupo_Nombre);
        spGrupos = findViewById(R.id.spGrupos_Edit);
        btnSave = findViewById(R.id.btnGuardar);
        btnEliminar_Alumnos = findViewById(R.id.btnEliminar_Alummnos);
        btnEliminar_Grupo = findViewById(R.id.btnEliminar_Grupo);
        btnSeleccionar_Grupo = findViewById(R.id.btnSelect_Grupo_Edit);

        sms_service.getUserInfo(token, id_usuario).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user_data = response.body();

                    asignaturas_original = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());

                    asignaturas_temporal.addAll(asignaturas_original);

                    for (int i = 0; i < asignaturas_original.size(); i++) {
                        List<Grupo> grupo_asignatura = new Gson()
                                .fromJson(asignaturas_original.get(i).getGrupos(),
                                        new TypeToken<List<Grupo>>() {
                                        }.getType());

                        for (int j = 0; j < grupo_asignatura.size(); j++) {
                            grupos_original.add(grupo_asignatura.get(j));
                            grupos_for_spinner.add(String.format("%s - %s", grupo_asignatura.get(j).getNombre_grupo(), asignaturas_original.get(i).getNombre_materia()));
                        }
                    }

                    grupos_edited.addAll(grupos_original);

                    grupos_for_spinner.add("Crear grupo");

                    spGrupos.setAdapter(new ArrayAdapter<>(Add_Edit_Grupos.this, R.layout.custom_spinner, grupos_for_spinner.toArray()));

                    btnSeleccionar_Grupo.setOnClickListener(v -> {
                        if (spGrupos.getSelectedItemPosition() < grupos_original.size()) {
                            loadDataSpinner(grupos_original.get(spGrupos.getSelectedItemPosition()));
                        } else {
                            //TODO Handle --> Crear nuevo grupo
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

    }

    private void loadDataSpinner(Grupo grupo_seleccionado) {
        Toasty.info(Add_Edit_Grupos.this, grupo_seleccionado.getNombre_grupo()).show();
    }

    private void loadDatalstAlumnos_Inscritos() {

    }

    private void loadDatalstAlumnos_Online() {

    }

}

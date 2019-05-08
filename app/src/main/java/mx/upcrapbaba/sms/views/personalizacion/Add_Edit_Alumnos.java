package mx.upcrapbaba.sms.views.personalizacion;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

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
    private List<Alumno> alumnos_en_grupo = new LinkedList<>(), alumnos_general = new LinkedList<>();
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

        Toolbar toolbar = findViewById(R.id.ToolBar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.barTitle, SELECCIONADO));
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

    private boolean validateFields() {
        return (!etNombre_Alumno.getText().toString().trim().isEmpty() ||
                !etMatricula_Alumno.getText().toString().trim().isEmpty() ||
                !etApellidos_Alumno.getText().toString().trim().isEmpty());
    }

}

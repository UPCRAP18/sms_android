package mx.upcrapbaba.sms.views.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import kotlin.Unit;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.api.Service.SMSService;
import mx.upcrapbaba.sms.extras.Alert_Dialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private MaterialEditText etUsr_Nombre, etUsr_Aps, etUsr_Matricula, etUsr_Email, etUsr_Pwd;
    private AVLoadingIndicatorView pbar;
    private SMSService sms_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        Button btnRegresar = findViewById(R.id.btnBack);
        etUsr_Nombre = findViewById(R.id.etMatricula);
        etUsr_Aps = findViewById(R.id.etUsuario_Apellidos);
        etUsr_Matricula = findViewById(R.id.etUsuario_Matricula);
        etUsr_Email = findViewById(R.id.etUsuario_Email);
        etUsr_Pwd = findViewById(R.id.etUsuario_Pwd);
        pbar = findViewById(R.id.PBar);

        sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);

        btnRegresar.setOnClickListener(v -> {
            startActivity(new Intent(this, Login.class));
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            this.finish();
        });


        btnRegistrar.setOnClickListener(v -> {
            if (validateData()) {
                pbar.smoothToShow();
                String usr_nombre = Objects.requireNonNull(etUsr_Nombre.getText()).toString();
                String usr_apellidos = Objects.requireNonNull(etUsr_Aps.getText()).toString();
                String usr_correo = Objects.requireNonNull(etUsr_Email.getText()).toString();
                String usr_matricula = Objects.requireNonNull(etUsr_Matricula.getText()).toString();
                String usr_pwd = Objects.requireNonNull(etUsr_Pwd.getText()).toString();

                JsonObject user_data = new JsonObject();

                user_data.addProperty("nombre", usr_nombre);
                user_data.addProperty("apellidos", usr_apellidos);
                user_data.addProperty("email", usr_correo);
                user_data.addProperty("password", usr_pwd);
                user_data.addProperty("matricula_empleado", usr_matricula);

                Call<JsonObject> create_user = sms_service.register(user_data);

                create_user.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toasty.success(Register.this, response.body().get("message").toString()).show();
                            System.out.println("Se ha creado el usuario correctamente \n" + response.body().toString());
                        } else {
                            Toasty.warning(Register.this, getResources().getString(R.string.error_usr_duplicate)).show();
                        }
                        pbar.smoothToHide();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Alert_Dialog.showWarnMessage(Register.this, getString(R.string.header_warning), getString(R.string.request_error))
                                .positiveButton(R.string.aceptar, null, materialDialog -> {
                                    Register.this.recreate();
                                    return Unit.INSTANCE;
                                }).show();
                        System.out.println(t.toString());
                        pbar.smoothToHide();
                    }
                });

            } else {
                Toasty.error(this, getResources().getString(R.string.fields_error)).show();
                pbar.smoothToHide();
            }
        });

    }

    private boolean validateData() {
        return (!Objects.requireNonNull(etUsr_Nombre.getText()).toString().isEmpty() &&
                !Objects.requireNonNull(etUsr_Aps.getText()).toString().isEmpty() &&
                !Objects.requireNonNull(etUsr_Matricula.getText()).toString().isEmpty() &&
                !Objects.requireNonNull(etUsr_Email.getText()).toString().isEmpty() &&
                !Objects.requireNonNull(etUsr_Pwd.getText()).toString().isEmpty());
    }

}

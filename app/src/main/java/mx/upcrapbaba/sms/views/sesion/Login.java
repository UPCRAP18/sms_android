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
import mx.upcrapbaba.sms.API.ApiWeb;
import mx.upcrapbaba.sms.API.Service.SMSService;
import mx.upcrapbaba.sms.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private MaterialEditText etUsuario, etPwd;
    private AVLoadingIndicatorView pbar;
    private SMSService sms_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnReset = findViewById(R.id.btnReset);
        etUsuario = findViewById(R.id.etUsuario_Nombre);
        etPwd = findViewById(R.id.etUsuario_Apellido_Pat);
        pbar = findViewById(R.id.PBar);

        //Se crea el servicio para poder hacer las request
        sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);

        //TODO Validar si ya existe un usuario activo

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            this.finish();
        });

        btnLogin.setOnClickListener(v -> {
            if (validateDataFields()) {
                pbar.smoothToShow();
                String usuario = Objects.requireNonNull(etUsuario.getText()).toString();
                String pwd = Objects.requireNonNull(etPwd.getText()).toString();

                JsonObject credenciales = new JsonObject();

                credenciales.addProperty("email", usuario);
                credenciales.addProperty("password", pwd);

                Call<JsonObject> login_user = sms_service.login(credenciales);

                login_user.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toasty.success(Login.this, response.body().get("message").toString()).show();
                            System.out.println("Se ha iniciado sesion correctamente");
                            //TODO Guardar los datos en la base de datos
                        } else {
                            Toasty.error(Login.this, getResources().getString(R.string.creds_invalidas)).show();
                        }
                        pbar.smoothToHide();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toasty.warning(Login.this, getResources().getString(R.string.request_error)).show();
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

    /**
     * Valida que las cajas de texto no estén vacias
     *
     * @return True si es que no estan vacias
     */
    private boolean validateDataFields() {
        return (!Objects.requireNonNull(etUsuario.getText()).toString().isEmpty() &&
                !Objects.requireNonNull(etPwd.getText()).toString().isEmpty());
    }

}

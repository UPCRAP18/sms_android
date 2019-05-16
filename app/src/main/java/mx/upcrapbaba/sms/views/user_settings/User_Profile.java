package mx.upcrapbaba.sms.views.user_settings;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;

import es.dmoral.toasty.Toasty;
import kotlin.Unit;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.api.Service.SMSService;
import mx.upcrapbaba.sms.extras.Alert_Dialog;
import mx.upcrapbaba.sms.models.User;
import mx.upcrapbaba.sms.sqlite.DBHelper;
import mx.upcrapbaba.sms.views.inicio.Inicio;
import mx.upcrapbaba.sms.views.sesion.Login;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User_Profile extends AppCompatActivity {

    private DBHelper helper;
    private EditText etNombre, etApellidos, etEmail, etMatricula;
    private SMSService sms_service;
    private AVLoadingIndicatorView pbar;
    private ImageView imgPhoto;
    private User usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);

        helper = new DBHelper(this);

        pbar = findViewById(R.id.PBar);

        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etEmail = findViewById(R.id.etEmail);
        etMatricula = findViewById(R.id.etMatricula);
        imgPhoto = findViewById(R.id.imgPhoto);

        setData_User();

        Button btnSave = findViewById(R.id.btnGuardar);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            if(new DBHelper(User_Profile.this).dropUsr()){
                startActivity(new Intent(User_Profile.this, Login.class));
                User_Profile.this.finish();
            }else {
                Toasty.warning(User_Profile.this, "Ha ocurrido un error al cerrar sesion").show();
            }
        });

        Toolbar toolbar = findViewById(R.id.ToolBar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.tab_name_perfil);
        } else {
            System.out.println("Ha ocurrido un error al inicializar la barra de titulo");
            Alert_Dialog.showErrorMessage(this);
        }

        btnSave.setOnClickListener(v -> update_Data());

    }

    private void setData_User() {
        String auth = "Bearer " + helper.getData_Usuario().get(1);
        Call<User> getUserData = sms_service.getUserInfo(auth, helper.getData_Usuario().get(0));

        getUserData.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                pbar.smoothToShow();
                if (response.isSuccessful() && response.body() != null) {
                    usuario = response.body();
                    etNombre.setText(usuario.getNombre());
                    etMatricula.setText(usuario.getMatricula_empleado());
                    etApellidos.setText(usuario.getApellidos());
                    etEmail.setText(usuario.getEmail());
                    String url = new ApiWeb().getBASE_URL_GLITCH() + "/" + usuario.getImagen_perfil();
                    Glide.with(User_Profile.this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(url).into(imgPhoto);
                    pbar.smoothToHide();
                } else {
                    pbar.smoothToHide();
                    System.out.println("Ha ocurrido un error al obtener los datos del usuario " + response.errorBody());
                    Alert_Dialog.showErrorMessage(User_Profile.this);
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                pbar.smoothToHide();
                System.out.println("Ha ocurrido un error en la request para obtener los datos del usuario " + t.getMessage());
                Alert_Dialog.showErrorMessage(User_Profile.this);

            }
        });

    }

    private void update_Data() {
        JsonObject update = new JsonObject();

        update.addProperty("nombre", etNombre.getText().toString());
        update.addProperty("apellidos", etApellidos.getText().toString());
        update.addProperty("email", etEmail.getText().toString());
        String auth = "Bearer " + helper.getData_Usuario().get(1);

        Call<JsonObject> updateData = sms_service.update_data(update, auth, helper.getData_Usuario().get(0));

        updateData.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                pbar.smoothToShow();
                if (response.isSuccessful() && response.body() != null) {
                    Alert_Dialog.showWarnMessage(User_Profile.this, getString(R.string.header_correcto), "Se han actualizado los datos")
                            .positiveButton(R.string.aceptar, null, materialDialog -> {
                                User_Profile.this.recreate();
                                return Unit.INSTANCE;
                            }).show();
                } else {
                    pbar.smoothToHide();
                    System.out.println("Ha ocurrido un error al actualizar los datos \n" + response.errorBody());
                    Alert_Dialog.showErrorMessage(User_Profile.this);
                }
            }

            @Override
            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                pbar.smoothToHide();
                System.out.println("Ha ocurrido un error en la request para actualizar los datos \n" + t.getMessage());
                Alert_Dialog.showErrorMessage(User_Profile.this);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(User_Profile.this, Inicio.class));
        User_Profile.this.finish();
    }
}

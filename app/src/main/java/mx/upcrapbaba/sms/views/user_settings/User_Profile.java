package mx.upcrapbaba.sms.views.user_settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import es.dmoral.toasty.Toasty;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.api.Service.SMSService;
import mx.upcrapbaba.sms.extras.Alert_Dialog;
import mx.upcrapbaba.sms.models.User;
import mx.upcrapbaba.sms.sqlite.DBHelper;
import mx.upcrapbaba.sms.views.sesion.Login;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User_Profile extends AppCompatActivity {

    private static int RESULT_CODE = 1;
    private ImageButton imgEditPhoto;
    private ImageView imgEdit;
    private DBHelper helper;
    private EditText etNombre, etApellidos, etEmail, etMatricula;
    private SMSService sms_service;
    private AVLoadingIndicatorView pbar;
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

        setData_User();

        Button btnSave = findViewById(R.id.btnGuardar);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            if(new DBHelper(User_Profile.this).dropUsr()){
                startActivity(new Intent(User_Profile.this, Login.class));
                User_Profile.this.finish();
                //TODO handle de donde viene
                //Inicio.class.
            }else {
                Toasty.warning(User_Profile.this, "Ha ocurrido un error al cerrar sesion").show();
            }
        });

        imgEdit = findViewById(R.id.imgEdit);
        imgEditPhoto = findViewById(R.id.imgPhoto);

        imgEditPhoto.setOnClickListener(v -> {
            uploadImage();
        });

        imgEdit.setOnClickListener(v -> {
            uploadImage();
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

        btnSave.setOnClickListener(v -> {
            update_Data();
        });

    }

    private void setData_User() {
        String auth = "Bearer " + helper.getData_Usuario().get(1);
        Call<User> getUserData = sms_service.getUserInfo(auth, helper.getData_Usuario().get(0));

        getUserData.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                pbar.smoothToShow();
                if (response.isSuccessful() && response.body() != null) {
                    usuario = response.body();
                    etNombre.setText(usuario.getNombre());
                    etMatricula.setText(usuario.getMatricula_empleado());
                    etApellidos.setText(usuario.getApellidos());
                    etEmail.setText(usuario.getEmail());
                    String url = new ApiWeb().getBASE_URL_GLITCH() + "/" + usuario.getImagen_perfil();
                    Glide.with(User_Profile.this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(url).into(imgEditPhoto);
                    pbar.smoothToHide();

                } else {
                    //TODO HANDLE
                    System.out.println("Ha ocurrido un error al obtener los datos \n" + response.message());
                    pbar.smoothToHide();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("Ha ocurrido un error en la request \n" + t.toString());
                pbar.smoothToHide();
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
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                pbar.smoothToShow();
                if (response.isSuccessful() && response.body() != null) {
                    Toasty.success(User_Profile.this, response.body().get("message").toString()).show();
                    User_Profile.this.recreate();
                    pbar.smoothToHide();
                } else {
                    System.out.println("Ha ocurrido un error al obtener los datos \n" + response.errorBody().toString());
                    pbar.smoothToHide();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pbar.smoothToHide();
            }
        });

    }

    private void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CODE) {
            return;
        } else {
            if (data != null) {
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    //Get image
                    Bitmap newProfilePic = extras.getParcelable("data");

                }
            } else {
                //TODO Handle
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

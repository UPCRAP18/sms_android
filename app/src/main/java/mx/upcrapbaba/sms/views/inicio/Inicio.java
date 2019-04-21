package mx.upcrapbaba.sms.views.inicio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.adaptadores.Alumnos_Adapter;
import mx.upcrapbaba.sms.adaptadores.Spinner_Adapter;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.api.Service.SMSService;
import mx.upcrapbaba.sms.extras.Alert_Dialog;
import mx.upcrapbaba.sms.models.Alumno;
import mx.upcrapbaba.sms.models.Asignatura;
import mx.upcrapbaba.sms.models.Grupo;
import mx.upcrapbaba.sms.models.User;
import mx.upcrapbaba.sms.sqlite.DBHelper;
import mx.upcrapbaba.sms.views.user_settings.User_Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inicio extends AppCompatActivity implements BottomNavigation.OnMenuItemSelectionListener {

    private BottomNavigation nav_bar;
    private AVLoadingIndicatorView pbar;
    private String token, id_usuario;
    private ListView lstAlumnos;
    private LottieAnimationView anim_empty_list;
    private TextView txtError_Message, lblAlumnos;
    private User user_data;
    private ImageView imgUsuario;
    private Spinner spAsignaturas, spGrupos;
    private List<Asignatura> asignaturas = new LinkedList<>();
    private List<Grupo> grupos = new LinkedList<>();
    private List<Alumno> alumnos = new LinkedList<>();

    private List<String> nombre_grupos = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        SMSService sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);
        token = "Bearer " + new DBHelper(this).getData_Usuario().get(1);
        id_usuario = new DBHelper(this).getData_Usuario().get(0);

        getUserInfo(sms_service);

        nav_bar = findViewById(R.id.bottom_nav_bar);

        nav_bar.setSelectedIndex(0, true);

        nav_bar.setMenuItemSelectionListener(this);

        pbar = findViewById(R.id.PBar_Main);

        lstAlumnos = findViewById(R.id.lstAlumnos);

        spAsignaturas = findViewById(R.id.spAsignaturas);
        spGrupos = findViewById(R.id.spGrupos);

        anim_empty_list = findViewById(R.id.empty_list);
        txtError_Message = findViewById(R.id.txtError_Message);
        lblAlumnos = findViewById(R.id.lblAlumnos);

        Toolbar toolbar = findViewById(R.id.ToolBar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.tab_name_inicio);

        } else {
            System.out.println("Ha ocurrido un error al inicializar la barra de titulo");
            Alert_Dialog.showErrorMessage(this);
        }


    }

    @Override
    public void onMenuItemReselect(int i, int i1, boolean b) {
        startActivity(new Intent(this, Inicio.class));
        overridePendingTransition(0, 0);
        this.finish();
    }

    @Override
    public void onMenuItemSelect(int i, int i1, boolean b) {
        nav_bar.setSelectedIndex(i);
    }

    private void getUserInfo(SMSService sms_service) {
        Call<User> user_info = sms_service.getUserInfo(token, id_usuario);

        user_info.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                pbar.smoothToShow();
                if (response.isSuccessful() && response.body() != null) {
                    user_data = response.body();
                    String url = new ApiWeb().getBASE_URL_GLITCH() + "/" + user_data.getImagen_perfil();
                    Glide.with(Inicio.this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(url).into(imgUsuario);
                    asignaturas = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());

                    lblAlumnos.setVisibility(View.GONE);
                    lstAlumnos.setAdapter(new ArrayAdapter<>(Inicio.this, android.R.layout.simple_list_item_1));

                    if (asignaturas.isEmpty()) {
                        anim_empty_list.playAnimation();
                        anim_empty_list.setVisibility(View.VISIBLE);
                        txtError_Message.setText(R.string.err_asignatura);
                        txtError_Message.setVisibility(View.VISIBLE);
                        spAsignaturas.setEnabled(false);
                        pbar.smoothToHide();
                    } else {
                        pbar.smoothToHide();
                        spAsignaturas.setAdapter(new Spinner_Adapter(Inicio.this, R.layout.asignatura_item, asignaturas));
                        setOnSelectedListener();
                    }

                } else {
                    System.out.println(response.errorBody());
                    pbar.smoothToHide();
                    Toasty.warning(Inicio.this, "Ha ocurrido un error").show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println(t.toString());
            }
        });

    }

    private void setOnSelectedListener() {
        spAsignaturas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Asignatura asignatura_seleccionada = asignaturas.get(position);
                grupos = new Gson().fromJson(asignatura_seleccionada.getGrupos(), new TypeToken<List<Grupo>>() {
                }.getType());
                nombre_grupos.clear();
                lblAlumnos.setVisibility(View.GONE);
                lstAlumnos.setAdapter(new ArrayAdapter<>(Inicio.this, android.R.layout.simple_list_item_1));
                spGrupos.setAdapter(new ArrayAdapter<>(Inicio.this, R.layout.custom_spinner, new ArrayList<>()));
                for (Grupo grupo : grupos) {
                    nombre_grupos.add(grupo.getNombre_grupo());
                }

                if (grupos.isEmpty()) {
                    spGrupos.setEnabled(false);
                    anim_empty_list.playAnimation();
                    anim_empty_list.setVisibility(View.VISIBLE);
                    txtError_Message.setText(R.string.err_grupo);
                    txtError_Message.setVisibility(View.VISIBLE);
                } else {
                    spGrupos.setEnabled(true);
                    anim_empty_list.setVisibility(View.GONE);
                    txtError_Message.setVisibility(View.GONE);
                    spGrupos.setAdapter(new ArrayAdapter<>(Inicio.this, R.layout.custom_spinner, nombre_grupos));
                    setAlumnos();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setAlumnos() {
        spGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Grupo grupo_seleccionado = grupos.get(position);

                alumnos = new Gson().fromJson(grupo_seleccionado.getAlumnos(), new TypeToken<List<Alumno>>() {
                }.getType());

                if (alumnos.isEmpty()) {
                    anim_empty_list.playAnimation();
                    anim_empty_list.setVisibility(View.VISIBLE);
                    txtError_Message.setText(R.string.err_alumnos);
                    txtError_Message.setVisibility(View.VISIBLE);
                    lblAlumnos.setVisibility(View.GONE);
                    lstAlumnos.setAdapter(new ArrayAdapter<>(Inicio.this, android.R.layout.simple_list_item_1));
                } else {
                    anim_empty_list.setVisibility(View.GONE);
                    txtError_Message.setVisibility(View.GONE);
                    lblAlumnos.setVisibility(View.VISIBLE);
                    lstAlumnos.setAdapter(new Alumnos_Adapter(alumnos, Inicio.this));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        MenuItem itemIconMenuBar = menu.findItem(R.id.Carrito_menu);
        View user_photo = itemIconMenuBar.getActionView();

        if (user_photo != null) {
            imgUsuario = user_photo.findViewById(R.id.imgUser_Photo);

            user_photo.setOnClickListener(v -> {
                startActivity(new Intent(Inicio.this, User_Profile.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            });


        }

        return true;
    }

}


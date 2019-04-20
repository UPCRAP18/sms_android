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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.adaptadores.Alumnos_Adapter;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.api.Service.SMSService;
import mx.upcrapbaba.sms.extras.Alert_Dialog;
import mx.upcrapbaba.sms.models.Alumno;
import mx.upcrapbaba.sms.models.Asignaturas;
import mx.upcrapbaba.sms.models.Grupos;
import mx.upcrapbaba.sms.models.User;
import mx.upcrapbaba.sms.sqlite.DBHelper;
import mx.upcrapbaba.sms.views.user_settings.User_Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inicio extends AppCompatActivity implements BottomNavigation.OnMenuItemSelectionListener {

    private BottomNavigation nav_bar;
    private Spinner spAsignaturas, spGrupos;
    private List<Asignaturas> asignaturas = new LinkedList<>();
    private List<String> nombre_asignaturas = new LinkedList<>();
    private List<String> nombre_grupo = new LinkedList<>();
    private AVLoadingIndicatorView pbar;
    private String token, id_usuario;
    private ListView lstAlumnos;
    private LottieAnimationView anim_empty_list;
    private TextView txtError_Message, lblAlumnos;
    private ArrayList<Alumno> alumnos_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        SMSService sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);
        token = "Bearer " + new DBHelper(this).getData_Usuario().get(1);
        id_usuario = new DBHelper(this).getData_Usuario().get(0);

        nav_bar = findViewById(R.id.bottom_nav_bar);

        nav_bar.setSelectedIndex(0, true);

        nav_bar.setMenuItemSelectionListener(this);

        spAsignaturas = findViewById(R.id.spAsignaturas);
        spGrupos = findViewById(R.id.spGrupos);

        pbar = findViewById(R.id.PBar_Main);

        lstAlumnos = findViewById(R.id.lstAlumnos);

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


        setSpinnerItems(sms_service);


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

    private void setSpinnerItems(SMSService sms_service) {
        Call<User> getUserInfo = sms_service.getUserInfo(token, id_usuario);

        getUserInfo.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                pbar.smoothToShow();
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getMaterias().length != 0) {
                        for (String materia : response.body().getMaterias()) {
                            Call<Asignaturas> getAsignaturaInfo = sms_service.getAsignaturas(token, materia);

                            getAsignaturaInfo.enqueue(new Callback<Asignaturas>() {
                                @Override
                                public void onResponse(Call<Asignaturas> call, Response<Asignaturas> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        asignaturas.add(response.body());
                                        nombre_asignaturas.add(response.body().getNombre_materia());
                                        spAsignaturas.setAdapter(new ArrayAdapter<>(Inicio.this, R.layout.custom_spinner_item, nombre_asignaturas));
                                        pbar.smoothToHide();
                                        setItemsGrupos(sms_service);
                                    } else {
                                        System.out.println("Lista vacia");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Asignaturas> call, Throwable t) {
                                    System.out.println("Ha ocurrido un error en la request");
                                    pbar.smoothToHide();
                                }
                            });
                        }
                    } else {
                        anim_empty_list.playAnimation();
                        anim_empty_list.setVisibility(View.VISIBLE);
                        txtError_Message.setText(R.string.err_asignatura);
                        txtError_Message.setVisibility(View.VISIBLE);
                        spGrupos.setEnabled(true);
                        spAsignaturas.setEnabled(false);
                        pbar.smoothToHide();
                    }
                } else {
                    //TODO Handle This shit
                    Toasty.error(Inicio.this, "Ha ocurrido un error \n" + response.errorBody()).show();
                    pbar.smoothToHide();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //TODO Handle This shit
                Toasty.error(Inicio.this, "Ha ocurrido un error \n" + t.toString()).show();
                pbar.smoothToHide();
            }
        });
    }

    private void setItemsGrupos(SMSService smsService) {
        spAsignaturas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Asignaturas asignatura_actual = asignaturas.get(position);

                nombre_grupo.clear();

                nombre_grupo.addAll(Arrays.asList(asignatura_actual.getGrupos()));

                if (nombre_grupo.isEmpty()) {
                    spGrupos.setAdapter(new ArrayAdapter<>(Inicio.this, R.layout.custom_spinner_item, new ArrayList<>()));
                    //Toasty.warning(Inicio.this, "No se han encontrado grupos").show();
                    spGrupos.setEnabled(false);
                    anim_empty_list.playAnimation();
                    anim_empty_list.setVisibility(View.VISIBLE);
                    txtError_Message.setText(R.string.err_grupo);
                    txtError_Message.setVisibility(View.VISIBLE);
                } else {
                    spGrupos.setEnabled(true);
                    spGrupos.setAdapter(new ArrayAdapter<>(Inicio.this, R.layout.custom_spinner_item, nombre_grupo));
                    setItemGroupsListener(smsService);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setItemGroupsListener(SMSService smsService) {
        spGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Call<Grupos> getGroupInfo = smsService.getInfoGrupo(token, nombre_grupo.get(position));

                getGroupInfo.enqueue(new Callback<Grupos>() {
                    @Override
                    public void onResponse(Call<Grupos> call, Response<Grupos> response) {
                        pbar.smoothToShow();
                        if (response.isSuccessful() && response.body() != null) {
                            Grupos grupo = response.body().getAlumnos();

                            if (!grupo.getAlumnos_List().isEmpty()) {

                                for (String alumno : grupo.getAlumnos_List()) {
                                    Call<Alumno> alumno_info = smsService.getAlumnoInfo(token, alumno);

                                    alumno_info.enqueue(new Callback<Alumno>() {
                                        @Override
                                        public void onResponse(Call<Alumno> call, Response<Alumno> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                alumnos_list.add(response.body());
                                                Alumnos_Adapter adapter = new Alumnos_Adapter(alumnos_list, Inicio.this);
                                                lstAlumnos.setAdapter(adapter);
                                                lblAlumnos.setVisibility(View.VISIBLE);
                                                pbar.smoothToHide();

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Alumno> call, Throwable t) {
                                            pbar.smoothToHide();
                                        }
                                    });

                                }

                            } else {
                                anim_empty_list.playAnimation();
                                anim_empty_list.setVisibility(View.VISIBLE);
                                txtError_Message.setText(R.string.err_alumnos);
                                txtError_Message.setVisibility(View.VISIBLE);
                                pbar.smoothToHide();
                            }

                        } else {
                            pbar.smoothToHide();
                            Toasty.warning(Inicio.this, response.errorBody().toString()).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Grupos> call, Throwable t) {
                        pbar.smoothToHide();
                        Toasty.error(Inicio.this, "Ha ocurrido un error " + t.toString()).show();
                    }
                });

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
            ImageView imgUsuario = user_photo.findViewById(R.id.imgUser_Photo);

            user_photo.setOnClickListener(v -> {
                startActivity(new Intent(Inicio.this, User_Profile.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            });


        }

        return true;
    }

}

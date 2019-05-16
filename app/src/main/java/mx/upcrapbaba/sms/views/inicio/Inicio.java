package mx.upcrapbaba.sms.views.inicio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import kotlin.Unit;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.adaptadores.listviews.Alumnos_GeneralList_Adapter;
import mx.upcrapbaba.sms.adaptadores.listviews.Calificaciones_Adapter;
import mx.upcrapbaba.sms.adaptadores.spinners.Asignaturas_General_Adapter;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.api.Service.SMSService;
import mx.upcrapbaba.sms.extras.Alert_Dialog;
import mx.upcrapbaba.sms.extras.NetworkStatus;
import mx.upcrapbaba.sms.models.Alumno;
import mx.upcrapbaba.sms.models.Asignatura;
import mx.upcrapbaba.sms.models.Calificacion;
import mx.upcrapbaba.sms.models.Grupo;
import mx.upcrapbaba.sms.models.User;
import mx.upcrapbaba.sms.sqlite.DBHelper;
import mx.upcrapbaba.sms.views.personalizacion.Add_Edit_Alumnos;
import mx.upcrapbaba.sms.views.personalizacion.Add_Edit_Asignaturas;
import mx.upcrapbaba.sms.views.personalizacion.Add_Edit_Grupos;
import mx.upcrapbaba.sms.views.user_settings.User_Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inicio extends AppCompatActivity implements BottomNavigation.OnMenuItemSelectionListener, Alumnos_GeneralList_Adapter.FilterStudentsAdapterListener {

    private AVLoadingIndicatorView pbar;
    private String token, id_usuario;
    private ListView lstAlumnos;
    private LottieAnimationView anim_empty_list;
    private TextView txtError_Message;
    private User user_data;
    private ImageView imgUsuario;
    private Spinner spAsignaturas, spGrupos;
    private List<Asignatura> asignaturas = new LinkedList<>();
    private List<Grupo> grupos = new LinkedList<>();
    private List<Alumno> alumnos = new LinkedList<>();
    private List<String> nombre_grupos = new LinkedList<>();
    private int RESULT_POPUP = 0;
    private LinearLayout layAlumnos;
    private Alumnos_GeneralList_Adapter alumnos_adapter;
    private Calificaciones_Adapter calificaciones_adapter;
    private MaterialSearchBar mSearchBar;
    private View popupCalificaciones;
    private Asignatura asignatura_seleccionada;
    private Grupo grupo_seleccionado;
    private SMSService sms_service;
    private List<Calificacion> calificaciones = new LinkedList<>();

    /**
     * Comprueba el estado de la red del telefono
     * Para mas informacion ver:
     * {@link NetworkStatus#getTypeConnection()}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialDialog mDialog = new MaterialDialog(this);
        mDialog.title(R.string.header_warning, null);
        mDialog.cancelOnTouchOutside(false);

        if (new NetworkStatus(this).getTypeConnection() == 0) {
            mDialog.message(R.string.no_Net, null, false, 1);
            mDialog.negativeButton(R.string.reintentar, null, materialDialog -> {
                startActivity(new Intent(Inicio.this, Inicio.class));
                this.overridePendingTransition(0, 0);
                this.finish();
                return Unit.INSTANCE;
            });
            mDialog.show();
        } else {
            loadDataContent();
        }

    }

    /**
     * Una vez que se ha evaluado la conexion a internet, si es correcta, se cargan los datos y el layout
     */
    private void loadDataContent() {
        setContentView(R.layout.activity_inicio);

        sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);
        token = "Bearer " + new DBHelper(this).getData_Usuario().get(1);
        id_usuario = new DBHelper(this).getData_Usuario().get(0);

        getUserInfo(sms_service);

        BottomNavigation nav_bar = findViewById(R.id.bottom_nav_bar);

        nav_bar.setDefaultSelectedIndex(0);

        nav_bar.setMenuItemSelectionListener(this);

        pbar = findViewById(R.id.PBar_Main);

        lstAlumnos = findViewById(R.id.lstAlumnos);

        spAsignaturas = findViewById(R.id.spAsignaturas);
        spGrupos = findViewById(R.id.spGrupos_Edit);

        anim_empty_list = findViewById(R.id.empty_list);
        txtError_Message = findViewById(R.id.txtError_Message);
        layAlumnos = findViewById(R.id.layAlumnos);
        mSearchBar = findViewById(R.id.searchBarAlumnos);
        popupCalificaciones = findViewById(R.id.popupCalificaciones);

        FloatingActionButton fab_Add_Alumno = findViewById(R.id.fabAddAlumno);
        FloatingActionButton fab_Add_Asignatura = findViewById(R.id.fabAddAsignatura);
        FloatingActionButton fab_Add_Grupo = findViewById(R.id.fabAddGrupo);


        fab_Add_Asignatura.setOnClickListener(v -> startActivityForResult(new Intent(Inicio.this, Add_Edit_Asignaturas.class).putExtra("SELECCIONADO", "Asignaturas"), RESULT_POPUP));

        fab_Add_Alumno.setOnClickListener(v -> startActivityForResult(new Intent(Inicio.this, Add_Edit_Alumnos.class).putExtra("SELECCIONADO", "Alumnos"), RESULT_POPUP));

        fab_Add_Grupo.setOnClickListener(v -> startActivityForResult(new Intent(Inicio.this, Add_Edit_Grupos.class).putExtra("SELECCIONADO", "Grupos"), RESULT_POPUP));

        Toolbar toolbar = findViewById(R.id.ToolBar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.tab_name_inicio);

        } else {
            System.out.println("Ha ocurrido un error al inicializar la barra de titulo");
            Alert_Dialog.showErrorMessage(this);
        }


        mSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (alumnos_adapter != null) {
                    List<Alumno> filteredList = alumnos_adapter.getfilterData(s.toString());
                    if (filteredList.size() >= 1) {
                        Alumnos_GeneralList_Adapter filterResults =
                                new Alumnos_GeneralList_Adapter(filteredList, Inicio.this, Inicio.this);
                        lstAlumnos.setAdapter(filterResults);
                    } else {
                        lstAlumnos.setAdapter(new ArrayAdapter<>(Inicio.this, android.R.layout.simple_list_item_1, new ArrayList<>()));
                    }
                } else {
                    Inicio.this.recreate();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * Actualiza la interfaz al regresar de un intent, ya que actualicé los datos en la seccion de edicion
     *
     * @param requestCode --> Valor que pido
     * @param resultCode  --> Valor de resultado
     * @param data        --> Datos opcionales que paso
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.recreate();
    }

    /**
     * Recarga la pagina cuando se es reseleccionado un boton de la barra de navegacion inferior
     *
     * @param i  --> Indice previo
     * @param i1 --> Nuevo indice de seleccion
     * @param b  --> Animado
     */
    @Override
    public void onMenuItemReselect(int i, int i1, boolean b) {
        Inicio.this.recreate();
    }

    /**
     * Navegacion de la barra inferior
     *
     * @param i  --> Indice previamente seleccionado
     * @param i1 --> Indice seleccionado
     * @param b  --> Animacion (Cierto o falso)
     */
    @Override
    public void onMenuItemSelect(int i, int i1, boolean b) {
        switch (i1) {
            case 1:
                startActivity(new Intent(Inicio.this, Estadisticas.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                this.finish();
                break;
            case 2:
                startActivity(new Intent(Inicio.this, Information.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                this.finish();
                break;
        }
    }

    /**
     * Obtiene los datos del servidor mediante una request.
     * Los datos que obtiene los guarda en el modelo y empieza a actualizar la UI con los valores
     *
     * @param sms_service --> Servicio estatico de la interfaz para realizar las peticiones
     */
    private void getUserInfo(SMSService sms_service) {
        Call<User> user_info = sms_service.getUserInfo(token, id_usuario);

        user_info.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                pbar.smoothToShow();
                if (response.isSuccessful() && response.body() != null) {
                    user_data = response.body();
                    String url = new ApiWeb().getBASE_URL_GLITCH() + "/" + user_data.getImagen_perfil();
                    Glide.with(Inicio.this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(url).into(imgUsuario);
                    asignaturas = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());

                    lstAlumnos.setAdapter(new ArrayAdapter<>(Inicio.this, android.R.layout.simple_list_item_1));

                    if (asignaturas.isEmpty()) {
                        layAlumnos.setVisibility(View.INVISIBLE);
                        anim_empty_list.playAnimation();
                        anim_empty_list.setVisibility(View.VISIBLE);
                        txtError_Message.setText(R.string.err_asignatura);
                        txtError_Message.setVisibility(View.VISIBLE);
                        spAsignaturas.setEnabled(false);
                        pbar.smoothToHide();

                    } else {
                        layAlumnos.setVisibility(View.VISIBLE);
                        spAsignaturas.setAdapter(new Asignaturas_General_Adapter(Inicio.this, R.layout.asignatura_item, asignaturas));
                        setOnSelectedListener();
                    }

                } else {
                    System.out.println(response.errorBody());
                    pbar.smoothToHide();
                    Alert_Dialog.showErrorMessage(Inicio.this);
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                pbar.smoothToHide();
                Alert_Dialog.showErrorMessage(Inicio.this);
                System.out.println(t.toString());
            }
        });

    }

    /**
     * Evento onItemSelectedListener del spinner de Asignaturas
     * Carga los grupos que tiene esa asignatura
     */
    private void setOnSelectedListener() {
        spAsignaturas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                asignatura_seleccionada = asignaturas.get(position);
                grupos = new Gson().fromJson(asignatura_seleccionada.getGrupos(), new TypeToken<List<Grupo>>() {
                }.getType());
                nombre_grupos.clear();

                lstAlumnos.setAdapter(new ArrayAdapter<>(Inicio.this, android.R.layout.simple_list_item_1));
                spGrupos.setAdapter(new ArrayAdapter<>(Inicio.this, R.layout.custom_spinner, new ArrayList<>()));
                for (Grupo grupo : grupos) {
                    nombre_grupos.add(grupo.getNombre_grupo());
                }

                if (grupos.isEmpty()) {
                    layAlumnos.setVisibility(View.INVISIBLE);
                    spGrupos.setEnabled(false);
                    anim_empty_list.playAnimation();
                    anim_empty_list.setVisibility(View.VISIBLE);
                    txtError_Message.setText(R.string.err_grupo);
                    txtError_Message.setVisibility(View.VISIBLE);
                    pbar.smoothToHide();
                } else {
                    layAlumnos.setVisibility(View.VISIBLE);
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

    /**
     * Actualiza la lista de alumnos con los valores que tiene el grupo
     */
    private void setAlumnos() {
        spGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                grupo_seleccionado = grupos.get(position);

                alumnos = new Gson().fromJson(grupo_seleccionado.getAlumnos(), new TypeToken<List<Alumno>>() {
                }.getType());

                if (alumnos.isEmpty()) {
                    anim_empty_list.playAnimation();
                    anim_empty_list.setVisibility(View.VISIBLE);
                    txtError_Message.setText(R.string.err_alumnos);
                    txtError_Message.setVisibility(View.VISIBLE);
                    layAlumnos.setVisibility(View.INVISIBLE);
                    lstAlumnos.setAdapter(new ArrayAdapter<>(Inicio.this, android.R.layout.simple_list_item_1));
                    pbar.smoothToHide();
                } else {
                    anim_empty_list.setVisibility(View.GONE);
                    txtError_Message.setVisibility(View.GONE);
                    layAlumnos.setVisibility(View.VISIBLE);
                    alumnos_adapter = new Alumnos_GeneralList_Adapter(alumnos, Inicio.this, Inicio.this);
                    lstAlumnos.setAdapter(alumnos_adapter);
                    pbar.smoothToHide();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Infla/Carga el layout de la barra de titulo a manera de que soporte el imageview
     * con la imagen de perfil del usuario
     *
     * @param menu --> Menu que se está sobreescribiendo
     * @return --> True si es que está inflado
     */
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
                Inicio.this.finish();
            });


        }

        return true;
    }

    /**
     * Listener al seleccionar un alumno del listview de alumnos en el grupo
     *
     * @param alumno_seleccionado --> Devuelve el alumno seleccionado del adaptador
     */
    @Override
    public void onStudentSelected(Alumno alumno_seleccionado) {
        Button btnClose_Calif = popupCalificaciones.findViewById(R.id.btnClose_Calificaciones);
        ListView lstCalificaciones = popupCalificaciones.findViewById(R.id.lstCalificaciones);
        Button btnSave = popupCalificaciones.findViewById(R.id.btnSave_Calificaciones);
        RadioGroup rgParciales = popupCalificaciones.findViewById(R.id.rgParciales);
        RadioButton rbPrimer = popupCalificaciones.findViewById(R.id.rbPrimer);
        RadioButton rbSegundo = popupCalificaciones.findViewById(R.id.rbSegundo);
        RadioButton rbTercer = popupCalificaciones.findViewById(R.id.rbTercer);
        TextView txtPromedio_Parcial = popupCalificaciones.findViewById(R.id.txtPromedioParcial);
        TextView txtPromedio_General = popupCalificaciones.findViewById(R.id.txtPromedioGeneral);
        calificaciones.clear();
        calificaciones = new Gson().fromJson(alumno_seleccionado.getCalificaciones(), new TypeToken<List<Calificacion>>() {
        }.getType());
        List<Calificacion> calificacion_refinada = new LinkedList<>();

        ArrayList<Double> promedio_primer_parcial = new ArrayList<>();
        ArrayList<Double> promedio_segundo_parcial = new ArrayList<>();
        ArrayList<Double> promedio_tercer_parcial = new ArrayList<>();
        ArrayList<Double> promedio_general = new ArrayList<>();
        int cant_np = 0;


        promedio_general.add(0, 0.0);

        double promedio_final = 0;
        for (Calificacion calificacion : calificaciones) {
            if (!calificacion.getObtenido().equals("NP")) {
                double obtenido = Double.parseDouble(calificacion.getObtenido());
                double valor = Double.parseDouble(calificacion.getValor_actividad());
                promedio_final += ((obtenido * valor) / 100);
            } else {
                cant_np++;
            }
        }

        if (cant_np < calificaciones.size()) {
            promedio_final /= 3;
            promedio_general.add(0, promedio_final);
            txtPromedio_General.setText(getResources().getString(R.string.lblPromGeneral, String.format(Locale.getDefault(), "%.2f", promedio_general.get(0))));
        } else {
            txtPromedio_General.setText(getResources().getString(R.string.lblPromGeneral, "NP"));
            alumno_seleccionado.setPromedio("NP");
        }

        rgParciales.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.rbPrimer:
                    promedio_primer_parcial.add(0, 0.0);
                    calificacion_refinada.clear();
                    int cant_np_primer = 0;
                    for (Calificacion calificacion : calificaciones) {
                        if (calificacion.getParcial().equals("Primer Parcial")) {
                            calificacion_refinada.add(calificacion);
                            if (!calificacion.getObtenido().equals("NP")) {
                                double calificacion_final = promedio_primer_parcial.get(0);
                                double obtenido = Double.parseDouble(calificacion.getObtenido());
                                double valor = Double.parseDouble(calificacion.getValor_actividad());
                                calificacion_final += obtenido * (valor / 100);
                                promedio_primer_parcial.add(0, calificacion_final);
                            } else {
                                cant_np_primer ++;
                            }
                        }
                    }
                    if (cant_np_primer < calificacion_refinada.size()) {
                        double promedio = promedio_primer_parcial.get(0);
                        txtPromedio_Parcial.setText(getResources().getString(R.string.lblPromParc, String.format(Locale.getDefault(), "%.2f", promedio)));
                    } else {
                        txtPromedio_Parcial.setText(getResources().getString(R.string.lblPromParc,"NP"));
                    }
                    break;
                case R.id.rbSegundo:
                    promedio_segundo_parcial.add(0, 0.0);
                    calificacion_refinada.clear();
                    int cant_np_segundo = 0;
                    for (Calificacion calificacion : calificaciones) {
                        if (calificacion.getParcial().equals("Segundo Parcial")) {
                            calificacion_refinada.add(calificacion);
                            if (!calificacion.getObtenido().equals("NP")) {
                                double calificacion_final = promedio_segundo_parcial.get(0);
                                double obtenido = Double.parseDouble(calificacion.getObtenido());
                                double valor = Double.parseDouble(calificacion.getValor_actividad());
                                calificacion_final += obtenido * (valor / 100);
                                promedio_segundo_parcial.add(0, calificacion_final);
                            } else {
                                cant_np_segundo++;
                            }
                        }
                    }
                    if (cant_np_segundo < calificacion_refinada.size()) {
                        double promedio = promedio_segundo_parcial.get(0);
                        txtPromedio_Parcial.setText(getResources().getString(R.string.lblPromParc, String.format(Locale.getDefault(), "%.2f", promedio)));
                    } else {
                        txtPromedio_Parcial.setText(getResources().getString(R.string.lblPromParc,"NP"));
                    }
                    break;
                case R.id.rbTercer:
                    promedio_tercer_parcial.add(0, 0.0);
                    calificacion_refinada.clear();
                    int cant_np_tercer = 0;
                    for (Calificacion calificacion : calificaciones) {
                        if (calificacion.getParcial().equals("Tercer Parcial")) {
                            calificacion_refinada.add(calificacion);
                            if (!calificacion.getObtenido().equals("NP")) {
                                double calificacion_final = promedio_tercer_parcial.get(0);
                                double obtenido = Double.parseDouble(calificacion.getObtenido());
                                double valor = Double.parseDouble(calificacion.getValor_actividad());
                                calificacion_final += obtenido * (valor / 100);
                                promedio_tercer_parcial.add(0, calificacion_final);
                            } else {
                                cant_np_tercer++;
                            }
                        }
                    }
                    if (cant_np_tercer < calificacion_refinada.size()) {
                        double promedio = promedio_tercer_parcial.get(0);
                        txtPromedio_Parcial.setText(getResources().getString(R.string.lblPromParc, String.format(Locale.getDefault(), "%.2f", promedio)));
                    } else {
                        txtPromedio_Parcial.setText(getResources().getString(R.string.lblPromParc,"NP"));
                    }
                    break;
            }


            if (!calificacion_refinada.isEmpty()) {
                txtPromedio_General.setVisibility(View.VISIBLE);
                txtPromedio_Parcial.setVisibility(View.VISIBLE);

                calificaciones_adapter = new Calificaciones_Adapter(Inicio.this, calificacion_refinada);

                lstCalificaciones.setAdapter(calificaciones_adapter);

            } else {
                lstCalificaciones.setAdapter(new ArrayAdapter<>(Inicio.this, android.R.layout.simple_list_item_1, new ArrayList<>()));
                txtPromedio_General.setVisibility(View.GONE);
                txtPromedio_Parcial.setVisibility(View.GONE);
            }
        });


        popupCalificaciones.setVisibility(View.VISIBLE);

        btnSave.setOnClickListener(v -> {
            int index = alumnos.indexOf(alumno_seleccionado);
            //actualizacion de alumnos
            alumnos.remove(index);
            calificaciones.removeAll(calificacion_refinada);
            calificaciones.addAll(calificaciones_adapter.getDataSet());
            alumno_seleccionado.setCalificaciones((JsonArray) new Gson().toJsonTree(calificaciones, new TypeToken<List<Calificacion>>() {
            }.getType()));
            int cant_tot_np = 0;
            double promedio_final_global = 0;
            for (Calificacion calificacion : calificaciones) {
                if (calificacion.getObtenido().equals("NP")) {
                    cant_tot_np++;
                } else {
                    double obtenido = Double.parseDouble(calificacion.getObtenido());
                    double valor = Double.parseDouble(calificacion.getValor_actividad());
                    promedio_final_global += ((obtenido * valor) / 100);
                }
            }
            if (cant_tot_np < calificaciones.size()) {
                promedio_final_global /= 3;
                alumno_seleccionado.setPromedio(String.format(Locale.getDefault(), "%.2f", promedio_final_global));
            } else {
                alumno_seleccionado.setPromedio("NP");
            }


            alumnos.add(index, alumno_seleccionado);
            //Actualizacion de grupo
            index = grupos.indexOf(grupo_seleccionado);
            grupos.remove(index);
            grupo_seleccionado.setAlumnos((JsonArray) new Gson().toJsonTree(alumnos, new TypeToken<List<Alumno>>() {
            }.getType()));
            grupos.add(index, grupo_seleccionado);
            //Actualizacion de asignatura
            index = asignaturas.indexOf(asignatura_seleccionada);
            asignaturas.remove(index);
            asignatura_seleccionada.setGrupos((JsonArray) new Gson().toJsonTree(grupos, new TypeToken<List<Grupo>>() {
            }.getType()));
            asignaturas.add(index, asignatura_seleccionada);
            //Actualizacion del usuario

            JsonObject data_Usuario = new JsonObject();

            JsonArray asignaturas_array = (JsonArray) new Gson().toJsonTree(asignaturas,
                    new TypeToken<List<Asignatura>>() {
                    }.getType());

            data_Usuario.add("materias", asignaturas_array);

            sms_service.update_data(data_Usuario, token, id_usuario).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Alert_Dialog.showWarnMessage(Inicio.this, "¡Correcto!", "Se han actualizado los datos")
                                .positiveButton(R.string.aceptar, null, materialDialog -> {
                                    onStudentSelected(alumno_seleccionado);
                                    return Unit.INSTANCE;
                                }).show();
                    } else {
                        System.out.println(response.errorBody());
                        Alert_Dialog.showErrorMessage(Inicio.this);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                    System.out.println(t.getMessage());
                    Alert_Dialog.showErrorMessage(Inicio.this);
                }
            });

        });

        btnClose_Calif.setOnClickListener(v -> {
            popupCalificaciones.setVisibility(View.GONE);
            rgParciales.clearCheck();
            lstCalificaciones.setAdapter(new ArrayAdapter<>(Inicio.this, android.R.layout.simple_list_item_1, new ArrayList<>()));
            calificaciones.clear();
            calificacion_refinada.clear();
            rgParciales.setSelected(false);
            rbPrimer.setSelected(false);
            rbSegundo.setSelected(false);
            rbTercer.setSelected(false);
            txtPromedio_General.setVisibility(View.INVISIBLE);
            txtPromedio_Parcial.setVisibility(View.INVISIBLE);
        });

    }


}


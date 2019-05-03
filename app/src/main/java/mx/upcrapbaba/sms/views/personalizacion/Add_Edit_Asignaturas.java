package mx.upcrapbaba.sms.views.personalizacion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import kotlin.Unit;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.adaptadores.listviews.Asignaturas_Edit_Adapter;
import mx.upcrapbaba.sms.adaptadores.listviews.Grupos_Edit_Adapter;
import mx.upcrapbaba.sms.adaptadores.spinners.Asignaturas_General_Adapter;
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

public class Add_Edit_Asignaturas extends AppCompatActivity implements Grupos_Edit_Adapter.GroupListener, Asignaturas_Edit_Adapter.AsignaturaListener {

    private String SELECCIONADO = "", token = "";
    private User user_data;
    private List<Asignatura> asignaturas_original = new LinkedList<>(), asignaturas = new LinkedList<>();
    private List<Grupo> grupos = new LinkedList<>(), grupos_to_add = new LinkedList<>();
    private List<String> nombre_asignatura_grupo = new LinkedList<>();
    private EditText etNombre_Asig, etCod_Asig;
    private Spinner spAsignaturas;
    private ImageView imgAsignatura;
    private Button btnSeleccionar_Asignatura;
    private int REQUEST_GET_SINGLE_FILE = 1;
    private boolean isForUpdate = false, isDeletedAsign = false;
    private ListView lstGrupos, lstAsignaturas;
    private Asignatura asignatura_seleccionada;
    private TextView txtTipo_Grupo;
    private SMSService sms_service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add_edit_asignaturas);

        sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);
        token = "Bearer " + new DBHelper(this).getData_Usuario().get(1);
        String id_usuario = new DBHelper(this).getData_Usuario().get(0);

        if (getIntent().getExtras() != null) {
            SELECCIONADO = getIntent().getStringExtra("SELECCIONADO");
        } else {
            Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                    .positiveButton(R.string.aceptar, null, materialDialog -> {
                        startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                        Add_Edit_Asignaturas.this.finish();
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
            Alert_Dialog.showErrorMessage(Add_Edit_Asignaturas.this);
        }

        etNombre_Asig = findViewById(R.id.etAsig_Edit_Nombre);
        etCod_Asig = findViewById(R.id.etAsig_Edit_Cod);
        imgAsignatura = findViewById(R.id.imgAsig_Edit);
        spAsignaturas = findViewById(R.id.spAsignaturas_Edit);
        lstGrupos = findViewById(R.id.lstGrupos);
        txtTipo_Grupo = findViewById(R.id.txtTipo_Grupos);
        lstAsignaturas = findViewById(R.id.lstAsignaturas_Online);

        Button btnSave = findViewById(R.id.btnGuardar);
        Button btnEliminar_Grupo = findViewById(R.id.btnEliminar_Grupo);
        Button btnEliminar_Asignatura = findViewById(R.id.btnEliminar_Asignatura);

        btnEliminar_Grupo.setOnClickListener(v -> {
            if (asignatura_seleccionada != null) {
                if (!grupos_to_add.isEmpty()) {
                    DeleteGroup(grupos_to_add);
                    grupos_to_add.clear();
                } else {
                    Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.warning_notSel_Grupo)).show();
                }
            } else {
                grupos_to_add.clear();
                Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.warning_notSel_Asignatura)).show();
            }
        });

        btnEliminar_Asignatura.setOnClickListener(v -> {
            if (asignatura_seleccionada != null) {
                DeleteAsignatura(asignatura_seleccionada);
                grupos_to_add.clear();
            } else {
                grupos_to_add.clear();
                Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.warning_notSel_Asignatura_Del)).show();
            }
        });

        btnSeleccionar_Asignatura = findViewById(R.id.btnSelect_Grupo_Edit);

        sms_service.getAllAsignaturas(token).enqueue(new Callback<List<Asignatura>>() {
            @Override
            public void onResponse(@NotNull Call<List<Asignatura>> call, @NotNull Response<List<Asignatura>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lstAsignaturas.setAdapter(new Asignaturas_Edit_Adapter(Add_Edit_Asignaturas.this, response.body(), Add_Edit_Asignaturas.this));
                } else {
                    Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                            .positiveButton(R.string.aceptar, null, materialDialog -> {
                                startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                                Add_Edit_Asignaturas.this.finish();
                                return Unit.INSTANCE;
                            }).show();
                    System.out.println("Error al obtener las asignaturas del servidor \n" + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Asignatura>> call, @NotNull Throwable t) {
                Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                        .positiveButton(R.string.aceptar, null, materialDialog -> {
                            startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                            Add_Edit_Asignaturas.this.finish();
                            return Unit.INSTANCE;
                        }).show();
                System.out.println("Error en la request para obtener las asignaturas del servidor \n" + t.getMessage());
            }
        });

        sms_service.getUserInfo(token, id_usuario).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user_data = response.body();

                    asignaturas = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());

                    asignaturas_original = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());

                    getGrupos_General();

                    asignaturas.add(new Asignatura("", "Agregar nueva asignatura", "", new JsonArray()));

                    spAsignaturas.setAdapter(new Asignaturas_General_Adapter(Add_Edit_Asignaturas.this, R.layout.asignatura_item, asignaturas));

                    btnSeleccionar_Asignatura.setOnClickListener(v -> {
                        if (spAsignaturas.getSelectedItemPosition() != (asignaturas.size() - 1)) {
                            isForUpdate = true;
                            loadDataAsignatura(asignaturas.get(spAsignaturas.getSelectedItemPosition()));
                        } else {
                            asignatura_seleccionada = null;
                            isForUpdate = false;
                            etCod_Asig.getText().clear();
                            etNombre_Asig.getText().clear();
                            imgAsignatura.setImageDrawable(getDrawable(R.drawable.materia_holder));
                            getGrupos_General();
                        }
                    });

                    //TODO FIX THISSSS
                    imgAsignatura.setOnClickListener(v -> {
                        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_FILE);*/
                    });

                } else {
                    Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                            .positiveButton(R.string.aceptar, null, materialDialog -> {
                                startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                                Add_Edit_Asignaturas.this.finish();
                                return Unit.INSTANCE;
                            }).show();
                    System.out.println("Error al obtener la informacion de usuario del servidor \n" + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                        .positiveButton(R.string.aceptar, null, materialDialog -> {
                            startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                            Add_Edit_Asignaturas.this.finish();
                            return Unit.INSTANCE;
                        }).show();
                System.out.println("Error en la request para obtener la informacion de usuario del servidor \n" + t.getMessage());
            }
        });

        btnSave.setOnClickListener(v -> {
            if (isForUpdate) {
                if (validateFiedlsAsignaturas()) {
                    String codigo_mat = etCod_Asig.getText().toString();
                    String nombre_mat = etNombre_Asig.getText().toString();

                    if (asignatura_seleccionada.getNombre_materia().equals("Agregar nueva asignatura")) {
                        System.out.println("No hay ninguna asignatura que actualizar, actaulizando los valores en el arreglo");
                    } else {
                        //Actualizo los valores del seleccionado, elimino el original y subo el nuevo, respetando los indices
                        int index = asignaturas_original.indexOf(asignatura_seleccionada);
                        asignaturas_original.remove(asignatura_seleccionada);
                        asignatura_seleccionada.setCodigo_materia(codigo_mat);
                        asignatura_seleccionada.setNombre_materia(nombre_mat);
                        asignaturas_original.add(index, asignatura_seleccionada);
                    }

                    new AlertDialog.Builder(Add_Edit_Asignaturas.this)
                            .setTitle(getResources().getString(R.string.header_warning))
                            .setMessage(getResources().getString(R.string.warn_update_asign))
                            .setNegativeButton(getResources().getString(R.string.cancelar), (dialog, which) -> dialog.dismiss())
                            .setPositiveButton(getResources().getString(R.string.btnSave), (dialog, which) -> {

                                JsonObject data_Usuario = new JsonObject();

                                JsonArray asignaturas_array = (JsonArray) new Gson().toJsonTree(asignaturas_original,
                                        new TypeToken<List<Asignatura>>() {
                                        }.getType());

                                data_Usuario.add("materias", asignaturas_array);

                                sms_service.update_data(data_Usuario, token, id_usuario).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                                        if (response.isSuccessful() && response.body() != null){

                                        }else {
                                            Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                                                    .positiveButton(R.string.aceptar, null, materialDialog -> {
                                                        startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                                                        Add_Edit_Asignaturas.this.finish();
                                                        return Unit.INSTANCE;
                                                    }).show();
                                            System.out.println("Error al obtener la informacion de usuario del servidor \n" + response.errorBody());
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                                        Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                                                .positiveButton(R.string.aceptar, null, materialDialog -> {
                                                    startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                                                    Add_Edit_Asignaturas.this.finish();
                                                    return Unit.INSTANCE;
                                                }).show();
                                        System.out.println("Error al realizar la request para obtener la informacion de usuario del servidor \n" + t.getMessage());
                                    }
                                });

                            }).create().show();

                } else {
                    //Quiere guardar el codigo o nombre vacio
                    Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.err_Add_Edit_Asignatura)).show();
                }
            } else {
                if (validateFiedlsAsignaturas()) {
                    JsonArray grupos_selected = (JsonArray) new Gson().toJsonTree(grupos_to_add, new TypeToken<List<Grupo>>() {
                    }.getType());

                    Asignatura new_asignatura = new Asignatura(
                            etCod_Asig.getText().toString(),
                            etNombre_Asig.getText().toString(),
                            "",
                            grupos_selected);

                    JsonObject data_asignatura = new JsonObject();

                    data_asignatura.addProperty("codigo_materia", new_asignatura.getCodigo_materia());
                    data_asignatura.addProperty("nombre_materia", new_asignatura.getNombre_materia());
                    data_asignatura.addProperty("imagen_materia", new_asignatura.getImagen_materia());
                    data_asignatura.addProperty("grupos", new JsonArray().toString());

                    sms_service.add_asignatura(data_asignatura, token).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                JsonObject update_data_usr = new JsonObject();

                                List<Asignatura> asignaturas_usuario = new LinkedList<>(asignaturas_original);

                                new_asignatura.setImagen_materia(response.body().get("imagen").toString().replaceAll("\"", ""));

                                asignaturas_usuario.add(new_asignatura);

                                JsonArray asignaturas_array = (JsonArray) new Gson().toJsonTree(asignaturas_usuario,
                                        new TypeToken<List<Asignatura>>() {
                                        }.getType());

                                update_data_usr.add("materias", asignaturas_array);

                                sms_service.update_data(update_data_usr, token, id_usuario).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_correcto), getString(R.string.content_correcto))
                                                    .positiveButton(R.string.aceptar, null, materialDialog -> {
                                                        Add_Edit_Asignaturas.this.recreate();
                                                        return Unit.INSTANCE;
                                                    }).show();
                                            Add_Edit_Asignaturas.this.recreate();
                                        } else {
                                            Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                                                    .positiveButton(R.string.aceptar, null, materialDialog -> {
                                                        startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                                                        Add_Edit_Asignaturas.this.finish();
                                                        return Unit.INSTANCE;
                                                    }).show();
                                            System.out.println("Error al actualizar los datos de usuario (materias) \n" + response.errorBody());
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                                        Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                                                .positiveButton(R.string.aceptar, null, materialDialog -> {
                                                    startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                                                    Add_Edit_Asignaturas.this.finish();
                                                    return Unit.INSTANCE;
                                                }).show();
                                        System.out.println("Error en la request para actualizar los datos de usuario (materias) \n" + t.getMessage());
                                    }
                                });

                            } else {
                                Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                                        .positiveButton(R.string.aceptar, null, materialDialog -> {
                                            startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                                            Add_Edit_Asignaturas.this.finish();
                                            return Unit.INSTANCE;
                                        }).show();
                                System.out.println("Error al añadir la nueva asignatura \n" + response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                                    .positiveButton(R.string.aceptar, null, materialDialog -> {
                                        startActivity(new Intent(Add_Edit_Asignaturas.this, Add_Edit_Asignaturas.class));
                                        Add_Edit_Asignaturas.this.finish();
                                        return Unit.INSTANCE;
                                    }).show();
                            System.out.println("Error en la request para añadir la nueva asignatura \n" + t.getMessage());
                        }
                    });

                } else {
                    Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.err_Add_Edit_Asignatura)).show();
                }
            }
        });

    }

    /**
     * Obtiene los grupos de manera general, tanto los que estan en las asignaturas del usuario
     * como los que estan almacenados en la nube (plantillas)
     */
    private void getGrupos_General() {
        sms_service.getAllGroups(token).enqueue(new Callback<List<Grupo>>() {
            @Override
            public void onResponse(Call<List<Grupo>> call, Response<List<Grupo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Grupo> grupos_general = new LinkedList<>();
                    List<List<Grupo>> grupos_List = new LinkedList<>();
                    nombre_asignatura_grupo.clear();
                    txtTipo_Grupo.setText(getResources().getString(R.string.lblGrupos_General));

                    for (int i = 0; i < asignaturas.size(); i++) {
                        grupos_List.add(new Gson().fromJson(asignaturas.get(i).getGrupos(), new TypeToken<List<Grupo>>() {
                        }.getType()));
                        for (int j = 0; j < grupos_List.get(i).size(); j++) {
                            nombre_asignatura_grupo.add(asignaturas.get(i).getNombre_materia());
                        }
                    }

                    for (int i = 0; i < grupos_List.size(); i++) {
                        grupos_general.addAll(grupos_List.get(i));
                    }

                    for (Grupo grupo_online : response.body()) {
                        grupos_general.add(grupo_online);
                        nombre_asignatura_grupo.add("Servidor");
                    }

                    lstGrupos.setAdapter(new Grupos_Edit_Adapter(Add_Edit_Asignaturas.this, grupos_general, nombre_asignatura_grupo, Add_Edit_Asignaturas.this));
                } else {
                    Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                            .positiveButton(R.string.aceptar, null, materialDialog -> {
                                Add_Edit_Asignaturas.this.recreate();
                                return Unit.INSTANCE;
                            }).show();
                    System.out.println(response.errorBody());

                }
            }

            @Override
            public void onFailure(Call<List<Grupo>> call, Throwable t) {
                Alert_Dialog.showWarnMessage(Add_Edit_Asignaturas.this, getString(R.string.header_warning), getString(R.string.request_error))
                        .positiveButton(R.string.aceptar, null, materialDialog -> {
                            Add_Edit_Asignaturas.this.recreate();
                            return Unit.INSTANCE;
                        }).show();
                System.out.println(t.toString());
            }
        });

    }

    /**
     * Carga los datos de la asignatura seleccionada
     *
     * @param asignatura --> Asignatura seleccionada
     */
    private void loadDataAsignatura(Asignatura asignatura) {
        txtTipo_Grupo.setText(getResources().getString(R.string.lblGrupos_Asignatura));
        asignatura_seleccionada = asignatura;
        etCod_Asig.setText(asignatura_seleccionada.getCodigo_materia());
        etNombre_Asig.setText(asignatura_seleccionada.getNombre_materia());
        nombre_asignatura_grupo.clear();
        if (asignatura_seleccionada.getImagen_materia() != null) {
            String url = new ApiWeb().getBASE_URL_GLITCH() + "/" + asignatura_seleccionada.getImagen_materia();
            Glide.with(Add_Edit_Asignaturas.this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(url).into(imgAsignatura);
        } else {
            Glide.with(Add_Edit_Asignaturas.this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(Add_Edit_Asignaturas.this.getDrawable(R.drawable.materia_holder)).into(imgAsignatura);
        }

        grupos.clear();

        grupos = new Gson().fromJson(asignatura_seleccionada.getGrupos(), new TypeToken<List<Grupo>>() {
        }.getType());

        for (int i = 0; i < grupos.size(); i++) {
            nombre_asignatura_grupo.add(asignatura_seleccionada.getNombre_materia());
        }

        if (asignatura_seleccionada.getNombre_materia() != null && !grupos.isEmpty()) {
            lstGrupos.setAdapter(new Grupos_Edit_Adapter(Add_Edit_Asignaturas.this, grupos, nombre_asignatura_grupo, Add_Edit_Asignaturas.this));
        } else {
            getGrupos_General();
        }


    }

    /**
     * En el caso de que se vaya a crear una nueva asignatura o se vayan a actualizar los campos de una asignatura
     * esta funcion checa si los campos estan o no vacios
     *
     * @return True si es que ningun campo está vacio; False si alguno de los 2 esta vacio
     */
    private boolean validateFiedlsAsignaturas() {
        return !etCod_Asig.getText().toString().isEmpty() && !etNombre_Asig.getText().toString().isEmpty();
    }

    /**
     * ItemListener de la clase {@link Grupos_Edit_Adapter}
     * Funciona para ir acumulando los grupos que se desean agregar a una asignatura sin grupos
     *
     * @param grupo_seleccionado --> Devuelve el grupo seleccionado
     * @param asignatura_grupo   --> Devuelve el nombre de la asignatura en el que está tal grupo
     */
    @Override
    public void OnItemGroupSelected(Grupo grupo_seleccionado, String asignatura_grupo) {
        grupos_to_add.add(grupo_seleccionado);
    }

    /**
     * ItemListener de la clase {@link Grupos_Edit_Adapter}
     * Funciona para eliminar el grupo de la lista de "grupos a agregar" a una asignatura sin grupos
     *
     * @param grupo_seleccionado --> El grupo que se desea eliminar
     * @param grupo_asignatura   --> La Asignatura donde se encuentra el grupo
     */
    @Override
    public void OnItemGroupDeselected(Grupo grupo_seleccionado, String grupo_asignatura) {
        grupos_to_add.remove(grupo_seleccionado);
    }

    /**
     * Elimina los grupos seleccionado y actualiza la UI
     * @param grupos_seleccionados --> Grupo que se ha seleccionado de manera general
     */
    private void DeleteGroup(List<Grupo> grupos_seleccionados) {
        StringBuilder grupos_string = new StringBuilder();
        List<Grupo> grupos_temporal = new Gson().fromJson(asignatura_seleccionada.getGrupos(), new TypeToken<List<Grupo>>() {
        }.getType());

        int index = asignaturas.indexOf(asignatura_seleccionada);
        asignaturas_original.remove(index);

        for (int i = 0; i < grupos_seleccionados.size(); i++) {
            Grupo grupo = grupos_seleccionados.get(i);
            grupos_string.append(grupo.getNombre_grupo()).append(" ");
            for (int j = 0; j < grupos_temporal.size(); j++) {
                if (grupos_temporal.get(j).getNombre_grupo().equals(grupo.getNombre_grupo())) {
                    grupos_temporal.remove(j);
                }
            }
        }

        new AlertDialog.Builder(Add_Edit_Asignaturas.this)
                .setTitle(getResources().getString(R.string.header_warning))
                .setMessage(getResources().getString(R.string.warning_del_grupo, grupos_string.toString(), asignatura_seleccionada.getNombre_materia()))
                .setNegativeButton(getResources().getString(R.string.cancelar), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getResources().getString(R.string.lblEliminar_Grupo), (dialog, which) -> {
                    JsonArray grupos_list = (JsonArray) new Gson().toJsonTree(grupos_temporal, new TypeToken<List<Grupo>>() {
                    }.getType());

                    asignatura_seleccionada.setGrupos(grupos_list);

                    asignaturas_original.add(index, asignatura_seleccionada);

                    loadDataAsignatura(asignatura_seleccionada);

                }).create().show();
    }


    /**
     *Elimina la asignatura seleccionada de manera general y actualiza la UI
     * @param asignatura_seleccionada --> Asignatura seleccionada en el spinner
     */
    private void DeleteAsignatura(Asignatura asignatura_seleccionada) {

        new AlertDialog.Builder(Add_Edit_Asignaturas.this)
                .setTitle(getResources().getString(R.string.header_warning))
                .setMessage(getResources().getString(R.string.warning_del_asignatura, asignatura_seleccionada.getNombre_materia()))
                .setNegativeButton(getResources().getString(R.string.cancelar), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getResources().getString(R.string.btnEliminar_Asignatura), (dialog, which) -> {
                    asignaturas.remove(asignatura_seleccionada);
                    ArrayDeque<Asignatura> asignaturas_original_temp = new ArrayDeque<>(asignaturas);
                    Add_Edit_Asignaturas.this.asignatura_seleccionada = asignaturas_original_temp.getLast();
                    asignaturas_original_temp.removeLast();
                    asignaturas_original.clear();
                    asignaturas_original.addAll(asignaturas_original_temp);
                    spAsignaturas.setAdapter(new Asignaturas_General_Adapter(Add_Edit_Asignaturas.this, R.layout.asignatura_item, asignaturas));
                    loadDataAsignatura(Add_Edit_Asignaturas.this.asignatura_seleccionada);
                    isForUpdate = true;
                }).create().show();
    }

    /**
     * ItemListener para las asignaturas, añade la asignatura desde el servidor al arreglo
     * para mas informacion ver {@link Asignaturas_Edit_Adapter}
     * @param asignatura_seleccionado --> Asignatura seleccionada
     */
    @Override
    public void OnAsignaturaSelected(Asignatura asignatura_seleccionado) {
        ArrayDeque<Asignatura> asignaturas_original_temp = new ArrayDeque<>(asignaturas);
        asignaturas_original_temp.removeLast();
        asignaturas.add(asignaturas_original_temp.size(), asignatura_seleccionado);
        asignaturas_original.add(asignatura_seleccionado);
        ArrayDeque<Asignatura> asignaturas_temp = new ArrayDeque<>(asignaturas);
        asignatura_seleccionada = asignaturas_temp.getLast();
        spAsignaturas.setAdapter(new Asignaturas_General_Adapter(Add_Edit_Asignaturas.this, R.layout.asignatura_item, asignaturas));
        loadDataAsignatura(asignatura_seleccionada);
        isForUpdate = true;
    }

    /**
     * Elimina del arreglo la asignatura de internet (Unicamente las que estn en el servidor)
     * @param asignatura_seleccionado --> Asignatura deseleccionada em el listview
     */
    @Override
    public void OnAsignaturaDeselected(Asignatura asignatura_seleccionado) {
        asignaturas.remove(asignatura_seleccionado);
        asignaturas_original.remove(asignatura_seleccionado);
        ArrayDeque<Asignatura> asignaturas_temp = new ArrayDeque<>(asignaturas);
        asignatura_seleccionada = asignaturas_temp.getLast();
        loadDataAsignatura(asignatura_seleccionada);
        spAsignaturas.setAdapter(new Asignaturas_General_Adapter(Add_Edit_Asignaturas.this, R.layout.asignatura_item, asignaturas));
        isForUpdate = true;

    }

    //TODO FIX THIS
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GET_SINGLE_FILE) {
                    Uri selectedImageUri = data.getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                    }

                    // Set the image in ImageView
                    imgAsignatura.setImageURI(selectedImageUri);
                }
            }
        } catch (Exception e) {
            Timber.tag("FileSelectorActivity").e(e, "File select error");
        }

    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        if (cursor != null) {
            cursor.close();
        }
        return res;
    }

 */

}

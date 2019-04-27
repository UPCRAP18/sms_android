package mx.upcrapbaba.sms.views.personalizacion;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.adaptadores.Grupos_Adapter;
import mx.upcrapbaba.sms.adaptadores.Spinner_Adapter;
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
import timber.log.Timber;

public class Add_Edit_Asignaturas extends AppCompatActivity implements Grupos_Adapter.GroupListener {

    private String SELECCIONADO = "";
    private User user_data;
    private List<Asignatura> asignaturas_original = new LinkedList<>(), asignaturas = new LinkedList<>();
    private List<Grupo> grupos = new LinkedList<>(), grupos_to_add = new LinkedList<>();
    private List<String> nombre_asignatura_grupo = new LinkedList<>();
    private EditText etNombre_Asig, etCod_Asig;
    private Spinner spAsignaturas;
    private ImageView imgAsignatura;
    private Button btnSeleccionar_Asignatura;
    private int REQUEST_GET_SINGLE_FILE = 1;
    private boolean isForUpdate = false;
    private ListView lstGrupos;
    private Asignatura asignatura_seleccionada;
    private Grupo grupo_seleccionado = new Grupo();
    private TextView txtTipo_Grupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add_edit_asignaturas);

        SMSService sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);
        String token = "Bearer " + new DBHelper(this).getData_Usuario().get(1);
        String id_usuario = new DBHelper(this).getData_Usuario().get(0);

        if (getIntent().getExtras() != null) {
            SELECCIONADO = getIntent().getStringExtra("SELECCIONADO");
        } else {
            //TODO Regresar al inicio
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

        Button btnSave = findViewById(R.id.btnGuardar);
        Button btnEliminar_Grupo = findViewById(R.id.btnEliminar_Grupo);
        Button btnEliminar_Asignatura = findViewById(R.id.btnEliminar_Asignatura);

        btnEliminar_Grupo.setOnClickListener(v -> {
            if (asignatura_seleccionada != null) {
                if (grupo_seleccionado != null) {
                    DeleteGroup(grupos_to_add);
                } else {
                    Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.warning_notSel_Grupo)).show();
                }
            } else {
                Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.warning_notSel_Asignatura)).show();
            }
        });

        btnEliminar_Asignatura.setOnClickListener(v -> {
            if (asignatura_seleccionada != null) {
                DeleteAsignatura(asignatura_seleccionada);
            } else {
                Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.warning_notSel_Asignatura_Del)).show();
            }
        });

        btnSeleccionar_Asignatura = findViewById(R.id.btnSelect_Edit);


        sms_service.getUserInfo(token, id_usuario).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user_data = response.body();

                    asignaturas = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());

                    asignaturas_original = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());

                    getGrupos_General();

                    asignaturas.add(new Asignatura("", "", "", new JsonArray()));

                    spAsignaturas.setAdapter(new Spinner_Adapter(Add_Edit_Asignaturas.this, R.layout.asignatura_item, asignaturas));

                    btnSeleccionar_Asignatura.setOnClickListener(v -> {
                        if (spAsignaturas.getSelectedItemPosition() != (asignaturas.size() - 1)) {
                            isForUpdate = true;
                            loadDataAsignatura(asignaturas.get(spAsignaturas.getSelectedItemPosition()));
                        } else {
                            asignatura_seleccionada = null;
                            grupo_seleccionado = null;
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
                    System.out.println(response.errorBody());
                    Toasty.warning(Add_Edit_Asignaturas.this, "Ha ocurrido un error").show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println(t.toString());
            }
        });


        btnSave.setOnClickListener(v -> {
            if (validateFiedlsAsignaturas()) {
                if (isForUpdate) {
                    Asignatura asignatura_seleccionada = asignaturas.get(spAsignaturas.getSelectedItemPosition());
                    //Toasty.info(Add_Edit_Asignaturas.this,
                    //"Esta por actualizar la materia " + .getNombre_materia()).show();

                    asignaturas_original.remove(asignatura_seleccionada);



                } else {

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
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
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
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        if (response.isSuccessful()) {
                                            Add_Edit_Asignaturas.this.recreate();
                                        } else {
                                            System.out.println(response.body().toString());
                                            Toasty.warning(Add_Edit_Asignaturas.this, "Ha ocurrido un error").show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                        System.out.println(t.toString());
                                        Toasty.warning(Add_Edit_Asignaturas.this, "Ha ocurrido un error en la request").show();
                                    }
                                });

                            } else {
                                System.out.println(response.body().get("message"));
                                Toasty.warning(Add_Edit_Asignaturas.this, "Ha ocurrido un error").show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            System.out.println(t.toString());
                            Toasty.warning(Add_Edit_Asignaturas.this, "Ha ocurrido un error en la request").show();
                        }
                    });

                }
            } else {
                Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.err_Add_Edit_Asignatura)).show();
            }
        });

    }

    //TODO FIX THISSSSS
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


    private void getGrupos_General() {
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


        lstGrupos.setAdapter(new Grupos_Adapter(Add_Edit_Asignaturas.this, grupos_general, nombre_asignatura_grupo, Add_Edit_Asignaturas.this));

    }

    private void loadDataAsignatura(Asignatura asignatura) {
        txtTipo_Grupo.setText(getResources().getString(R.string.lblGrupos_Asignatura));
        asignatura_seleccionada = asignatura;
        etCod_Asig.setText(asignatura.getCodigo_materia());
        etNombre_Asig.setText(asignatura.getNombre_materia());
        nombre_asignatura_grupo.clear();
        if (asignatura.getImagen_materia() != null) {
            String url = new ApiWeb().getBASE_URL_GLITCH() + "/" + asignatura.getImagen_materia();
            Glide.with(Add_Edit_Asignaturas.this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(url).into(imgAsignatura);
        } else {
            Glide.with(Add_Edit_Asignaturas.this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(Add_Edit_Asignaturas.this.getDrawable(R.drawable.materia_holder)).into(imgAsignatura);
        }

        grupos.clear();

        grupos = new Gson().fromJson(asignatura.getGrupos(), new TypeToken<List<Grupo>>() {
        }.getType());

        for (int i = 0; i < grupos.size(); i++) {
            nombre_asignatura_grupo.add(asignatura_seleccionada.getNombre_materia());
        }

        if (asignatura.getNombre_materia() != null && !grupos.isEmpty()) {
            lstGrupos.setAdapter(new Grupos_Adapter(Add_Edit_Asignaturas.this, grupos, nombre_asignatura_grupo, Add_Edit_Asignaturas.this));
        } else {
            getGrupos_General();
        }


    }

    private boolean validateFiedlsAsignaturas() {
        return !etCod_Asig.getText().toString().isEmpty() || !etNombre_Asig.getText().toString().isEmpty();
    }

    @Override
    public void OnItemGroupSelected(Grupo grupo_seleccionado, String asignatura_grupo) {
        this.grupo_seleccionado = grupo_seleccionado;
        Toasty.success(Add_Edit_Asignaturas.this, "Se ha seleccionado el grupo " + grupo_seleccionado.getNombre_grupo()).show();
        grupos_to_add.add(grupo_seleccionado);
    }

    @Override
    public void OnItemGroupDeselected(Grupo grupo_seleccionado, String grupo_asignatura) {
        Toasty.success(Add_Edit_Asignaturas.this, "Se ha deseleccionado el grupo " + grupo_seleccionado.getNombre_grupo()).show();
        grupos_to_add.remove(grupo_seleccionado);
    }

    private void DeleteGroup(List<Grupo> grupos_seleccionados) {
        StringBuilder grupos_string = new StringBuilder();

        for (Grupo grupo : grupos_seleccionados) {
            grupos_string.append(grupo.getNombre_grupo()).append(" ");
        }

        new AlertDialog.Builder(Add_Edit_Asignaturas.this)
                .setTitle(getResources().getString(R.string.header_warning))
                .setMessage(getResources().getString(R.string.warning_del_grupo, grupos_string.toString(), asignatura_seleccionada.getNombre_materia()))
                .setNegativeButton(getResources().getString(R.string.cancelar), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getResources().getString(R.string.btnEliminar_Grupo), (dialog, which) -> {
                    //Toasty.info(Add_Edit_Asignaturas.this, "Se ha eliminado").show();

                    
                }).create().show();
    }

    private void DeleteAsignatura(Asignatura asignatura_seleccionada) {
        new AlertDialog.Builder(Add_Edit_Asignaturas.this)
                .setTitle(getResources().getString(R.string.header_warning))
                .setMessage(getResources().getString(R.string.warning_del_asignatura, asignatura_seleccionada.getNombre_materia()))
                .setNegativeButton(getResources().getString(R.string.cancelar), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getResources().getString(R.string.btnEliminar_Asignatura), (dialog, which) -> {
                    Toasty.info(Add_Edit_Asignaturas.this, "Se ha eliminado").show();
                }).create().show();
    }

}

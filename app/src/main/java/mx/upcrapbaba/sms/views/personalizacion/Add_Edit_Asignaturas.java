package mx.upcrapbaba.sms.views.personalizacion;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import mx.upcrapbaba.sms.R;
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

public class Add_Edit_Asignaturas extends AppCompatActivity {

    private String SELECCIONADO = "";
    private User user_data;
    private List<Asignatura> asignaturas = new LinkedList<>();
    private List<Grupo> grupos = new LinkedList<>();
    private List<String> nombre_grupos = new LinkedList<>();
    private EditText etNombre_Asig, etCod_Asig;
    private Spinner spAsignaturas, spGrupos;
    private ImageView imgAsignatura;
    private Button btnEliminarGrupo, btnSeleccionar_Asignatura;
    private int REQUEST_GET_SINGLE_FILE = 1;
    private boolean isForUpdate = false;

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
        spGrupos = findViewById(R.id.spGrupos_Edit);
        btnEliminarGrupo = findViewById(R.id.btnEliminarGrupo);
        Button btnSave = findViewById(R.id.btnGuardar);
        btnSeleccionar_Asignatura = findViewById(R.id.btnSelect_Edit);

        Call<User> user_info = sms_service.getUserInfo(token, id_usuario);

        user_info.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user_data = response.body();

                    asignaturas = new Gson().fromJson(user_data.getMaterias(), new TypeToken<List<Asignatura>>() {
                    }.getType());

                    getGrupos_General();

                    asignaturas.add(new Asignatura("", "", "", new JsonArray()));

                    spAsignaturas.setAdapter(new Spinner_Adapter(Add_Edit_Asignaturas.this, R.layout.asignatura_item, asignaturas));

                    btnSeleccionar_Asignatura.setOnClickListener(v -> {
                        if (spAsignaturas.getSelectedItemPosition() != (asignaturas.size() - 1)) {
                            isForUpdate = true;
                            loadDataAsignatura(asignaturas.get(spAsignaturas.getSelectedItemPosition()));
                        } else {
                            isForUpdate = false;
                            etCod_Asig.getText().clear();
                            etNombre_Asig.getText().clear();
                            imgAsignatura.setImageDrawable(getDrawable(R.drawable.materia_holder));
                        }
                    });

                    //TODO FIX THISSSS
                    imgAsignatura.setOnClickListener(v -> {
                        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_FILE);*/
                    });

                    btnEliminarGrupo.setOnClickListener(v -> {
                        if (spAsignaturas.getSelectedItemPosition() != (asignaturas.size() - 1)) {
                            if (spGrupos.getSelectedItemPosition() != (nombre_grupos.size() - 1)) {
                                Asignatura asignatura_seleccionada = asignaturas.get(spAsignaturas.getSelectedItemPosition());
                                Grupo grupo_seleccionado = grupos.get(spGrupos.getSelectedItemPosition());
                                new AlertDialog.Builder(Add_Edit_Asignaturas.this)
                                        .setTitle(getResources().getString(R.string.header_warning))
                                        .setMessage(getResources().getString(R.string.warning_del_grupo, grupo_seleccionado.getNombre_grupo(), asignatura_seleccionada.getNombre_materia()))
                                        .setNegativeButton(getResources().getString(R.string.cancelar), (dialog, which) -> dialog.dismiss())
                                        .setPositiveButton(getResources().getString(R.string.btnEliminar_Grupo), (dialog, which) -> {
                                            Toasty.info(Add_Edit_Asignaturas.this, "Se ha eliminado").show();
                                        }).create().show();
                            } else {

                                Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.warning_notSel_Grupo)).show();
                            }

                        } else {
                            Toasty.warning(Add_Edit_Asignaturas.this, getResources().getString(R.string.warning_notSel_Asignatura)).show();
                        }

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
                    Toasty.info(Add_Edit_Asignaturas.this,
                            "Esta por actualizar la materia " + asignaturas.get(spAsignaturas.getSelectedItemPosition()).getNombre_materia()).show();
                } else {
                    Toasty.info(Add_Edit_Asignaturas.this, "Va a crear una nueva asignatura").show();
                    Grupo new_grupo = new Grupo();
                    if (spGrupos.getSelectedItemPosition() != (grupos.size() - 1)) {
                        new_grupo = grupos.get(spGrupos.getSelectedItemPosition());
                    }

                    JsonArray grupos_to_add = new JsonArray();

                    grupos_to_add.add(new Gson().toJson(new_grupo));

                    Asignatura new_asignatura = new Asignatura(
                            etCod_Asig.getText().toString(),
                            etNombre_Asig.getText().toString(),
                            "",
                            grupos_to_add
                    );

                    System.out.println(new Gson().toJson(new_asignatura));

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
        List<List<Grupo>> lista_Grupos = new LinkedList<>();
        List<String> nombre_Grupos = new LinkedList<>();
        for (Asignatura asignatura : asignaturas) {
            List<Grupo> grupo_temporal = new Gson().fromJson(asignatura.getGrupos(), new TypeToken<List<Grupo>>() {
            }.getType());
            lista_Grupos.add(grupo_temporal);
        }

        for (int i = 0; i < lista_Grupos.size(); i++) {
            for (int j = 0; j < lista_Grupos.get(i).size(); j++) {
                nombre_Grupos.add(lista_Grupos.get(i).get(j).getNombre_grupo());
            }
        }

        HashSet<String> hashSet = new HashSet<>(nombre_Grupos);
        nombre_Grupos.clear();
        nombre_Grupos.addAll(hashSet);


        nombre_Grupos.add("");

        spGrupos.setAdapter(new ArrayAdapter<>(Add_Edit_Asignaturas.this, R.layout.custom_spinner, nombre_Grupos));
        btnEliminarGrupo.setEnabled(false);

    }

    private void loadDataAsignatura(Asignatura asignatura) {
        etCod_Asig.setText(asignatura.getCodigo_materia());
        etNombre_Asig.setText(asignatura.getNombre_materia());
        if (asignatura.getImagen_materia() != null) {
            String url = new ApiWeb().getBASE_URL_GLITCH() + "/" + asignatura.getImagen_materia();
            Glide.with(Add_Edit_Asignaturas.this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(url).into(imgAsignatura);
        } else {
            Glide.with(Add_Edit_Asignaturas.this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(Add_Edit_Asignaturas.this.getDrawable(R.drawable.materia_holder)).into(imgAsignatura);
        }

        grupos.clear();
        nombre_grupos.clear();

        grupos = new Gson().fromJson(asignatura.getGrupos(), new TypeToken<List<Grupo>>() {
        }.getType());

        for (Grupo grupo : grupos) {
            nombre_grupos.add(grupo.getNombre_grupo());
        }

        nombre_grupos.add("");

        if (!nombre_grupos.isEmpty()) {
            btnEliminarGrupo.setEnabled(true);
        }

        spGrupos.setAdapter(new ArrayAdapter<>(Add_Edit_Asignaturas.this, R.layout.custom_spinner, nombre_grupos));
    }

    private boolean validateFiedlsAsignaturas() {
        return !etCod_Asig.getText().toString().isEmpty() || !etNombre_Asig.getText().toString().isEmpty();
    }

}

package mx.upcrapbaba.sms.views.inicio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.LinkedList;
import java.util.List;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.api.Service.SMSService;
import mx.upcrapbaba.sms.extras.Alert_Dialog;
import mx.upcrapbaba.sms.models.Asignatura;
import mx.upcrapbaba.sms.models.Grupo;
import mx.upcrapbaba.sms.models.User;
import mx.upcrapbaba.sms.sqlite.DBHelper;

public class Estadisticas extends AppCompatActivity implements BottomNavigation.OnMenuItemSelectionListener {

    private User user_data;
    private List<Asignatura> asignaturas_original = new LinkedList<>();
    private List<Grupo> grupos_original = new LinkedList<>();
    private BottomNavigation nav_bar;
    private SMSService smsService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        SMSService sms_service = ApiWeb.getApi(new ApiWeb().getBASE_URL_GLITCH()).create(SMSService.class);
        String token = "Bearer " + new DBHelper(this).getData_Usuario().get(1);
        String id_usuario = new DBHelper(this).getData_Usuario().get(0);

        nav_bar = findViewById(R.id.bottom_nav_bar);

        nav_bar.setDefaultSelectedIndex(1);

        nav_bar.setMenuItemSelectionListener(this);

        Toolbar toolbar = findViewById(R.id.ToolBar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_Estadisticas);
        } else {
            System.out.println("Ha ocurrido un error al inicializar la barra de titulo");
            Alert_Dialog.showErrorMessage(this);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Estadisticas.this, Inicio.class));
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        Estadisticas.this.finish();
    }

    @Override
    public void onMenuItemReselect(int i, int i1, boolean b) {
        Estadisticas.this.recreate();
    }

    @Override
    public void onMenuItemSelect(int i, int i1, boolean b) {
        switch (i1) {
            case 0:
                startActivity(new Intent(Estadisticas.this, Inicio.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                this.finish();
                break;
            case 2:
                break;
        }
    }
}

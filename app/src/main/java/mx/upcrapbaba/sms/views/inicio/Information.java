package mx.upcrapbaba.sms.views.inicio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.extras.Alert_Dialog;

public class Information extends AppCompatActivity implements BottomNavigation.OnMenuItemSelectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        BottomNavigation nav_bar = findViewById(R.id.bottom_nav_bar);

        nav_bar.setDefaultSelectedIndex(2);

        nav_bar.setMenuItemSelectionListener(this);

        Toolbar toolbar = findViewById(R.id.ToolBar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.tabInfo_Title);
        } else {
            System.out.println("Ha ocurrido un error al inicializar la barra de titulo");
            Alert_Dialog.showErrorMessage(this);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Information.this, Inicio.class));
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        Information.this.finish();
    }

    @Override
    public void onMenuItemReselect(int i, int i1, boolean b) {
        Information.this.recreate();
    }

    @Override
    public void onMenuItemSelect(int i, int i1, boolean b) {
        switch (i1) {
            case 0:
                startActivity(new Intent(Information.this, Inicio.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                this.finish();
                break;
            case 1:
                startActivity(new Intent(Information.this, Estadisticas.class));
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                this.finish();
                break;
        }
    }
}

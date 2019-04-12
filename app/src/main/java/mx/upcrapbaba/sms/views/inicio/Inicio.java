package mx.upcrapbaba.sms.views.inicio;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import mx.upcrapbaba.sms.R;

public class Inicio extends AppCompatActivity implements BottomNavigation.OnMenuItemSelectionListener {

    private BottomNavigation nav_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        nav_bar = findViewById(R.id.bottom_nav_bar);

        nav_bar.setSelectedIndex(0);

        nav_bar.setMenuItemSelectionListener(this);

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
}

//TODO fix the chingadera of Toast inflate
//region Chingadera a Fixear
    /*Toast toasty = new Toast(this);
    View popupView = getLayoutInflater().inflate(R.layout.empty_list, null);

        toasty.setView(popupView);
                toasty.setDuration(Toast.LENGTH_LONG);
                toasty.setGravity(Gravity.CENTER, 0,0);
                toasty.show();

                TextView lblStatus = popupView.findViewById(R.id.txtStatus);
                lblStatus.setText("Holi");*/
//endregion

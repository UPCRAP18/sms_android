package mx.upcrapbaba.sms.views.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import mx.upcrapbaba.sms.views.sesion.Login;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                startActivity(new Intent(this, Login.class));
                this.finish();
            }
        }).run();

        startActivity(new Intent(this, Login.class));
        this.finish();

    }
}

package mx.upcrapbaba.sms.extras;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus {
    private Context mContext;

    public NetworkStatus(Context cont) {
        this.mContext = cont;
    }

    /**
     * Utilizando la API de Gestor de conectividad de android, se obtiene el tipo de red que
     * esta actualmente en en uso.
     *
     * @return {
     * 0 --> Sin Conexion
     * 1 --> Conexion de wifi
     * 2 --> Conexion de datos
     * }
     */
    public int getTypeConnection() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return 0;
        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            return 1;
        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            return 2;
        } else {
            return 0;
        }
    }
}



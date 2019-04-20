package mx.upcrapbaba.sms.extras;

import android.content.Context;
import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;

import kotlin.Unit;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.views.inicio.Inicio;

public class Alert_Dialog {

    public static void showErrorMessage(Context context) {
        MaterialDialog mDialog = new MaterialDialog(context);

        mDialog.title(R.string.header_error, null);
        mDialog.message(R.string.body_error, null, false, 1);
        mDialog.positiveButton(R.string.aceptar, null, materialDialog -> {
            context.startActivity(new Intent(context, Inicio.class));
            return Unit.INSTANCE;
        });
        mDialog.show();
    }

    public static void showMessage(Context context, String title, String message) {
        MaterialDialog mDialog = new MaterialDialog(context);

        mDialog.title(null, title);
        mDialog.message(null, message, false, 1);
        mDialog.positiveButton(R.string.aceptar, null, materialDialog -> Unit.INSTANCE);
        mDialog.show();
    }

    public static MaterialDialog showWarnMessage(Context context, String title, String message) {
        return new MaterialDialog(context)
                .title(null, title)
                .message(null, message, false, 1);
    }


}

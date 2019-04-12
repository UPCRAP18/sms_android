package mx.upcrapbaba.sms.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "SMS_Preferencias";
    private static final String COLUMN_ID_USUARIO = "ID_Usuario";
    private static final String COLUMN_TOKEN_USUARIO = "Token_Usuario";
    private static final String TABLE_PREFERENCIAS = "preferencias";
    private static SQLiteDatabase db;


    /**
     * Constructor de la base de datos
     *
     * @param context --> Contexto para manipular los datos
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);

    }

    /**
     * Creacion de las tablas de la base de datos
     *
     * @param db --> Qué DB se va a crear o, cual se va a manipular
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Ejecutando el onCreate de la DB");
        String Usr_Prefs = "CREATE TABLE IF NOT EXISTS " + TABLE_PREFERENCIAS + " ( " + COLUMN_ID_USUARIO + " TEXT NOT NULL, " + COLUMN_TOKEN_USUARIO + " TEXT NOT NULL)";
        db.execSQL(Usr_Prefs);
    }

    /**
     * Actualizacion a la base de datos, se borran las tablas y se ejecuta el onCreate
     *
     * @param db         --> DB que se manipula
     * @param oldVersion --> Numero de la version anterior
     * @param newVersion --> Nueva version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Ejecutando el onUpgrade de la DB");

        String drop_Usr = "DROP TABLE IF EXISTS " + TABLE_PREFERENCIAS;
        db.execSQL(drop_Usr);

        onCreate(db);
    }


    private Cursor getDataUsr() {
        db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery("SELECT * FROM " + TABLE_PREFERENCIAS, null);
        return res;
    }

    public ArrayList<String> getData_Usuario() {

        Cursor res = getDataUsr();
        ArrayList<String> credenciales = new ArrayList<>();

        res.moveToFirst();

        while (!res.isAfterLast()) {
            credenciales.add(res.getString(res.getColumnIndex(COLUMN_ID_USUARIO)));
            credenciales.add(res.getString(res.getColumnIndex(COLUMN_TOKEN_USUARIO)));
            res.moveToNext();
        }

        db.close();

        return credenciales;

    }

    public boolean addCredentials(String ID, String Token) {
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_USUARIO, ID);
        values.put(COLUMN_TOKEN_USUARIO, Token);

        if (db.insert(TABLE_PREFERENCIAS, null, values) != 0) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }

    }

    public boolean dropUsr() {
        db = getWritableDatabase();
        if (db.delete(TABLE_PREFERENCIAS, null, null) != 0) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }

    }


}

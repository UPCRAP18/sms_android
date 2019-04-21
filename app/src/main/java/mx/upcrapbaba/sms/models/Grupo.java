package mx.upcrapbaba.sms.models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Grupo {

    @Expose
    private String nombre_grupo;
    @SerializedName("alumnos")
    private JsonArray alumnos;

    public Grupo(String nombre_grupo, JsonArray alumnos) {
        this.nombre_grupo = nombre_grupo;
        this.alumnos = alumnos;
    }

    public String getNombre_grupo() {
        return nombre_grupo;
    }

    public void setNombre_grupo(String nombre_grupo) {
        this.nombre_grupo = nombre_grupo;
    }

    public JsonArray getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(JsonArray alumnos) {
        this.alumnos = alumnos;
    }

}

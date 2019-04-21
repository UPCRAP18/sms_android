package mx.upcrapbaba.sms.models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Asignatura {
    @Expose
    private String codigo_materia;
    @Expose
    private String nombre_materia;
    @Expose
    private String imagen_materia;
    @SerializedName("grupos")
    private JsonArray grupos;

    public Asignatura(String codigo_materia, String nombre_materia, String imagen_materia, JsonArray grupos) {
        this.codigo_materia = codigo_materia;
        this.nombre_materia = nombre_materia;
        this.imagen_materia = imagen_materia;
        this.grupos = grupos;
    }

    public String getCodigo_materia() {
        return codigo_materia;
    }

    public void setCodigo_materia(String codigo_materia) {
        this.codigo_materia = codigo_materia;
    }

    public String getNombre_materia() {
        return nombre_materia;
    }

    public void setNombre_materia(String nombre_materia) {
        this.nombre_materia = nombre_materia;
    }

    public String getImagen_materia() {
        return imagen_materia;
    }

    public void setImagen_materia(String imagen_materia) {
        this.imagen_materia = imagen_materia;
    }

    public JsonArray getGrupos() {
        return grupos;
    }

    public void setGrupos(JsonArray grupos) {
        this.grupos = grupos;
    }
}

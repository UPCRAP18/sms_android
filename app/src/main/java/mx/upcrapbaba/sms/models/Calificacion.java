package mx.upcrapbaba.sms.models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Calificacion {
    @Expose
    private String nombre_actividad;
    @Expose
    private String parcial;
    @Expose
    private String valor_actividad;
    @SerializedName("obtenido")
    private JsonArray obtenido;

    public Calificacion(String nombre_actividad, String parcial, String valor_actividad, JsonArray obtenido) {
        this.nombre_actividad = nombre_actividad;
        this.parcial = parcial;
        this.valor_actividad = valor_actividad;
        this.obtenido = obtenido;
    }

    public String getNombre_actividad() {
        return nombre_actividad;
    }

    public void setNombre_actividad(String nombre_actividad) {
        this.nombre_actividad = nombre_actividad;
    }

    public String getParcial() {
        return parcial;
    }

    public void setParcial(String parcial) {
        this.parcial = parcial;
    }

    public String getValor_actividad() {
        return valor_actividad;
    }

    public void setValor_actividad(String valor_actividad) {
        this.valor_actividad = valor_actividad;
    }

    public JsonArray getObtenido() {
        return obtenido;
    }

    public void setObtenido(JsonArray obtenido) {
        this.obtenido = obtenido;
    }
}

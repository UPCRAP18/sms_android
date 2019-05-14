package mx.upcrapbaba.sms.models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;

public class Actividad {
    @Expose
    private String nombre_actividad;
    @Expose
    private String valor;
    @Expose
    private JsonArray obtenido;

    public Actividad() {
    }

    public Actividad(String nombre_actividad, String valor, JsonArray obtenido) {
        this.nombre_actividad = nombre_actividad;
        this.valor = valor;
        this.obtenido = obtenido;
    }

    public String getNombre_actividad() {
        return nombre_actividad;
    }

    public void setNombre_actividad(String nombre_actividad) {
        this.nombre_actividad = nombre_actividad;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public JsonArray getObtenido() {
        return obtenido;
    }

    public void setObtenido(JsonArray obtenido) {
        this.obtenido = obtenido;
    }
}

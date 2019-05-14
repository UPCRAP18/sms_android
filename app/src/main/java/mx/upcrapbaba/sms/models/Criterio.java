package mx.upcrapbaba.sms.models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Criterio {
    @Expose
    private String parcial;
    @Expose
    private String valor_total;
    @SerializedName("actividades")
    private JsonArray actividades;


    public Criterio(String parcial, String valor_total, JsonArray actividades) {
        this.parcial = parcial;
        this.valor_total = valor_total;
        this.actividades = actividades;
    }

    public Criterio() {
    }

    public String getParcial() {
        return parcial;
    }

    public void setParcial(String parcial) {
        this.parcial = parcial;
    }

    public String getValor_total() {
        return valor_total;
    }

    public void setValor_total(String valor_total) {
        this.valor_total = valor_total;
    }

    public JsonArray getActividades() {
        return actividades;
    }

    public void setActividades(JsonArray actividades) {
        this.actividades = actividades;
    }
}


package mx.upcrapbaba.sms.models;

import com.google.gson.annotations.Expose;

public class Actividad {
    @Expose
    private String nombre_actividad;
    @Expose
    private String valor_actividad;
    @Expose
    private String parcial;

    public Actividad(String nombre_actividad, String valor_actividad, String parcial) {
        this.nombre_actividad = nombre_actividad;
        this.valor_actividad = valor_actividad;
        this.parcial = parcial;
    }

    public String getNombre_actividad() {
        return nombre_actividad;
    }

    public void setNombre_actividad(String nombre_actividad) {
        this.nombre_actividad = nombre_actividad;
    }

    public String getValor_actividad() {
        return valor_actividad;
    }

    public void setValor_actividad(String valor_actividad) {
        this.valor_actividad = valor_actividad;
    }

    public String getParcial() {
        return parcial;
    }

    public void setParcial(String parcial) {
        this.parcial = parcial;
    }
}


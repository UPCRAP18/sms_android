package mx.upcrapbaba.sms.models;

import com.google.gson.annotations.Expose;

public class Calificacion {
    @Expose
    private String nombre_actividad;
    @Expose
    private String parcial;
    @Expose
    private String valor_actividad;
    @Expose
    private String obtenido;

    public Calificacion(String nombre_actividad, String parcial, String valor_actividad, String obtenido) {
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

    public String getObtenido() {
        return obtenido;
    }

    public void setObtenido(String obtenido) {
        this.obtenido = obtenido;
    }
}

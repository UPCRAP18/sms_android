package mx.upcrapbaba.sms.models;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Grupos {

    @Expose
    private Grupos Alumnos;
    @Expose
    private List<String> alumnos;
    @Expose
    private String nombre_grupo;

    public Grupos(Grupos alumnos, List<String> alumnos1, String nombre_grupo) {
        Alumnos = alumnos;
        this.alumnos = alumnos1;
        this.nombre_grupo = nombre_grupo;
    }

    public Grupos getAlumnos() {
        return Alumnos;
    }

    public void setAlumnos(List<String> alumnos) {
        this.alumnos = alumnos;
    }

    public void setAlumnos(Grupos alumnos) {
        Alumnos = alumnos;
    }

    public List<String> getAlumnos_List() {
        return alumnos;
    }

    public String getNombre_grupo() {
        return nombre_grupo;
    }

    public void setNombre_grupo(String nombre_grupo) {
        this.nombre_grupo = nombre_grupo;
    }
}

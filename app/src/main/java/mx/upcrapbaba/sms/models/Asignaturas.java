package mx.upcrapbaba.sms.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Asignaturas {

    @SerializedName("Materias")
    private List<Asignaturas> asignaturasList;
    @Expose
    private String nombre_materia;
    @Expose
    private String codigo_materia;
    @Expose
    private String[] grupos;

    public Asignaturas(List<Asignaturas> asignaturasList, String nombre_materia, String codigo_materia, String[] grupos) {
        this.asignaturasList = asignaturasList;
        this.nombre_materia = nombre_materia;
        this.codigo_materia = codigo_materia;
        this.grupos = grupos;
    }

    public List<Asignaturas> getAsignaturasList() {
        return asignaturasList;
    }

    public void setAsignaturasList(List<Asignaturas> asignaturasList) {
        this.asignaturasList = asignaturasList;
    }

    public String getNombre_materia() {
        return nombre_materia;
    }

    public void setNombre_materia(String nombre_materia) {
        this.nombre_materia = nombre_materia;
    }

    public String getCodigo_materia() {
        return codigo_materia;
    }

    public void setCodigo_materia(String codigo_materia) {
        this.codigo_materia = codigo_materia;
    }

    public String[] getGrupos() {
        return grupos;
    }

    public void setGrupos(String[] grupos) {
        this.grupos = grupos;
    }
}

package mx.upcrapbaba.sms.models;

import com.google.gson.annotations.Expose;

public class Alumno {

    @Expose
    String matricula_alumno;
    @Expose
    String nombre_alumno;
    @Expose
    String apellidos_alumno;
    @Expose
    String[] grupo;
    @Expose
    String[] calificaciones;

    public Alumno(String matricula_alumno, String nombre_alumno, String apellidos_alumno, String[] grupo, String[] calificaciones) {
        this.matricula_alumno = matricula_alumno;
        this.nombre_alumno = nombre_alumno;
        this.apellidos_alumno = apellidos_alumno;
        this.grupo = grupo;
        this.calificaciones = calificaciones;
    }

    public String getMatricula_alumno() {
        return matricula_alumno;
    }

    public void setMatricula_alumno(String matricula_alumno) {
        this.matricula_alumno = matricula_alumno;
    }

    public String getNombre_alumno() {
        return nombre_alumno;
    }

    public void setNombre_alumno(String nombre_alumno) {
        this.nombre_alumno = nombre_alumno;
    }

    public String getApellidos_alumno() {
        return apellidos_alumno;
    }

    public void setApellidos_alumno(String apellidos_alumno) {
        this.apellidos_alumno = apellidos_alumno;
    }

    public String[] getGrupo() {
        return grupo;
    }

    public void setGrupo(String[] grupo) {
        this.grupo = grupo;
    }

    public String[] getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(String[] calificaciones) {
        this.calificaciones = calificaciones;
    }
}

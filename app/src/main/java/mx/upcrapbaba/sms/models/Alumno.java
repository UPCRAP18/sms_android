package mx.upcrapbaba.sms.models;

import com.google.gson.annotations.Expose;

public class Alumno {

    @Expose
    private String matricula_alumno;
    @Expose
    private String nombre_alumno;
    @Expose
    private String apellidos;
    @Expose
    private String imagen_alumno;
    @Expose
    private String[] calificaciones;

    public Alumno(String matricula_alumno, String nombre_alumno, String apellidos, String imagen_alumno, String[] calificaciones) {
        this.matricula_alumno = matricula_alumno;
        this.nombre_alumno = nombre_alumno;
        this.apellidos = apellidos;
        this.imagen_alumno = imagen_alumno;
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getImagen_alumno() {
        return imagen_alumno;
    }

    public void setImagen_alumno(String imagen_alumno) {
        this.imagen_alumno = imagen_alumno;
    }

    public String[] getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(String[] calificaciones) {
        this.calificaciones = calificaciones;
    }
}

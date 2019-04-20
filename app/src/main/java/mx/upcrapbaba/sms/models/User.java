package mx.upcrapbaba.sms.models;

import com.google.gson.annotations.Expose;

public class User {
    @Expose
    private String matricula_empleado;
    @Expose
    private String email;
    @Expose
    private String[] criterios;
    @Expose
    private String[] materias;
    @Expose
    private String nombre;
    @Expose
    private String apellidos;

    public User(String matricula_empleado, String email, String[] criterios, String[] materias, String nombre, String apellidos) {
        this.matricula_empleado = matricula_empleado;
        this.email = email;
        this.criterios = criterios;
        this.materias = materias;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public String getMatricula_empleado() {
        return matricula_empleado;
    }

    public void setMatricula_empleado(String matricula_empleado) {
        this.matricula_empleado = matricula_empleado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getCriterios() {
        return criterios;
    }

    public void setCriterios(String[] criterios) {
        this.criterios = criterios;
    }

    public String[] getMaterias() {
        return materias;
    }

    public void setMaterias(String[] materias) {
        this.materias = materias;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
}

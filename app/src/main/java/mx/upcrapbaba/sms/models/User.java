package mx.upcrapbaba.sms.models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @Expose
    private String matricula_empleado;
    @Expose
    private String nombre;
    @Expose
    private String apellidos;
    @Expose
    private String email;
    @Expose
    private String imagen_perfil;
    @SerializedName("materias")
    private JsonArray materias;

    public User(String matricula_empleado, String nombre, String apellidos, String email, String imagen_perfil, JsonArray materias) {
        this.matricula_empleado = matricula_empleado;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.imagen_perfil = imagen_perfil;
        this.materias = materias;
    }

    public String getMatricula_empleado() {
        return matricula_empleado;
    }

    public void setMatricula_empleado(String matricula_empleado) {
        this.matricula_empleado = matricula_empleado;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagen_perfil() {
        return imagen_perfil;
    }

    public void setImagen_perfil(String imagen_perfil) {
        this.imagen_perfil = imagen_perfil;
    }

    public JsonArray getMaterias() {
        return materias;
    }

    public void setMaterias(JsonArray materias) {
        this.materias = materias;
    }
}

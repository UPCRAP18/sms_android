package mx.upcrapbaba.sms.api.Service;

import com.google.gson.JsonObject;

import mx.upcrapbaba.sms.models.Alumno;
import mx.upcrapbaba.sms.models.Asignatura;
import mx.upcrapbaba.sms.models.Grupo;
import mx.upcrapbaba.sms.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SMSService {

    /* Peticiones GET a sms-api-v1 */

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @GET("/usuarios/{id}")
    Call<User> getUserInfo(@Header("Authorization") String token, @Path("id") String id_usuario);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @GET("/materias/{id_materia}")
    Call<Asignatura> getAsignaturas(@Header("Authorization") String token, @Path("id_materia") String id_materia);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @GET("/grupos/{id_grupo}")
    Call<Grupo> getInfoGrupo(@Header("Authorization") String token, @Path("id_grupo") String id_grupo);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @GET("/alumnos/{id_alumno}")
    Call<Alumno> getAlumnoInfo(@Header("Authorization") String token, @Path("id_alumno") String id_alumno);


    /* Peticiones POST a sms-api-v1 */

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @POST("/usuarios/login")
    Call<JsonObject> login(@Body JsonObject credenciales);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @POST("/usuarios/signup")
    Call<JsonObject> register(@Body JsonObject user_info);


    /* Peticiones PATCH a sms-api-v1 */

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @PATCH("/usuarios/{id_user}")
    Call<JsonObject> update_data(@Body JsonObject user_data, @Header("Authorization") String token, @Path("id_user") String id_user);

}

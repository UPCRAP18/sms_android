package mx.upcrapbaba.sms.api.Service;

import com.google.gson.JsonObject;

import mx.upcrapbaba.sms.models.Alumno;
import mx.upcrapbaba.sms.models.Asignaturas;
import mx.upcrapbaba.sms.models.Grupos;
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
    @GET("/users/{id}")
    Call<User> getUserInfo(@Header("Authorization") String token, @Path("id") String id_usuario);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @GET("/materias/{id_materia}")
    Call<Asignaturas> getAsignaturas(@Header("Authorization") String token, @Path("id_materia") String id_materia);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @GET("/grupos/{id_grupo}")
    Call<Grupos> getInfoGrupo(@Header("Authorization") String token, @Path("id_grupo") String id_grupo);

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
    @POST("/users/login")
    Call<JsonObject> login(@Body JsonObject credenciales);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @POST("/users/signup")
    Call<JsonObject> register(@Body JsonObject user_info);


    /* Peticiones PATCH a sms-api-v1 */

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @PATCH("/users/{id_user}")
    Call<JsonObject> update_data(@Body JsonObject user_data, @Header("Authorization") String token, @Path("id_user") String id_user);

}

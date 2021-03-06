package mx.upcrapbaba.sms.api.Service;

import com.google.gson.JsonObject;

import java.util.List;

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
    @GET("/grupos/")
    Call<List<Grupo>> getAllGroups(@Header("Authorization") String token);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @GET("/materias/")
    Call<List<Asignatura>> getAllAsignaturas(@Header("Authorization") String token);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @GET("/alumnos/")
    Call<List<Alumno>> getAllAlumnos(@Header("Authorization") String token);


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


    @Headers({
            "Content-Type: application/json",
            "Accept: x-www-form-urlencoded"
    })
    @POST("/materias/")
    Call<JsonObject> add_asignatura(@Body JsonObject data, @Header("Authorization") String token);

    @Headers({
            "Content-Type: application/json",
            "Accept: x-www-form-urlencoded"
    })
    @POST("/grupos/")
    Call<JsonObject> add_grupo(@Body JsonObject data, @Header("Authorization") String token);

    @Headers({
            "Content-Type: application/json",
            "Accept: x-www-form-urlencoded"
    })
    @POST("/alumnos/")
    Call<JsonObject> add_alumno(@Body JsonObject data, @Header("Authorization") String token);

    /* Peticiones PATCH a sms-api-v1 */

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json "
    })
    @PATCH("/usuarios/{id_user}")
    Call<JsonObject> update_data(@Body JsonObject user_data, @Header("Authorization") String token, @Path("id_user") String id_user);


}

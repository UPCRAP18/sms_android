package mx.upcrapbaba.sms.API.Service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SMSService {


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


}

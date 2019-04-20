package mx.upcrapbaba.sms.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiWeb {

    private static Retrofit retrofit = null;
    private final String BASE_URL_GLITCH = "https://sms-api-v1.glitch.me";

    public static Retrofit getApi(String URL_USE) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpclient = new OkHttpClient.Builder();
        httpclient.addInterceptor(logging);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL_USE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpclient.build())
                    .build();
        }
        return retrofit;
    }

    public void ApiWeb() {

    }

    public String getBASE_URL_GLITCH() {
        return BASE_URL_GLITCH;
    }

}

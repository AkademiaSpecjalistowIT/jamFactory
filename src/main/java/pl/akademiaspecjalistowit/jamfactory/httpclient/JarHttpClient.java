package pl.akademiaspecjalistowit.jamfactory.httpclient;

import pl.akademiaspecjalistowit.jamfactory.dto.JarOrderRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.UUID;

public interface JarHttpClient {

    @POST("/api/v1/jars/order")
    Call<UUID> createJarOrder(@Body JarOrderRequestDto jarOrderRequestDto);
}

package com.example.moconitrillo.proyectocurso.accesodatos;

import com.example.moconitrillo.proyectocurso.entidades.ImageResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface AccesoWebserviceImgur {
    @Multipart
    @Headers({
            "Authorization: Client-ID 067a0f8188bad14"
    })
    @POST("image")
    Call<ImageResponse> postImage(
            @Query("title") String title,
            @Query("description") String description,
            @Query("album") String albumId,
            @Query("account_url") String username,
            @Part MultipartBody.Part file);
}

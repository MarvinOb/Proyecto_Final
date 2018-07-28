package com.example.moconitrillo.proyectocurso.accesodatos;

import com.example.moconitrillo.proyectocurso.entidades.AveriaServicio;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AccesoWebservice {

    @Headers(ValoresGlobales.VARIABLE_X_API_KEY)
    @GET(ValoresGlobales.VARIEBLE_SERVICIO_AVERIAS_GET_TODAS)
    Call<List<AveriaServicio>> obtenerTodasAverias();

    @Headers(ValoresGlobales.VARIABLE_X_API_KEY)
    @GET(ValoresGlobales.VARIEBLE_SERVICIO_AVERIAS_GET_ID)
    Call<AveriaServicio> obtenerDetallesAveria(@Path(ValoresGlobales.VARIABLE_SERVICIO_AVERIAS_GET_ID_NOMBRE_PARAMETRO) String id);

    @Headers(ValoresGlobales.VARIABLE_X_API_KEY)
    @POST(ValoresGlobales.VARIEBLE_SERVICIO_AVERIAS_POST_NUEVA)
    Call<AveriaServicio> crearNuevaAveria(@Body AveriaServicio nueva);

    @Headers(ValoresGlobales.VARIABLE_X_API_KEY)
    @POST(ValoresGlobales.VARIEBLE_SERVICIO_AVERIAS_POST_MODIFICAR)
    Call<AveriaServicio> modificarAveria(@Path(ValoresGlobales.VARIABLE_SERVICIO_AVERIAS_GET_ID_NOMBRE_PARAMETRO) String id,@Body AveriaServicio nueva);

    @Headers(ValoresGlobales.VARIABLE_X_API_KEY)
    @DELETE(ValoresGlobales.VARIEBLE_SERVICIO_AVERIAS_POST_BORRAR)
    Call<AveriaServicio> borrarAveria(@Path(ValoresGlobales.VARIABLE_SERVICIO_AVERIAS_GET_ID_NOMBRE_PARAMETRO) String id);
}

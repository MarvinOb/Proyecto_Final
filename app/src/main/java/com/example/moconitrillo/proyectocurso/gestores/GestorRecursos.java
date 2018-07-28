package com.example.moconitrillo.proyectocurso.gestores;

import com.example.moconitrillo.proyectocurso.accesodatos.AccesoWebservice;
import com.example.moconitrillo.proyectocurso.accesodatos.AccesoWebserviceImgur;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GestorRecursos {
    private static AccesoWebservice singletonAccesoWebservice;
    private static AccesoWebserviceImgur singletonAccesoWebserviceImgur;

    public static AccesoWebservice obtenerServicioAverias()
    {
        if(singletonAccesoWebservice == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ValoresGlobales.VARIABLE_URL_SERVICIO_AVERIAS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            //Se crea la instancia de la interfaz ServicioPosts
            singletonAccesoWebservice = retrofit.create(AccesoWebservice.class);
        }

        //Se retorna la instancia
        return singletonAccesoWebservice;
    }

    public static AccesoWebserviceImgur obtenerServicioImgur()
    {
        if(singletonAccesoWebserviceImgur == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ValoresGlobales.VARIABLE_URL_SERVICIO_IMGUR)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            //Se crea la instancia de la interfaz ServicioPosts
            singletonAccesoWebserviceImgur = retrofit.create(AccesoWebserviceImgur.class);
        }

        //Se retorna la instancia
        return singletonAccesoWebserviceImgur;
    }
}

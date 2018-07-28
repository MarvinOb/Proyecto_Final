package com.example.moconitrillo.proyectocurso.utilitarios;

import android.content.Context;
import android.content.SharedPreferences;


public class ManagerPreferencias {
    private SharedPreferences prefs;
    private Context contexto;
    private String nombreUsuario;
    private boolean recordarUsuario;

    public ManagerPreferencias(Context contexto)
    {
        this.contexto=contexto;
        prefs =this.contexto.getSharedPreferences(ValoresGlobales.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        leerPreferencias();
    }

    private void leerPreferencias()
    {
        try
        {
            setNombreUsuario(prefs.getString(ValoresGlobales.COLUMNA_NOMBRE_USUARIO_PREFERENCIAS,""));
            setRecordarUsuario(prefs.getBoolean(ValoresGlobales.COLUMNA_RECORDAR_USUARIO_PREFERENCIAS,false));
        }
        catch (Exception e)
        {

        }
    }

    public boolean guardarPreferencias()
    {
        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(ValoresGlobales.COLUMNA_NOMBRE_USUARIO_PREFERENCIAS, getNombreUsuario());
            editor.putBoolean(ValoresGlobales.COLUMNA_RECORDAR_USUARIO_PREFERENCIAS, isRecordarUsuario());
            editor.commit();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public boolean isRecordarUsuario() {
        return recordarUsuario;
    }

    public void setRecordarUsuario(boolean recordarUsuario) {
        this.recordarUsuario = recordarUsuario;
    }
}

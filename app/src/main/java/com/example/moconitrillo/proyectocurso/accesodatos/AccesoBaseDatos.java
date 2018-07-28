package com.example.moconitrillo.proyectocurso.accesodatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.moconitrillo.proyectocurso.R;
import com.example.moconitrillo.proyectocurso.entidades.Averia;
import com.example.moconitrillo.proyectocurso.entidades.Usuario;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;

import java.sql.SQLException;

public class AccesoBaseDatos  extends OrmLiteSqliteOpenHelper {
    private Dao<Usuario, Integer> usuarioDao=null;

    public AccesoBaseDatos(Context context)
    {
        super(context, ValoresGlobales.NOMBREA_BASE_DATOS,null, ValoresGlobales.NUMERO_VERSION_BASE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Usuario.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Usuario.class, true);
            onCreate(db, connectionSource);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<Usuario, Integer> getUsuarioDao() throws SQLException {
        if (usuarioDao == null) {
            usuarioDao = getDao(Usuario.class);
        }
        return usuarioDao;
    }

    @Override
    public void close() {
        usuarioDao=null;
        super.close();
    }
}

package com.example.moconitrillo.proyectocurso.logica;

import android.content.Context;
import android.os.Parcelable;
import android.widget.Toast;

import com.example.moconitrillo.proyectocurso.accesodatos.AccesoBaseDatos;
import com.example.moconitrillo.proyectocurso.entidades.Usuario;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

public class Usuarios {
    private AccesoBaseDatos baseDatos;
    private Context contexto;

    public Usuarios(Context contexto)
    {
        this.contexto=contexto;
        if(baseDatos==null)
        {
            baseDatos=new AccesoBaseDatos(this.contexto);
        }
    }

    /*método que busca un usuario por nombre y contraseña. Retorna un objeto Usuario*/
    public Usuario validarUsuario(String nombre, String contrasena)
    {
        try
        {
            Dao<Usuario,Integer> usuarioDao=baseDatos.getUsuarioDao();
            List<Usuario> listaUsuarios=usuarioDao.queryBuilder().where().eq(ValoresGlobales.NOMBRE_COLUMNA_NOMBRE_USUARIO,nombre)
                    .and().eq(ValoresGlobales.NOMBRE_COLUMNA_CONTRASENA_USUARIO,contrasena).query();
            if (listaUsuarios.size()>0)
            {
                return listaUsuarios.get(0);
            }
            else
                {
                    return null;
                }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /*método que busca un usuario por nombre. Retorna un usuario*/
    public Usuario getUsuario(String nombre)
    {
        try
        {
            Dao<Usuario,Integer> usuarioDao=baseDatos.getUsuarioDao();
            List<Usuario> listaUsuarios=usuarioDao.queryBuilder().where().eq(ValoresGlobales.NOMBRE_COLUMNA_NOMBRE_USUARIO,nombre).query();
            if (listaUsuarios.size()>0)
            {
                return listaUsuarios.get(0);
            }
            else
            {
                return null;
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /*Método que crea un nuevo usuario. Requiere un objeto Usuario. Retorna un boolean*/
    public boolean crearUsuario(Usuario nuevoUsuario)
    {
        try
        {
            Dao<Usuario,Integer> usuarioDao=baseDatos.getUsuarioDao();

            int resultado= usuarioDao.create(nuevoUsuario);

            if(resultado>0)
                return true;
            else
                return false;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /*Método que realiza las validaciones para el objeto Usuario. Recibe un objeto Usuario y una
    confirmación de la contraseña. Retorna un int indicando el dato que incumple*/
    public int verificarObjetoUsuario(Usuario nuevoUsuario, String confirmarContrasena)
    {
        if(nuevoUsuario.getNombre().trim().length()==0)
        {
            Toast.makeText(this.contexto,"Debe ingresar un nombre",Toast.LENGTH_LONG).show();
            return 1;
        }

        if(nuevoUsuario.getCorreo().trim().length()==0)
        {
            Toast.makeText(this.contexto,"Debe ingresar un correo electrónico para el usuario",Toast.LENGTH_LONG).show();
            return 2;
        }

        if(!verificarCorreo(nuevoUsuario.getCorreo().trim()))
        {
            Toast.makeText(this.contexto,"El correo ingresado no tiene el formato correcto, ingrese un correo electrónico válido",Toast.LENGTH_LONG).show();
            return 2;
        }

        if(nuevoUsuario.getTelefono().trim().length()==0)
        {
            Toast.makeText(this.contexto,"Debe ingresar un número de teléfono para el usuario",Toast.LENGTH_LONG).show();
            return 3;
        }

        if(nuevoUsuario.getNumeroCedula().trim().length()==0)
        {
            Toast.makeText(this.contexto,"Debe ingresar un número de cédula para el usuario",Toast.LENGTH_LONG).show();
            return 4;
        }

        if(nuevoUsuario.getNombreUsuario().trim().length()==0)
        {
            Toast.makeText(this.contexto,"Debe ingresar un nombre de usuario",Toast.LENGTH_LONG).show();
            return 5;
        }

        if(nuevoUsuario.getContrasena().trim().length()==0)
        {
            Toast.makeText(this.contexto,"Debe ingresar una contraseña",Toast.LENGTH_LONG).show();
            return 6;
        }

        if(confirmarContrasena.trim().length()==0)
        {
            Toast.makeText(this.contexto,"Debe ingresar una confirmación de la contraseña para verificar que la ha digitado correctamente",Toast.LENGTH_LONG).show();
            return 7;
        }

        /*si todas las validaciones arriba no aplican se devuelve un 0 para indicar que todo es correcto*/
        return 0;
    }

    private  boolean verificarCorreo(String correo) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
    }


}

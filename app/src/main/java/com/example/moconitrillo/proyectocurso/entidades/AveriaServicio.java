package com.example.moconitrillo.proyectocurso.entidades;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class AveriaServicio implements Parcelable {
    private String id;
    private String imagen;
    private String tipo;
    private String descripcion;
    private String nombre;
    private Ubicacion ubicacion;
    private Usuario usuario;
    private String fecha;

    public AveriaServicio()
    {}

    public AveriaServicio(String id,String imagen,String tipo,String descripcion,String nombre,Ubicacion ubicacion, String fecha)
    {
        this.setId(id);
        this.setImagen(imagen);
        this.setTipo(tipo);
        this.setDescripcion(descripcion);
        this.setNombre(nombre);
        this.setUbicacion(ubicacion);
        this.setFecha(fecha);
    }

    protected AveriaServicio(Parcel in) {
        setId(in.readString());
        setImagen(in.readString());
        setTipo(in.readString());
        setDescripcion(in.readString());
        setNombre(in.readString());
        setFecha(in.readString());
        //setUbicacion(in.readParcelable(Ubicacion.class.getClassLoader()));
        //setUsuario(in.readParcelable(Usuario.class.getClassLoader()));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getImagen());
        dest.writeString(getTipo());
        dest.writeString(getDescripcion());
        dest.writeString(getNombre());
        dest.writeParcelable(getUbicacion(), flags);
        dest.writeParcelable(getUsuario(), flags);
        dest.writeString(getFecha());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AveriaServicio> CREATOR = new Creator<AveriaServicio>() {
        @Override
        public AveriaServicio createFromParcel(Parcel in) {
            return new AveriaServicio(in);
        }

        @Override
        public AveriaServicio[] newArray(int size) {
            return new AveriaServicio[size];
        }
    };

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if(!id.trim().equals("")) {
            this.id = id;
        }
        else
        {
            this.id=random();
        }
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    private static String random() {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(ValoresGlobales.VARIABLE_MAXIMO_LETRAS_ID_GENERADO_AVERIA);
        for(int i=0;i<ValoresGlobales.VARIABLE_MAXIMO_LETRAS_ID_GENERADO_AVERIA;++i)
            sb.append(ValoresGlobales.VARIABLE_CARACTERES_ACEPTADOS_ID_AVERIA.charAt(random.nextInt(ValoresGlobales.VARIABLE_CARACTERES_ACEPTADOS_ID_AVERIA.length())));
        return sb.toString();
    }

    public boolean verificarAveria(Context contexto)
    {
        if(getTipo().trim().equals(""))
        {
            Toast.makeText(contexto,"No se ha ingresado el tipo de la avería",Toast.LENGTH_LONG);
            return false;
        }

        if(getDescripcion().trim().equals(""))
        {
            Toast.makeText(contexto,"No se ha ingresado la descripción de la avería",Toast.LENGTH_LONG);
            return false;
        }

        if(getNombre().trim().equals(""))
        {
            Toast.makeText(contexto,"No se ha ingresado el nombre de la avería",Toast.LENGTH_LONG);
            return false;
        }

        if(getFecha().trim().equals(""))
        {
            Toast.makeText(contexto,"No se ha ingresado la fecha de la avería",Toast.LENGTH_LONG);
            return false;
        }

        try{
            SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
            Date temp=format.parse(getFecha());
        }
        catch (ParseException ex)
        {
            Toast.makeText(contexto,"La fecha ingresada no tiene un formato correcto de día/mes/año",Toast.LENGTH_LONG);
            return false;
        }

        return true;
    }
}

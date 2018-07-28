package com.example.moconitrillo.proyectocurso.entidades;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = ValoresGlobales.NOMBRE_TABLA_USUARIOS)
public class Usuario  implements Parcelable {
    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_NOMBRE_USUARIO, id = true)
    private String nombreUsuario;

    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_NOMBRE_MOSTRAR_USUARIO, canBeNull = false)
    private String nombre;

    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_CORREO_USUARIO, canBeNull = false)
    private String correo;

    @SerializedName("tel")
    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_TELEFONO_USUARIO, canBeNull = false)
    private String telefono;

    @SerializedName("cedula")
    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_NUMERO_CEDULA_USUARIO, canBeNull = false)
    private String numeroCedula;

    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_CONTRASENA_USUARIO, canBeNull = false)
    private String contrasena;

    /*@ForeignCollectionField(columnName = ValoresGlobales.NOMBRE_COLUMNA_FOREIGN_COLLECTION_FIELD_AVERIA,eager = true)
    private ForeignCollection<Averia> averias;*/

    public Usuario()
    {}

    public Usuario(String nombreUsuario, String nombre, String correo, String telefono, String numeroCedula, String contrasena)
    {
        setNombreUsuario(nombreUsuario);
        setNombre(nombre);
        setCorreo(correo);
        setTelefono(telefono);
        setNumeroCedula(numeroCedula);
        setContrasena(contrasena);
    }

    public Usuario(Parcel in)
    {
        setNombreUsuario(in.readString());
        setNombre(in.readString());
        setCorreo(in.readString());
        setTelefono(in.readString());
        setNumeroCedula(in.readString());
        setContrasena(in.readString());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest,int flags){
        dest.writeString(getNombreUsuario());
        dest.writeString(getNombre());
        dest.writeString(getCorreo());
        dest.writeString(getTelefono());
        dest.writeString(getNumeroCedula());
        dest.writeString(getContrasena());
    }

    public static final Parcelable.Creator<Usuario> CREATOR=new Parcelable.Creator<Usuario>()
    {
        public Usuario createFromParcel(Parcel in)
        {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size)
        {
            return new Usuario[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNumeroCedula() {
        return numeroCedula;
    }

    public void setNumeroCedula(String numeroCedula) {
        this.numeroCedula = numeroCedula;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}

package com.example.moconitrillo.proyectocurso.entidades;

import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = ValoresGlobales.NOMBRE_TABLA_AVERIAS)
public class Averia {

    @DatabaseField(id = true, columnName = ValoresGlobales.NOMBRE_COLUMNA_ID_AVERIA)
    private String idAveria;

    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_IMAGEN_AVERIA, canBeNull = false)
    private String imagen;

    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_TIPO_AVERIA, canBeNull = false)
    private String tipo;

    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_DESCRIPCION_AVERIA, canBeNull = false)
    private String descripcion;

    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_NOMBRE_AVERIA, canBeNull = false)
    private String nombre;

    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_LATITUD_AVERIA, canBeNull = false)
    private double latitud;

    @DatabaseField(columnName = ValoresGlobales.NOMBRE_COLUMNA_LONGITUD_AVERIA, canBeNull = false)
    private double longitud;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Usuario usuario;


    public Averia()
    {}

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

    public String getIdAveria() {
        return idAveria;
    }

    public void setIdAveria(String idAveria) {
        this.idAveria = idAveria;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}

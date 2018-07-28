package com.example.moconitrillo.proyectocurso.utilitarios;

public class ValoresGlobales {

    //valores de la base de datos
    public static final String NOMBREA_BASE_DATOS="ormlite.db";
    public static final int NUMERO_VERSION_BASE_DATOS=1;

    public static final String NOMBRE_TABLA_AVERIAS ="averia";
    public static final String NOMBRE_COLUMNA_ID_AVERIA ="idAveria";
    public static final String NOMBRE_COLUMNA_IMAGEN_AVERIA ="imagen";
    public static final String NOMBRE_COLUMNA_TIPO_AVERIA="tipo";
    public static final String NOMBRE_COLUMNA_DESCRIPCION_AVERIA="descripcion";
    public static final String NOMBRE_COLUMNA_NOMBRE_AVERIA="nombre";
    public static final String NOMBRE_COLUMNA_LATITUD_AVERIA="latitud";
    public static final String NOMBRE_COLUMNA_LONGITUD_AVERIA="longitud";

    public static final String NOMBRE_TABLA_USUARIOS="usuario";
    public static final String NOMBRE_COLUMNA_NOMBRE_USUARIO="nombreUsuario";
    public static final String NOMBRE_COLUMNA_NOMBRE_MOSTRAR_USUARIO="nombre";
    public static final String NOMBRE_COLUMNA_CORREO_USUARIO="correo";
    public static final String NOMBRE_COLUMNA_TELEFONO_USUARIO="telefono";
    public static final String NOMBRE_COLUMNA_NUMERO_CEDULA_USUARIO="numeroCedula";
    public static final String NOMBRE_COLUMNA_CONTRASENA_USUARIO="contrasena";
    public static final String NOMBRE_COLUMNA_FOREIGN_COLLECTION_FIELD_AVERIA="averias";



    //valores de preferencias
    public static final String NOMBRE_PREFERENCIAS = "app.preferencias";
    public static final String COLUMNA_NOMBRE_PREFERENCIAS="nombreMostrarUsuario";
    public static final String COLUMNA_NOMBRE_USUARIO_PREFERENCIAS="nombreUsuario";
    public static final String COLUMNA_CORREO_PREFERENCIAS="correoUsuario";
    public static final String COLUMNA_TELEFONO_PREFERENCIAS="correoUsuario";
    public static final String COLUMNA_CEDULA_PREFERENCIAS="cedulaUsuario";
    public static final String COLUMNA_RECORDAR_USUARIO_PREFERENCIAS="recordarUsuario";

    //valores varios globales
    public static final String VARIABLE_LISTA_USUARIOS_LOGUEADOS="usuariosLogueados";
    public static final String VARIABLE_LISTA_AVERIAS="averiasCargadas";
    public static final String VARIABLE_LISTA_UBICACION_AVERIAS="ubicacionAveriasCargadas";
    public static final String VARIABLE_LISTA_USUARIO_AVERIAS="usuarioAveriasCargadas";
    public static final String VARIABLE_ID_AVERIA_SELECCIONADA="idSeleccionado";
    public static final String VARIABLE_FUENTE_CREACION_AVERIA="fuenteCreacionAveria";
    public static final int VARIABLE_MAXIMO_LETRAS_ID_GENERADO_AVERIA=15;
    public static final String VARIABLE_CARACTERES_ACEPTADOS_ID_AVERIA="0123456789-qwertyuiopasdfghjklzxcvbnm";
    public static final int AVERIA_CREADA_EN_LISTA=1;
    public static final int AVERIA_CREADA_EN_MAPA=2;

    //valores webservice Averías
    public static final String VARIABLE_URL_SERVICIO_AVERIAS="https://fn3arhnwsg.execute-api.us-west-2.amazonaws.com/produccion/";
    public static final String VARIEBLE_SERVICIO_AVERIAS_GET_TODAS="averias";
    public static final String VARIEBLE_SERVICIO_AVERIAS_GET_ID="averias/{id}";
    public static final String VARIABLE_SERVICIO_AVERIAS_GET_ID_NOMBRE_PARAMETRO="id";
    public static final String VARIEBLE_SERVICIO_AVERIAS_POST_NUEVA="averias/";
    public static final String VARIEBLE_SERVICIO_AVERIAS_POST_BORRAR="averias/{id}";
    public static final String VARIEBLE_SERVICIO_AVERIAS_POST_MODIFICAR="averias/{id}";
    public static final String VARIABLE_X_API_KEY="x-api-key:rabArf10E86thWRQ5u4MH3pFXVpiQiXv8jg1c4hO";

    //valores webservice Imgur
    public static final String VARIABLE_URL_SERVICIO_IMGUR="https://api.imgur.com/3/";


    //valores permisos
    public static final int SOLICITUD_TOMAR_FOTO = 101;
    public static final int SOLICITUD_ESCRITURA_EXTERNA = 102;
    public static final int SOLICITUD_UBICACION = 103;
    public static final int SOLICITUD_LECTURA_EXTERNA = 104;
}

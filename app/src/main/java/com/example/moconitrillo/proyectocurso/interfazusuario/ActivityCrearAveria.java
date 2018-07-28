package com.example.moconitrillo.proyectocurso.interfazusuario;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.moconitrillo.proyectocurso.R;
import com.example.moconitrillo.proyectocurso.accesodatos.AccesoWebservice;
import com.example.moconitrillo.proyectocurso.accesodatos.AccesoWebserviceImgur;
import com.example.moconitrillo.proyectocurso.entidades.AveriaServicio;
import com.example.moconitrillo.proyectocurso.entidades.ImageResponse;
import com.example.moconitrillo.proyectocurso.entidades.Ubicacion;
import com.example.moconitrillo.proyectocurso.entidades.Usuario;
import com.example.moconitrillo.proyectocurso.gestores.GestorRecursos;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCrearAveria extends AppCompatActivity {

    @BindView(R.id.etNombreAveriaCrear)
    EditText etNombreAveriaCrear;

    @BindView(R.id.etTipoAveriaCrear)
    EditText etTipoAveriaCrear;

    @BindView(R.id.etFechaAveriaCrear)
    TextView etFechaAveriaCrear;

    @BindView(R.id.etDescripcionAveriaCrear)
    EditText etDescripcionAveriaCrear;

    @BindView(R.id.tvUbicacionLatitudCrear)
    TextView tvUbicacionLatitudCrear;

    @BindView(R.id.tvUbicacionLongitudCrear)
    TextView tvUbicacionLongitudCrear;

    @BindView(R.id.ivImagenCrear)
    ImageView ivImagenCrear;

    @BindView(R.id.btnCrearAveria)
    Button btnCrearAveria;

    private Usuario usuario;

    private int fuenteCreacionBoleta=0; //indica si la boleta fue creada desde el fragment de la lista con las averías (valor 1) o desde el fragment con el mapa (valor 2)

    private AccesoWebservice accesoWebService;

    private AccesoWebserviceImgur accesoWebserviceImgur;

    private Uri uriFotografia; //variable con dirección de la fotografía

    private LocationManager locationManager;

    private int tipoUbicacion = 2; //tipo de ubicación 0 es GPS, 1 es Network

    private Ubicacion ubicacionAveria; //ubicacion con latitud y longitud para la avería

    private File fotografia; //fotografia a subir

    private AveriaServicio nuevaAveria; //objeto con la nueva avería

    private MaterialDialog dialogoEspera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_averia);

        ButterKnife.bind(this);

        Date fechaActual= Calendar.getInstance().getTime();
        etFechaAveriaCrear.setText(new SimpleDateFormat("dd/MM/yyyy").format(fechaActual));


        fuenteCreacionBoleta=getIntent().getIntExtra(ValoresGlobales.VARIABLE_FUENTE_CREACION_AVERIA,0);

        List<Usuario> usuarioLogueado = getIntent().getParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS);
        if(usuarioLogueado.size()>0)
        {
            usuario=usuarioLogueado.get(0);
        }

        if(accesoWebService==null)
        {
            accesoWebService= GestorRecursos.obtenerServicioAverias();
        }

        if(accesoWebserviceImgur==null)
        {
            accesoWebserviceImgur= GestorRecursos.obtenerServicioImgur();
        }

        if(fuenteCreacionBoleta==ValoresGlobales.AVERIA_CREADA_EN_MAPA)
        {
            double lat=getIntent().getDoubleExtra(ValoresGlobales.NOMBRE_COLUMNA_LATITUD_AVERIA,0);
            double lon=getIntent().getDoubleExtra(ValoresGlobales.NOMBRE_COLUMNA_LONGITUD_AVERIA,0);
            this.ubicacionAveria=new Ubicacion();
            this.ubicacionAveria.lat=lat;
            this.ubicacionAveria.lon=lon;
            tvUbicacionLatitudCrear.setText(this.ubicacionAveria.lat+"");
            tvUbicacionLongitudCrear.setText(this.ubicacionAveria.lon+"");
        }
        else {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            obtenerLocalizacion();
        }
    }

    @OnClick(R.id.btnCrearAveria)
    public void agregarAveria()
    {
        nuevaAveria=new AveriaServicio();

        nuevaAveria.setId(""); //se pone en vacio para que en el método se escoja con un random
        nuevaAveria.setTipo(etTipoAveriaCrear.getText().toString());
        nuevaAveria.setDescripcion(etDescripcionAveriaCrear.getText().toString());
        nuevaAveria.setNombre(etNombreAveriaCrear.getText().toString());
        nuevaAveria.setUbicacion(ubicacionAveria);
        nuevaAveria.setUsuario(usuario);
        nuevaAveria.setFecha(etFechaAveriaCrear.getText().toString());

        if(!nuevaAveria.verificarAveria(getApplicationContext()))
        {
            return;
        }

        mostrarDialogoEspera(true,true);
        cargaFoto(); //cargar fotografia a Imgur
    }

    @OnClick(R.id.btnTomarFoto)
    public void tomarFoto()
    {
        verificarPermisosEscrituraExterna();
    }

    @OnClick(R.id.etFechaAveriaCrear)
    public void escogerFecha()
    {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                StringBuffer strBuf = new StringBuffer();
                strBuf.append(ajustarNumero(dayOfMonth+""));
                strBuf.append("/");
                strBuf.append(ajustarNumero(""+(month+1)));
                strBuf.append("/");
                strBuf.append(year);

                etFechaAveriaCrear.setText(strBuf.toString());
            }
        };

        // Get current year, month and day.
        Calendar now = Calendar.getInstance();
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH);
        int day = now.get(java.util.Calendar.DAY_OF_MONTH);

        // Create the new DatePickerDialog instance.
        DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityCrearAveria.this, onDateSetListener, year, month, day);

        // Set dialog icon and title.
        //datePickerDialog.setIcon(R.drawable.if_snowman);
        datePickerDialog.setTitle("Seleccione una fecha");

        // Popup the dialog.
        datePickerDialog.show();
    }

    private String ajustarNumero(String texto)
    {
        if(texto.trim().length()==1)
            return "0"+texto;
        else
            return  texto;
    }

    private void guardarAveria()
    {
        accesoWebService.crearNuevaAveria(nuevaAveria).enqueue(new Callback<AveriaServicio>() {
            @Override
            public void onResponse(Call<AveriaServicio> call, Response<AveriaServicio> response) {
                AveriaServicio averiaCargada= response.body();
                if(averiaCargada!=null)
                {
                    resultadoAveriaCreada(true);
                }
            }

            @Override
            public void onFailure(Call<AveriaServicio> call, Throwable t) {
                resultadoAveriaCreada(false);
            }
        });
    }

    private void resultadoAveriaCreada(boolean estado)
    {
        if(estado)
        {
            mostrarDialogoEspera(false,true);

            Toast.makeText(this,"Se creó satisfactoriamente la avería",Toast.LENGTH_SHORT).show();

            Intent intent=new Intent(this,ActivityPrincipal.class);
            ArrayList<Usuario> listaUsuariosLogueados=new ArrayList<Usuario>();
            listaUsuariosLogueados.add(usuario);
            intent.putParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS,listaUsuariosLogueados);
            startActivity(intent);
        }
        else
        {
            mostrarDialogoEspera(false,true);
            Toast.makeText(this,"Hubo un error al crear la avería",Toast.LENGTH_SHORT).show();
        }
    }

    private void verificarPermisosEscrituraExterna() {
        //Obtenemos el estado actual de los permisos
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Si ya tenemos permisos, continuamos tomando la foto
        //Si no, pedimos permiso
        if(permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            continuarTomarFoto();
        } else
            {
                pedirPermisoCamara();
            }
    }

    public void pedirPermisoCamara(){
        //Hacemos la solicitud de permiso
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                ValoresGlobales.SOLICITUD_ESCRITURA_EXTERNA);
    }

    //Este callback es llamado cuando un usuario contesta
    //a la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Si obtuvimos valores en grantResults y el primer elemento
        //es de PERMISSION_GRANTED (permiso concedido), volvemos a llamar
        //a verificarPermisos.

        //Si el usuario no dio permiso, llamamos finish() para cerrar la
        //aplicacion
        if(requestCode==ValoresGlobales.SOLICITUD_ESCRITURA_EXTERNA) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                verificarPermisosEscrituraExterna();
            } else {
                finish();
            }
        }
        else
            if(requestCode==ValoresGlobales.SOLICITUD_UBICACION)
            {
                //Si el usuario nos dio permiso, entonces podemos llamar a
                //chequearPermiso de nuevo. Si no, no hacemos nada (y el boton de
                //ubicacion no se va a mostrar)
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    verificarPermisoUbicacion();
                }
            }
    }

    private void continuarTomarFoto() {
        //Llamamos al metodo crearArchivo para obtener un
        //archivo en el cual guardar la foto
        fotografia=crearArchivo();


        //Construimos un intent con una peticion de captura
        //de imagenes
        Intent takePictureIntent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Guardamos en el directorio usando el FileProvider:
        uriFotografia = FileProvider.getUriForFile(this,
                "com.example.moconitrillo.album",
                fotografia);

        //Especificamos el URI en el que queremos que se guarde
        //la imagen
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotografia);
        //Ejecutamos el intent, cediendo control a la aplicacion
        //de toma de fotos que el usuario seleccione
        startActivityForResult(takePictureIntent, ValoresGlobales.SOLICITUD_TOMAR_FOTO);
    }

    private File crearArchivo() {
        try {
            //Creamos un nombre unico para el archivo, basado
            //en la fecha y hora actual
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            String imageFileName = "JPEG_" + timeStamp;

            File storageDir =
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            //Creamos el archivo para la imagen...
            File image = File.createTempFile(
                    imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            );
            //...y lo retornamos
            return image;
        }catch(Exception e){
            Log.d("crearArchivo", e.getMessage());
            return null;
        }
    }

    //Al haber llamado a onStartActivityForResult, indicamos al
    //sistema que llame a este callback una vez la foto haya sido
    //capturada y almacenada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verificamos que el codigo de respuesta sea igual al codigo
        //de peticion que especificamos al ejecutar el intent

        //Tambien verificamos que el codigo resultado sea RESULT_OK,
        //lo cual indica que la foto fue capturada exitosamente.
        if (requestCode == ValoresGlobales.SOLICITUD_TOMAR_FOTO &&
                resultCode == RESULT_OK) {
            try {
                //Obtenemos el BitMap a partir del URI que habiamos
                //obtenido anteriormente
                Bitmap imageBitmap =
                        MediaStore.Images.Media.
                                getBitmap(getContentResolver(), uriFotografia);

                //Mostramos el bitmap en el ImageView declarado
                //en nuestro layout file
                ivImagenCrear.setImageBitmap(imageBitmap);
            }catch(Exception e){
                Log.d("onActivityResult", e.getMessage());
            }
        }
    }

    private void cargaFoto()
    {
        final Call<ImageResponse> call=accesoWebserviceImgur.postImage(nuevaAveria.getNombre(),"Descripción avería: "+nuevaAveria.getDescripcion(),"","",
                MultipartBody.Part.createFormData("image",fotografia.getName(), RequestBody.create(MediaType.parse("image/*"),fotografia)));

        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if (response == null) {
                        Toast.makeText(ActivityCrearAveria.this,"No se cargó la foto a Imgur",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.isSuccessful()) {
                    Log.d("URL Picture", "http://imgur.com/" + response.body().data.link);
                    nuevaAveria.setImagen(response.body().data.link);
                    guardarAveria();
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                mostrarDialogoEspera(false,true);
                Toast.makeText(getApplicationContext(),"Hubo un error con la carga de la fotografía",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerLocalizacion()
    {
        boolean gps_enabled = false;
        boolean network_enabled = false;

        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        //gps = 0
        //network = 1

        if (gps_enabled){
            tipoUbicacion = 0;
            gps_loc = verificarPermisoUbicacion();
        }
        if (network_enabled){
            tipoUbicacion = 1;
            net_loc = verificarPermisoUbicacion();
        }

        if (gps_loc != null && net_loc != null) {

            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

            asignarCoordenadas(finalLoc);
        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }

            asignarCoordenadas(finalLoc);
        }
    }

    private Location verificarPermisoUbicacion() {
        Location loc = null;
        //Obtenemos el estado del permiso de ubicacion
        int state = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckAC = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        //Si lo tenemos, habilitamos el boton de ubicacion del usuario
        if (state == PackageManager.PERMISSION_GRANTED && permissionCheckAC == PackageManager.PERMISSION_GRANTED) {
            //hacer lo que se ocupa
            if(tipoUbicacion == 0)
                loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            else if(tipoUbicacion == 1)
                loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } else {
            //Si no, pedimos permiso
            pedirPermisoUbicacion();
            int state2 = ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCheckAC2 = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            //Si lo tenemos, habilitamos el boton de ubicacion del usuario
            if (state2 == PackageManager.PERMISSION_GRANTED && permissionCheckAC2 == PackageManager.PERMISSION_GRANTED) {
                //hacer lo que se ocupa
                if(tipoUbicacion == 0)
                    loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                else if(tipoUbicacion == 1)
                    loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        return loc;
    }

    public void pedirPermisoUbicacion(){
        //Pedimos permiso para el de tipo ACCESS_FINE_LOCATION
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ValoresGlobales.SOLICITUD_UBICACION);
    }

    private void asignarCoordenadas (Location location)
    {
        if(location!= null){
            double latitud = location.getLatitude();
            double longitud = location.getLongitude();

            this.ubicacionAveria=new Ubicacion();
            this.ubicacionAveria.lat=latitud;
            this.ubicacionAveria.lon=longitud;
            tvUbicacionLatitudCrear.setText("Latitud: "+latitud);
            tvUbicacionLongitudCrear.setText("Longitud: "+longitud);
        }

    }

    private void mostrarDialogoEspera(Boolean mostrar, Boolean horizontal)
    {
        if(mostrar)
        {
            dialogoEspera=new MaterialDialog.Builder(this)
                    .title("Progreso")
                    .content("Espere un momento ...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(horizontal)
                    .show();
        }
        else
        {
            dialogoEspera.dismiss();
        }
    }
}

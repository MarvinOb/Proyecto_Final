package com.example.moconitrillo.proyectocurso.interfazusuario;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.squareup.picasso.Picasso;

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

public class ActivityEditarAveria extends AppCompatActivity {
    @BindView(R.id.tvIdAveriaEditar)
    TextView tvIdAveriaEditar;

    @BindView(R.id.tvUsuarioEditar)
    TextView tvUsuarioEditar;

    @BindView(R.id.etNombreAveriaEditar)
    EditText etNombreAveriaEditar;

    @BindView(R.id.etTipoAveriaEditar)
    EditText etTipoAveriaEditar;

    @BindView(R.id.etFechaAveriaEditar)
    TextView etFechaAveriaEditar;

    @BindView(R.id.etDescripcionAveriaEditar)
    EditText etDescripcionAveriaEditar;

    @BindView(R.id.tvUbicacionLatitudEditar)
    TextView tvUbicacionLatitudEditar;

    @BindView(R.id.tvUbicacionLongitudEditar)
    TextView tvUbicacionLongitudEditar;

    @BindView(R.id.btnTomarFotoEditar)
    Button btnTomarFotoEditar;

    @BindView(R.id.btnEditarAveria)
    Button btnEditarAveria;

    @BindView(R.id.ivImagenEditar)
    ImageView ivImagenEditar;

    private Usuario usuario;

    private AccesoWebservice accesoWebService;

    private AccesoWebserviceImgur accesoWebserviceImgur;

    private Uri uriFotografia; //variable con dirección de la fotografía

    private AveriaServicio averiaCargada;

    private AveriaServicio nuevaAveria;

    private String idAveria;

    private File fotografia; //fotografia a subir

    private MaterialDialog dialogoEspera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_averia);

        ButterKnife.bind(this);

        usuario = getIntent().getExtras().getParcelable(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS);

        this.idAveria=getIntent().getStringExtra(ValoresGlobales.VARIABLE_ID_AVERIA_SELECCIONADA);

        if(accesoWebService==null)
        {
            accesoWebService= GestorRecursos.obtenerServicioAverias();
        }

        if(accesoWebserviceImgur==null)
        {
            accesoWebserviceImgur= GestorRecursos.obtenerServicioImgur();
        }

        cargarDetalle();
    }

    @OnClick(R.id.etFechaAveriaEditar)
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

                etFechaAveriaEditar.setText(strBuf.toString());
            }
        };

        // Get current year, month and day.
        Calendar now = Calendar.getInstance();
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH);
        int day = now.get(java.util.Calendar.DAY_OF_MONTH);

        // Create the new DatePickerDialog instance.
        DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityEditarAveria.this, onDateSetListener, year, month, day);

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

    @OnClick(R.id.btnTomarFotoEditar)
    public void tomarFoto()
    {
        verificarPermisosEscrituraExterna();
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
                ivImagenEditar.setImageBitmap(imageBitmap);
            }catch(Exception e){
                Log.d("onActivityResult", e.getMessage());
            }
        }
    }

    private void cargaFoto()
    {


        try {
            final Call<ImageResponse> call = accesoWebserviceImgur.postImage(nuevaAveria.getNombre(), "Descripción avería: " + nuevaAveria.getDescripcion(), "", "",
                    MultipartBody.Part.createFormData("image", fotografia.getName(), RequestBody.create(MediaType.parse("image/*"), fotografia)));

            call.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    if (response == null) {
                        Toast.makeText(ActivityEditarAveria.this, "No se cargó la foto a Imgur", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.isSuccessful()) {
                        Log.d("URL Picture", "http://imgur.com/" + response.body().data.link);
                        nuevaAveria.setImagen(response.body().data.link);
                        modificarAveria();
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Hubo un error con la carga de la fotografía", Toast.LENGTH_SHORT).show();

                }
            });
        }
        catch (Exception ex)
        {
            Log.d("Error",ex.getStackTrace().toString());
        }
        finally {
            mostrarDialogoEspera(false,true);
        }
    }

    private void cargarInterfaz()
    {
        tvIdAveriaEditar.setText(averiaCargada.getId());
        tvUsuarioEditar.setText(averiaCargada.getUsuario().getNombre());
        etNombreAveriaEditar.setText(averiaCargada.getNombre());
        etTipoAveriaEditar.setText(averiaCargada.getTipo());
        etFechaAveriaEditar.setText(averiaCargada.getFecha());
        etDescripcionAveriaEditar.setText(averiaCargada.getDescripcion());
        tvUbicacionLatitudEditar.setText("Latitud: "+averiaCargada.getUbicacion().lat);
        tvUbicacionLongitudEditar.setText("Longitud: "+averiaCargada.getUbicacion().lon);

        try {
            if(averiaCargada.getImagen().trim().contains("http://") || averiaCargada.getImagen().trim().contains("https://"))
            {
                if(!averiaCargada.getImagen().trim().toLowerCase().endsWith(".jpg"))
                    Picasso.get().load(averiaCargada.getImagen()+".jpg").fit().centerCrop().into(ivImagenEditar);
                else
                    Picasso.get().load(averiaCargada.getImagen()).fit().centerCrop().into(ivImagenEditar);
            }
            else
            {
                Picasso.get().load(R.drawable.img_sin_imagen).fit().centerCrop().into(ivImagenEditar);
            }
        }
        catch (Exception ex)
        {
            Picasso.get().load(R.drawable.img_sin_imagen).fit().centerCrop().into(ivImagenEditar);
            Log.d("Error",Log.getStackTraceString(ex.getCause().getCause()));
        }
    }

    private void cargarDetalle()
    {
        accesoWebService.obtenerDetallesAveria(this.idAveria).enqueue(new Callback<AveriaServicio>() {
            @Override
            public void onResponse(Call<AveriaServicio> call, Response<AveriaServicio> response) {
                averiaCargada= response.body();
                if(averiaCargada!=null)
                {
                    cargarInterfaz();
                }
            }

            @Override
            public void onFailure(Call<AveriaServicio> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.btnEditarAveria)
    public void editarAveria()
    {
        nuevaAveria=new AveriaServicio();
        Ubicacion ubicacion=new Ubicacion();
        ubicacion.lat=averiaCargada.getUbicacion().lat;
        ubicacion.lon=averiaCargada.getUbicacion().lon;

        nuevaAveria.setId(averiaCargada.getId());
        nuevaAveria.setTipo(etTipoAveriaEditar.getText().toString());
        nuevaAveria.setDescripcion(etDescripcionAveriaEditar.getText().toString());
        nuevaAveria.setNombre(etNombreAveriaEditar.getText().toString());
        nuevaAveria.setUbicacion(ubicacion);
        nuevaAveria.setUsuario(usuario);
        nuevaAveria.setFecha(etFechaAveriaEditar.getText().toString());

        if(nuevaAveria.verificarAveria(getApplicationContext())) {
            mostrarDialogoEspera(true,true);
            cargaFoto();
        }
    }

    private void modificarAveria()
    {
        try {
            accesoWebService.modificarAveria(nuevaAveria.getId(), nuevaAveria).enqueue(new Callback<AveriaServicio>() {
                @Override
                public void onResponse(Call<AveriaServicio> call, Response<AveriaServicio> response) {
                    averiaCargada = response.body();
                    if (averiaCargada != null) {
                        resultadoAveriaMoficada(true);
                    }
                }

                @Override
                public void onFailure(Call<AveriaServicio> call, Throwable t) {
                    resultadoAveriaMoficada(false);
                }
            });
        }
        catch (Exception ex)
        {
            Log.d("Error",ex.getStackTrace().toString());
        }
        finally {
            mostrarDialogoEspera(false,true);
        }
    }

    private void resultadoAveriaMoficada(boolean estado)
    {
        mostrarDialogoEspera(false,true);

        if(estado)
        {
            Toast.makeText(this,"Se modificó satisfactoriamente la avería",Toast.LENGTH_SHORT).show();

            Intent intent=new Intent(this,ActivityPrincipal.class);
            ArrayList<Usuario> listaUsuariosLogueados=new ArrayList<Usuario>();
            listaUsuariosLogueados.add(usuario);
            intent.putParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS,listaUsuariosLogueados);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this,"Hubo un error al modificar la avería",Toast.LENGTH_SHORT).show();
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

package com.example.moconitrillo.proyectocurso.interfazusuario;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.moconitrillo.proyectocurso.R;
import com.example.moconitrillo.proyectocurso.accesodatos.AccesoWebservice;
import com.example.moconitrillo.proyectocurso.entidades.AveriaServicio;
import com.example.moconitrillo.proyectocurso.entidades.Usuario;
import com.example.moconitrillo.proyectocurso.gestores.GestorRecursos;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityDetalleAveria extends AppCompatActivity {

    private Usuario usuarioLogueado;
    private AccesoWebservice accesoWebService;
    private AveriaServicio averiaCargada;
    private String idAveria;
    private boolean imagenValida=false;
    private MaterialDialog dialogoEspera;

    @BindView(R.id.tvIdAveriaDetalles)
    public TextView tvIdAveriaDetalles;

    @BindView(R.id.tvNombreAveriaDetalles)
    public TextView tvNombreAveriaDetalles;

    @BindView(R.id.tvTipoAveriaDetalles)
    public TextView tvTipoAveriaDetalles;

    @BindView(R.id.tvFechaAveriaDetalles)
    public TextView tvFechaAveriaDetalles;

    @BindView(R.id.tvDescripcionAveriaDetalles)
    public TextView tvDescripcionAveriaDetalles;

    @BindView(R.id.tvUsuarioAveriaDetalles)
    public TextView tvUsuarioAveriaDetalles;

    @BindView(R.id.tvUbicacionLatitudDetalles)
    public TextView tvUbicacionLatitudDetalles;

    @BindView(R.id.tvUbicacionLongitudDetalles)
    public TextView tvUbicacionLongitudDetalles;

    @BindView(R.id.ivImagenDetalles)
    public ImageView ivImagenDetalles;

    @BindView(R.id.btnEditarAveria)
    public Button btnEditarAveria;

    @BindView(R.id.btnBorrarAveria)
    public Button btnBorrarAveria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_averia);

        ButterKnife.bind(this);

        List<Usuario> usuarioLogueado = getIntent().getParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS);
        this.idAveria=getIntent().getStringExtra(ValoresGlobales.VARIABLE_ID_AVERIA_SELECCIONADA);

        if(usuarioLogueado.size()>0)
        {
            this.usuarioLogueado=usuarioLogueado.get(0);
        }

        if(accesoWebService==null)
        {
            accesoWebService= GestorRecursos.obtenerServicioAverias();
        }

        cargarDetalle();
    }

    @OnClick(R.id.ivImagenDetalles)
    public void verFoto()
    {
        if(imagenValida)
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            if(averiaCargada.getImagen().toLowerCase().endsWith(".jpg"))
                intent.setDataAndType(Uri.parse(averiaCargada.getImagen()), "image/*");
            else
                intent.setDataAndType(Uri.parse(averiaCargada.getImagen()+".jpg"), "image/*");
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnBorrarAveria)
    public void botonBorrarAveria()
    {
        new MaterialDialog.Builder(this)
                .title("Borrar")
                .content("¿Desea borrar esta avería?")
                .positiveText("Sí")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        borrarAveria();
                    }
                })
                .show();
    }

    public void borrarAveria() {
        try {
            mostrarDialogoEspera(true,true);

            accesoWebService.borrarAveria(this.idAveria).enqueue(new Callback<AveriaServicio>() {
                @Override
                public void onResponse(Call<AveriaServicio> call, Response<AveriaServicio> response) {
                    averiaCargada = response.body();
                    if (averiaCargada != null) {
                        resultadoAveriaBorrada(true);
                    }
                }

                @Override
                public void onFailure(Call<AveriaServicio> call, Throwable t) {
                    resultadoAveriaBorrada(false);
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

    @OnClick(R.id.btnEditarAveria)
    public void editarAveria()
    {
        Intent intent = new Intent(getBaseContext(), ActivityEditarAveria.class);
        intent.putExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS,usuarioLogueado);
        intent.putExtra(ValoresGlobales.VARIABLE_ID_AVERIA_SELECCIONADA,averiaCargada.getId());

        startActivity(intent);
    }

    private void resultadoAveriaBorrada(boolean estado)
    {
        mostrarDialogoEspera(false,true);

        if(estado) {
            Toast.makeText(this, "Se eliminó satisfactoriamente la avería", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, ActivityPrincipal.class);
            ArrayList<Usuario> listaUsuariosLogueados = new ArrayList<Usuario>();
            listaUsuariosLogueados.add(usuarioLogueado);
            intent.putParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS, listaUsuariosLogueados);

            startActivity(intent);
        }
        else
        {
            Toast.makeText(this,"Hubo un error al eliminar la avería",Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDetalle()
    {
        try {
            mostrarDialogoEspera(true,true);

            accesoWebService.obtenerDetallesAveria(this.idAveria).enqueue(new Callback<AveriaServicio>() {
                @Override
                public void onResponse(Call<AveriaServicio> call, Response<AveriaServicio> response) {
                    averiaCargada = response.body();
                    if (averiaCargada != null) {
                        cargarInterfaz();
                    }
                }

                @Override
                public void onFailure(Call<AveriaServicio> call, Throwable t) {

                }
            });
        }
        catch (Exception ex)
        {
            mostrarDialogoEspera(false,true);
            Log.d("Error",ex.getStackTrace().toString());
        }
    }

    private void cargarInterfaz()
    {
        tvIdAveriaDetalles.setText(averiaCargada.getId());
        tvNombreAveriaDetalles.setText(averiaCargada.getNombre());
        tvTipoAveriaDetalles.setText(averiaCargada.getTipo());
        tvFechaAveriaDetalles.setText(averiaCargada.getFecha());
        tvDescripcionAveriaDetalles.setText(averiaCargada.getDescripcion());
        tvUsuarioAveriaDetalles.setText(averiaCargada.getUsuario().getNombre());
        tvUbicacionLatitudDetalles.setText("Latitud: "+averiaCargada.getUbicacion().lat);
        tvUbicacionLongitudDetalles.setText("Longitud: "+averiaCargada.getUbicacion().lon);

        try {
            if(averiaCargada.getImagen().trim().contains("http://") || averiaCargada.getImagen().trim().contains("https://"))
            {
                if(!averiaCargada.getImagen().trim().toLowerCase().endsWith(".jpg"))
                    Picasso.get().load(averiaCargada.getImagen()+".jpg").fit().centerCrop().into(ivImagenDetalles);
                else
                    Picasso.get().load(averiaCargada.getImagen()).fit().centerCrop().into(ivImagenDetalles);
                this.imagenValida = true;
            }
            else
            {
                Picasso.get().load(R.drawable.img_sin_imagen).fit().centerCrop().into(ivImagenDetalles);
                this.imagenValida = false;
            }
        }
        catch (Exception ex)
        {
            this.imagenValida=false;
            Picasso.get().load(R.drawable.img_sin_imagen).fit().centerCrop().into(ivImagenDetalles);
            Log.d("Error",Log.getStackTraceString(ex.getCause().getCause()));
        }
        finally {
            mostrarDialogoEspera(false,true);
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

package com.example.moconitrillo.proyectocurso.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.moconitrillo.proyectocurso.R;
import com.example.moconitrillo.proyectocurso.accesodatos.AccesoWebservice;
import com.example.moconitrillo.proyectocurso.entidades.AveriaServicio;
import com.example.moconitrillo.proyectocurso.entidades.Usuario;
import com.example.moconitrillo.proyectocurso.gestores.GestorRecursos;
import com.example.moconitrillo.proyectocurso.interfazusuario.ActivityCrearAveria;
import com.example.moconitrillo.proyectocurso.interfazusuario.ActivityDetalleAveria;
import com.example.moconitrillo.proyectocurso.interfazusuario.ActivityPrincipal;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    @BindView(R.id.mapView)
    public MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<AveriaServicio> listaAverias=new ArrayList<AveriaServicio>();
    public boolean isMapViewReady=false;
    private ArrayList<LugaresMapa> listaMarcadores=new ArrayList<LugaresMapa>();
    private Usuario usuarioLogueado;
    private Context contexto;
    private AccesoWebservice accesoWebService;
    private MaterialDialog dialogoEspera;

    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mapa, container, false);

        usuarioLogueado =getArguments().getParcelable(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS);
        ButterKnife.bind(this,rootView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                isMapViewReady=true;

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        final String idAveria= (String) marker.getTag();

                        new MaterialDialog.Builder(contexto)
                                .title("Detalles")
                                .content("¿Desea ver el detalle de esta avería?")
                                .positiveText("Sí")
                                .negativeText("No")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent=new Intent(contexto, ActivityDetalleAveria.class);

                                        ArrayList<Usuario> listaUsuariosLogueados=new ArrayList<Usuario>();
                                        listaUsuariosLogueados.add(usuarioLogueado);
                                        intent.putParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS,listaUsuariosLogueados);
                                        intent.putExtra(ValoresGlobales.VARIABLE_ID_AVERIA_SELECCIONADA,idAveria);

                                        contexto.startActivity(intent);
                                    }
                                })
                                .show();

                        return false;
                    }
                });

                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        final LatLng localizacion=latLng;

                        new MaterialDialog.Builder(contexto)
                                .title("Detalles")
                                .content("¿Desea crear una avería en este lugar?")
                                .positiveText("Sí")
                                .negativeText("No")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent=new Intent(contexto, ActivityCrearAveria.class);
                                        ArrayList<Usuario> listaUsuariosLogueados=new ArrayList<Usuario>();
                                        listaUsuariosLogueados.add(usuarioLogueado);
                                        intent.putParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS,listaUsuariosLogueados);
                                        intent.putExtra(ValoresGlobales.VARIABLE_FUENTE_CREACION_AVERIA, ValoresGlobales.AVERIA_CREADA_EN_MAPA);
                                        intent.putExtra(ValoresGlobales.NOMBRE_COLUMNA_LATITUD_AVERIA,localizacion.latitude);
                                        intent.putExtra(ValoresGlobales.NOMBRE_COLUMNA_LONGITUD_AVERIA, localizacion.longitude);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                });

                if(listaMarcadores.size()==0) {
                    crearMarcadores();
                }

                verificarPermisosLocalizacion();
                cargarListaAverias();
            }
        });

        return rootView;
    }

    private void cargarListaAverias()
    {
        try {
            mostrarDialogoEspera(true,true);

            if (accesoWebService == null) {
                accesoWebService = GestorRecursos.obtenerServicioAverias();
            }

            accesoWebService.obtenerTodasAverias().enqueue(new Callback<List<AveriaServicio>>() {
                @Override
                public void onResponse(Call<List<AveriaServicio>> call, Response<List<AveriaServicio>> response) {
                    List<AveriaServicio> temp = response.body(); //recibir
                    ArrayList<AveriaServicio> tempNuevaLista = new ArrayList<AveriaServicio>(temp);

                    if (tempNuevaLista != null) {
                        listaAverias.clear();
                        listaAverias.addAll(0, tempNuevaLista);
                        crearMarcadores();
                    }
                }

                @Override
                public void onFailure(Call<List<AveriaServicio>> call, Throwable t) {

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

    private void verificarPermisosLocalizacion() {
        //Obtenemos el estado actual de los permisos
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            continuarConLocalizacionActual();
        } else
        {
            pedirPermisoCamara();
        }
    }

    public void pedirPermisoCamara(){
        //Hacemos la solicitud de permiso
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ValoresGlobales.SOLICITUD_UBICACION);
    }

    //Este callback es llamado cuando un usuario contesta
    //a la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Si obtuvimos valores en grantResults y el primer elemento
        //es de PERMISSION_GRANTED (permiso concedido), volvemos a llamar
        //a verificarPermisos.


        if(requestCode==ValoresGlobales.SOLICITUD_UBICACION)
        {
            //Si el usuario nos dio permiso, entonces podemos llamar a
            //chequearPermiso de nuevo. Si no, no hacemos nada (y el boton de
            //ubicacion no se va a mostrar)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                verificarPermisosLocalizacion();
            }
        }
    }

    public void continuarConLocalizacionActual()
    {
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onAttach(Context contexto)
    {
        super.onAttach(contexto);
        this.contexto=contexto;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onMarkerClick(final Marker marker)
    {
        return true;
    }

    private void crearMarcadores()
    {
        int indice=0;

        for(AveriaServicio averia:listaAverias)
        {
            listaMarcadores.add(new LugaresMapa(averia));
            Marker temp= googleMap.addMarker(listaMarcadores.get(indice).getMarcadorOpciones());
            temp.setTag(averia.getId());
            listaMarcadores.get(indice).setMarcador(temp);
            indice++;
        }
    }

    private void mostrarDialogoEspera(Boolean mostrar, Boolean horizontal)
    {
        if(mostrar)
        {
            dialogoEspera=new MaterialDialog.Builder(contexto)
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

    public class LugaresMapa
    {
        private LatLng latLng;
        private MarkerOptions marcadorOpciones;
        private Marker marcador;

        public LugaresMapa(AveriaServicio averia)
        {
            setLatLng(new LatLng(averia.getUbicacion().lat,averia.getUbicacion().lon));
            marcadorOpciones= new MarkerOptions().position(getLatLng()).title(averia.getNombre()).snippet(averia.getDescripcion());
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public void setLatLng(LatLng latLng) {
            this.latLng = latLng;
        }

        public MarkerOptions getMarcadorOpciones() {
            return marcadorOpciones;
        }

        public void setMarcadorOpciones(MarkerOptions marcador) {
            this.marcadorOpciones = marcador;
        }

        public Marker getMarcador(){ return marcador;}

        public void setMarcador(Marker marcador) {this.marcador=marcador;}
    }

}

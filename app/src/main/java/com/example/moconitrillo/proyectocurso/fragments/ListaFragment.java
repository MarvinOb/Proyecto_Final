package com.example.moconitrillo.proyectocurso.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.moconitrillo.proyectocurso.R;
import com.example.moconitrillo.proyectocurso.accesodatos.AccesoWebservice;
import com.example.moconitrillo.proyectocurso.adapters.RecyclerAdapter;
import com.example.moconitrillo.proyectocurso.entidades.AveriaServicio;
import com.example.moconitrillo.proyectocurso.entidades.Usuario;
import com.example.moconitrillo.proyectocurso.gestores.GestorRecursos;
import com.example.moconitrillo.proyectocurso.interfazusuario.ActivityCrearAveria;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaFragment extends Fragment{
    @BindView(R.id.btnAgregarAveria)
    Button btnAgregarAveria;

    @BindView(R.id.rvListaAverias)
    public RecyclerView rvListaAverias;

    public RecyclerView.Adapter adapterRecycler;
    public GridLayoutManager layoutManager;
    private AccesoWebservice accesoWebService;
    private ArrayList<AveriaServicio> listaAverias=new ArrayList<>();
    private Context contexto;
    private Usuario usuarioLogueado;
    private MaterialDialog dialogoEspera;

    public ListaFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_lista, container, false);
        ButterKnife.bind(this,view);

        usuarioLogueado =getArguments().getParcelable(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS);
        cargarInterfaz();
        return view;
    }

    @Override
    public void onAttach(Context contexto)
    {
        super.onAttach(contexto);
        this.contexto=contexto;
    }

    @OnClick(R.id.btnAgregarAveria)
    public void agregarAveria()
    {
        Intent intent=new Intent(contexto,ActivityCrearAveria.class);
        ArrayList<Usuario> listaUsuariosLogueados=new ArrayList<Usuario>();
        listaUsuariosLogueados.add(usuarioLogueado);
        intent.putParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS,listaUsuariosLogueados);
        intent.putExtra(ValoresGlobales.VARIABLE_FUENTE_CREACION_AVERIA, ValoresGlobales.AVERIA_CREADA_EN_LISTA);
        startActivity(intent);
    }

    private void cargarInterfaz()
    {
        int numeroColumnas=1;
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
            numeroColumnas=2;
        else
            numeroColumnas=1;

        rvListaAverias.setHasFixedSize(true);
        rvListaAverias.setItemViewCacheSize(5);
        rvListaAverias.setDrawingCacheEnabled(true);
        rvListaAverias.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        layoutManager=new GridLayoutManager(this.contexto,numeroColumnas);
        rvListaAverias.setLayoutManager(layoutManager);
        adapterRecycler=new RecyclerAdapter(this.contexto,listaAverias, this.usuarioLogueado);
        rvListaAverias.setAdapter(adapterRecycler);
        cargarListaAverias();
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
                        adapterRecycler.notifyDataSetChanged();
                        mostrarDialogoEspera(false,true);
                    }
                }

                @Override
                public void onFailure(Call<List<AveriaServicio>> call, Throwable t) {
                    mostrarDialogoEspera(false,true);
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
}

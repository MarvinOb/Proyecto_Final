package com.example.moconitrillo.proyectocurso.interfazusuario;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.moconitrillo.proyectocurso.R;
import com.example.moconitrillo.proyectocurso.accesodatos.AccesoWebservice;
import com.example.moconitrillo.proyectocurso.entidades.AveriaServicio;
import com.example.moconitrillo.proyectocurso.entidades.Usuario;
import com.example.moconitrillo.proyectocurso.fragments.ListaFragment;
import com.example.moconitrillo.proyectocurso.fragments.MapaFragment;
import com.example.moconitrillo.proyectocurso.gestores.GestorRecursos;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPrincipal extends AppCompatActivity {
    private ArrayList<Usuario> usuarioLogueado;
    private PagerAdapter pagerAdapter;

    @BindView(R.id.viewPager)
    public ViewPager viewPager;

    @BindView(R.id.pagerTabStrip)
    public PagerTabStrip pagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        ButterKnife.bind(this);

        usuarioLogueado = getIntent().getParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS);
        if (usuarioLogueado == null) {
            Intent temp = new Intent(this, ActivityLogin.class);
            startActivity(temp);
        }

        pagerAdapter=new AdaptadorFragment(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
    }

    private class AdaptadorFragment extends FragmentPagerAdapter
    {
        private ListaFragment fragmentoLista;
        private MapaFragment fragmentoMapa;


        public AdaptadorFragment(FragmentManager fm)
        {
            super(fm);
            fragmentoLista=new ListaFragment();
            fragmentoMapa=new MapaFragment();
            Bundle argumentoLista=new Bundle();
            argumentoLista.putParcelable(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS,usuarioLogueado.get(0));
            fragmentoLista.setArguments(argumentoLista);
            fragmentoMapa.setArguments(argumentoLista);
        }

        @Override
        public Fragment getItem(int position)
        {
            if(position==0)
            {
                return fragmentoLista;
            }
            else
            {
                return fragmentoMapa;
            }
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            if(position==0)
            {
                return "Lista";
            }
            else
            {
                return "Mapa";
            }
        }
    }
}

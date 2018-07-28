package com.example.moconitrillo.proyectocurso.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.moconitrillo.proyectocurso.R;
import com.example.moconitrillo.proyectocurso.entidades.Averia;
import com.example.moconitrillo.proyectocurso.entidades.AveriaServicio;
import com.example.moconitrillo.proyectocurso.entidades.Usuario;
import com.example.moconitrillo.proyectocurso.interfazusuario.ActivityDetalleAveria;
import com.example.moconitrillo.proyectocurso.interfazusuario.ActivityPrincipal;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.query.In;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<AveriaServicio> mDataset;
    private LayoutInflater inflater;
    private Context context;
    private Usuario usuarioLogueado;

    public RecyclerAdapter(Context context,List<AveriaServicio> myDataset, Usuario usuario) {
        this.inflater=LayoutInflater.from(context);
        mDataset = myDataset;
        this.context=context;
        this.usuarioLogueado=usuario;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.averia_item,parent,false);
        ViewHolder vh = new ViewHolder(v, this.context);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView itemName=holder.mTextViewNombre;
        itemName.setText(mDataset.get(position).getNombre()+"\n("+mDataset.get(position).getId()+")");

        TextView itemTipo=holder.mTextViewTipo;
        itemTipo.setText(mDataset.get(position).getTipo());

        TextView itemDescripcion=holder.mTextViewDescripcion;
        itemDescripcion.setText(mDataset.get(position).getDescripcion());

        holder.idAveria= mDataset.get(position).getId();
        holder.usuarioLogeuado=this.usuarioLogueado;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // convenience method for getting data at click position
    AveriaServicio getItem(int id) {
        return mDataset.get(id);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        @BindView(R.id.tvItemListaNombreAveria)
        public TextView mTextViewNombre;

        @BindView(R.id.tvItemListaTipoAveria)
        public TextView mTextViewTipo;

        @BindView(R.id.tvItemListaDescripcionAveria)
        public TextView mTextViewDescripcion;

        public Usuario usuarioLogeuado;

        public Context context;
        public String idAveria="";


        public ViewHolder(View v, Context con) {
            super(v);
            this.context=con;

            ButterKnife.bind(this,v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            Intent intent=new Intent(this.context, ActivityDetalleAveria.class);
            ArrayList<Usuario> listaUsuariosLogueados=new ArrayList<Usuario>();
            listaUsuariosLogueados.add(this.usuarioLogeuado);
            intent.putParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS,listaUsuariosLogueados);
            intent.putExtra(ValoresGlobales.VARIABLE_ID_AVERIA_SELECCIONADA,idAveria);
            this.context.startActivity(intent);
        }
    }
}

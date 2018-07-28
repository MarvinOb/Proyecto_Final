package com.example.moconitrillo.proyectocurso.interfazusuario;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moconitrillo.proyectocurso.R;
import com.example.moconitrillo.proyectocurso.entidades.Usuario;
import com.example.moconitrillo.proyectocurso.logica.Usuarios;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityRegistro extends AppCompatActivity {

    @BindView(R.id.etNombre)
    EditText etNombre;

    @BindView(R.id.etCorreo)
    EditText etCorreo;

    @BindView(R.id.etTelefono)
    EditText etTelefono;

    @BindView(R.id.etCedula)
    EditText etCedula;

    @BindView(R.id.etUsuario)
    EditText etUsuario;

    @BindView(R.id.etContrasena)
    EditText etContrasena;

    @BindView(R.id.etConfirmaContrasena)
    EditText etConfirmaContrasena;

    @BindView(R.id.btnRegistrar)
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnRegistrar)
    public void registrarUsuario()
    {
        Usuario nuevoUsuario=new Usuario(etUsuario.getText().toString().trim(), etNombre.getText().toString().trim(),
                etCorreo.getText().toString().trim(),etTelefono.getText().toString().trim(),etCedula.getText().toString().trim(),
                etContrasena.getText().toString().trim());

        Usuarios logicaUsuario=new Usuarios(getApplicationContext());
        int controlError=logicaUsuario.verificarObjetoUsuario(nuevoUsuario,etConfirmaContrasena.getText().toString().trim());

        if(controlError>0)
        {
            switch (controlError)
            {
                case 1:
                    etNombre.requestFocus();
                    break;
                case 2:
                    etCorreo.requestFocus();
                    break;
                case 3:
                    etTelefono.requestFocus();
                    break;
                case 4:
                    etCedula.requestFocus();
                    break;
                case 5:
                    etUsuario.requestFocus();
                    break;
                case 6:
                    etContrasena.requestFocus();
                    break;
                case 7:
                    etConfirmaContrasena.requestFocus();
                    break;
            }
        }
        else
        {
            if(logicaUsuario.crearUsuario(nuevoUsuario)) {
                Toast.makeText(this,"Se ha registrado satisfactoriamente el usuario",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ActivityLogin.class);
                startActivity(intent);
            }
        }
    }
}

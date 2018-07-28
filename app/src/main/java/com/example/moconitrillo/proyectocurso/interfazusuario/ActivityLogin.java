package com.example.moconitrillo.proyectocurso.interfazusuario;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moconitrillo.proyectocurso.R;
import com.example.moconitrillo.proyectocurso.entidades.Usuario;
import com.example.moconitrillo.proyectocurso.logica.Usuarios;
import com.example.moconitrillo.proyectocurso.utilitarios.ManagerPreferencias;
import com.example.moconitrillo.proyectocurso.utilitarios.ValoresGlobales;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class ActivityLogin extends AppCompatActivity {
    ManagerPreferencias preferencias;


    @BindView(R.id.etNombreUsuario)
    EditText etNombreUsuario;

    @BindView(R.id.etContrasenaUsuario)
    EditText etContrasenaUsuario;

    @BindView(R.id.btnRegistrar)
    Button btnRegistrar;

    @BindView(R.id.btnEntrar)
    Button btnEntrar;

    @BindView(R.id.chkRecordarUsuario)
    CheckBox chkRecordarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        leerPreferencias(); //leer preferencias
    }

    @OnClick(R.id.btnEntrar)
    public void realizarLogin()
    {
        Usuarios logicaUsuarios=new Usuarios(getApplicationContext());
        Usuario usuario= logicaUsuarios.validarUsuario(etNombreUsuario.getText().toString().trim(),etContrasenaUsuario.getText().toString().trim());

        if(usuario!=null)
        {
            if(chkRecordarUsuario.isChecked())
            {
                preferencias.setRecordarUsuario(true);
                preferencias.setNombreUsuario(etNombreUsuario.getText().toString());
                preferencias.guardarPreferencias();
            }
            else
            {
                preferencias.setRecordarUsuario(false);
                preferencias.setNombreUsuario("");
                preferencias.guardarPreferencias();
            }

            Intent intent=new Intent(this,ActivityPrincipal.class);
            ArrayList<Usuario> listaUsuariosLogueados=new ArrayList<Usuario>();
            listaUsuariosLogueados.add(usuario);
            intent.putParcelableArrayListExtra(ValoresGlobales.VARIABLE_LISTA_USUARIOS_LOGUEADOS,listaUsuariosLogueados);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this,"El usuario / password no existen",Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnRegistrar)
    public void registrarUsuario()
    {
        Intent intent=new Intent(this,ActivityRegistro.class);
        startActivity(intent);
    }

//    @OnCheckedChanged(R.id.chkRecordarUsuario)
//    public void recordarUsuario(boolean estado)
//    {
////        preferencias.setRecordarUsuario(estado);
////        if(estado)
////        {
////            preferencias.setNombreUsuario(etNombreUsuario.getText().toString());
////        }
////        preferencias.guardarPreferencias();
//    }

    private void leerPreferencias()
    {
        preferencias=new ManagerPreferencias(getApplicationContext());
        if(preferencias.isRecordarUsuario()) {
            chkRecordarUsuario.setChecked(preferencias.isRecordarUsuario());
            etNombreUsuario.setText(preferencias.getNombreUsuario());
        }
    }
}

package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.turnosapp.Interface.TurnosAPI;
import com.app.turnosapp.Interface.UsuarioService;
import com.app.turnosapp.Model.Usuario;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Usuario_verPerfil extends AppCompatActivity {

    private EditText etMail;
    private EditText etTelefono;
    private Button btnAtras;
    private Button btnActualizarDatos;
    private Usuario nuevoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_ver_perfil);

        Intent intentAgendaMedico = getIntent();
        nuevoUsuario = (Usuario)intentAgendaMedico.getSerializableExtra(("usuario"));

        etMail = (EditText) findViewById(R.id.etEmail);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        btnAtras = (Button) findViewById(R.id.btAtras);
        btnActualizarDatos = (Button) findViewById(R.id.btActualizar);


        etMail.setText(nuevoUsuario.getMail().trim());
        etTelefono.setText(nuevoUsuario.getTelefono().trim());

        btnActualizarDatos.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View view) {
                nuevoUsuario.setMail(etMail.getText().toString().trim());
                nuevoUsuario.setTelefono((etTelefono.getText().toString().trim()));
                actualizarUsuario(nuevoUsuario.getIdUsuario());
                Intent intent = new Intent(Usuario_verPerfil.this, AgendaMedicoActivity.class);
                startActivity(intent);
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View view) {
                Intent intent = new Intent(Usuario_verPerfil.this, AgendaMedicoActivity.class);
                startActivity(intent);
            }
        });

    }

    private void actualizarUsuario(long idUsuario) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioService usuarioService = retrofit.create(UsuarioService.class);

        Call<Usuario> call = usuarioService.actualizarUsuario(idUsuario, nuevoUsuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Usuario_verPerfil.this, "No se pudo actualizar el usuario", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Usuario_verPerfil.this, "Se han actualizado los datos", Toast.LENGTH_SHORT).show();
                    nuevoUsuario = response.body();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(Usuario_verPerfil.this, "Error al actualizar el usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

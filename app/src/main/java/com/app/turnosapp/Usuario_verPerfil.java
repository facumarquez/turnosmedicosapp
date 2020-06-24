package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.turnosapp.Callbacks.IUsuarioCallback;
import com.app.turnosapp.Helpers.RetrofitConnection;
import com.app.turnosapp.Interface.UsuarioService;
import com.app.turnosapp.Model.Usuario;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Usuario_verPerfil extends AppCompatActivity {

    private EditText etMail;
    private EditText etTelefono;
    private Button btnAtras;
    private Button btnActualizarDatos;
    private Usuario nuevoUsuario;
    private String tipoUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_ver_perfil);

        Intent intent = getIntent();
        nuevoUsuario = (Usuario)intent.getSerializableExtra(("usuario"));
        tipoUsuario = (String)intent.getSerializableExtra(("tipo"));

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

                if (etMail.getText().toString().trim().equals("")) {
                    Toast.makeText(Usuario_verPerfil.this, "Debe completar el campo e-mail", Toast.LENGTH_SHORT).show();
                }else if (etTelefono.getText().toString().trim().equals("")) {
                    Toast.makeText(Usuario_verPerfil.this, "Debe completar el campo teléfono", Toast.LENGTH_SHORT).show();
                }else if (!esMailValido(etMail.getText().toString().trim())){
                    Toast.makeText(Usuario_verPerfil.this, "Formato de e-mail inválido", Toast.LENGTH_SHORT).show();
                }else if (!esTelefonoValido(etTelefono.getText().toString().trim())){
                    Toast.makeText(Usuario_verPerfil.this, "Formato de teléfono inválido", Toast.LENGTH_SHORT).show();
                }else{
                    actualizarUsuario(nuevoUsuario.getIdUsuario(), new IUsuarioCallback() {
                        @Override
                        public void getUsuario(Usuario user) {
                            nuevoUsuario = user;

                            if (tipoUsuario.equals("paciente".toUpperCase())) {
                                Intent intentPaciente = new Intent(Usuario_verPerfil.this, Paciente_HomeActivity.class);
                                intentPaciente.putExtra("usuario", (Serializable) nuevoUsuario);
                                startActivity(intentPaciente);
                            }
                            if (tipoUsuario.equals("medico".toUpperCase())) {
                                Intent intentMedico = new Intent(Usuario_verPerfil.this, AgendaMedicoActivity.class);
                                intentMedico.putExtra("usuario", (Serializable) nuevoUsuario);
                                startActivity(intentMedico);
                            }
                        }
                    });
                }
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View view) {
                finish();
            }
        });
    }

    private static boolean esMailValido(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private static boolean esTelefonoValido(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches());
    }

    private void actualizarUsuario(long idUsuario, final IUsuarioCallback callback) {

        UsuarioService usuarioService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(UsuarioService.class);

        Call<Usuario> call = usuarioService.actualizarUsuario(idUsuario, nuevoUsuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Usuario_verPerfil.this, "No se pudo actualizar el usuario", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Usuario_verPerfil.this, "Se han actualizado los datos", Toast.LENGTH_SHORT).show();
                    callback.getUsuario(response.body());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(Usuario_verPerfil.this, "Error al actualizar el usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

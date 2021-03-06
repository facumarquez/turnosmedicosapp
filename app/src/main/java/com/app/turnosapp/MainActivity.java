package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.turnosapp.Interface.UsuarioService;
import com.app.turnosapp.Model.Usuario;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usuario;
    private EditText password;
    private Button login;
    private Spinner perfiles;
    private boolean resultado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario = (EditText) findViewById(R.id.etUsuario);
        password = (EditText) findViewById(R.id.etPassword);
        login = (Button) findViewById(R.id.btLogin);
        perfiles = (Spinner) findViewById(R.id.spPerfil);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.perfiles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        perfiles.setAdapter(adapter);

        login.setOnClickListener(this);
    }

    private void loginUser(String user, String password, String tipo){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioService usuarioService = retrofit.create(UsuarioService.class);

        Call<Usuario> call = usuarioService.loginUser(user, password,tipo);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Error en el servicio de login", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    if(response.body() == null ){
                        Toast.makeText(MainActivity.this, "Usuario o password incorrecto", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        System.out.println("Datos: "+response.body());
                        Intent intent = null;
                        if (perfiles.getSelectedItem().toString().toLowerCase().equals("paciente")){
                            intent = new Intent(MainActivity.this, Paciente_HomeActivity.class);
                        }
                        if (perfiles.getSelectedItem().toString().toLowerCase().equals("medico")){
                            intent = new Intent(MainActivity.this, AgendaMedicoActivity.class);
                        }
                        intent.putExtra("usuario", (Serializable) response.body());
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de conexión al servicio", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLogin:
                loginUser(usuario.getText().toString(),password.getText().toString(),perfiles.getSelectedItem().toString().toLowerCase());
                break;
        }
    }
}

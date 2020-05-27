package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.turnosapp.Interface.AgendaMedicoService;
import com.app.turnosapp.Interface.TurnosAPI;
import com.app.turnosapp.Interface.UsuarioService;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.Usuario;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgendaMedicoActivity extends AppCompatActivity {

    private Spinner spMeses;
    private Spinner spAnios;
    private Button btnAgenda;
    private Button btnPerfil;

    private Usuario usuario;
    private AgendaMedico agendaMedico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_medico);

        Intent intentLogin = getIntent();
        //TODO: poner el id posta
        long idUsuario = 1; //intent.getStringExtra("usuario");
        obtenerUsuario(idUsuario);


        spMeses = (Spinner) findViewById(R.id.spMes);
        spAnios = (Spinner) findViewById(R.id.spAnio);
        btnAgenda = (Button) findViewById(R.id.btnAgenda);
        btnPerfil = (Button) findViewById(R.id.btnPerfil);

        ArrayAdapter<CharSequence> adapterMeses = ArrayAdapter.createFromResource(this,
                R.array.meses_array, android.R.layout.simple_spinner_item);
        adapterMeses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMeses.setAdapter(adapterMeses);

        ArrayAdapter<CharSequence> adapterAnios = ArrayAdapter.createFromResource(this,
                R.array.anios_array, android.R.layout.simple_spinner_item);
        adapterAnios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAnios.setAdapter(adapterAnios);


        //Botones
        btnAgenda.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View view) {
                long idMedico = usuario.getIdUsuario();
                int mes = Integer.valueOf(spMeses.getSelectedItemPosition()+1);
                int anio= Integer.valueOf(spAnios.getSelectedItem().toString());

                obtenerAgenda(idMedico, mes, anio);

                if (agendaMedico == null){
                    //crearAgenda(idMedico, mes, anio);
                }

                Intent intent = new Intent(AgendaMedicoActivity.this, AgendaMedicoFechaActivity.class);
                intent.putExtra("agendaMedico", (Serializable) agendaMedico);
                startActivity(intent);
            }
        });

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View view) {
                Intent intent = new Intent(AgendaMedicoActivity.this, Usuario_verPerfil.class);
                intent.putExtra("usuario", (Serializable) usuario);
                startActivity(intent);
            }
        });
    }

    private void obtenerUsuario(long idUsuario) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioService usuarioService = retrofit.create(UsuarioService.class);

        Call<Usuario> call = usuarioService.getUsuarioPorID(idUsuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoActivity.this, "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                } else {
                    usuario = response.body();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(AgendaMedicoActivity.this, "Error al obtener el usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerAgenda(long idMedico, int mes, int anio) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoService agendaMedicoService = retrofit.create(AgendaMedicoService.class);

        Call<AgendaMedico> call = agendaMedicoService.getAgendaMedico(idMedico,mes,anio);
        call.enqueue(new Callback<AgendaMedico>() {
            @Override
            public void onResponse(Call<AgendaMedico> call, Response<AgendaMedico> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoActivity.this, "No se encontró la agenda", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AgendaMedicoActivity.this, "Se encontró la agenda", Toast.LENGTH_SHORT).show();
                    agendaMedico = response.body();
                }
            }

            @Override
            public void onFailure(Call<AgendaMedico> call, Throwable t) {
                Toast.makeText(AgendaMedicoActivity.this, "Error al obtener la agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearAgenda(long idMedico, int mes, int anio) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoService agendaMedicoService = retrofit.create(AgendaMedicoService.class);

        Call<AgendaMedico> call = agendaMedicoService.crearAgendaMedico(agendaMedico);
        call.enqueue(new Callback<AgendaMedico>() {
            @Override
            public void onResponse(Call<AgendaMedico> call, Response<AgendaMedico> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoActivity.this, "No se creó la agenda", Toast.LENGTH_SHORT).show();
                } else {
                    agendaMedico = response.body();
                }
            }

            @Override
            public void onFailure(Call<AgendaMedico> call, Throwable t) {
                Toast.makeText(AgendaMedicoActivity.this, "Error al crear la agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

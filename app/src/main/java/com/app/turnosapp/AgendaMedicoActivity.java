package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.turnosapp.Callbacks.IAgendaMedicoCallback;
import com.app.turnosapp.Callbacks.IMedicoCallback;
import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Helpers.RetrofitConnection;
import com.app.turnosapp.Interface.AgendaMedicoService;
import com.app.turnosapp.Interface.MedicoService;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.ManejoErrores.MensajeError;
import com.app.turnosapp.Model.Medico;
import com.app.turnosapp.Model.Usuario;
import com.google.gson.Gson;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaMedicoActivity extends AppCompatActivity {

    private Spinner spMeses;
    private Spinner spAnios;
    private Button btnAgenda;
    private Button btnPerfil;

    private Usuario usuario;
    private Medico medico;
    private AgendaMedico agendaMedico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_medico);

        Intent intentLogin = getIntent();
        usuario = (Usuario) intentLogin.getSerializableExtra("usuario");

        obtenerMedico(usuario.getIdUsuario(), new IMedicoCallback() {
            @Override
            public void getMedico(Medico user) {
                medico = user;
            }
        });

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
        btnAgenda.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view) {

                final int mes = spMeses.getSelectedItemPosition() + 1;
                final int anio= Integer.parseInt(spAnios.getSelectedItem().toString());

                obtenerAgenda(medico.getIdUsuario(), mes, anio, new IAgendaMedicoCallback() {
                    @Override
                    public void getAgendaMedico(AgendaMedico agenda) {
                        agendaMedico = agenda;
                        if (agendaMedico == null){
                            crearAgenda(medico, mes, anio, new IAgendaMedicoCallback() {
                                @Override
                                public void getAgendaMedico(AgendaMedico agenda) {
                                    agendaMedico = agenda;
                                    Intent intent = new Intent(AgendaMedicoActivity.this, AgendaMedicoFechaActivity.class);
                                    intent.putExtra("agendaMedico", (Serializable) agendaMedico);
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Calendar ahora = Calendar.getInstance();
                            int mesActual  = ahora.get(Calendar.MONTH);
                            if (mesActual  <= agendaMedico.getMes()){
                                Intent intent = new Intent(AgendaMedicoActivity.this, AgendaMedicoFechaActivity.class);
                                intent.putExtra("agendaMedico", (Serializable) agendaMedico);
                                startActivity(intent);
                            }else{
                                //TODO: preguntar si se puede acceder a agendas menores al periodo actual
                                Toast.makeText(AgendaMedicoActivity.this,"La agenda seleccionada es menor al período actual", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View view) {
                Intent intent = new Intent(AgendaMedicoActivity.this, Usuario_verPerfil.class);
                intent.putExtra("usuario", (Serializable) usuario);
                intent.putExtra("tipo", "medico".toUpperCase());
                startActivity(intent);
            }
        });
    }

    private void obtenerMedico(long idMedico, final IMedicoCallback callback) {

        MedicoService medicoService = RetrofitConnection.obtenerConexion
                                    (getString(R.string.apiTurnosURL)).create(MedicoService.class);

        Call<Medico> call = medicoService.getMedicoPorID(idMedico);
        call.enqueue(new Callback<Medico>() {
            @Override
            public void onResponse(Call<Medico> call, Response<Medico> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoActivity.this, "No se encontró el medico", Toast.LENGTH_SHORT).show();
                } else {
                    callback.getMedico(response.body());
                }
            }

            @Override
            public void onFailure(Call<Medico> call, Throwable t) {
                Toast.makeText(AgendaMedicoActivity.this, "Error al obtener el medico", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerAgenda(long idMedico, int mes, int anio, final IAgendaMedicoCallback callback) {

        AgendaMedicoService agendaMedicoService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoService.class);

        Call<AgendaMedico> call = agendaMedicoService.getAgendaMedico(idMedico, mes,anio);
        call.enqueue(new Callback<AgendaMedico>() {
            @Override
            public void onResponse(Call<AgendaMedico> call, Response<AgendaMedico> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoActivity.this, "No se encontró la agenda", Toast.LENGTH_SHORT).show();
                } else {
                    callback.getAgendaMedico(response.body());
                }
            }

            @Override
            public void onFailure(Call<AgendaMedico> call, Throwable t) {
                Toast.makeText(AgendaMedicoActivity.this, "Error al obtener la agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearAgenda(Medico medico, int mes, int anio, final IAgendaMedicoCallback callback) {

        AgendaMedicoService agendaMedicoService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoService.class);

        Calendar calendario = Calendar.getInstance();
        calendario.setTime(new Date());

        String anioCreacion = StringHelper.rellenarConCeros(String.valueOf(calendario.get(Calendar.YEAR)),4);
        //Arranca de cero el indice por eso sumo 1
        String mesCreacion = StringHelper.rellenarConCeros(String.valueOf(calendario.get(Calendar.MONTH) + 1),2);
        String diaCreacion =  StringHelper.rellenarConCeros(String.valueOf(calendario.get(Calendar.DAY_OF_MONTH)),2);

        String fechaCreacion = anioCreacion + mesCreacion + diaCreacion;

        agendaMedico = new AgendaMedico(mes,anio,medico,fechaCreacion);

        Call<AgendaMedico> call = agendaMedicoService.crearAgendaMedico(agendaMedico);
        call.enqueue(new Callback<AgendaMedico>() {
            @Override
            public void onResponse(Call<AgendaMedico> call, Response<AgendaMedico> response) {
                if (!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(AgendaMedicoActivity.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    callback.getAgendaMedico(response.body());
                }
            }

            @Override
            public void onFailure(Call<AgendaMedico> call, Throwable t) {
                Toast.makeText(AgendaMedicoActivity.this, "Error al crear la agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

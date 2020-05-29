package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.turnosapp.Helpers.RetrofitConnection;
import com.app.turnosapp.Interface.EspecialidadService;
import com.app.turnosapp.Interface.MedicoService;
import com.app.turnosapp.Interface.TurnosAPI;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.Especialidad;
import com.app.turnosapp.Model.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgendaMedicoFechaActivity extends AppCompatActivity {

    private AgendaMedico agendaMedico;
    private Spinner especialidades;
    private List<Especialidad> listaEspecialidades;
    private ArrayList<String> listaFormateadaEspecialidades = new ArrayList<String>();

    private Button modHorarios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha_medico);

       Intent intentAgendaMedico = getIntent();
       agendaMedico = (AgendaMedico)intentAgendaMedico.getSerializableExtra(("agendaMedico"));


        especialidades = (Spinner) findViewById(R.id.spEspecialidad);
        //TODO:poner idMedico verdadero
        getEspecialidadesDelMedico(1);

        modHorarios = (Button)findViewById(R.id.buttonModHorarios);

        //Botones
        modHorarios.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                Intent intent = new Intent(AgendaMedicoFechaActivity.this, AgendaMedicoHorarioActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getEspecialidadesDelMedico(long idMedico){

        MedicoService medicoService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(MedicoService.class);

        Call<List<Especialidad>> call = medicoService.getEspecialidadesPorMedico(idMedico);
        call.enqueue(new Callback<List<Especialidad>>() {
            @Override
            public void onResponse(Call<List<Especialidad>> call, Response<List<Especialidad>> response) {

                if(!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoFechaActivity.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
                }
                else{
                    listaEspecialidades = response.body();

                    //Ordeno alfabeticamente las especilidades
                    Collections.sort(listaEspecialidades, new Comparator<Especialidad>() {
                        @Override
                        public int compare(Especialidad o1, Especialidad o2) {
                            return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                        }
                    });

                    //Creo un array auxiliar para guardar solamente los nombres y mostrarlos en el spinner
                    for(Especialidad especialidad: listaEspecialidades){
                        listaFormateadaEspecialidades.add(especialidad.getNombre());
                    }

                    //Cargo los datos en el Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AgendaMedicoFechaActivity.this,android.R.layout.simple_spinner_item, listaFormateadaEspecialidades);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    especialidades.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Especialidad>> call, Throwable t) {
                Toast.makeText(AgendaMedicoFechaActivity.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

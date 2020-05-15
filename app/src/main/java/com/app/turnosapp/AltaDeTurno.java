package com.app.turnosapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.turnosapp.Interface.TurnosAPI;
import com.app.turnosapp.Model.Especialidad;
import com.app.turnosapp.Model.Medico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AltaDeTurno extends AppCompatActivity {


    private EditText fechaDesde;
    private EditText horaDesde;
    private Spinner especialidades;
    private List<Especialidad> listaEspecialidades;
    private List<Medico> listaMedicos;
    private ArrayList<String> listaFormateadaEspecialidades = new ArrayList<String>();
    private ArrayList<String> listaFormateadaMedicos = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_de_turno);

        // Inicializo los controles
        //especialidades = (Spinner) findViewById(R.id.spEspecialidad);

        //Cargo las especialidades en el Spinner
        //getEspecialidades();



    }


    private void getEspecialidades(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TurnosAPI turnosAPI = retrofit.create(TurnosAPI.class);

        Call<List<Especialidad>> call = turnosAPI.getEspecialidades();
        call.enqueue(new Callback<List<Especialidad>>() {
            @Override
            public void onResponse(Call<List<Especialidad>> call, Response<List<Especialidad>> response) {

                if(!response.isSuccessful()) {
                    Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AltaDeTurno.this,android.R.layout.simple_spinner_item, listaFormateadaEspecialidades);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    especialidades.setAdapter(adapter);
                    //System.out.println(listaEspecialidades);
                }
            }
            @Override
            public void onFailure(Call<List<Especialidad>> call, Throwable t) {
                Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private void getMedicosPorEspecialidad(String id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TurnosAPI turnosAPI = retrofit.create(TurnosAPI.class);

        Call<List<Medico>> call = turnosAPI.getMedicosPorEspecialidad(id);
        call.enqueue(new Callback<List<Medico>>() {
            @Override
            public void onResponse(Call<List<Medico>> call, Response<List<Medico>> response) {

                if(!response.isSuccessful()) {
                    Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
                }
                else{
                    listaMedicos = response.body();

                    //Ordeno alfabeticamente las especilidades
                    Collections.sort(listaMedicos, new Comparator<Medico>() {
                        @Override
                        public int compare(Medico o1, Medico o2) {
                            return o1.getNombreCompleto().compareToIgnoreCase(o2.getNombreCompleto());
                        }
                    });

                    //Creo un array auxiliar para guardar solamente los nombres y mostrarlos en el spinner
                    for(Medico medico: listaMedicos){
                        listaFormateadaEspecialidades.add(medico.getNombreCompleto());
                    }

                    //Cargo los datos en el Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AltaDeTurno.this,android.R.layout.simple_spinner_item, listaFormateadaEspecialidades);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    especialidades.setAdapter(adapter);
                    //System.out.println(listaEspecialidades);
                }
            }
            @Override
            public void onFailure(Call<List<Medico>> call, Throwable t) {
                Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}

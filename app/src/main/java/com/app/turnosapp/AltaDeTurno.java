//TODO
// 1- Pasar datos a la siguiente pantalla
// 2- Pintar los dias que no haya turnos de acuerdo a los datos de los spinners
// 3- Darle comportamiento al botón SIGUIENTE

package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Interface.EspecialidadService;
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
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

public class AltaDeTurno extends AppCompatActivity {


    private Spinner spEspecialidades;
    private Spinner spMedicos;
    private MCalendarView calendarView;
    private Spinner horarios;

    private List<Especialidad> listaEspecialidades;
    private Especialidad especialidadSeleccionada;
    private List<Medico> listaMedicos;
    private DateData fechaSeleccionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_de_turno);

        // Inicializo los controles
        spEspecialidades = (Spinner) findViewById(R.id.spEspecialidad);
        spMedicos = (Spinner) findViewById(R.id.spMedico);
        horarios = (Spinner) findViewById(R.id.spHorario);
        calendarView = (MCalendarView) findViewById(R.id.mcvFechaTurno);

        //Cargo los horarios posibles (Mañana|Tarde)
        horarios = (Spinner) findViewById(R.id.spHorario);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.horarios, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        horarios.setAdapter(adapter);


        //Cargo las especialidades en el Spinner
        getEspecialidades();

        //Cuando selecciona la especialidad se cargan los médicos de esa especialidad
        spEspecialidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                especialidadSeleccionada = (Especialidad) parent.getItemAtPosition(position);
                getMedicosPorEspecialidad(especialidadSeleccionada.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //TODO: ver si poner algo aca....
            }
        });

        calendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {

                boolean Marcado = false;
                MarkedDates markedDates = calendarView.getMarkedDates();
                ArrayList markData = markedDates.getAll();
                for (int i = 0; i < markData.size(); i++) {
                    if (markData.get(i) == date) {
                        Marcado = true;
                    }
                }

                String anio = StringHelper.rellenarConCeros(String.valueOf(date.getYear()),4);
                String mes = StringHelper.rellenarConCeros(String.valueOf(date.getMonth()),2);
                String dia = StringHelper.rellenarConCeros(String.valueOf(date.getDay()),2);
                String fechaFormatoJapones = anio + mes + dia;

                if (Marcado) {
                    calendarView.unMarkDate(date);
                } else {
                    calendarView.getMarkedDates().getAll().clear();
                    calendarView.markDate(date);
                }
                Toast.makeText(AltaDeTurno.this, fechaFormatoJapones, Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void getTurnosCalendario(){
        //Esta funcion va a pintar el calendario con los turnos disponibles

    }


    private void getEspecialidades(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EspecialidadService especialidadService = retrofit.create(EspecialidadService.class);

        Call<List<Especialidad>> call = especialidadService.getEspecialidades();
        call.enqueue(new Callback<List<Especialidad>>() {
            @Override
            public void onResponse(Call<List<Especialidad>> call, Response<List<Especialidad>> response) {

                if(!response.isSuccessful()) {
                    Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!response.body().isEmpty()) {
                        listaEspecialidades = response.body();
                        //Ordeno alfabeticamente las especilidades
                        Collections.sort(listaEspecialidades, new Comparator<Especialidad>() {
                            @Override
                            public int compare(Especialidad o1, Especialidad o2) {
                                return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                            }
                        });

                        //Cargo los datos en el Spinner
                        ArrayAdapter<Especialidad> adapter = new ArrayAdapter<Especialidad>(AltaDeTurno.this, android.R.layout.simple_spinner_item, listaEspecialidades);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spEspecialidades.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Especialidad>> call, Throwable t) {
                Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMedicosPorEspecialidad(Long id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EspecialidadService especialidadService = retrofit.create(EspecialidadService.class);

        Call<List<Medico>> call = especialidadService.getMedicosPorEspecialidad(id);
        call.enqueue(new Callback<List<Medico>>() {
            @Override
            public void onResponse(Call<List<Medico>> call, Response<List<Medico>> response) {

                if(!response.isSuccessful()) {
                    Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
                }
                else{

                    if(!response.body().isEmpty()) {
                        listaMedicos = response.body();

                        //Ordeno alfabeticamente las especialidades
                        Collections.sort(listaMedicos, new Comparator<Medico>() {
                            @Override
                            public int compare(Medico o1, Medico o2) {
                                return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                            }
                        });

                        //Cargo los datos en el Spinner
                        ArrayAdapter<Medico> adapter = new ArrayAdapter<Medico>(AltaDeTurno.this, android.R.layout.simple_spinner_item, listaMedicos);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spMedicos.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Medico>> call, Throwable t) {
                Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

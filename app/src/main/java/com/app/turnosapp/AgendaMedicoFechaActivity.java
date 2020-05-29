package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Helpers.RetrofitConnection;
import com.app.turnosapp.Interface.MedicoService;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.Especialidad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

public class AgendaMedicoFechaActivity extends AppCompatActivity {

    private MCalendarView calendarView;
    private Spinner spEspecialidades;
    private Button modHorarios;

    private AgendaMedico agendaMedico;
    private List<Especialidad> listaEspecialidades;
    private ArrayList<String> listaFormateadaEspecialidades = new ArrayList<String>();
    private ArrayList<String> listaFechasCalendario =  new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha_medico);

       Intent intentAgendaMedico = getIntent();
       agendaMedico = (AgendaMedico)intentAgendaMedico.getSerializableExtra(("agendaMedico"));


        spEspecialidades = (Spinner) findViewById(R.id.spEspecialidad);
        getEspecialidadesDelMedico(agendaMedico.getMedico().getIdUsuario());

        calendarView = (MCalendarView) findViewById(R.id.mcvFechasMedico);
        calendarView.travelTo(new DateData(agendaMedico.getAnio(), agendaMedico.getMes(), 1));

        modHorarios = (Button)findViewById(R.id.buttonModHorarios);

        //Botones
        modHorarios.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){

                //TODO:ver como capturar las fechas

                Intent intent = new Intent(AgendaMedicoFechaActivity.this, AgendaMedicoHorarioActivity.class);
                startActivity(intent);
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

                    listaFechasCalendario.remove(fechaFormatoJapones);

                } else {
                    calendarView.markDate(date);
                    listaFechasCalendario.add(fechaFormatoJapones);
                }
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
                    spEspecialidades.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Especialidad>> call, Throwable t) {
                Toast.makeText(AgendaMedicoFechaActivity.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

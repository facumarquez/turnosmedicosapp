package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.turnosapp.Callbacks.IAgendaMedicoFechaCallback;
import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Helpers.RetrofitConnection;
import com.app.turnosapp.Interface.AgendaMedicoFechaService;
import com.app.turnosapp.Interface.MedicoService;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.AgendaMedicoFecha;
import com.app.turnosapp.Model.Especialidad;

import java.io.Serializable;
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

    private Especialidad especialidadSeleccionada;
    private List<Especialidad> listaEspecialidades = new ArrayList<Especialidad>();

    private ArrayList<String> listaFechasCalendario =  new ArrayList<String>();
    private List<AgendaMedicoFecha> fechasAgenda = new ArrayList<AgendaMedicoFecha>();


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

        spEspecialidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                especialidadSeleccionada = (Especialidad) parent.getItemAtPosition(position);
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

                    listaFechasCalendario.remove(fechaFormatoJapones);

                } else {
                    calendarView.markDate(date);
                    listaFechasCalendario.add(fechaFormatoJapones);
                }
            }
        });

        //Botones
        modHorarios.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){

                for (String fecha : listaFechasCalendario){
                    fechasAgenda.add(new AgendaMedicoFecha(fecha,agendaMedico,especialidadSeleccionada));
                }

                crearFechasAgenda(fechasAgenda, new IAgendaMedicoFechaCallback() {
                    @Override
                    public void getFechasAgendaMedico(List<AgendaMedicoFecha> fechas) {
                        fechasAgenda = fechas;
                        Intent intent = new Intent(AgendaMedicoFechaActivity.this, AgendaMedicoHorarioActivity.class);
                        intent.putExtra("fechasAgenda", (Serializable) fechasAgenda);
                        startActivity(intent);
                    }
                });
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

                    //Cargo los datos en el Spinner
                    ArrayAdapter<Especialidad> adapter = new ArrayAdapter<Especialidad>(AgendaMedicoFechaActivity.this,android.R.layout.simple_spinner_item, listaEspecialidades);
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

    private void crearFechasAgenda(List<AgendaMedicoFecha> fechasAgenda, final IAgendaMedicoFechaCallback callback) {

        AgendaMedicoFechaService agendaMedicoFechaService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoFechaService.class);

        Call<List<AgendaMedicoFecha>> call = agendaMedicoFechaService.crearFechasAgendaMedico(fechasAgenda);
        call.enqueue(new Callback <List<AgendaMedicoFecha>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoFecha>> call, Response<List<AgendaMedicoFecha>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoFechaActivity.this, "No se crearon las fechas", Toast.LENGTH_SHORT).show();
                } else {
                    callback.getFechasAgendaMedico(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AgendaMedicoFecha>> call, Throwable t) {
                Toast.makeText(AgendaMedicoFechaActivity.this, "Error al crear las fechas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

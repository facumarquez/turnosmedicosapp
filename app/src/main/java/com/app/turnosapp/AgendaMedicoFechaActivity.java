package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import com.app.turnosapp.Interface.AgendaMedicoHorarioService;
import com.app.turnosapp.Interface.AgendaMedicoService;
import com.app.turnosapp.Interface.MedicoService;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.AgendaMedicoFecha;
import com.app.turnosapp.Model.AgendaMedicoHorario;
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
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

public class AgendaMedicoFechaActivity extends AppCompatActivity {

    private MCalendarView calendarView;
    private Spinner spEspecialidades;
    private Button btHorarios;
    private Button btEliminarHorarios;
    private Button btConfirmarAgenda;

    private AgendaMedico agendaMedico;

    private Especialidad especialidadSeleccionada;
    private List<Especialidad> listaEspecialidades = new ArrayList<Especialidad>();

    private ArrayList<String> listaFechasSeleccionadas =  new ArrayList<String>();

    private List<AgendaMedicoFecha> fechasAgendaMedico = new ArrayList<AgendaMedicoFecha>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha_medico);

        Intent intentAgendaMedico = getIntent();
        agendaMedico = (AgendaMedico)intentAgendaMedico.getSerializableExtra(("agendaMedico"));
/*
       obtenerFechasAgenda(new IAgendaMedicoFechaCallback() {
           @Override
           public void getFechasAgendaMedico(List<AgendaMedicoFecha> fechasAgenda) {
               fechasAgendaMedico = fechasAgenda;

               if(fechasAgenda != null){
                   marcarFechasOcupadasEnCalendario();
               }
           }
       });
*/

        spEspecialidades = (Spinner) findViewById(R.id.spEspecialidad);
        getEspecialidadesDelMedico(agendaMedico.getMedico().getIdUsuario());

        calendarView = (MCalendarView) findViewById(R.id.mcvFechasMedico);
        calendarView.travelTo(new DateData(agendaMedico.getAnio(), agendaMedico.getMes(), 1));




        btHorarios = (Button)findViewById(R.id.btnHorarios);
        btEliminarHorarios = (Button)findViewById(R.id.btnEliminarHorarios);
        btConfirmarAgenda = (Button)findViewById(R.id.btnConfirmarAgenda);

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
                MarkedDates diasMarcados = calendarView.getMarkedDates();
                ArrayList markData = diasMarcados.getAll();
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
                    listaFechasSeleccionadas.remove(fechaFormatoJapones);
                    //marcarFechasOcupadasEnCalendario();
                    marcarFechasSeleccionadasEnCalendario();

                } else {
                    calendarView.getMarkedDates().getAll().clear();
                    //marcarFechasOcupadasEnCalendario();
                    marcarFechasSeleccionadasEnCalendario();
                   calendarView.markDate(date);
                     if(listaFechasSeleccionadas.contains(fechaFormatoJapones)) {
                        //calendarView.unMarkDate(date.setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.RED)));
                    }
                    calendarView.markDate(date.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Color.GREEN)));
                    listaFechasSeleccionadas.add(fechaFormatoJapones);
                }
            }
        });

        //Botones
        btHorarios.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){

                fechasAgendaMedico = new ArrayList<AgendaMedicoFecha>();
                if (listaFechasSeleccionadas== null || listaFechasSeleccionadas.size() == 0){
                    fechasAgendaMedico = new ArrayList<AgendaMedicoFecha>();
                }

                Collections.sort(listaFechasSeleccionadas);

                for (String fecha : listaFechasSeleccionadas){
                    fechasAgendaMedico.add(new AgendaMedicoFecha(fecha,agendaMedico,especialidadSeleccionada));
                }
                //TODO: ver si poner en null el objeto......manejar click del calendario
                crearFechasAgenda(fechasAgendaMedico, new IAgendaMedicoFechaCallback() {
                    @Override
                    public void getFechasAgendaMedico(List<AgendaMedicoFecha> fechas) {
                        fechasAgendaMedico = fechas;
                        Intent intent = new Intent(AgendaMedicoFechaActivity.this, AgendaMedicoHorarioActivity.class);
                        intent.putExtra("fechasAgenda", (Serializable) fechasAgendaMedico);
                        intent.putExtra("agendaMedico", (Serializable) agendaMedico);
                        startActivity(intent);
                        //finish();
                    }
                });
            }
        });


        btEliminarHorarios.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){

                for (String fecha : listaFechasSeleccionadas){
                    fechasAgendaMedico.add(new AgendaMedicoFecha(fecha,agendaMedico,especialidadSeleccionada));
                }

                crearFechasAgenda(fechasAgendaMedico, new IAgendaMedicoFechaCallback() {
                    @Override
                    public void getFechasAgendaMedico(List<AgendaMedicoFecha> fechas) {
                        fechasAgendaMedico = fechas;
                        Intent intent = new Intent(AgendaMedicoFechaActivity.this, AgendaMedicoHorarioActivity.class);
                        intent.putExtra("fechasAgenda", (Serializable) fechasAgendaMedico);
                        startActivity(intent);
                    }
                });
            }
        });

        btConfirmarAgenda.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
               confirmarAgenda(agendaMedico.getId());
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

    private void obtenerFechasAgenda(final IAgendaMedicoFechaCallback callback) {

        AgendaMedicoFechaService agendaMedicoFechaService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoFechaService.class);

        Call<List<AgendaMedicoFecha>> call = agendaMedicoFechaService.getFechasPorAgendaMedico(agendaMedico.getId());
        call.enqueue(new Callback<List<AgendaMedicoFecha>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoFecha>> call, Response<List<AgendaMedicoFecha>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoFechaActivity.this, "No se encontraron fechas de la agenda", Toast.LENGTH_SHORT).show();
                } else {
                    callback.getFechasAgendaMedico(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AgendaMedicoFecha>> call, Throwable t) {
                Toast.makeText(AgendaMedicoFechaActivity.this, "Error al obtener las fecha de la agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void marcarFechasOcupadasEnCalendario(){

        for (AgendaMedicoFecha fecha:fechasAgendaMedico) {
            int dia,mes,anio;
            dia = Integer.valueOf(fecha.getFecha().substring(6,8));
            mes = Integer.valueOf(fecha.getFecha().substring(4,6));
            anio = Integer.valueOf(fecha.getFecha().substring(0,4));
            calendarView.markDate(
                    new DateData(anio, mes, dia).setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.RED)));
        }
    }

    private void marcarFechasSeleccionadasEnCalendario() {
        calendarView.getMarkedDates().getAll().clear();
        if(listaFechasSeleccionadas != null) {
            if(!listaFechasSeleccionadas.isEmpty()) {
                for (String a : listaFechasSeleccionadas) {
                    DateData d = new DateData(Integer.valueOf(a.substring(0, 4)), Integer.valueOf(a.substring(4, 6)), Integer.valueOf(a.substring(6, 8)));
                    calendarView.markDate(
                            d.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Color.GREEN))
                    );
                }
            }
            else{
                //Fix choto
                calendarView.markDate(2015, 10, 7);
                calendarView.unMarkDate(2015, 10, 7);
            }
        }
    }

    private void confirmarAgenda(long idAgendaMedico) {

        AgendaMedicoService agendaMedicoService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoService.class);

        Call<Boolean> call = agendaMedicoService.confirmarAgenda(idAgendaMedico);
        call.enqueue(new Callback <Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoFechaActivity.this, "No se ha confirmado la agenda", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(AgendaMedicoFechaActivity.this, "Error al confirmar la agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarHorariosAgenda(List<AgendaMedicoHorario> horarios) {

        AgendaMedicoHorarioService agendaMedicoHorarioService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoHorarioService.class);

        Call<Void> call = agendaMedicoHorarioService.deleteHorarios(horarios);
        call.enqueue(new Callback <Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoFechaActivity.this, "No se eliminaron los horarios", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AgendaMedicoFechaActivity.this, "Error al eliminar los horarios", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

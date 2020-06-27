package com.app.turnosapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.app.turnosapp.Interface.MedicoService;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.AgendaMedicoFecha;
import com.app.turnosapp.Model.Especialidad;
import com.app.turnosapp.Model.ManejoErrores.MensajeError;
import com.google.gson.Gson;

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
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;

public class AgendaMedicoFechaActivity extends AppCompatActivity {

    private MCalendarView calendarView;
    private Spinner spEspecialidades;
    private Button btHorarios;
    private Button btEliminarHorarios;
    private Button btTurnos;

    private String diaSeleccionado;
    private AgendaMedico agendaMedico;

    private Especialidad especialidadSeleccionada;
    private List<Especialidad> listaEspecialidades = new ArrayList<Especialidad>();

    private ArrayList<String> listaFechasSeleccionadas =  new ArrayList<String>();
    private ArrayList<String> listaFechasOcupadas =  new ArrayList<String>();

    private List<AgendaMedicoFecha> fechasAgendaMedico = new ArrayList<AgendaMedicoFecha>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha_medico);

        Intent intentAgendaMedico = getIntent();
        agendaMedico = (AgendaMedico)intentAgendaMedico.getSerializableExtra(("agendaMedico"));

       obtenerFechasAgenda(new IAgendaMedicoFechaCallback() {
           @Override
           public void getFechasAgendaMedico(List<AgendaMedicoFecha> fechasAgenda) {
               fechasAgendaMedico = fechasAgenda;

               if(fechasAgenda != null){
                   marcarFechasOcupadasEnCalendario();
               }

               //FACU
               if(fechasAgendaMedico!=null) {
                   for (AgendaMedicoFecha fecha : fechasAgendaMedico) {
                       listaFechasOcupadas.add(fecha.getFecha());
                   }
               }
           }
       });

        spEspecialidades = (Spinner) findViewById(R.id.spEspecialidad);
        getEspecialidadesDelMedico(agendaMedico.getMedico().getIdUsuario());

        calendarView = (MCalendarView) findViewById(R.id.mcvFechasMedico);
        calendarView.travelTo(new DateData(agendaMedico.getAnio(), agendaMedico.getMes(), 1));

        btHorarios = (Button)findViewById(R.id.btnHorarios);
        btEliminarHorarios = (Button)findViewById(R.id.btnEliminarHorarios);
        btTurnos = (Button)findViewById(R.id.btnTurnos);


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

                String anio = StringHelper.rellenarConCeros(String.valueOf(date.getYear()),4);
                String mes = StringHelper.rellenarConCeros(String.valueOf(date.getMonth()),2);
                String dia = StringHelper.rellenarConCeros(String.valueOf(date.getDay()),2);
                String fechaFormatoJapones = anio + mes + dia;

                diaSeleccionado = fechaFormatoJapones;

                if (!StringHelper.mesCorrecto(diaSeleccionado, agendaMedico.getMes())){
                    Toast.makeText(getApplicationContext(), "Debe seleccionar un día dentro del mes de la agenda", Toast.LENGTH_LONG).show();
                }else{
                    if(listaFechasSeleccionadas.contains(fechaFormatoJapones)){
                        Marcado=true;
                    }

                    if (Marcado) {
                        listaFechasSeleccionadas.remove(fechaFormatoJapones);
                        marcarFechasOcupadasEnCalendario();
                        marcarFechasSeleccionadasEnCalendario();
                    } else {
                        listaFechasSeleccionadas.add(fechaFormatoJapones);
                        marcarFechasOcupadasEnCalendario();
                        marcarFechasSeleccionadasEnCalendario();
                        if(listaFechasOcupadas.contains(fechaFormatoJapones)) {
                            calendarView.unMarkDate(date.setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.RED)));
                        }
                        calendarView.markDate(date.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Color.BLUE)));
                    }
                }
            }
        });

        calendarView.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                startActivity(getIntent());
            }
        });

        //Botones
        btHorarios.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){

                fechasAgendaMedico = new ArrayList<AgendaMedicoFecha>();
                if (listaFechasSeleccionadas == null || listaFechasSeleccionadas.size() == 0){
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
                        if(fechas!= null){
                            fechasAgendaMedico = fechas;
                        }

                        Intent intent = new Intent(AgendaMedicoFechaActivity.this, AgendaMedicoHorarioActivity.class);
                        intent.putExtra("fechasAgenda", (Serializable) fechasAgendaMedico);
                        intent.putExtra("agendaMedico", (Serializable) agendaMedico);
                        startActivity(intent);
                    }
                });
            }
        });

        btEliminarHorarios.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                dialogEliminarHorarios(fechasAgendaMedico);
            }
        });

        btTurnos.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){

                if (diaSeleccionado == null){
                    Toast.makeText(getApplicationContext(), "Debe seleccionar una fecha", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(AgendaMedicoFechaActivity.this, AgendaMedicoTurnoActivity.class);
                    intent.putExtra("agendaMedico", (Serializable) agendaMedico);
                    intent.putExtra("diaSeleccionado",  diaSeleccionado);
                    startActivity(intent);
                }
            }
        });
    }

    //FACU
    private void marcarFechasSeleccionadasEnCalendario() {
        if(listaFechasSeleccionadas != null) {
            if(!listaFechasSeleccionadas.isEmpty()) {
                for (String a : listaFechasSeleccionadas) {
                    DateData d = new DateData(Integer.valueOf(a.substring(0, 4)), Integer.valueOf(a.substring(4, 6)), Integer.valueOf(a.substring(6, 8)));
                    if(listaFechasOcupadas.contains(a)){
                        calendarView.unMarkDate(d.setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.RED)));
                    }
                    calendarView.markDate(
                            d.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Color.BLUE))
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

    //FACU
    private void marcarFechasOcupadasEnCalendario(){
        calendarView.getMarkedDates().getAll().clear();
        if(fechasAgendaMedico!=null) {
            for (AgendaMedicoFecha fecha : fechasAgendaMedico) {
                DateData d = new DateData(Integer.valueOf(fecha.getFecha().substring(0, 4)), Integer.valueOf(fecha.getFecha().substring(4, 6)), Integer.valueOf(fecha.getFecha().substring(6, 8)));
                calendarView.markDate(
                        d.setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.RED))
                );
            }
        }
    }

    private void dialogEliminarHorarios(List<AgendaMedicoFecha> fechasCargadas){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aviso!");
        builder.setMessage("Está seguro que desea eliminar los horarios de los días seleccionados?");
        builder.setCancelable(false);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                List<AgendaMedicoFecha> fechasConHorariosABorrar = new ArrayList<AgendaMedicoFecha>();

                if (fechasCargadas != null && fechasCargadas.size()> 0 ){
                    for (AgendaMedicoFecha fecha:fechasCargadas) {
                        if (listaFechasSeleccionadas.contains(fecha.getFecha())){
                            fechasConHorariosABorrar.add(fecha);
                        }
                    }
                    boolean puedeModificarAgenda = true;
                    for (AgendaMedicoFecha fecha:fechasConHorariosABorrar) {
                        if (!StringHelper.puedeModificarFechaAgenda(fecha.getAgendaMedico(),fecha.getFecha())){
                            puedeModificarAgenda = false;
                            break;
                        }
                    }

                    if (puedeModificarAgenda){
                        eliminarHorariosAgenda(fechasConHorariosABorrar);
                    }else{
                        Toast.makeText(getApplicationContext(), "Sólo puede modificar la agenda 7 días después en el transcurso del mes", Toast.LENGTH_LONG).show();
                    }
               }else{
                    Toast.makeText(getApplicationContext(), "La/s fechas seleccionadas no están cargadas en el sistema!. Operación cancelada", Toast.LENGTH_SHORT).show();
               }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Operación cancelada!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void getEspecialidadesDelMedico(long idMedico){

        MedicoService medicoService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(MedicoService.class);

        Call<List<Especialidad>> call = medicoService.getEspecialidadesPorMedico(idMedico);
        call.enqueue(new Callback<List<Especialidad>>() {
            @Override
            public void onResponse(Call<List<Especialidad>> call, Response<List<Especialidad>> response) {

                if(!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoFechaActivity.this, "No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AgendaMedicoFechaActivity.this, "No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
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
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(AgendaMedicoFechaActivity.this, mensaje.getMessage(), Toast.LENGTH_LONG).show();
                    startActivity(getIntent());
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

    private void eliminarHorariosAgenda(List<AgendaMedicoFecha> fechas) {

        AgendaMedicoHorarioService agendaMedicoHorarioService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoHorarioService.class);

        Call<Void> call = agendaMedicoHorarioService.deleteHorarios(fechas);
        call.enqueue(new Callback <Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(AgendaMedicoFechaActivity.this, mensaje.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Operación confirmada!", Toast.LENGTH_SHORT).show();
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

//TODO
// 1- Pasar datos a la siguiente pantalla
// 2- Pintar los dias que no haya turnos de acuerdo a los datos de los spinners
// 3- Darle comportamiento al botón SIGUIENTE

package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Interface.AgendaMedicoFechaService;
import com.app.turnosapp.Interface.EspecialidadService;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.AgendaMedicoFecha;
import com.app.turnosapp.Model.AgendaMedicoTurno;
import com.app.turnosapp.Model.Especialidad;
import com.app.turnosapp.Model.ManejoErrores.MensajeError;
import com.app.turnosapp.Model.Medico;
import com.app.turnosapp.Model.Usuario;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

public class AltaDeTurno extends AppCompatActivity {


    //Controles de la pantalla
    private Spinner spEspecialidades;
    private Spinner spMedicos;
    private MCalendarView calendarView;
    private Spinner horarios;
    private Button btSiguiente;
    private CheckBox cbAllMedicos;

    //Atributos que voy a usar
    private List<AgendaMedicoFecha> listaAgendaMedicoFecha;
    private List<AgendaMedicoFecha> listaProximasAgendaMedicoFecha;
    private List<AgendaMedicoTurno> listaProximosTurnos;
    private AgendaMedicoFecha agendaMedicoFechaSeleccionada;
    private List<Especialidad> listaEspecialidades;
    private List<Medico> listaMedicos;
    private Usuario usuario;

    //Datos seleccionados en la pantalla
    private Especialidad especialidadSeleccionada;
    private Medico medicoSeleccionado;
    private int diaSeleccionado;
    private int mesSeleccionado;
    private int anioSeleccionado;
    private String horarioSeleccionado;

    //Auxiliares para pintar el calendario
    private ArrayList<String> listafechasConTurnosDisponibles;

    //Auxiliares
    private int check = 0; //Este check es para que no se ejecute la llamada que trae los turnosDisponibles 2 veces la primera vez(una cuando se setea el medico y otra cuando se setea el horario)
    private boolean checkAllMedicos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_de_turno);

        //Recibo el dato de la pantalla anterior
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            usuario = (Usuario) extras.getSerializable("usuario");
        }

        // Inicializo los controles
        cbAllMedicos = (CheckBox) findViewById(R.id.cbAllMedicos);
        spEspecialidades = (Spinner) findViewById(R.id.spEspecialidad);
        spMedicos = (Spinner) findViewById(R.id.spMedico);
        horarios = (Spinner) findViewById(R.id.spHorario);
        calendarView = (MCalendarView) findViewById(R.id.mcvFechaTurno);
        btSiguiente = (Button) findViewById(R.id.btConfirmar);
        listafechasConTurnosDisponibles = new ArrayList<String>();
        checkAllMedicos = cbAllMedicos.isChecked();


        //Limpio el calendario por si ya había navegado la pantalla
        calendarView.getMarkedDates().getAll().clear();


         ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.horarios, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        horarios.setAdapter(adapter);

        //Inicializo el calendario
        Date date = new Date();
        String day          = (String) DateFormat.format("dd",   date.getTime()); // 20
        String monthNumber  = (String) DateFormat.format("MM",   date.getTime()); // 6
        String year         = (String) DateFormat.format("yyyy", date.getTime()); // 2020
        mesSeleccionado=Integer.valueOf(monthNumber);
        anioSeleccionado=Integer.valueOf(year);

        //Cargo las especialidades en el Spinner
        getEspecialidades();

        //lISTENERS
        cbAllMedicos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                spMedicos.setEnabled(isChecked ? false : true);
                checkAllMedicos = isChecked;

                //Esto es para que cuando tilde "Todos" lo obligue a seleccionar el dia de nuevo (ya que se limpia el calendario pero queda la variable cargada)
                diaSeleccionado=0;

                //Si pasa de destildado a tildado tengo que actualizar el calendario
                if(isChecked){
                    getTurnosCalendarioAllMedicos(especialidadSeleccionada.getId(),mesSeleccionado,anioSeleccionado,horarioSeleccionado);

                }else{
                    getTurnosCalendario(especialidadSeleccionada.getId(),medicoSeleccionado.getIdUsuario(),mesSeleccionado,anioSeleccionado,horarioSeleccionado);
                    if(listaAgendaMedicoFecha!=null){
                        if(listaAgendaMedicoFecha.isEmpty()){ //Si no hay turnos en este mes
                            getProximoTurnoDisponible(especialidadSeleccionada.getId(),medicoSeleccionado.getIdUsuario(),mesSeleccionado,anioSeleccionado,horarioSeleccionado,checkAllMedicos);
                        }
                    }
                }
            }
        });


        btSiguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(diaSeleccionado==0){
                    Toast.makeText(AltaDeTurno.this, "ERROR: Debe seleccionar un día en el calendario", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Busco la AgendaMedicoFecha del dia seleccionado por el usuario
                    String fechaFormateada=String.format("%d%02d%02d", anioSeleccionado,mesSeleccionado,diaSeleccionado); // Formato 20200615
                    agendaMedicoFechaSeleccionada=getAgendaMedicoFecha(fechaFormateada);
                    if (agendaMedicoFechaSeleccionada == null) {
                        Toast.makeText(AltaDeTurno.this, "ERROR: No hay turnos en la fecha seleccionada", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(AltaDeTurno.this, Paciente_AltaDeTurno2.class);
                        intent.putExtra("usuario", (Serializable) usuario);
                        intent.putExtra("agendaMedicoFecha", (Serializable) agendaMedicoFechaSeleccionada);
                        intent.putExtra("horarioSeleccionado", (Serializable) horarioSeleccionado);
                        intent.putExtra("fechaSeleccionada", (Serializable) fechaFormateada);
                        intent.putExtra("especialidadSeleccionada", (Serializable) especialidadSeleccionada);
                        intent.putExtra("checkAllMedicos", checkAllMedicos);
                        startActivity(intent);
                    }
                }
            }
        });

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

        horarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                if(parent.getItemAtPosition(i).toString().equals("Tarde")){
                    horarioSeleccionado = "T";
                }
                else{
                    horarioSeleccionado = "M";
                }

                if(++check > 1) {
                    getTurnosCalendario(especialidadSeleccionada.getId(),medicoSeleccionado.getIdUsuario(),mesSeleccionado,anioSeleccionado,horarioSeleccionado);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        calendarView.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                mesSeleccionado = month;
                anioSeleccionado = year;
                Toast.makeText(AltaDeTurno.this, mesSeleccionado + " "+ anioSeleccionado, Toast.LENGTH_SHORT).show();
                getTurnosCalendario(especialidadSeleccionada.getId(),medicoSeleccionado.getIdUsuario(),mesSeleccionado,anioSeleccionado,horarioSeleccionado);
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
                    pintarCalendario();
                } else {
                    //calendarView.getMarkedDates().getAll().clear();
                    pintarCalendario();
                    if(listafechasConTurnosDisponibles.contains(fechaFormatoJapones)) {
                        calendarView.unMarkDate(date.setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.GREEN)));
                    }
                    calendarView.markDate(date.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Color.GREEN)));
                    diaSeleccionado=date.getDay();
                }
            }
        });

        spMedicos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                medicoSeleccionado = (Medico) parent.getItemAtPosition(position);
                getTurnosCalendario(especialidadSeleccionada.getId(),medicoSeleccionado.getIdUsuario(),mesSeleccionado,anioSeleccionado,horarioSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //TODO: ver si poner algo aca....
            }
        });
    }

    private AgendaMedicoFecha getAgendaMedicoFecha(String fechaFormateada) {
        if(listaAgendaMedicoFecha!=null) {
            for (AgendaMedicoFecha agendaMedicoFecha : listaAgendaMedicoFecha) {
                if (agendaMedicoFecha.getFecha().equals(fechaFormateada)) {
                    return agendaMedicoFecha;
                }
            }
        }
        return null;
    }

    private void getTurnosCalendarioAllMedicos(Long idEspecialidad, int mes, int anio, String horario){
        //Esta funcion va a pintar el calendario con los turnos disponibles
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoFechaService agendaMedicoFechaService = retrofit.create(AgendaMedicoFechaService.class);

        Call<List<AgendaMedicoFecha>> call = agendaMedicoFechaService.getAgendaMedicoFechasByEspecialidad_Periodo_Horario(idEspecialidad,mes, anio, horario);
        call.enqueue(new Callback<List<AgendaMedicoFecha>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoFecha>> call, Response<List<AgendaMedicoFecha>> response) {

                if (!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(AltaDeTurno.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (!response.body().isEmpty()) {
                        listaAgendaMedicoFecha = response.body();
                    } else {
                        if (listaAgendaMedicoFecha != null) {
                            listaAgendaMedicoFecha.clear();
                        }
                    }

                    //Cargo el array con los dias que hay turnos disponibles
                    getDiasConTurnosDisponibles();
                    pintarCalendario();
                }

            }

            @Override
            public void onFailure(Call<List<AgendaMedicoFecha>> call, Throwable t) {
                Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTurnosCalendario(Long idEspecialidad, Long idMedico, int mes, int anio, String horario){
        //Esta funcion va a pintar el calendario con los turnos disponibles
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoFechaService agendaMedicoFechaService = retrofit.create(AgendaMedicoFechaService.class);

        Call<List<AgendaMedicoFecha>> call = agendaMedicoFechaService.getAgendaMedicoFechasByEspecialidad_Medico_Periodo_Horario(idEspecialidad,idMedico,mes, anio, horario);
        call.enqueue(new Callback<List<AgendaMedicoFecha>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoFecha>> call, Response<List<AgendaMedicoFecha>> response) {

                if(!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(AltaDeTurno.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!response.body().isEmpty()) {
                        listaAgendaMedicoFecha = response.body();
                    }
                    else{
                        if(listaAgendaMedicoFecha!=null) {
                            listaAgendaMedicoFecha.clear();
                        }
                    }

                    //Cargo el array con los dias que hay turnos disponibles
                    getDiasConTurnosDisponibles();
                    pintarCalendario();
                }
            }
            @Override
            public void onFailure(Call<List<AgendaMedicoFecha>> call, Throwable t) {
                Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Esta función devuelve el turno más proximo disponible.
    private void getProximoTurnoDisponible(Long idEspecialidad, Long idMedico, int mes, int anio, String horario, Boolean checkAll){
        int mesSiguiente;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoFechaService agendaMedicoFechaService = retrofit.create(AgendaMedicoFechaService.class);

        //Calculo el mes siguiente porque el actual ya se que no hay turnos

        if(mes==12) {
            mesSiguiente = 1;
            anio++;
        }
        else{
            mesSiguiente = mes+1;
        }

        if(checkAllMedicos) { //Si busca los turnos de TODOS los medicos

        }
        else{ // Si busca los tunos de un medico en particular
            Call<List<AgendaMedicoFecha>> call = agendaMedicoFechaService.getAgendaMedicoFechasByEspecialidad_Medico_Periodo_Horario(idEspecialidad, idMedico, mesSiguiente, anio, horario);
            call.enqueue(new Callback<List<AgendaMedicoFecha>>() {
                @Override
                public void onResponse(Call<List<AgendaMedicoFecha>> call, Response<List<AgendaMedicoFecha>> response) {
                    if (!response.isSuccessful()) {
                        Gson gson = new Gson();
                        MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                        Toast.makeText(AltaDeTurno.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        if (!response.body().isEmpty()) {
                            listaProximasAgendaMedicoFecha = response.body();
                            if (listaProximasAgendaMedicoFecha != null) {
                                if(!listaProximasAgendaMedicoFecha.isEmpty()) { //Si hay turnos el mes que viene
                                    Collections.sort(listaProximasAgendaMedicoFecha, new Comparator<AgendaMedicoFecha>() {
                                        @Override
                                        public int compare(AgendaMedicoFecha o1, AgendaMedicoFecha o2) {
                                            String t1 = o1.getFecha();
                                            String t2 = o2.getFecha();
                                            return t1.compareToIgnoreCase(t2);
                                        }
                                    });
                                    //Me traigo los turnos de la AgendaMedicoFecha
                                    getTurnosDeFechaAgendaMedico(listaProximasAgendaMedicoFecha.get(0).getId(),horarioSeleccionado);
                                }
                            }
                        } else {
                            Toast.makeText(AltaDeTurno.this, "No hay turnos disponibles en este mes ni en el próximo. LISTA DE ESPERA2", Toast.LENGTH_LONG).show();
                            if (listaProximasAgendaMedicoFecha != null) {
                                listaProximasAgendaMedicoFecha.clear();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<AgendaMedicoFecha>> call, Throwable t) {
                    Toast.makeText(AltaDeTurno.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Esta funcion trae los turnos de una AgendaMedicoFecha y carga la lista de proximosTurnos
    private void getTurnosDeFechaAgendaMedico(long idAgendaMedicoFecha, String horarioSeleccionado) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoFechaService agendaMedicoFechaService = retrofit.create(AgendaMedicoFechaService.class);

        Call<List<AgendaMedicoTurno>> call = agendaMedicoFechaService.getTurnosDeUnaFechaYHorarioEspecifico(idAgendaMedicoFecha,horarioSeleccionado);
        call.enqueue(new Callback<List<AgendaMedicoTurno>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoTurno>> call, Response<List<AgendaMedicoTurno>> response) {
                if(!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(AltaDeTurno.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                   listaProximosTurnos=response.body();
                    if (listaProximosTurnos != null) {
                        if(!listaProximosTurnos.isEmpty()) { //Si hay turnos en la agenda
                            Collections.sort(listaProximosTurnos, new Comparator<AgendaMedicoTurno>() {
                                @Override
                                public int compare(AgendaMedicoTurno o1, AgendaMedicoTurno o2) {
                                    String t1 = o1.getTurnoDesde();
                                    String t2 = o2.getTurnoDesde();
                                    return t1.compareToIgnoreCase(t2);
                                }
                            });

                            String fechaFormateada = StringHelper.convertirFechaAFormato_dd_mm_aaaa(listaProximasAgendaMedicoFecha.get(0).getFecha());
                            Toast.makeText(AltaDeTurno.this, "El próximo turno disponible es: "+fechaFormateada + " "+listaProximosTurnos.get(0).getTurnoDesde(), Toast.LENGTH_SHORT).show();
                            listaProximasAgendaMedicoFecha.clear();
                            listaProximosTurnos.clear();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<AgendaMedicoTurno>> call, Throwable t) {
                Toast.makeText(AltaDeTurno.this, "ERROR: Falló la conexión al servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pintarCalendario() {
        calendarView.getMarkedDates().getAll().clear();
        if(listafechasConTurnosDisponibles!=null) {
            if(!listafechasConTurnosDisponibles.isEmpty()) {
                for (String a : listafechasConTurnosDisponibles) {
                    DateData d = new DateData(Integer.valueOf(a.substring(0, 4)), Integer.valueOf(a.substring(4, 6)), Integer.valueOf(a.substring(6, 8)));
                    calendarView.markDate(
                            d.setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.GREEN))
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


    private void getDiasConTurnosDisponibles() {
        listafechasConTurnosDisponibles.clear();
        if(listaAgendaMedicoFecha !=null) {
            for (AgendaMedicoFecha a : listaAgendaMedicoFecha) {
                listafechasConTurnosDisponibles.add(a.getFecha());
            }

            // delete duplicates (if any) from 'listafechasConTurnosDisponibles'
            if (!listafechasConTurnosDisponibles.isEmpty()) {
                listafechasConTurnosDisponibles = new ArrayList<String>(new LinkedHashSet<String>(listafechasConTurnosDisponibles));
            }
        }
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
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(AltaDeTurno.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(AltaDeTurno.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
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

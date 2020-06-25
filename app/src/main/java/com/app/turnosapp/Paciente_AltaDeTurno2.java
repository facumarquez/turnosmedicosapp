package com.app.turnosapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Interface.AgendaMedicoFechaService;
import com.app.turnosapp.Interface.AgendaPacienteService;
import com.app.turnosapp.Interface.PacienteService;
import com.app.turnosapp.Model.AgendaMedicoFecha;
import com.app.turnosapp.Model.AgendaMedicoTurno;
import com.app.turnosapp.Model.AgendaPaciente;
import com.app.turnosapp.Model.Especialidad;
import com.app.turnosapp.Model.ManejoErrores.MensajeError;
import com.app.turnosapp.Model.Medico;
import com.app.turnosapp.Model.Paciente;
import com.app.turnosapp.Model.Turno;
import com.app.turnosapp.Model.Usuario;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class Paciente_AltaDeTurno2 extends AppCompatActivity {

    //Inicializo los controles
    private ListView listView;
    private Button btSolicitarTurno;
    private Spinner spMedico;
    private TextView tvMedico;
    private CheckBox cbFiltrar;

    //Atributos que voy a utilizar
    private Usuario usuario;
    private Medico medico;
    private AgendaMedicoFecha agendaMedicoFecha;
    private AgendaMedicoTurno turnoSeleccionado;
    private Paciente paciente;
    private String horarioSeleccionado;
    private boolean checkAllMedicos;
    private String fechaSeleccionada; //Fecha seleccionada en pantalla anterior
    private Especialidad especialidadSeleccionada; //Fecha seleccionada en pantalla anterior
    private List<Medico> listaMedicos;
    private Medico medicoSeleccionado;
    private int posicionSeleccionada=-1;

    //Auxiliares
    private int check = 0; //Este check es para que no se ejecute la llamada que trae los turnosDisponibles 2 veces la primera vez(una cuando se setea el medico y otra cuando carga la pantalla)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente__alta_de_turno2);

        listView = (ListView) findViewById(R.id.lvTurnos);
        btSolicitarTurno = (Button) findViewById(R.id.btSolicitarTurno);
        spMedico = (Spinner) findViewById(R.id.spMedico2);
        tvMedico = (TextView) findViewById(R.id.tvMedico2);
        cbFiltrar = (CheckBox) findViewById(R.id.cbFiltrar);

        //Recibo el dato de la pantalla anterior
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = (Usuario) extras.getSerializable("usuario");
            horarioSeleccionado = (String) extras.getSerializable("horarioSeleccionado");
            agendaMedicoFecha = (AgendaMedicoFecha) extras.getSerializable("agendaMedicoFecha");
            fechaSeleccionada = (String) extras.getSerializable("fechaSeleccionada");
            especialidadSeleccionada = (Especialidad) extras.getSerializable("especialidadSeleccionada");
            checkAllMedicos = (boolean) extras.getSerializable("checkAllMedicos");
        }

        //Formateo los datos para hacer la llamada que trae los turnos
        int horarioFormateado;
        if (horarioSeleccionado == "M") {
            horarioFormateado = 0;
        } else {
            horarioFormateado = 1;
        }

        //Me traigo el objeto Paciente porque lo voy a necesitar para solicitar el turno
        getPaciente(usuario.getIdUsuario());

        //Me traigo todos los turnos con los filtros de la pantalla anterior
        if (!checkAllMedicos) { //Si no seleccionó TODOS
            tvMedico.setVisibility(View.GONE);
            spMedico.setVisibility(View.GONE);
            cbFiltrar.setVisibility(View.GONE);
            //Para listar los turnos del medico seleccionado en la pantalla anterior
            getTurnosSegunFiltros();
        } else { //Si seleccionó TODOS
            tvMedico.setEnabled(false);
            spMedico.setEnabled(false);
            cbFiltrar.setVisibility(View.VISIBLE);
            //Para llenar el Spinner
            getMedicosConTurnosEnFechayHorarioPorEspecialidad();
            //Para listar los turnos de TODOS los Medicos
            getTurnosAllMedicos();
        }

        spMedico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(++check > 1) {
                    medicoSeleccionado = (Medico) parent.getItemAtPosition(position);
                    getTurnosSegunFiltrosDelMedicoSeleccionado();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //TODO: ver si poner algo aca....
            }
        });

        cbFiltrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    tvMedico.setEnabled(false);
                    spMedico.setEnabled(false);
                    //Para listar los turnos de TODOS los Medicos
                    getTurnosAllMedicos();
                }
                else{
                    tvMedico.setEnabled(true);
                    spMedico.setEnabled(true);
                    //Para listar los turnos del medico seleccionado en el Spinner de esta pantalla
                    medicoSeleccionado = (Medico) spMedico.getSelectedItem();
                    getTurnosSegunFiltrosDelMedicoSeleccionado();
                }
            }
        });


        btSolicitarTurno.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(turnoSeleccionado==null){
                    Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: Debe seleccionar un turno", Toast.LENGTH_SHORT).show();
                }
                else{
                   //Armo el objeto agendaPaciente para solicitar el turno luego
                    AgendaPaciente agendaPaciente = new AgendaPaciente();
                    agendaPaciente.setPaciente(paciente);
                    agendaPaciente.setTurno(turnoSeleccionado);

                    //Solicito el turno seleccionado del listado
                    solicitarTurno(agendaPaciente);
                }
            }
        });

    }

    //Este metodo es para llenar el Spinner de Medicos con los filtros de la pantalla anterior
    private void getMedicosConTurnosEnFechayHorarioPorEspecialidad() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoFechaService agendaMedicoFechaService = retrofit.create(AgendaMedicoFechaService.class);

        Call<List<Medico>> call = agendaMedicoFechaService.getMedicosPorFechaDeAtencion_Especialidad_Horario(fechaSeleccionada,horarioSeleccionado,especialidadSeleccionada.getId());
        call.enqueue(new Callback<List<Medico>>() {
            @Override
            public void onResponse(Call<List<Medico>> call, Response<List<Medico>> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: No se pudieron obtener las especialidades", Toast.LENGTH_SHORT).show();
                } else {
                    if (!response.body().isEmpty()) {
                        listaMedicos = response.body();

                        //Cargo los datos en el Spinner
                        if(listaMedicos!=null) {
                            ArrayAdapter<Medico> adapter = new ArrayAdapter<Medico>(Paciente_AltaDeTurno2.this, android.R.layout.simple_spinner_item, listaMedicos);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spMedico.setAdapter(adapter);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Medico>> call, Throwable t) {
                Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: No se pudieron obtener los medicos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPaciente(long idUsuario) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PacienteService pacienteService = retrofit.create(PacienteService.class);
        Call<Paciente> call = pacienteService.getPaciente(idUsuario);
        call.enqueue(new Callback<Paciente>() {
            @Override
            public void onResponse(Call<Paciente> call, Response<Paciente> response) {
                if(!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(Paciente_AltaDeTurno2.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    //Cargo los turnos en el listado para que el paciente pueda seleccionar el que desea.
                    paciente=response.body();
                }
            }
            @Override
            public void onFailure(Call<Paciente> call, Throwable t) {
                Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: Falló la conexión al servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void solicitarTurno(AgendaPaciente agendaPaciente) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaPacienteService agendaPacienteService = retrofit.create(AgendaPacienteService.class);
        Call<AgendaPaciente> call = agendaPacienteService.crearAgendaPaciente(agendaPaciente);
        call.enqueue(new Callback<AgendaPaciente>() {
            @Override
            public void onResponse(Call<AgendaPaciente> call, Response<AgendaPaciente> response) {
                if(!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(Paciente_AltaDeTurno2.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    //Cargo los turnos en el listado para que el paciente pueda seleccionar el que desea.
                    Toast.makeText(Paciente_AltaDeTurno2.this, "Se ha agendado el turno", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(Paciente_AltaDeTurno2.this, Paciente_HomeActivity.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(Call<AgendaPaciente> call, Throwable t) {
                Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: Falló la conexión al servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getTurnosSegunFiltros() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoFechaService agendaMedicoFechaService = retrofit.create(AgendaMedicoFechaService.class);

        Call<List<AgendaMedicoTurno>> call = agendaMedicoFechaService.getTurnosDeUnaFechaYHorarioEspecifico(agendaMedicoFecha.getId(),horarioSeleccionado);
        call.enqueue(new Callback<List<AgendaMedicoTurno>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoTurno>> call, Response<List<AgendaMedicoTurno>> response) {
                if(!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(Paciente_AltaDeTurno2.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    //Cargo los turnos en el listado para que el paciente pueda seleccionar el que desea.
                    cargarDatos(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<AgendaMedicoTurno>> call, Throwable t) {
                Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: Falló la conexión al servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Este hay que modificarlo cuando este el método que trae todos los turnos
    private void getTurnosAllMedicos() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoFechaService agendaMedicoFechaService = retrofit.create(AgendaMedicoFechaService.class);


        Call<List<AgendaMedicoTurno>> call = agendaMedicoFechaService.getTurnosDeTodosLosMedicos(fechaSeleccionada,horarioSeleccionado,especialidadSeleccionada.getId());
        call.enqueue(new Callback<List<AgendaMedicoTurno>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoTurno>> call, Response<List<AgendaMedicoTurno>> response) {
                if(!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(Paciente_AltaDeTurno2.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    //Cargo los turnos en el listado para que el paciente pueda seleccionar el que desea.
                    cargarDatos(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<AgendaMedicoTurno>> call, Throwable t) {
                Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: Falló la conexión al servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Este hay que modificarlo cuando este el método que trae todos los turnos
    private void getTurnosSegunFiltrosDelMedicoSeleccionado() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoFechaService agendaMedicoFechaService = retrofit.create(AgendaMedicoFechaService.class);

        Call<List<AgendaMedicoTurno>> call = agendaMedicoFechaService.getTurnosDeUnMedicoEspecifico(fechaSeleccionada,horarioSeleccionado,medicoSeleccionado.getIdUsuario(),especialidadSeleccionada.getId());
        call.enqueue(new Callback<List<AgendaMedicoTurno>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoTurno>> call, Response<List<AgendaMedicoTurno>> response) {
                if(!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(Paciente_AltaDeTurno2.this, mensaje.getMessage(), Toast.LENGTH_LONG).show();
                }
                else{
                    //Cargo los turnos en el listado para que el paciente pueda seleccionar el que desea.
                    cargarDatos(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<AgendaMedicoTurno>> call, Throwable t) {
                Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: Falló la conexión al servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDatos(List<AgendaMedicoTurno> turnos) {

        //si no hay turnos se inicializa listado...
        if (turnos == null) {
            turnos = new ArrayList<AgendaMedicoTurno>();
        }
        else{
            Collections.sort(turnos, new Comparator<AgendaMedicoTurno>() {
                @Override
                public int compare(AgendaMedicoTurno o1, AgendaMedicoTurno o2) {
                    String t1=o1.getTurnoDesde();
                    String t2=o2.getTurnoDesde();
                    return t1.compareToIgnoreCase(t2);
                }
            });
        }

        Paciente_AltaDeTurno2.MyAdapter adapter = new Paciente_AltaDeTurno2.MyAdapter(this, turnos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                turnoSeleccionado = (AgendaMedicoTurno) parent.getAdapter().getItem(position);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbTurno);

                if (checkBox.isChecked()) {
                    posicionSeleccionada = -1;
                } else {
                    posicionSeleccionada = position;
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    class MyAdapter extends ArrayAdapter<AgendaMedicoTurno> {

        Context context;
        List<AgendaMedicoTurno> turnos;

        MyAdapter(Context c, List<AgendaMedicoTurno> turnos) {
            super(c, R.layout.paciente_item_turno_alta, turnos);
            this.context = c;
            this.turnos = turnos;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.paciente_item_turno_alta, parent, false);

            TextView fecha = row.findViewById(R.id.textView1);
            TextView especialidad = row.findViewById(R.id.textView2);
            TextView doctor = row.findViewById(R.id.textView3);
            CheckBox checkBox = row.findViewById(R.id.cbTurno);

            checkBox.setTag(position);
            if (position == posicionSeleccionada) {
                checkBox.setChecked(true);
            } else checkBox.setChecked(false);

            String fechaFormateada = StringHelper.convertirFechaAFormato_dd_mm_aaaa(turnos.get(position).getAgendaMedicoHorario().getAgendaMedicoFecha().getFecha());
            fecha.setText(fechaFormateada + " " + turnos.get(position).getTurnoDesde());
            especialidad.setText(turnos.get(position).getAgendaMedicoHorario().getAgendaMedicoFecha().getEspecialidad().getNombre());
            doctor.setText(turnos.get(position).getAgendaMedicoHorario().getAgendaMedicoFecha().getAgendaMedico().getMedico().getFullname());

            return row;
        }
    }
}

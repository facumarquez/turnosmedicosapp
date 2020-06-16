package com.app.turnosapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
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
import com.app.turnosapp.Model.Medico;
import com.app.turnosapp.Model.Paciente;
import com.app.turnosapp.Model.Usuario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Paciente_AltaDeTurno2 extends AppCompatActivity {

    private ListView listView;
    private Button btSolicitarTurno;

    private Usuario usuario;
    private Medico medico;
    private AgendaMedicoFecha agendaMedicoFecha;
    private AgendaMedicoTurno turnoSeleccionado;
    private Paciente paciente;
    private String horarioSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente__alta_de_turno2);

        listView = (ListView) findViewById(R.id.lvTurnos);
        btSolicitarTurno = (Button) findViewById(R.id.btSolicitarTurno);


        //Recibo el dato de la pantalla anterior
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            usuario = (Usuario) extras.getSerializable("usuario");
            horarioSeleccionado = (String) extras.getSerializable("horarioSeleccionado");
            agendaMedicoFecha = (AgendaMedicoFecha) extras.getSerializable("agendaMedicoFecha");
        }

        //Formateo los datos para hacer la llamada que trae los turnos
        int horarioFormateado;
        if(horarioSeleccionado == "M"){
            horarioFormateado=0;
        }
        else{
            horarioFormateado=1;
        }

        //Me traigo el objeto Paciente porque lo voy a necesitar para solicitar el turno
        getPaciente(usuario.getIdUsuario());

        //Me traigo todos los turnos con los filtros de la pantalla anterior
        getTurnosSegunFiltros(agendaMedicoFecha.getFecha(),agendaMedicoFecha.getAgendaMedico().getMedico().getIdUsuario(),agendaMedicoFecha.getEspecialidad().getId(),horarioFormateado);

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
                    Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: No se pudieron obtener los turnos", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: No se pudieron obtener los turnos", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Cargo los turnos en el listado para que el paciente pueda seleccionar el que desea.
                    Toast.makeText(Paciente_AltaDeTurno2.this, "Se ha agendado el turno", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AgendaPaciente> call, Throwable t) {
                Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: Falló la conexión al servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getTurnosSegunFiltros(String fecha, long idMedico, long idEspecialidad, int horario ) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaMedicoFechaService agendaMedicoFechaService = retrofit.create(AgendaMedicoFechaService.class);

        //Si hay muchas AgendaMedico en la lista como hago??????????????????????
        Call<List<AgendaMedicoTurno>> call = agendaMedicoFechaService.getTurnosDeUnafechaEspecifica(agendaMedicoFecha.getId());
        call.enqueue(new Callback<List<AgendaMedicoTurno>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoTurno>> call, Response<List<AgendaMedicoTurno>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(Paciente_AltaDeTurno2.this, "ERROR: No se pudieron obtener los turnos", Toast.LENGTH_SHORT).show();
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

        Paciente_AltaDeTurno2.MyAdapter adapter = new Paciente_AltaDeTurno2.MyAdapter(this, turnos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < parent.getCount(); i++) {
                    View v = parent.getChildAt(i);
                    CheckBox checkBox = (CheckBox) v.findViewById(R.id.cbTurno);
                    checkBox.setChecked(false);
                }
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbTurno);
                checkBox.setChecked(true);
                turnoSeleccionado = (AgendaMedicoTurno) parent.getAdapter().getItem(position);
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

            String fechaFormateada = StringHelper.convertirFechaAFormato_dd_mm_aaaa(turnos.get(position).getAgendaMedicoHorario().getAgendaMedicoFecha().getFecha());
            fecha.setText(fechaFormateada + " " + turnos.get(position).getTurnoDesde());
            especialidad.setText(turnos.get(position).getAgendaMedicoHorario().getAgendaMedicoFecha().getEspecialidad().getNombre());
            doctor.setText(turnos.get(position).getAgendaMedicoHorario().getAgendaMedicoFecha().getAgendaMedico().getMedico().getFullname());

            return row;
        }
    }
}

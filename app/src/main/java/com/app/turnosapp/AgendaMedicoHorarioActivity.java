package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.turnosapp.Helpers.RetrofitConnection;
import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Interface.AgendaMedicoHorarioService;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.AgendaMedicoFecha;
import com.app.turnosapp.Model.AgendaMedicoHorario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaMedicoHorarioActivity extends AppCompatActivity {

    MyAdapter adapter;
    private List<AgendaMedicoFecha> fechasAgendaSeleccionadas = new ArrayList<AgendaMedicoFecha>();
    private List<AgendaMedicoHorario> horariosAgenda = new ArrayList<AgendaMedicoHorario>();
    private AgendaMedico agendaMedico;

    private TextView tvdias;

    private TimePicker tpkDesde;
    private TimePicker tpkHasta;
    ListView lvHorarios;
    private Button volverAtras;
    private Button btAgregar;
    private Button btConfirmarAgenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_medico);

        Intent intentAgendaMedicoFecha = getIntent();
        fechasAgendaSeleccionadas = (List<AgendaMedicoFecha>)intentAgendaMedicoFecha.getSerializableExtra(("fechasAgenda"));
        agendaMedico = (AgendaMedico)intentAgendaMedicoFecha.getSerializableExtra(("agendaMedico"));

        tvdias = (TextView) findViewById(R.id.tvdias);
        for (AgendaMedicoFecha fecha: fechasAgendaSeleccionadas) {
            tvdias.setText(tvdias.getText() + " - " + fecha.getFecha().substring(6,8));
        }

        tpkDesde = (TimePicker) findViewById(R.id.tpDesde);
        tpkDesde.setIs24HourView(true);
        tpkDesde.setCurrentHour(9);
        tpkDesde.setCurrentMinute(0);

        tpkHasta = (TimePicker) findViewById(R.id.tpHasta);
        tpkHasta.setIs24HourView(true);
        tpkHasta.setCurrentHour(18);
        tpkHasta.setCurrentMinute(0);


        volverAtras = (Button)findViewById(R.id.buttonVolver);
        btConfirmarAgenda = (Button)findViewById(R.id.btnConfirmarAgenda);

        btAgregar = (Button)findViewById(R.id.btnAgregar);

        lvHorarios = findViewById(R.id.listView);

        adapter = new MyAdapter(this, horariosAgenda, R.drawable.confirm,R.drawable.bin);
        lvHorarios.setAdapter(adapter);

        //Botones
        volverAtras.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                Intent intent = new Intent(AgendaMedicoHorarioActivity.this, AgendaMedicoFechaActivity.class);
                intent.putExtra("agendaMedico", (Serializable) agendaMedico);

                startActivity(intent);
            }
        });

        btAgregar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                for (AgendaMedicoFecha fecha:fechasAgendaSeleccionadas) {
                    List<AgendaMedicoHorario> horarios = new ArrayList<AgendaMedicoHorario>();
                    String horarioDesde;
                    String horarioHasta;

                    int horaDesde= tpkDesde.getCurrentHour();
                    int horaHasta= tpkHasta.getCurrentHour();

                    int minDesde=tpkDesde.getCurrentMinute();
                    int minHasta=tpkHasta.getCurrentMinute();

                    horarioDesde = StringHelper.rellenarConCeros(String.valueOf(horaDesde),2) + ":"+ StringHelper.rellenarConCeros(String.valueOf(minDesde),2);
                    horarioHasta = StringHelper.rellenarConCeros(String.valueOf(horaHasta),2) + ":"+ StringHelper.rellenarConCeros(String.valueOf(minHasta),2);

                    AgendaMedicoHorario horarioNuevo = new AgendaMedicoHorario(horarioDesde,horarioHasta);
                    horarioNuevo.setAgendaMedicoFecha(fecha);
                    horarios = fecha.getHorarios();
                    if (horarios == null) {
                        horarios = new ArrayList<AgendaMedicoHorario>();
                    }
                    horarios.add(horarioNuevo);
                    fecha.setHorarios(horarios);
                    adapter.add(horarioNuevo);
                }
                //adapter.add(horarios);
            }
        });

        //Botones
        btConfirmarAgenda.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                Intent intent = new Intent(AgendaMedicoHorarioActivity.this, AgendaMedicoFechaActivity.class);
                intent.putExtra("agendaMedico", (Serializable) agendaMedico);

                startActivity(intent);
            }
        });
    }


    class MyAdapter extends ArrayAdapter<AgendaMedicoHorario> {

        Context context;
        List<AgendaMedicoHorario> horarios;
        int rImgs_confirm;
        int rImgs_delete;

        MyAdapter (Context c, List<AgendaMedicoHorario> horariosAgenda, int imgs_confirm,int imgs_delete) {
            super(c, R.layout.medico_item_horario, R.id.tvFecha, horariosAgenda);
            this.context = c;
            this.rImgs_confirm = imgs_confirm;
            this.rImgs_delete = imgs_delete;
            this.horarios = horariosAgenda;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.medico_item_horario, parent, false);
            ImageView ivConfirmar = row.findViewById(R.id.ivconfirmar);
            ImageView ivEliminar = row.findViewById(R.id.iveliminar);
            TextView tvFecha = row.findViewById(R.id.tvFecha);
            TextView horario = row.findViewById(R.id.tvHorario);

            ivConfirmar.setImageResource(rImgs_confirm);
            ivEliminar.setImageResource(rImgs_delete);
            String fecha = StringHelper.convertirFechaAFormato_dd_mm_aaaa(horariosAgenda.get(position).getAgendaMedicoFecha().getFecha());
            tvFecha.setText(fecha);
            horario.setText(horariosAgenda.get(position).getHoraDesde() + "-" + horariosAgenda.get(position).getHoraHasta());


            //adding a click listener to the button to remove item from the list
            ivEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eliminarHorario(position);
                }
            });
            ivConfirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //modificarHorario(position);
                }
            });

            return row;
        }


        private void eliminarHorario(final int position) {
            //TODO:sacar en un futuro
            adapter.remove(horariosAgenda.get(position));

            AgendaMedicoHorarioService agendaMedicoHorarioService = RetrofitConnection.obtenerConexion
                    (getString(R.string.apiTurnosURL)).create(AgendaMedicoHorarioService.class);

            Call<Void> call = agendaMedicoHorarioService.deleteAgendaMedicoHorario (horariosAgenda.get(position).getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(AgendaMedicoHorarioActivity.this, "No se ha podido eliminar el horario", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AgendaMedicoHorarioActivity.this, "No se ha podido eliminar el horario", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AgendaMedicoHorarioActivity.this, "No se ha podido eliminar el horario", Toast.LENGTH_SHORT).show();
                }
            });
         }


        private void modificarhorario(final int position) {
/*
            AgendaPacienteService agendaPacienteService = RetrofitConnection.obtenerConexion
                    (getString(R.string.apiTurnosURL)).create(AgendaPacienteService.class);

            Call<Turno> call = agendaPacienteService.confirmarTurno(turnos.get(position).getId());
            call.enqueue(new Callback<Turno>() {
                @Override
                public void onResponse(Call<Turno> call, Response<Turno> response) {
                    if (!response.isSuccessful()) {
                        Gson gson = new Gson();
                        MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                        Toast.makeText(Paciente_HomeActivity.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Paciente_HomeActivity.this, "Se ha confirmado el turno", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                }

                @Override
                public void onFailure(Call<Turno> call, Throwable t) {
                    Toast.makeText(Paciente_HomeActivity.this, "Error al actualizar el estado del turno", Toast.LENGTH_SHORT).show();
                }
            });
            */
        }

    }
}


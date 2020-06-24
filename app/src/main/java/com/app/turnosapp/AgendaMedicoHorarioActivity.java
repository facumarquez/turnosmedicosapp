package com.app.turnosapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.turnosapp.Callbacks.IAgendaMedicoHorarioCallback;
import com.app.turnosapp.Helpers.RetrofitConnection;
import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Interface.AgendaMedicoFechaService;
import com.app.turnosapp.Interface.AgendaMedicoHorarioService;
import com.app.turnosapp.Interface.AgendaMedicoService;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.AgendaMedicoFecha;
import com.app.turnosapp.Model.AgendaMedicoHorario;
import com.app.turnosapp.Model.ManejoErrores.MensajeError;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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

    TimePickerDialog picker;
    EditText etDesde;
    EditText etHasta;

    ListView lvHorarios;
    private Button volverAtras;
    private Button btAgregar;
    private Button btConfirmarAgenda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_medico);

        volverAtras = (Button)findViewById(R.id.buttonVolver);
        btConfirmarAgenda = (Button)findViewById(R.id.btnConfirmarAgenda);
        btAgregar = (Button)findViewById(R.id.btnAgregar);
        lvHorarios = findViewById(R.id.listView);

        Intent intentAgendaMedicoFecha = getIntent();
        fechasAgendaSeleccionadas = (List<AgendaMedicoFecha>)intentAgendaMedicoFecha.getSerializableExtra(("fechasAgenda"));
        agendaMedico = (AgendaMedico)intentAgendaMedicoFecha.getSerializableExtra(("agendaMedico"));

        tvdias = (TextView) findViewById(R.id.tvdias);
        for (AgendaMedicoFecha fecha: fechasAgendaSeleccionadas) {
            tvdias.setText(tvdias.getText() + " - " + fecha.getFecha().substring(6,8));
        }

        etDesde = (EditText) findViewById(R.id.ethoradesde);
        etHasta = (EditText) findViewById(R.id.ethorahasta);
        etDesde.setInputType(InputType.TYPE_NULL);
        etHasta.setInputType(InputType.TYPE_NULL);

        etDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(AgendaMedicoHorarioActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int hora, int minuto) {
                                String desde = StringHelper.rellenarConCeros(String.valueOf(hora),2) + ":"+ StringHelper.rellenarConCeros(String.valueOf(minuto),2);
                                etDesde.setText(desde);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        etHasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(AgendaMedicoHorarioActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int hora, int minuto) {
                                String hasta = StringHelper.rellenarConCeros(String.valueOf(hora),2) + ":"+ StringHelper.rellenarConCeros(String.valueOf(minuto),2);
                                etHasta.setText(hasta);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        obtenerHorariosDeFechasSeleccionadas(fechasAgendaSeleccionadas, new IAgendaMedicoHorarioCallback() {
            @Override
            public void getHorariosAgendaMedico(List<AgendaMedicoHorario> horarios) {
                horariosAgenda = horarios;
                setearAdapter(horariosAgenda);
            }
        });


        //Botones
        volverAtras.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                Intent intent = new Intent(AgendaMedicoHorarioActivity.this, AgendaMedicoFechaActivity.class);
                intent.putExtra("agendaMedico", (Serializable) agendaMedico);
                //TODO: ver si poner tambien la lista de fechas obtenidas de la pantalla anterior....
                finish();
                startActivity(intent);
            }
        });

        btConfirmarAgenda.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                dialogConfirmarAgenda();
            }
        });

        btAgregar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (etDesde.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Debe ingresar el horario desde!", Toast.LENGTH_SHORT).show();
                }else if (etHasta.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Debe ingresar el horario hasta!", Toast.LENGTH_SHORT).show();
                }else{
                    for (AgendaMedicoFecha fecha:fechasAgendaSeleccionadas) {

                        AgendaMedicoHorario horarioNuevo = new AgendaMedicoHorario(etDesde.getText().toString(),etHasta.getText().toString());
                        horarioNuevo.setAgendaMedicoFecha(fecha);
                        horariosAgenda.add(horarioNuevo);
                    }

                    crearHorariosAgenda(horariosAgenda, new IAgendaMedicoHorarioCallback() {
                        @Override
                        public void getHorariosAgendaMedico(List<AgendaMedicoHorario> horarios) {
                            //horariosAgenda = horarios;
                            //adapter.notifyDataSetChanged();
                            finish();
                            startActivity(getIntent());
                        }
                    });
                }

            }
        });
    }

    private void dialogEliminarHorario(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aviso!");
        builder.setMessage("Está seguro que desea eliminar el horario del día seleccionado?");
        builder.setCancelable(false);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                eliminarHorario(position);

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

    private void dialogConfirmarAgenda(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aviso!");
        builder.setMessage("Está seguro que desea confirmar la agenda para los días seleccionados?");
        builder.setCancelable(false);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                confirmarAgenda(agendaMedico.getId());

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

    private void confirmarAgenda(long idAgendaMedico) {

        AgendaMedicoService agendaMedicoService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoService.class);

        Call<Boolean> call = agendaMedicoService.confirmarAgenda(idAgendaMedico);
        call.enqueue(new Callback <Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoHorarioActivity.this, "No se ha confirmado la agenda", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AgendaMedicoHorarioActivity.this, AgendaMedicoFechaActivity.class);
                    intent.putExtra("agendaMedico", (Serializable) agendaMedico);
                    Toast.makeText(AgendaMedicoHorarioActivity.this, "Se ha confirmado los días de la agenda", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(AgendaMedicoHorarioActivity.this, "Error al confirmar la agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setearAdapter(List<AgendaMedicoHorario> horarios){

        adapter = new MyAdapter( this, horarios, R.drawable.confirm,R.drawable.bin);
        lvHorarios.setAdapter(adapter);
    }

    private void crearHorariosAgenda(List<AgendaMedicoHorario> horarios, final IAgendaMedicoHorarioCallback callback) {

       AgendaMedicoHorarioService agendaMedicoHorarioService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoHorarioService.class);
        //TODO: debe devolver algo???
        Call<List<AgendaMedicoHorario>> call = agendaMedicoHorarioService.crearHorarios(horarios);
        call.enqueue(new Callback <List<AgendaMedicoHorario>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoHorario>> call, Response<List<AgendaMedicoHorario>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoHorarioActivity.this, "No se creó el horario", Toast.LENGTH_SHORT).show();
                } else {
                    callback.getHorariosAgendaMedico(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AgendaMedicoHorario>> call, Throwable t) {
                Toast.makeText(AgendaMedicoHorarioActivity.this, "Error al crear el horario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerHorariosDeFechasSeleccionadas(List<AgendaMedicoFecha> fechasSeleccionadas, final IAgendaMedicoHorarioCallback callback) {

        AgendaMedicoFechaService agendaMedicoFechaService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoFechaService.class);

        Call<List<AgendaMedicoHorario>> call = agendaMedicoFechaService.obtenerHorariosDeFechas(fechasSeleccionadas);
        call.enqueue(new Callback <List<AgendaMedicoHorario>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoHorario>> call, Response<List<AgendaMedicoHorario>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoHorarioActivity.this, "No se pudieron obtener los horarios", Toast.LENGTH_SHORT).show();
                } else {
                    callback.getHorariosAgendaMedico(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AgendaMedicoHorario>> call, Throwable t) {
                Toast.makeText(AgendaMedicoHorarioActivity.this, "No se pudieron obtener los horarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarHorario(final int position) {

        AgendaMedicoHorarioService agendaMedicoHorarioService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoHorarioService.class);

        Call<Void> call = agendaMedicoHorarioService.deleteAgendaMedicoHorario (horariosAgenda.get(position).getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(AgendaMedicoHorarioActivity.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Operación confirmada!", Toast.LENGTH_SHORT).show();
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

                    dialogEliminarHorario(position);

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


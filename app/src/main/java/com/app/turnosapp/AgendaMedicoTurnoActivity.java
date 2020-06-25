package com.app.turnosapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.DialogInterface;
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
import android.widget.Toast;

import com.app.turnosapp.Callbacks.IAgendaMedicoTurnoCallback;
import com.app.turnosapp.Helpers.RetrofitConnection;
import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Interface.AgendaMedicoFechaService;
import com.app.turnosapp.Interface.AgendaMedicoService;
import com.app.turnosapp.Interface.AgendaMedicoTurnoService;
import com.app.turnosapp.Model.AgendaMedico;
import com.app.turnosapp.Model.AgendaMedicoFecha;
import com.app.turnosapp.Model.AgendaMedicoTurno;
import com.app.turnosapp.Model.ManejoErrores.MensajeError;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaMedicoTurnoActivity extends AppCompatActivity {

    MyAdapter adapter;
    TextView tvFecha;
    ListView lvTurnos;
    private Button btAtras;

    private AgendaMedico agendaMedico;
    private AgendaMedicoFecha fechaSeleccionada;
    private String diaSeleccionado;

    private List<AgendaMedicoTurno> turnosAgenda = new ArrayList<AgendaMedicoTurno>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turno_medico);

        Intent intentAgendaMedico = getIntent();
        agendaMedico = (AgendaMedico)intentAgendaMedico.getSerializableExtra(("agendaMedico"));
        diaSeleccionado = (String)intentAgendaMedico.getSerializableExtra(("diaSeleccionado"));

        tvFecha = findViewById(R.id.tvFecha);
        lvTurnos = findViewById(R.id.lvturnos);
        btAtras = (Button)findViewById(R.id.btnvolver);


        //Botones
        btAtras.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                Intent intent = new Intent(AgendaMedicoTurnoActivity.this, AgendaMedicoFechaActivity.class);
                intent.putExtra("agendaMedico", (Serializable) agendaMedico);
                startActivity(intent);
            }
        });

        obtenerFechaEspecifica(agendaMedico, diaSeleccionado);

        tvFecha.setText(tvFecha.getText() + StringHelper.convertirFechaAFormato_dd_mm_aaaa(diaSeleccionado));

    }

    class MyAdapter extends ArrayAdapter<AgendaMedicoTurno> {

        Context context;
        List<AgendaMedicoTurno> turnos;
        int rImgs_delete;

        MyAdapter (Context c, List<AgendaMedicoTurno> turnosFecha, int imgs_confirm,int imgs_delete) {
            super(c, R.layout.medico_item_turno, R.id.tvFecha, turnosFecha);
            this.context = c;
            this.rImgs_delete = imgs_delete;
            this.turnos = turnosFecha;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.medico_item_turno, parent, false);
            ImageView ivEliminar = row.findViewById(R.id.iveliminar);
            TextView turno = row.findViewById(R.id.tvTurno);
            TextView estado = row.findViewById(R.id.tvEstado);
            ivEliminar.setImageResource(rImgs_delete);

            turno.setText(turnosAgenda.get(position).getTurnoDesde());
            estado.setText(turnosAgenda.get(position).getEstado().toString());

            ivEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogEliminarTurno(position);
                }
            });

            return row;
        }
    }

    private void setearAdapter(List<AgendaMedicoTurno> turnos){

        adapter = new MyAdapter( this, turnos, R.drawable.confirm,R.drawable.bin);
        lvTurnos.setAdapter(adapter);
    }

    private void dialogEliminarTurno(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aviso!");
        builder.setMessage("Está seguro que desea eliminar el turno seleccionado?");
        builder.setCancelable(false);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                eliminarTurno(position);

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

    private void eliminarTurno(final int position) {

        AgendaMedicoTurnoService agendaMedicoTurnoService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoTurnoService.class);

        Call<Void> call = agendaMedicoTurnoService.deleteTurno(turnosAgenda.get(position).getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(AgendaMedicoTurnoActivity.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Operación confirmada!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AgendaMedicoTurnoActivity.this, "No se ha podido eliminar el turno", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerTurnosDeFechaSeleccionada(AgendaMedicoFecha fechaSeleccionada, final IAgendaMedicoTurnoCallback callback) {

        AgendaMedicoFechaService agendaMedicoFechaService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoFechaService.class);

        Call<List<AgendaMedicoTurno>> call = agendaMedicoFechaService.obtenerTurnosDeFecha(fechaSeleccionada.getId());
        call.enqueue(new Callback <List<AgendaMedicoTurno>>() {
            @Override
            public void onResponse(Call<List<AgendaMedicoTurno>> call, Response<List<AgendaMedicoTurno>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoTurnoActivity.this, "No se pudieron obtener los turnos", Toast.LENGTH_SHORT).show();
                } else {
                    callback.getTurnosAgendaMedico(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AgendaMedicoTurno>> call, Throwable t) {
                Toast.makeText(AgendaMedicoTurnoActivity.this, "No se pudieron obtener los turnos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerFechaEspecifica(AgendaMedico agendaMedico, String fecha) {

        AgendaMedicoService agendaMedicoService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(AgendaMedicoService.class);

        Call<AgendaMedicoFecha> call = agendaMedicoService.obtenerFechaEspecificaDeAgenda(agendaMedico.getId(),fecha);
        call.enqueue(new Callback <AgendaMedicoFecha>() {
            @Override
            public void onResponse(Call<AgendaMedicoFecha> call, Response<AgendaMedicoFecha> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AgendaMedicoTurnoActivity.this, "No se pudo obtener la fecha", Toast.LENGTH_SHORT).show();
                } else {
                    fechaSeleccionada = response.body();

                    obtenerTurnosDeFechaSeleccionada(fechaSeleccionada, new IAgendaMedicoTurnoCallback() {
                        @Override
                        public void getTurnosAgendaMedico(List<AgendaMedicoTurno> turnos) {
                            turnosAgenda = turnos;
                            if (turnos == null || turnos.size() == 0){
                                Toast.makeText(AgendaMedicoTurnoActivity.this, "No hay turnos para la fecha especificada", Toast.LENGTH_SHORT).show();
                            }
                            setearAdapter(turnos);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<AgendaMedicoFecha> call, Throwable t) {
                Toast.makeText(AgendaMedicoTurnoActivity.this, "No se pudo obtener la fecha", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


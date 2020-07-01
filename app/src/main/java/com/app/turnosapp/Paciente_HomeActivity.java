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
import android.widget.Toast;

import com.app.turnosapp.Helpers.RetrofitConnection;
import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Interface.AgendaPacienteService;
import com.app.turnosapp.Interface.PacienteService;
import com.app.turnosapp.Model.Especialidad;
import com.app.turnosapp.Model.ManejoErrores.MensajeError;
import com.app.turnosapp.Model.Turno;
import com.app.turnosapp.Model.Usuario;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Paciente_HomeActivity extends AppCompatActivity {

    ListView listView;
    Usuario usuario;
    Button altaDeTurno;
    Button verPerfil;
    Button btnCerrarSesion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente__home);

        altaDeTurno = (Button) findViewById(R.id.btAltaTurno);
        verPerfil = (Button) findViewById(R.id.btVerPerfil);
        btnCerrarSesion = (Button) findViewById(R.id.btCerrarSesion);


        //Recibo el dato de la pantalla anterior
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = (Usuario) extras.getSerializable("usuario");
        }

        getTurnosPaciente(usuario.getIdUsuario());

        altaDeTurno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                estaAlDiaConLaCuota(usuario.getIdUsuario());
            }
        });

        verPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Paciente_HomeActivity.this, Usuario_verPerfil.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("tipo", "paciente".toUpperCase());
                startActivity(intent);
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Paciente_HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Hace una llamada a la API, obtiene los turnos del paciente y los muestra en la lista
    private void getTurnosPaciente(long idPaciente) {
        List<Turno> listaTurnos;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AgendaPacienteService agendaPacienteService = retrofit.create(AgendaPacienteService.class);

        Call<List<Turno>> call = agendaPacienteService.getTurnosPaciente(idPaciente);
        call.enqueue(new Callback<List<Turno>>() {
            @Override
            public void onResponse(Call<List<Turno>> call, Response<List<Turno>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Paciente_HomeActivity.this, "ERROR: No se pudieron obtener los turnos", Toast.LENGTH_SHORT).show();
                } else {
                    cargarDatos(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Turno>> call, Throwable t) {
                Toast.makeText(Paciente_HomeActivity.this, "ERROR: Falló la conexión al servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private Boolean estaAlDiaConLaCuota(long idPaciente) {

        PacienteService pacienteService = RetrofitConnection.obtenerConexion
                (getString(R.string.apiTurnosURL)).create(PacienteService.class);

        Call<Boolean> call = pacienteService.pacienteAlDia(idPaciente);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    Gson gson = new Gson();
                    MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                    Toast.makeText(Paciente_HomeActivity.this, mensaje.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if (response.body() == true) {
                        Intent intent = new Intent(Paciente_HomeActivity.this, AltaDeTurno.class);
                        intent.putExtra("usuario", usuario);
                        startActivity(intent);
                    } else {
                         Toast.makeText(Paciente_HomeActivity.this, "No puede tomar un turno porque que no se encuentra al día con el pago de la cuota.", Toast.LENGTH_LONG).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(Paciente_HomeActivity.this, "Error al obtener el paciente", Toast.LENGTH_SHORT).show();
            }

        });
        return null;
    }

    private void cargarDatos(List<Turno> turnos) {
        listView = findViewById(R.id.listView);
        //si no hay turnos se inicializa listado...
        if (turnos == null) {
            turnos = new ArrayList<Turno>();
        }
        else {
            Collections.sort(turnos, new Comparator<Turno>() {
                @Override
                public int compare(Turno o1, Turno o2) {
                    String t1=o1.getFechaTurno()+o1.getTurnoDesde();
                    String t2=o2.getFechaTurno()+o2.getTurnoDesde();
                    return t1.compareToIgnoreCase(t2);
                }
            });
        }

        MyAdapter adapter = new MyAdapter(this, turnos, R.drawable.confirm, R.drawable.bin);
        listView.setAdapter(adapter);
    }


    class MyAdapter extends ArrayAdapter<Turno> {

        Context context;
        List<Turno> turnos;
        int rImgs_confirm;
        int rImgs_delete;

        MyAdapter (Context c, List<Turno> turnos, int imgs_confirm,int imgs_delete) {
            super(c, R.layout.paciente_item_turno, R.id.textView1, turnos);
            this.context = c;
            this.rImgs_confirm = imgs_confirm;
            this.rImgs_delete = imgs_delete;
            this.turnos=turnos;

        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.paciente_item_turno, parent, false);
            ImageView ivConfirmar = row.findViewById(R.id.ivconfirmar);
            ImageView ivEliminar = row.findViewById(R.id.iveliminar);
            TextView fecha = row.findViewById(R.id.textView1);
            TextView especialidad = row.findViewById(R.id.textView2);
            TextView doctor = row.findViewById(R.id.textView3);

            // now set our resources on views
            ivConfirmar.setImageResource(rImgs_confirm);
            ivEliminar.setImageResource(rImgs_delete);
            String estadoDelTurno = "";

            
            if (turnos.get(position).getEstadoTurno().equals("Reservado".toUpperCase())){
                estadoDelTurno = "";
            }else{
                estadoDelTurno = " - " + turnos.get(position).getEstadoTurno();
            }

            String fechaFormateada= StringHelper.convertirFechaAFormato_dd_mm_aaaa(turnos.get(position).getFechaTurno());
            fecha.setText(fechaFormateada +" "+ turnos.get(position).getTurnoDesde() + estadoDelTurno);
            especialidad.setText(turnos.get(position).getEspecialidad().getNombre());
            doctor.setText(turnos.get(position).getMedico().getFullname());


            //adding a click listener to the button to remove item from the list
            ivEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    anularTurno(position);
                }
            });
            ivConfirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmarTurno(position);
                }
            });

            return row;
        }

        private void anularTurno(final int position) {

            AgendaPacienteService agendaPacienteService = RetrofitConnection.obtenerConexion
                    (getString(R.string.apiTurnosURL)).create(AgendaPacienteService.class);

            Call<Turno> call = agendaPacienteService.anularTurno(turnos.get(position).getId());
            call.enqueue(new Callback<Turno>() {

                @Override
                public void onResponse(Call<Turno> call, Response<Turno> response) {
                    if (!response.isSuccessful()) {
                        Gson gson = new Gson();
                        MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                        Toast.makeText(Paciente_HomeActivity.this, mensaje.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    } else {
                        Toast.makeText(Paciente_HomeActivity.this, "Se ha anulado el turno", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                }

                @Override
                public void onFailure(Call<Turno> call, Throwable t) {
                    Toast.makeText(Paciente_HomeActivity.this, "Error al actualizar el estado del turno", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void confirmarTurno(final int position) {

            AgendaPacienteService agendaPacienteService = RetrofitConnection.obtenerConexion
                    (getString(R.string.apiTurnosURL)).create(AgendaPacienteService.class);

            Call<Void> call = agendaPacienteService.confirmarTurno(turnos.get(position).getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Gson gson = new Gson();
                        MensajeError mensaje = gson.fromJson(response.errorBody().charStream(), MensajeError.class);
                        Toast.makeText(Paciente_HomeActivity.this, mensaje.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Paciente_HomeActivity.this, "Se ha confirmado el turno", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(Paciente_HomeActivity.this, "Error al actualizar el estado del turno", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    }


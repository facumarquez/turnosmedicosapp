package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.turnosapp.Helpers.StringHelper;
import com.app.turnosapp.Interface.TurnosAPI;
import com.app.turnosapp.Model.Turno;
import com.app.turnosapp.Model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Paciente_HomeActivity extends AppCompatActivity {

    List<Turno> listaTurnos;
    String userID;
    ListView listView;
    Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente__home);

        //Recibo el dato de la pantalla anterior
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            usuario = (Usuario) extras.getSerializable("usuario");
        }

        getTurnosPaciente(usuario.getIdUsuario());
    }

    //Hace una llamada a la API, obtiene los turnos del paciente y los muestra en la lista
    private void getTurnosPaciente(long idPaciente){
        List<Turno> listaTurnos;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.apiTurnosURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TurnosAPI turnosAPI = retrofit.create(TurnosAPI.class);

        Call<List<Turno>> call = turnosAPI.getTurnosPaciente(idPaciente);
        call.enqueue(new Callback<List<Turno>>() {
            @Override
            public void onResponse(Call<List<Turno>> call, Response<List<Turno>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(Paciente_HomeActivity.this, "ERROR: No se pudieron obtener los turnos", Toast.LENGTH_SHORT).show();
                }
                else{
                    cargarDatos(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Turno>> call, Throwable t) {
                Toast.makeText(Paciente_HomeActivity.this, "ERROR: Falló la conexión al servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cargarDatos(List<Turno> turnos) {


        listView = findViewById(R.id.listView);
        MyAdapter adapter = new MyAdapter(this, turnos, R.drawable.confirm,R.drawable.bin);
        listView.setAdapter(adapter);
    }

    public void setListaTurnos(List<Turno> listaTurnos) {
        this.listaTurnos = listaTurnos;
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
            ImageView images = row.findViewById(R.id.image);
            ImageView images2 = row.findViewById(R.id.image2);
            TextView fecha = row.findViewById(R.id.textView1);
            TextView especialidad = row.findViewById(R.id.textView2);
            TextView doctor = row.findViewById(R.id.textView3);

            // now set our resources on views
            images.setImageResource(rImgs_confirm);
            images2.setImageResource(rImgs_delete);
            String fechaFormateada= StringHelper.convertirFechaAFormato_dd_mm_aaaa(turnos.get(position).getFechaTurno());
            fecha.setText(fechaFormateada +" "+turnos.get(position).getTurnoDesde());
            especialidad.setText(turnos.get(position).getEspecialidad().getNombre());
            doctor.setText(turnos.get(position).getMedico().getFullname());

            //adding a click listener to the button to remove item from the list
            images2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    borrarTurno(position);
                }
            });
            images.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmarTurno(position);
                }
            });

            return row;
        }

        private void borrarTurno(final int position) {
            Toast.makeText(Paciente_HomeActivity.this, "Borrar: "+position+" - "+turnos.get(position) , Toast.LENGTH_SHORT).show();
        }

        private void confirmarTurno(final int position) {
            Toast.makeText(Paciente_HomeActivity.this, "Confirmar: "+position+" - "+turnos.get(position), Toast.LENGTH_SHORT).show();
        }
    }
    }


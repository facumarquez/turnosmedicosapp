package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.turnosapp.Interface.TurnosAPI;
import com.app.turnosapp.Model.Turnos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    TextView tvTurnos;
    Button altaTurno, verPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        altaTurno = (Button)findViewById(R.id.btAltaTurno);
        tvTurnos = findViewById(R.id.tvTurnos);
        verPerfil = (Button)findViewById(R.id.btVerPerfil);

        //Cargo los turnos
        getTurnos();

        //Botones
        verPerfil.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                Intent intent = new Intent(HomeActivity.this, paciente_verPerfil.class);
                startActivity(intent);
            }
        });
    }

    private void getTurnos(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.pruebasURL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TurnosAPI turnosAPI = retrofit.create(TurnosAPI.class);

        Call<List<Turnos>> call = turnosAPI.getTurnos();
        call.enqueue(new Callback<List<Turnos>>() {
            @Override
            public void onResponse(Call<List<Turnos>> call, Response<List<Turnos>> response) {
                if(!response.isSuccessful()) {
                    tvTurnos.setText("Código de error:"+response.code());
                }
                else{
                    List<Turnos> listaTurnos = response.body();
                    for(Turnos turno: listaTurnos){
                        tvTurnos.append(turno.toString());
                        tvTurnos.append("\n----------------------------------------\n\n");
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Turnos>> call, Throwable t) {
                tvTurnos.setText(t.getMessage());
            }
        });
    }


}

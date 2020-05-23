package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AgendaMedicoActivity extends AppCompatActivity {
    private Spinner meses;
    private Spinner anios;
    private Button verAgenda;
    private Button verPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_medico);

        meses = (Spinner)findViewById(R.id.spMes);
        anios = (Spinner)findViewById(R.id.spAnio);
        verAgenda = (Button)findViewById(R.id.btnAgenda);
        verPerfil = (Button)findViewById(R.id.btnPerfil);

        ArrayAdapter<CharSequence> adapterMeses = ArrayAdapter.createFromResource(this,
                R.array.meses_array, android.R.layout.simple_spinner_item);
        adapterMeses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        meses.setAdapter(adapterMeses);

        ArrayAdapter<CharSequence> adapterAnios = ArrayAdapter.createFromResource(this,
                R.array.anios_array, android.R.layout.simple_spinner_item);
        adapterAnios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        anios.setAdapter(adapterAnios);

        //Botones
        verAgenda.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                Intent intent = new Intent(AgendaMedicoActivity.this, AgendaMedicoFechaActivity.class);
                startActivity(intent);
            }
        });

        verPerfil.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                Intent intent = new Intent(AgendaMedicoActivity.this, Usuario_verPerfil.class);
                startActivity(intent);
            }
        });
    }
}

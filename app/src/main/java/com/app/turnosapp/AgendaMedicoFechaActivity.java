package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AgendaMedicoFechaActivity extends AppCompatActivity {

    private Button modHorarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fecha_medico);

        modHorarios = (Button)findViewById(R.id.buttonModHorarios);

        //Botones
        modHorarios.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                Intent intent = new Intent(AgendaMedicoFechaActivity.this, AgendaMedicoHorarioActivity.class);
                startActivity(intent);
            }
        });

    }
}

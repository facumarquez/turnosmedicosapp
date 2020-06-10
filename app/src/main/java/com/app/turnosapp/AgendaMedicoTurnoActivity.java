package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

import java.io.LineNumberInputStream;

public class AgendaMedicoTurnoActivity extends AppCompatActivity {

    private Button volverAtras;

    ListView listView;
    String mTitle[] = {"04/05/2020", "11/05/2020", "18/05/2020", "25/05/2020"};
    String mDescription[] = {"08:00 - 12:00", "08:00 - 12:00", "08:00 - 12:00", "08:00 - 12:00"};
    int images_confirm[] = {R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm};
    int images_delete[] = {R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin};
    // so our images and other things are set in array

    // now paste some images in drawable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turno_medico);

        volverAtras = (Button)findViewById(R.id.buttonVolver);

        listView = findViewById(R.id.listView);
        // now create an adapter class

        MyAdapter adapter = new MyAdapter(this, mTitle, mDescription, images_confirm,images_delete);
        listView.setAdapter(adapter);
        // there is my mistake...
        // now again check this..

        // now set item click on list view

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AgendaMedicoTurnoActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
        // so item click is done now check list view

        //Botones
        volverAtras.setOnClickListener(new View.OnClickListener(){
            public void onClick(android.view.View view){
                Intent intent = new Intent(AgendaMedicoTurnoActivity.this, AgendaMedicoFechaActivity.class);
                startActivity(intent);
            }
        });


    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        String rDescription[];
        int rImgs_confirm[];
        int rImgs_delete[];

        MyAdapter (Context c, String title[], String description[], int imgs_confirm[],int imgs_delete[]) {
            super(c, R.layout.paciente_item_turno, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs_confirm = imgs_confirm;
            this.rImgs_delete = imgs_delete;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.paciente_item_turno, parent, false);
            ImageView ivConfirmar = row.findViewById(R.id.ivconfirmar);
            ImageView ivEliminar = row.findViewById(R.id.iveliminar);
            TextView myTitle = row.findViewById(R.id.textView1);
            TextView myDescription = row.findViewById(R.id.textView2);

            // now set our resources on views
            ivConfirmar.setImageResource(rImgs_confirm[position]);
            ivEliminar.setImageResource(rImgs_delete[position]);
            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);




            return row;
        }
    }
}

package com.app.turnosapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

import java.io.LineNumberInputStream;

public class Paciente_HomeActivity extends AppCompatActivity {

    ListView listView;
    String mTitle[] = {"20/05/2020 10:30hs", "21/05/2020 10:30hs", "22/05/2020 10:30hs", "23/05/2020 10:30hs", "24/05/2020 10:30hs", "21/05/2020 10:30hs", "22/05/2020 10:30hs", "23/05/2020 10:30hs", "24/05/2020 10:30hs"};
    String mDescription[] = {"Odontología", "Pediatría", "Pediatría", "Ginecología", "Oftalmología", "Pediatría", "Pediatría", "Ginecología", "Oftalmología"};
    int images_confirm[] = {R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm, R.drawable.confirm};
    int images_delete[] = {R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin, R.drawable.bin};
    // so our images and other things are set in array

    // now paste some images in drawable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente__home);

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
             Toast.makeText(Paciente_HomeActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
        // so item click is done now check list view
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
            ImageView images = row.findViewById(R.id.image);
            ImageView images2 = row.findViewById(R.id.image2);
            TextView myTitle = row.findViewById(R.id.textView1);
            TextView myDescription = row.findViewById(R.id.textView2);

            // now set our resources on views
            images.setImageResource(rImgs_confirm[position]);
            images2.setImageResource(rImgs_delete[position]);
            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);




            return row;
        }
    }
    }


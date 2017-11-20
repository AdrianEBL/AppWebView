package edu.tecii.android.appwebview;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    Toolbar toolBar;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        toolBar = (Toolbar) findViewById(R.id.toolBarH);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Historial");

        listView = (ListView)findViewById(R.id.listView);

        cargarHistorial();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                History h = MainActivity.visitas.get(i);
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                intent.putExtra("enlace", h.enlace);
                startActivity(intent);
            }
        });
    }

    public void onResume() {
        super.onResume();
        cargarHistorial();
    }

    public void cargarHistorial() {
        try {
            File file = new File(getApplicationContext().getFilesDir(), "historial.txt");
            if (file.exists()){
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
                MainActivity.visitas = (ArrayList<History>)stream.readObject();
                stream.close();
                ArrayList<String> aux = new ArrayList<>();
                for (History h : MainActivity.visitas){
                    aux.add(h.pagina);
                }
                ArrayAdapter adapter =  new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, aux);
                listView.setAdapter(adapter);
            }
        } catch (Exception e) {

        }
    }
}

package co.edu.unal.sqlite;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView list;
    DBHelper myDB;
    Spinner spinner ;
    SearchView search;
    ArrayAdapter<String> adapter;
    ArrayList<String> bussinesslist;
    String text;
    static Cursor fila;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.classif);

        search= findViewById(R.id.search);

        list = findViewById(R.id.list);
        bussinesslist= new ArrayList<>();
        myDB = new DBHelper(this);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this,create_empresa.class));
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bussinesslist);
        list.setAdapter(adapter);


        search();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                text = spinner.getSelectedItem().toString();
                if(text.equals("Todos")){

                    fila = myDB.getAllData();
                    bussinesslist.clear();


                }else{
                    bussinesslist.clear();
                    fila = myDB.clasif(text);

                }

                if(fila.moveToFirst()){
                    do{
                        bussinesslist.add(fila.getString(1) +", "+ fila.getString(2));
                    }while(fila.moveToNext());
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view , int position,long id){
                // join an exiting room
                String [] name = bussinesslist.get(position).split(",");

                Cursor fila = myDB.get_id(name[0]);
                while (fila.moveToNext()) {
                    String id_b = fila.getString(0);


                    startActivity(new Intent(MainActivity.this, empresa_perfil.class).putExtra("id", id_b));
                }
            }
        });

        //spinersel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            fila = myDB.getAllData();
            bussinesslist.clear();
            if(fila.moveToFirst()){
                do{
                    bussinesslist.add(fila.getString(1) +", "+ fila.getString(2));
                }while(fila.moveToNext());
            }
            adapter.notifyDataSetChanged();
            return true;
        }
        else if (id==R.id.exit){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    public void search(){
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

}
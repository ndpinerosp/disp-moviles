package co.edu.unal.sqlite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class empresa_perfil extends AppCompatActivity {
    DBHelper myDB;
    TextView text_name,text_url,text_phone,text_email,text_clasification,text_product;
    Button update, delete;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_perfil);

        text_name =findViewById(R.id.text_name);
        text_url =findViewById(R.id.text_url);
        text_phone =findViewById(R.id.text_phone);
        text_email =findViewById(R.id.text_email);
        text_clasification =findViewById(R.id.text_clasificacion);
        text_product =findViewById(R.id.text_product);
        update= findViewById(R.id.btn_update);
        delete= findViewById(R.id.btn_delete);

        id = getIntent().getExtras().getString("id");
        myDB = new DBHelper(this);
        SQLiteDatabase bd = myDB.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from empresa_table where id = "+Integer.parseInt(id),null);
        while(fila.moveToNext()){
            text_name.setText(fila.getString(1));
            text_url.setText(fila.getString(2));
            text_phone.setText(fila.getString(3));
            text_email.setText(fila.getString(4));
            text_clasification.setText(fila.getString(5));
            text_product.setText(fila.getString(6));
        }

        bd.close();


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder res = new AlertDialog.Builder(empresa_perfil.this);
                res.setMessage("Desea eliminar la empresa").setCancelable(false);
                res.setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Integer deleterows= myDB.deleteData(id);
                        if (deleterows >0 ) {
                            Toast.makeText(empresa_perfil.this, "Eliminado", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(empresa_perfil.this,MainActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(empresa_perfil.this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                res.setNegativeButton("No",null);
                AlertDialog dialog = res.create();
                dialog.show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(empresa_perfil.this,update.class).putExtra("id",id));
                finish();
            }
        });


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
            startActivity(new Intent(empresa_perfil.this,MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
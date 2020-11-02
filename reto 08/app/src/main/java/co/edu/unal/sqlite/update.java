package co.edu.unal.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class update extends AppCompatActivity {

    DBHelper myDB;
    EditText editName,editUrl,editPhone,editEmail,editProduct;
    RadioGroup rgroup;
    RadioButton editCalification;
    Button btn_update;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        myDB = new DBHelper(this);
        editName = findViewById(R.id.up_name);
        editUrl = findViewById(R.id.up_url);
        editPhone = findViewById(R.id.up_phone);
        editEmail = findViewById(R.id.up_email);
        editProduct =findViewById(R.id.up_products);
        rgroup = findViewById(R.id.radio);
        btn_update=findViewById(R.id.btn_updateemp);

        id = getIntent().getExtras().getString("id") ;
        SQLiteDatabase bd = myDB.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from empresa_table where id = "+Integer.parseInt(id),null);
        while(fila.moveToNext()){
            editName.setText(fila.getString(1));
            editUrl.setText(fila.getString(2));
            editPhone.setText(fila.getString(3));
            editEmail.setText(fila.getString(4));
            editProduct.setText(fila.getString(6));
        }

        bd.close();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioid = rgroup.getCheckedRadioButtonId();
                editCalification = findViewById(radioid);

                String name = editName.getText().toString();
                String url= editUrl.getText().toString();
                String phone= editPhone.getText().toString();
                String email =editEmail.getText().toString();
                String calification= editCalification.getText().toString();
                String products= editProduct.getText().toString();

                boolean isUpdated= myDB.updateData(id,name,url,Integer.parseInt(phone),email,calification, products);

                if(isUpdated==true){
                    startActivity(new Intent(update.this,empresa_perfil.class).putExtra("id",id));
                    finish();
                    Toast.makeText(update.this, "Empresa Actualizada", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(update.this, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
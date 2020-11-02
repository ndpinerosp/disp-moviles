package co.edu.unal.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class create_empresa extends AppCompatActivity {
    DBHelper myDB;
    EditText editName,editUrl,editPhone,editEmail,editProduct;
    RadioGroup rgroup;
    RadioButton editCalification;
    Button btn_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_empresa);

        myDB = new DBHelper(this);
        editName = findViewById(R.id.edit_name);
        editUrl = findViewById(R.id.edit_url);
        editPhone = findViewById(R.id.edit_phone);
        editEmail = findViewById(R.id.edit_email);
        editProduct = findViewById(R.id.edit_products);
        rgroup = findViewById(R.id.radio);
        btn_create =findViewById(R.id.byn_createemp);

        AddData();
    }

    public void AddData(){
        btn_create.setOnClickListener(new View.OnClickListener() {
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

                if(!name.isEmpty() && !url.isEmpty() && !phone.isEmpty() && !email.isEmpty()  && !calification.isEmpty() && !products.isEmpty()) {
                    boolean isInserted= myDB.insertData(name,url,Integer.parseInt(phone),email,calification,products);

                    if(isInserted==true){
                        startActivity(new Intent(create_empresa.this,MainActivity.class));
                        finish();
                        Toast.makeText(create_empresa.this, "Empresa Creada", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(create_empresa.this, "Error al crear empresa", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(create_empresa.this, "Hay campos vacios", Toast.LENGTH_SHORT).show();
                }

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
            startActivity(new Intent(create_empresa.this,MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
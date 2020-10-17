package co.edu.unal.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class nameMultiplayer extends AppCompatActivity {

    EditText editText;
    Button signbtn;
    String playerName ="";

    FirebaseDatabase db;
    DatabaseReference playerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_multiplayer);

        editText = findViewById(R.id.editText);
        signbtn = findViewById(R.id.button);

        db= FirebaseDatabase.getInstance();
        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        playerName = preferences.getString("playerName","");
        if (!playerName.equals("")){
            playerRef= db.getReference("players/"+ playerName);
            addEventListener();
            playerRef.setValue("");
        }

        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // loging the player
                playerName = editText.getText().toString();
                editText.setText("");
                if(!playerName.equals("")){
                    signbtn.setText("Logging In");
                    signbtn.setEnabled(false);
                    playerRef=  db.getReference("players/"+ playerName);
                    addEventListener();
                    playerRef.setValue("");

                }
            }
        });

    }

    private void addEventListener(){
        //read from database
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //exito.
                if (!playerName.equals("")){
                    SharedPreferences preferences =getSharedPreferences("PREFS",0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(),lista_multiplayer.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //error
                signbtn.setText("Log In");
                signbtn.setEnabled(true);
                Toast.makeText(nameMultiplayer.this, "Error !", Toast.LENGTH_SHORT).show();
            }
        });



    }
}
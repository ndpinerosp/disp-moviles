package co.edu.unal.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class lista_multiplayer extends AppCompatActivity {

    ListView listView;
    Button button_room;
    List<String> roomlist;
    String playerName="";
    String roomName = "";

    FirebaseDatabase db;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_multiplayer);

        db= FirebaseDatabase.getInstance();
        //get player name
        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        playerName = preferences.getString("playerName","");
        roomName = playerName;

        listView = findViewById(R.id.list_room);
        button_room= findViewById(R.id.button_room);

        //all avaliable rooms
        roomlist = new ArrayList<>();

        button_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create room and add player
                button_room.setText("Creando Sala");
                button_room.setEnabled(false);
                roomName = playerName;
                roomRef= db.getReference("rooms/" + roomName + "/player1");
                Toast.makeText(lista_multiplayer.this, "Sala Creada", Toast.LENGTH_SHORT).show();
                addRoomEvent();
                roomRef.setValue(playerName);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view , int position,long id){
                // join an exiting room
                roomName = roomlist.get(position);
                roomRef= db.getReference("rooms/"+roomName+"/player2");
                addRoomEvent();
                roomRef.setValue(playerName);
            }
        });
        // show if new room avaliable
        addRoomsEvent();
    }

    private void addRoomEvent(){
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //join the room
                button_room.setText("Crear Sala");
                button_room.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(),AndroidTicTacToeActivity.class);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //error
                button_room.setText("Crear Sala");
                button_room.setEnabled(true);
                Toast.makeText(lista_multiplayer.this, "Error !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRoomsEvent(){
        roomsRef = db.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomlist.clear();
                Iterable<DataSnapshot> rooms= snapshot.getChildren();
                for (DataSnapshot snapshot1 :rooms){
                    roomlist.add(snapshot1.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(lista_multiplayer.this,android.R.layout.simple_list_item_1,roomlist);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error = nothing
            }
        });
    }
}
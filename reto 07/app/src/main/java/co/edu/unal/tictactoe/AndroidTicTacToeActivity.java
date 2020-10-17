package co.edu.unal.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AndroidTicTacToeActivity extends AppCompatActivity {


    String playerName="";
    String roomName="";
    String position="";
    String role="";
    String message="";

    FirebaseDatabase db;
    DatabaseReference messageRef;
    DatabaseReference posRef;
    DatabaseReference newRef;

    private TicTacToeGame mGame;
    private Boolean mGameOver=true,r=true;
    private int tiePoints,playerPoints,compPoints,move,winner;

    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;
    private BoardView mBoardView;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    boolean turn=true;
    private boolean mSoundOn = true;
    private SharedPreferences mPrefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseDatabase.getInstance();


        mGame = new TicTacToeGame();
        // Restore the scores from the persistent preference data source
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);




        mInfoTextView = (TextView) findViewById(R.id.information);
        mTextPlayer=(TextView) findViewById(R.id.player);
        mTextTie=(TextView) findViewById(R.id.tie);
        mTextComp=(TextView) findViewById(R.id.comp);


        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setColor(mPrefs.getInt("board_color",0xFFCCCCCC));
        mBoardView.setGame(mGame);



        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        playerName = preferences.getString("playerName","");

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            roomName= extras.getString("roomName");
            role();
        }
        messageRef= db.getReference("rooms/"+roomName+"/player");
        posRef= db.getReference("rooms/"+roomName+"/location");
        newRef= db.getReference("rooms/"+roomName+"/New Game");
        messageRef.setValue(message);
        posRef.setValue(position);
        newRef.setValue(1);
        addEventnewgame();
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

    }

    private void role(){
        if(roomName.equals(playerName)){
            role="host";
        }
        else{
            role="guest";
        }

    }



    private TextView mInfoTextView,mTextPlayer,mTextTie,mTextComp;

    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();   // Redraw the board
        mGameOver= false;
        turn=true;
        r=true;

    }

    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.player);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.comp);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }




    private boolean setMove(char player, int location){
    if(!mGameOver ) {
    if (mGame.setMove(player, location)) {
        if (player == TicTacToeGame.HUMAN_PLAYER) {
            if(mSoundOn){
            mHumanMediaPlayer.start();    // Play the sound effect
        }}else if (player == TicTacToeGame.COMPUTER_PLAYER){
                if(mSoundOn) {
                    mComputerMediaPlayer.start();
                }
            }

        message = role;
        messageRef.setValue(message);
        position = location+"";
        posRef.setValue(location);
        addRoomEvent();
        mBoardView.invalidate();   // Redraw the board

        return true;
        }



    }
             //mComputerMediaPlayer.start();
            return false;



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                newRef.setValue(1);
                //startNewGame();
                addEventnewgame();
                return true;
            case R.id.setting:
                startActivityForResult(new Intent(this, settings.class), 0);
                return true;

            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT_ID);
                return true;
        }
        return false;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tiePoints",tiePoints);
        outState.putInt("playerPoints",playerPoints);
        outState.putInt("compPoints",compPoints);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstance) {
        super.onRestoreInstanceState(savedInstance);

        tiePoints= savedInstance.getInt( "tiePoints");
        playerPoints= savedInstance.getInt( "playerPoints");
        compPoints= savedInstance.getInt( "compPoints");

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {

            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;

            case DIALOG_ABOUT_ID:

                builder = new AlertDialog.Builder(this);
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();

                break;


        }

        return dialog;
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;




            if (!mGameOver )	{
                winner = mGame.checkForWinner();

                    if(turn) {
                        setMove(TicTacToeGame.HUMAN_PLAYER, pos);
                        turn=false;
                    }
                    verification();
        }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            mSoundOn = mPrefs.getBoolean("sound", true);

        }
        mBoardView.setColor(mPrefs.getInt("board_color", 0xFFCCCCCC));
    }

    private void addRoomEvent(){
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //message recived
                if(!mGameOver && r){
                if (role.equals("host")){
                    if(snapshot.getValue(String.class).contains("guest")){
                       mInfoTextView.setText(R.string.turn_computer);
                       turn=false;


                    }else {
                        //turn=true;
                        mInfoTextView.setText(R.string.turn_human);
                        addPostEvent();

                    }
                }
                else{
                    if(snapshot.getValue(String.class).contains("host")){

                            mInfoTextView.setText(R.string.turn_computer);
                            turn=false;


                    }else{
                        //turn=true;
                        mInfoTextView.setText(R.string.turn_human);
                        addPostEvent();

                    }

                }
            }}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error Retry
                messageRef.setValue(message);
            }
        });
    }

    public void addPostEvent(){
        posRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                position = snapshot.getValue().toString();
                if(!mGameOver ) {
                    winner = mGame.checkForWinner();
                if (winner == 0 ) {

                move = Integer.parseInt(position);

                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    mBoardView.invalidate();
                    turn=true;

                    winner = mGame.checkForWinner();}
                    verification();
            }}


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verification() {
            if(winner==0){}
            else if (winner == 1 ) {
                mInfoTextView.setText(R.string.result_tie);
                if(r){
                tiePoints++;
                }
                turn = false;
                mTextTie.setText("Empate: " + tiePoints);
                r=false;

            } else if (winner == 2) {
                mGameOver = true;
                turn = false;
                playerPoints++;
                mTextPlayer.setText("Player: " + playerPoints);
                String defaultMessage = getResources().getString(R.string.result_human_wins);
                mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));


            } else {
                mInfoTextView.setText(R.string.result_computer_wins);
                mGameOver = true;
                turn = false;
                compPoints++;
                mTextComp.setText("Android: " + compPoints);
            }



    }

    private void addEventnewgame(){
        newRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(int.class).equals(1)){
                    startNewGame();
                    newRef.setValue(0);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}


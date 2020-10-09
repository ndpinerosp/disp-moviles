package co.edu.unal.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class AndroidTicTacToeActivity extends AppCompatActivity {
    private TicTacToeGame mGame;
    private Boolean mGameOver=true,r=false;
    private int tiePoints,playerPoints,compPoints;

    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;
    private BoardView mBoardView;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    boolean turn=false;
    private boolean mSoundOn = true;
    private SharedPreferences mPrefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGame = new TicTacToeGame();
        // Restore the scores from the persistent preference data source
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);




        mInfoTextView = (TextView) findViewById(R.id.information);
        mTextPlayer=(TextView) findViewById(R.id.player);
        mTextTie=(TextView) findViewById(R.id.tie);
        mTextComp=(TextView) findViewById(R.id.comp);


        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setColor(mPrefs.getInt("board_color",0xFFCCCCCC));
        mBoardView.setGame(mGame);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);
        startNewGame();

    }



    private TextView mInfoTextView,mTextPlayer,mTextTie,mTextComp;

    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();   // Redraw the board
        turn=false;
        mGameOver= false;

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
        }
        }
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
                startNewGame();
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
                                AndroidTicTacToeActivity.this.finish();
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
                int winner = mGame.checkForWinner();
                if (turn ==false){

                    setMove(TicTacToeGame.HUMAN_PLAYER, pos);
                    turn=true;
                }

                // If no winner yet, let the computer make a move

                if (winner == 0 ) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if(turn) {
                                mInfoTextView.setText(R.string.turn_computer);
                                if(mSoundOn) {
                                    mComputerMediaPlayer.start();
                                }
                                int move = mGame.getComputerMove();
                                turn = false;
                                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                                mBoardView.invalidate();

                            }
                        }
                    }, 1000);


                    winner = mGame.checkForWinner();}



                    if(winner==0){
                        mInfoTextView.setText(R.string.turn_human);
                    }
                    else if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                        tiePoints++;
                        mTextTie.setText("Empate: "+ tiePoints);
                        turn=false;
                    }
                    else if (winner == 2){
                        mGameOver=true;
                        turn=false;
                        playerPoints++;
                        mTextPlayer.setText("Player: "+playerPoints);
                        String defaultMessage = getResources().getString(R.string.result_human_wins);
                        mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));


                    }
                    else{
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mGameOver=true;
                        turn=false;
                        compPoints++;
                        mTextComp.setText("Android: "+compPoints);
                    }


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

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
        mBoardView.setColor(mPrefs.getInt("board_color", 0xFFCCCCCC));
    }


}


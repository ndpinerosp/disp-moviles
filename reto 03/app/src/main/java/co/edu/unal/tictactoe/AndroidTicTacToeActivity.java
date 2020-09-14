package co.edu.unal.tictactoe;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AndroidTicTacToeActivity extends AppCompatActivity {
    private TicTacToeGame mGame;
    private Boolean mGameOver=true,r=false;
    private int tiePoints,playerPoints,compPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mTextPlayer=(TextView) findViewById(R.id.player);
        mTextTie=(TextView) findViewById(R.id.tie);
        mTextComp=(TextView) findViewById(R.id.comp);

        mGame = new TicTacToeGame();
        startNewGame();

    }

    private Button mBoardButtons[];

    private TextView mInfoTextView,mTextPlayer,mTextTie,mTextComp;

    private void startNewGame(){
        mGame.clearBoard();
        mGameOver= false;
        //Resetear los botones
        for (int i = 0;i < mBoardButtons.length;i++){
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));

            mInfoTextView.setText(R.string.first_human);
            r= !r;
        }
    }
    private class ButtonClickListener implements View.OnClickListener{
        int location;

        public ButtonClickListener(int location){
            this.location=location;

        }
        public void onClick(View view){
            if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0 ) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }
if (mGameOver==false){
                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    tiePoints++;
                    mTextTie.setText("Empate: "+ tiePoints);
                }
                else if (winner == 2){
                    mInfoTextView.setText(R.string.result_human_wins);
                    mGameOver=true;
                    playerPoints++;
                    mTextPlayer.setText("Player: "+playerPoints);

                }
                else{
                    mInfoTextView.setText(R.string.result_computer_wins);
                    mGameOver=true;
                    compPoints++;
                    mTextComp.setText("Android: "+compPoints);
                }
        }}

    }
    }
    private void setMove(char player, int location){
        if(mGameOver==false){
        mGame.setMove(player,location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));


            if (player == TicTacToeGame.HUMAN_PLAYER)
                mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
            else
                mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.new_game);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startNewGame();
        return true;
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




}


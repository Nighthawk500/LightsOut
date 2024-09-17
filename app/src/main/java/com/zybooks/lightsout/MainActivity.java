package com.zybooks.lightsout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private final String GAME_STATE = "gameState";
    private LightsOutGame mGame;
    private GridLayout mLightGrid;
    private int mLightOnColor;
    private int mLightOffColor;
    private String mLightOnContent;
    private String mLightOffContent;
    private int mLightOnColorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLightGrid = findViewById(R.id.light_grid);

        // Add the same click handler to all grid buttons
        for (int buttonIndex = 0; buttonIndex < mLightGrid.getChildCount(); buttonIndex++) {
            Button gridButton = (Button) mLightGrid.getChildAt(buttonIndex);
            gridButton.setOnClickListener(this::onLightButtonClick);
        }

        // Long click listener for top left button cheat
        Button topLeftButton = (Button) mLightGrid.getChildAt(0);
        topLeftButton.setOnLongClickListener(this::onTopLeftButtonLongClick);

        mLightOffColor = ContextCompat.getColor(this, R.color.black);
        mLightOnContent = getString(R.string.on);
        mLightOffContent = getString(R.string.off);

        if (savedInstanceState == null) {
            //Set default color
            mLightOnColorId = R.color.yellow;
            mLightOnColor = ContextCompat.getColor(this, mLightOnColorId);
            mGame = new LightsOutGame();
            startGame();
        }
        else {
            mGame = new LightsOutGame();
            String gameState = savedInstanceState.getString(GAME_STATE);
            mGame.setState(gameState);
            //Restore color
            mLightOnColorId = savedInstanceState.getInt("lightOnColorId", R.color.yellow);
            //Set color
            mLightOnColor = ContextCompat.getColor(this, mLightOnColorId);
            setButtonColors();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(GAME_STATE, mGame.getState());
        //Save the color
        outState.putInt("lightOnColorId", mLightOnColorId);
    }

    private boolean onTopLeftButtonLongClick(View view) {
        mGame.turnAllLightsOff();
        setButtonColors();
        Toast.makeText(this, "Cheat activated, you win", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void startGame() {
        mGame.newGame();
        setButtonColors();
    }

    public void onHelpClick(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void onLightButtonClick(View view) {

        // Find the button's row and col
        int buttonIndex = mLightGrid.indexOfChild(view);
        int row = buttonIndex / LightsOutGame.GRID_SIZE;
        int col = buttonIndex % LightsOutGame.GRID_SIZE;

        mGame.selectLight(row, col);
        setButtonColors();

        // Congratulate the user if the game is over
        if (mGame.isGameOver()) {
            Toast.makeText(this, R.string.congrats, Toast.LENGTH_SHORT).show();
        }
    }

    private void setButtonColors() {

        for (int buttonIndex = 0; buttonIndex < mLightGrid.getChildCount(); buttonIndex++) {
            Button gridButton = (Button) mLightGrid.getChildAt(buttonIndex);

            // Find the button's row and col
            int row = buttonIndex / LightsOutGame.GRID_SIZE;
            int col = buttonIndex % LightsOutGame.GRID_SIZE;

            if (mGame.isLightOn(row, col)) {
                gridButton.setBackgroundColor(mLightOnColor);
                gridButton.setContentDescription(mLightOnContent);
            } else {
                gridButton.setBackgroundColor(mLightOffColor);
                gridButton.setContentDescription(mLightOffContent);
            }
        }
    }

    public void onNewGameClick(View view) {
        startGame();
    }

    public void onChangeColorClick(View view) {
        // Send current color ID to ColorActivity
        Intent intent = new Intent(this, ColorActivity.class);
        intent.putExtra(ColorActivity.EXTRA_COLOR, mLightOnColorId);
        mColorResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> mColorResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            //Create the on button color from the chosen color ID from ColorActivity
                            mLightOnColorId = data.getIntExtra(ColorActivity.EXTRA_COLOR, R.color.yellow);
                            mLightOnColor = ContextCompat.getColor(MainActivity.this, mLightOnColorId);
                            setButtonColors();
                        }
                    }
                }
            });
}
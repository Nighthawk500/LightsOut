// Specifies the app package name
package com.zybooks.lightsout;

// Imports class for handling activities
import android.app.Activity;

// Imports class to start a new activity
import android.content.Intent;

// Imports class for storing activity state
import android.os.Bundle;

// Imports class for handling UI
import android.view.View;

// Imports class for creating and handling buttons
import android.widget.Button;

// Imports class to create a layout grid type for the UI
import android.widget.GridLayout;

// Imports class to show short message popups to the user
import android.widget.Toast;

// Imports class for handling results from another activity
import androidx.activity.result.ActivityResult;

// Imports class for handling activity result callbacks
import androidx.activity.result.ActivityResultCallback;

// Imports class for launching an activity and receiving a result
import androidx.activity.result.ActivityResultLauncher;

// Imports class for handling activity results and permissions
import androidx.activity.result.contract.ActivityResultContracts;

// Imports class to add the nonnull annotation to indicate a method return null
import androidx.annotation.NonNull;

// Imports class for adding some compatibility for older features of the Activity class
import androidx.appcompat.app.AppCompatActivity;

// Imports class for accessing resources that's backwards compatible
import androidx.core.content.ContextCompat;

// Defines the main activity class and extends AppCompatActivity for backward compatibility
public class MainActivity extends AppCompatActivity {

    //  Joseph Marvin
    //  ISYS 221 - VL1
    //  Individual Programming Assignment #3 - Lights Out - Code Explanation
    //  Due: 9/22/2024

    // Create string for saving and restoring game state
    private final String GAME_STATE = "gameState";

    // Declares object for handling game logic
    private LightsOutGame mGame;

    // Declares object for the game's UI grid
    private GridLayout mLightGrid;

    // Variable to store the color when a light is on
    private int mLightOnColor;

    // Variable to store the color when a light is off
    private int mLightOffColor;

    // Variable to store the content description when a light is on
    private String mLightOnContent;

    // Variable to store the content description when a light is off
    private String mLightOffContent;

    // Variable to store the resource ID for the light on color
    private int mLightOnColorId;

    // Initializes the UI and game state
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Calls onCreate method to set up the activity
        super.onCreate(savedInstanceState);

        // Sets the content view to the activity_main layout
        setContentView(R.layout.activity_main);

        // Assigns the game's grid to mLightGrid
        mLightGrid = findViewById(R.id.light_grid);

        // Creates same listener for every button
        for (int buttonIndex = 0; buttonIndex < mLightGrid.getChildCount(); buttonIndex++) {
            // Finds the button at the current index of the game grid
            Button gridButton = (Button) mLightGrid.getChildAt(buttonIndex);
            // Sets the listener for the button
            gridButton.setOnClickListener(this::onLightButtonClick);
        }

        // Finds the top left button of the grid
        Button topLeftButton = (Button) mLightGrid.getChildAt(0);
        // Sets a long click listener for the top left button
        topLeftButton.setOnLongClickListener(this::onTopLeftButtonLongClick);

        // Assigns the resource color of black to the light out color variable
        mLightOffColor = ContextCompat.getColor(this, R.color.black);

        // Assigns the appropriate resource content to the light on content variable
        mLightOnContent = getString(R.string.on);

        // Assigns the appropriate resource content to the light off content variable
        mLightOffContent = getString(R.string.off);

        // Checks if the activity is being created for the first time
        if (savedInstanceState == null) {
            // Sets the default color for the light on ID variable to yellow
            mLightOnColorId = R.color.yellow;
            // Retrieves the appropriate color for light on based on the color ID
            mLightOnColor = ContextCompat.getColor(this, mLightOnColorId);
            // Starts a new game instance
            mGame = new LightsOutGame();
            // Starts a new game
            startGame();
        }
        // If there is saved state, restores the game state and settings
        else {
            // Starts a new game instance
            mGame = new LightsOutGame();
            // Retrieves the saved game state string
            String gameState = savedInstanceState.getString(GAME_STATE);
            // Sets the restored game state information
            mGame.setState(gameState);
            // Sets the saved color ID for light on with a default of yellow
            mLightOnColorId = savedInstanceState.getInt("lightOnColorId", R.color.yellow);
            // Sets the color for light on based on the saved color ID
            mLightOnColor = ContextCompat.getColor(this, mLightOnColorId);
            // Sets the colors from the restored game state
            setButtonColors();
        }
    }

    // Called when the activity is about to be paused or destroyed to save state
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Ensures state is properly saved
        super.onSaveInstanceState(outState);
        // Saves the current game state as a string
        outState.putString(GAME_STATE, mGame.getState());
        // Saves the current light on color ID
        outState.putInt("lightOnColorId", mLightOnColorId);
    }

    // Handles the long click event for the top left button for activating the cheat
    private boolean onTopLeftButtonLongClick(View view) {
        // Turns all the lights off in the game
        mGame.turnAllLightsOff();
        // Updates the button colors in the grid
        setButtonColors();
        // Displays a message indicating the user won by a cheat
        Toast.makeText(this, "Cheat activated, you win", Toast.LENGTH_SHORT).show();
        // Returns true to indicate the long click was handled
        return true;
    }

    // Starts a new game
    private void startGame() {
        // Resets the game to a new state
        mGame.newGame();
        // Updates the button colors in the grid
        setButtonColors();
    }

    // Method for when the help button is clicked
    public void onHelpClick(View view) {
        // Creates a new Intent to launch HelpActivity
        Intent intent = new Intent(this, HelpActivity.class);
        // Starts the HelpActivity
        startActivity(intent);
    }

    // Method for when a grid button is clicked
    private void onLightButtonClick(View view) {
        // Gets the index of the clicked button on the grid
        int buttonIndex = mLightGrid.indexOfChild(view);
        // Finds the row of the clicked button
        int row = buttonIndex / LightsOutGame.GRID_SIZE;
        // Finds the column of the clicked button
        int col = buttonIndex % LightsOutGame.GRID_SIZE;

        // Selects the light in the game based on the row and column
        mGame.selectLight(row, col);
        // Updates the button colors on the grid
        setButtonColors();

        // Show a congrats message if the game is over
        if (mGame.isGameOver()) {
            // Displays the congrats message to the user
            Toast.makeText(this, R.string.congrats, Toast.LENGTH_SHORT).show();
        }
    }

    // Updates the colors of the buttons on the game grid
    private void setButtonColors() {
        // Loops through each button in the grid
        for (int buttonIndex = 0; buttonIndex < mLightGrid.getChildCount(); buttonIndex++) {
            // Finds the button at the current index
            Button gridButton = (Button) mLightGrid.getChildAt(buttonIndex);

            // Calculates the row of the current button
            int row = buttonIndex / LightsOutGame.GRID_SIZE;
            // Calculates the column of the current button
            int col = buttonIndex % LightsOutGame.GRID_SIZE;

            // If the light is on, set the button background to the on color
            if (mGame.isLightOn(row, col)) {
                gridButton.setBackgroundColor(mLightOnColor);
                // Sets the content description for on state
                gridButton.setContentDescription(mLightOnContent);
            }
            // If the light is off, set the button background to the off color
            else {
                gridButton.setBackgroundColor(mLightOffColor);
                // Sets the content description for off state
                gridButton.setContentDescription(mLightOffContent);
            }
        }
    }

    // Handling what happens if the new game button is clicked
    public void onNewGameClick(View view) {
        // Calls startGame to reset the game
        startGame();
    }

    // Method to launch color changing UI when the color change button is clicked
    public void onChangeColorClick(View view) {
        // Creates a new Intent to launch ColorActivity
        Intent intent = new Intent(this, ColorActivity.class);
        // Adds the current light on color ID to the intent
        intent.putExtra(ColorActivity.EXTRA_COLOR, mLightOnColorId);
        // Launches the ColorActivity
        mColorResultLauncher.launch(intent);
    }

    // Initializes an ActivityResultLauncher to handle the result from ColorActivity
    private final ActivityResultLauncher<Intent> mColorResultLauncher = registerForActivityResult(
            // Registers the result contract for starting an activity and receiving a result
            new ActivityResultContracts.StartActivityForResult(),
            // Defines the callback to handle the result from ColorActivity
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Checks if the result code works
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Retrieves the intent data returned from ColorActivity
                        Intent data = result.getData();
                        // If the data is not null, retrieve the selected color ID
                        if (data != null) {
                            // Gets the light on color ID from the returned intent
                            mLightOnColorId = data.getIntExtra(ColorActivity.EXTRA_COLOR, R.color.yellow);
                            // Updates the light on color using the new color ID
                            mLightOnColor = ContextCompat.getColor(MainActivity.this, mLightOnColorId);
                            // Updates the button colors on the grid
                            setButtonColors();
                        }
                    }
                }
            }
    );
}

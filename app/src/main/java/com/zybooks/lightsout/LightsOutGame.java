// Specifies the package name
package com.zybooks.lightsout;

// Imports the class for generating random numbers
import java.util.Random;

// Defines the LightsOutGame class to handle the game logic
public class LightsOutGame {

    // Method to get the current lights grid as a string
    public String getState() {
        // StringBuilder to create a string for the grid state
        StringBuilder boardString = new StringBuilder();
        // Loops through each row in the grid
        for (int row = 0; row < GRID_SIZE; row++) {
            // Loops through each column
            for (int col = 0; col < GRID_SIZE; col++) {
                // Checks if the light is on or off and adds T or F
                char value = mLightsGrid[row][col] ? 'T' : 'F';
                // Adds the current light state to the string
                boardString.append(value);
            }
        }
        // Returns the string containing the lights of the grid
        return boardString.toString();
    }

    // Method to set the current game state
    public void setState(String gameState) {
        // Variable to keep track of the index of the string
        int index = 0;
        // Loops through each row in the grid
        for (int row = 0; row < GRID_SIZE; row++) {
            // Loops through each column
            for (int col = 0; col < GRID_SIZE; col++) {
                // Sets the cell to be on or off based on the character in the string
                mLightsGrid[row][col] = gameState.charAt(index) == 'T';
                // Moves to the next character in the string by adding 1 to index
                index++;
            }
        }
    }

    // Constant to define the size of the grid
    public static final int GRID_SIZE = 3;

    // Array of booleans to represent the grid
    private final boolean[][] mLightsGrid;

    // Method to initialize the game with an empty grid using the grid size
    public LightsOutGame() {
        // Creates the array using the GRID_SIZE
        mLightsGrid = new boolean[GRID_SIZE][GRID_SIZE];
    }

    // Method to turn off all the lights in the grid
    public void turnAllLightsOff() {
        // Loops through each row in the grid
        for (int row = 0; row < GRID_SIZE; row++) {
            // Loops through each column
            for (int col = 0; col < GRID_SIZE; col++) {
                // Sets the current light to off
                mLightsGrid[row][col] = false;
            }
        }
    }

    // Method to start a new game
    public void newGame() {
        // Creates a new Random object for generating numbers
        Random randomNumGenerator = new Random();
        // Loops through each row in the grid
        for (int row = 0; row < GRID_SIZE; row++) {
            // Loops through each column
            for (int col = 0; col < GRID_SIZE; col++) {
                // Randomly sets the current light to on or off (true/false)
                mLightsGrid[row][col] = randomNumGenerator.nextBoolean();
            }
        }
    }

    // Boolean method to check if a specific light is on or off
    public boolean isLightOn(int row, int col) {
        // Returns the current state of the light with true or false
        return mLightsGrid[row][col];
    }

    // Method to change the state of a certain light, and the ones adjacent
    public void selectLight(int row, int col) {
        // Toggles the state of the selected light
        mLightsGrid[row][col] = !mLightsGrid[row][col];
        // If the light is not in the first row, toggles the light above
        if (row > 0) {
            mLightsGrid[row - 1][col] = !mLightsGrid[row - 1][col];
        }
        // If the light is not in the last row, toggles the light below
        if (row < GRID_SIZE - 1) {
            mLightsGrid[row + 1][col] = !mLightsGrid[row + 1][col];
        }
        // If the light is not in the first column, toggles the light to the left
        if (col > 0) {
            mLightsGrid[row][col - 1] = !mLightsGrid[row][col - 1];
        }
        // If the light is not in the last column, toggles the light to the right
        if (col < GRID_SIZE - 1) {
            mLightsGrid[row][col + 1] = !mLightsGrid[row][col + 1];
        }
    }

    // Method to check if the game is over
    public boolean isGameOver() {
        // Loops through each row in the grid
        for (int row = 0; row < GRID_SIZE; row++) {
            // Loops through each column in the current row
            for (int col = 0; col < GRID_SIZE; col++) {
                // If any light is on, returns false
                if (mLightsGrid[row][col]) {
                    return false;
                }
            }
        }
        // Returns true if all lights are off, indicating the game is over
        return true;
    }
}

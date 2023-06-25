package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

/**
 * This class is the controller for the Difficulty Settings Scene. It loads the existing difficulty
 * for the selected user and update that value after changes are made.
 */
public class WindowDifficultyController {

  @FXML private Label accuracyLabel;
  @FXML private Label wordsLabel;
  @FXML private Label timeLabel;
  @FXML private Label confidenceLabel;
  @FXML private Button accuracyUpButton;
  @FXML private Button accuracyDownButton;
  @FXML private Button wordsUpButton;
  @FXML private Button wordsDownButton;
  @FXML private Button timeUpButton;
  @FXML private Button timeDownButton;
  @FXML private Button confidenceUpButton;
  @FXML private Button confidenceDownButton;

  private int accuracyLevel;
  private int wordsLevel;
  private int timeLevel;
  private int confidenceLevel;

  private User user;
  private String[] levels;
  private Button[] upButtons;
  private Button[] downButtons;
  private SoundPlayer sound;

  /**
   * This initializes the scene, setting up the difficulty settings of the current user.
   *
   * @throws IOException if the file cannot be found
   */
  public void initialize() throws IOException {
    sound = new SoundPlayer();
    // Get the current user
    user = User.getUser(User.currentUser);
    levels = new String[] {"EASY", "MEDIUM", "HARD", "MASTER"};
    // Get the current difficulty settings
    upButtons = new Button[] {accuracyUpButton, wordsUpButton, timeUpButton, confidenceUpButton};
    downButtons =
        new Button[] {accuracyDownButton, wordsDownButton, timeDownButton, confidenceDownButton};
    // Get the different values for the levels of the user
    accuracyLevel = User.getAccuracyLevel();
    wordsLevel = User.getWordsLevel();
    // Get the time level
    timeLevel = user.getTimeLevel();
    confidenceLevel = User.getConfidenceLevel();
    // Set labels and check if buttons needs to be disabled or not
    setLabels();
    checkButtons();
  }

  /**
   * This method set the text label that indicate the selected difficulty level for each category.
   */
  @FXML
  private void setLabels() {
    // Set text depending on current level
    accuracyLabel.setText(levels[accuracyLevel - 1]);
    wordsLabel.setText(levels[wordsLevel - 1]);
    timeLabel.setText(levels[timeLevel - 1]);
    confidenceLabel.setText(levels[confidenceLevel - 1]);
  }

  /**
   * This method ensures that the relevant increase and decrease buttons are on/off depending on
   * whether the level is at its maximum value for a category.
   */
  @FXML
  private void checkButtons() {
    int[] setLevels = new int[] {accuracyLevel, wordsLevel, timeLevel, confidenceLevel};
    for (int i = 0; i < 4; i++) {
      // Disable decrease when level is Easy, otherwise enable
      downButtons[i].setDisable(setLevels[i] <= 1);
      // Disable increase when level is Master, otherwise enable
      upButtons[i].setDisable(setLevels[i] >= 4);
      // Disable increase when Accuracy Level is Hard, otherwise enable
      upButtons[0].setDisable(accuracyLevel >= 3);
    }
  }

  /** This method increase the accuracy level upon pressing the relevant increase button. */
  @FXML
  private void onIncreaseAccuracy() {
    if (accuracyLevel >= 3) {
      return;
    }
    accuracyLevel += 1;
    // Set labels and check if buttons needs to be disabled or not
    setLabels();
    checkButtons();
  }

  /** This method increase the words level upon pressing the relevant increase button. */
  @FXML
  private void onIncreaseWords() {
    if (wordsLevel >= 4) {
      return;
    }
    wordsLevel += 1;
    // Set labels and check if buttons needs to be disabled or not
    setLabels();
    checkButtons();
  }

  /** This method increase the time level upon pressing the relevant increase button. */
  @FXML
  private void onIncreaseTime() {
    if (timeLevel >= 4) {
      return;
    }
    timeLevel += 1;
    // Set labels and check if buttons needs to be disabled or not
    setLabels();
    checkButtons();
  }

  /** This method increase the confidence level upon pressing the relevant increase button. */
  @FXML
  private void onIncreaseConfidence() {
    if (confidenceLevel >= 4) {
      return;
    }
    confidenceLevel += 1;
    // Set labels and check if buttons needs to be disabled or not
    setLabels();
    checkButtons();
  }

  /** This method decrease the accuracy level upon pressing the relevant increase button. */
  @FXML
  private void onDecreaseAccuracy() {
    if (accuracyLevel <= 1) {
      return;
    }
    accuracyLevel -= 1;
    // Set labels and check if buttons needs to be disabled or not
    setLabels();
    checkButtons();
  }

  /** This method decrease the words level upon pressing the relevant increase button. */
  @FXML
  private void onDecreaseWords() {
    if (wordsLevel <= 1) {
      return;
    }
    wordsLevel -= 1;
    // Set labels and check if buttons needs to be disabled or not
    setLabels();
    checkButtons();
  }

  /** This method decrease the time level upon pressing the relevant increase button. */
  @FXML
  private void onDecreaseTime() {
    if (timeLevel <= 1) {
      return;
    }
    timeLevel -= 1;
    // Set labels and check if buttons needs to be disabled or not
    setLabels();
    checkButtons();
  }

  /** This method decrease the confidence level upon pressing the relevant increase button. */
  @FXML
  private void onDecreaseConfidence() {
    if (confidenceLevel <= 1) {
      return;
    }
    confidenceLevel -= 1;
    // Set labels and check if buttons needs to be disabled or not
    setLabels();
    checkButtons();
  }

  /**
   * This method transfer and saves the changes made to the levels for the selected user
   *
   * @param start the start scene
   * @throws IOException if the file cannot be found
   */
  @FXML
  private void onConfirmSettings(MouseEvent start) throws IOException {

    // Saves changes made for the levels
    user.setAccuracyLevel(this.accuracyLevel);
    user.setWordsLevel(this.wordsLevel);
    user.setTimeLevel(this.timeLevel);
    user.setConfidenceLevel(this.confidenceLevel);
    User.saveUser(user);
    // Return to menu
    switchSettings(start);
  }

  /**
   * This method returns the scene to the menu where it was accessed from.
   *
   * @param start the start scene
   */
  private void switchSettings(MouseEvent start) {
    // Get scene that the button event is in
    Rectangle button = (Rectangle) start.getSource();
    Scene scene = button.getScene();
    // Change scene to canvas
    try {
      scene.setRoot(App.loadFxml("settings"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  /** This method plays the sound effect for buttons in the application */
  @FXML
  private void playPop() {
    sound.playPop();
  }
}

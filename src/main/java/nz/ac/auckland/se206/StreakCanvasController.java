package nz.ac.auckland.se206;

import static nz.ac.auckland.se206.User.getLongestStreak;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class StreakCanvasController extends MainGameController {

  @FXML private Canvas canvas;
  @FXML private Label wordLabel;
  @FXML private Label timeValue;
  @FXML private Label startLabel;
  @FXML private TextArea predictionList;
  @FXML private Rectangle startButton, saveButton, returnButton;
  @FXML private ImageView drawButton, eraseButton, clearButton;
  @FXML private Label title;
  private SoundPlayer sound;
  @FXML private Slider thermometer;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {
    sound = new SoundPlayer();
    // Initialises all setup
    initialiseSetup(canvas, saveButton, drawButton, eraseButton, clearButton, timeValue);

    // Set timelines for timer and predict
    timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> decrementTime()));
    timeline.setCycleCount(750 - (timeLevel * 150));
    predictTimeline =
        new Timeline(
            new KeyFrame(
                Duration.millis(100),
                e -> {
                  try {
                    predict();
                  } catch (TranslateException e1) {
                    e1.printStackTrace();
                  }
                }));
    predictTimeline.setCycleCount(750 - (timeLevel * 150));
  }

  /** This method starts the timer operation for counter and prediction */
  private void startTimer() {

    // Set initial value
    timer = 75 - (timeLevel * 15);
    timeline.play();
    predictTimeline.play();
  }

  /** This method decrements the time and notify the user when they have won and lost */
  private void decrementTime() {

    // Don't decrement time if user have won the game
    if (win == 1) {
      try {
        checkBestTime();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      newWord();
      return;
    }
    timer -= 0.1;
    // When user runs out of time they are notified that they have lost
    if (timer <= 0.5) {
      timeValue.setText("0");
      win = 2;
      completeGame();
      return;
    }

    // Adjust double and display changing time
    timeValue.setText(String.format("%.0f%n", timer));
  }

  private void newWord() {
    // Clear the canvas
    onClear();
    // Select a new word
    selectWord(wordLabel);
    // Reset win
    win = 0;
    sessionStreak++;
  }

  /**
   * This method is called when the user has won the game. It then checks to see if this is the best
   * time from the user. If it is, it is saved to the user profile.
   *
   * @throws IOException If the user profile cannot be found on the file system
   */
  private void checkBestTime() throws IOException {

    // Get the current time
    double currentTime = 75 - (timeLevel * 15) - timer;
    // Get the best time for the current level
    double bestTime = User.getBestTime();
    // If the current time is better than the best time, update the best time
    if (currentTime < bestTime) {
      user.setBestTime(currentTime);
      // Save the user profile
      User.saveUser(user);
    }
  }

  /**
   * This method sets the parameters on the case when the game have completed. Text to Speech is
   * also implemented here to notify the player if they have won or lost
   */
  private void completeGame() {

    resetCanvas(canvas, startButton, startLabel, saveButton, eraseButton, drawButton, clearButton);
    // Text to speech upon game over
    if (win == 2) {
      speak("Your win streak is " + sessionStreak);
      title.setText("Quick Draw: Streak (" + sessionStreak + ")");
      // Adding streak stats
      try {
        if (sessionStreak > getLongestStreak()) {
          user.setLongestStreak(sessionStreak);
          User.saveUser(user);
        }
        saveStats(user);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      // Reset streak
      sessionStreak = 0;
    }
    // Resetting the game
    restart = true;
    returnButton.setDisable(false);
  }

  /**
   * This method saves the user's stats to the user's profile
   *
   * @param user The user whose stats are being saved
   * @throws IOException If the user's profile cannot be found
   */
  private void saveStats(User user) throws IOException {
    // Gets the stats and saves it to the profile JSON file
    User.getWordHistory().clear();
    User.getWordHistory().addAll(WordSelector.getWordHistory());
    User.saveUser(user);
  }

  /**
   * This method activates when the start button is pressed on the scene. It alters parameters to
   * allow the user to start drawing, or if the game is completed, it allows the user to start a new
   * game
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  @FXML
  private void onStart() throws ModelException, IOException {

    // Start a new game after completion
    if (restart) {
      thermometer.setValue(0);
      wordLabel.setText("");
      returnButton.setDisable(false);
      timeline.stop();
      predictTimeline.stop();
      predictionList.setText("");
      // Reset the canvas
      onClear();
      initialize();
      startLabel.setText("Start");
      timeValue.setText(String.valueOf(75 - (timeLevel * 15)));
      return;
    }

    this.selectWord(wordLabel);

    // Enable drawings when user is ready
    returnButton.setDisable(true);
    canvas.setDisable(false);
    startButton.setDisable(true);
    // Start the timer
    timeline.stop();
    predictTimeline.stop();
    setupCanvas(predictionList, drawButton, eraseButton, clearButton);
    startTimer();
  }

  /**
   * This method activates when the erase/draw button is pressed. It allows the user to switch
   * between erase and draw during the game.
   */
  @FXML
  private void onErase() {
    super.onErase(drawButton, eraseButton, canvas);
  }

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    // Fill canvas with white
    graphic = canvas.getGraphicsContext2D();
    graphic.setFill(Color.WHITE);
    graphic.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    graphic.setFill(Color.BLACK);
    predictionList.setText("");
    active = false;
  }

  /**
   * This method call on another thread to operate the prediction function. It gets the top 10
   * results every 100 milliseconds and prints out those results every second. It does not activate
   * until the user starts drawing. Upon victory the result is also printed.
   *
   * @throws TranslateException If there is an error in reading the input/output of the DL model.
   */
  private void predict() throws TranslateException {
    super.predict(active, canvas, model, predictionList, thermometer, accuracyLevel);
  }

  /**
   * This method creates a new thread for the text to speech to operate, so it doesn't freeze the
   * program
   *
   * @param line The line that will be spoken
   */
  private void speak(String line) {

    Task<Void> speakingTask =
        new Task<>() {
          @Override
          protected Void call() {
            // Speak the input line
            speech.speak(line);
            return null;
          }
        };
    // Use separate thread for voice, so it doesn't freeze GUI
    Thread predictionThread = new Thread(speakingTask);
    predictionThread.start();
  }

  /**
   * Save the current snapshot on a bitmap file.
   *
   * @throws IOException If the image cannot be saved.
   */
  @FXML
  private void onSave() throws IOException {
    super.onSave(startButton, saveButton, returnButton, canvas);
  }

  /**
   * This method activates when the menu button is pressed. The button is only available once the
   * game finishes and takes the user back to the start Box.
   *
   * @param start The start button
   */
  @FXML
  private void onSwitchModeSelect(MouseEvent start) throws IOException {
    super.onSwitchModeSelect(start, sessionStreak);
  }
  /** This method plays the sound effect for buttons in the application */
  @FXML
  private void playPop() {
    sound.playPop();
  }
  /** This method plays the sound effect for the start buttons in the application */
  @FXML
  private void playStart() {
    sound.playStart();
  }
}

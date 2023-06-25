package nz.ac.auckland.se206;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
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
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.dictionary.DictionarySearch;

/**
 * This is the controller of the canvas. You are free to modify this class and the corresponding
 * FXML file as you see fit. For example, you might no longer need the "Predict" button because the
 * DL model should be automatically queried in the background every second.
 *
 * <p>!! IMPORTANT !!
 *
 * <p>Although we added the scale of the image, you need to be careful when changing the size of the
 * drawable canvas and the brush size. If you make the brush too big or too small with respect to
 * the canvas size, the ML model will not work correctly. So be careful. If you make some changes in
 * the canvas and brush sizes, make sure that the prediction works fine.
 */
public class HiddenCanvasController extends MainGameController {

  @FXML private Canvas canvas;
  @FXML private Label wordLabel;
  @FXML private Label timeValue;
  @FXML private Label winLabel;
  @FXML private TextArea predictionList;
  @FXML private Rectangle startButton;
  @FXML private Label startLabel;
  @FXML private Rectangle saveButton;
  @FXML private ImageView eraseButton;
  @FXML private ImageView drawButton;
  @FXML private ImageView clearButton;
  @FXML private Rectangle menuButton;
  @FXML private Rectangle hintButton;
  @FXML private Label definitionLabel;
  @FXML private Label hintLabel;
  @FXML private Slider thermometer;
  private List<String> definitions;
  private Timeline timeline;
  private Timeline predictTimeline;
  private SoundPlayer sound;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {
    sound = new SoundPlayer();
    initialiseSetup(canvas, saveButton, drawButton, eraseButton, clearButton, timeValue);
    // This chunk sets the initial conditions for all labels and things you can interact with
    saveButton.setOpacity(0.3);
    hintButton.setDisable(true);
    hintButton.setOpacity(0.3);

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

  /** This method selects the word and calls text to speech to say the word */
  private void selectWord() throws IOException {
    try {
      // Initiate and set up wordSelector
      WordSelector wordSelector = new WordSelector(User.getWordsLevel());
      wordSelector.getCurrentWordList().removeAll(User.getWordHistory());
      // Get a random word
      String randomWord;
      // Get a random word that is valid with a definition, otherwise keep searching
      do {
        randomWord = wordSelector.getRandomWord();
        currentWord = randomWord.replaceAll(" ", "_");
        definitions = DictionarySearch.searchDefinition(currentWord);
      } while (definitions == null);
      definitionLabel.setText(definitions.get(0));
      // Record the selected word in user word history
      user.setWordHistory(currentWord);
      User.saveUser(user);
      // Speak the definition
      speak("Please draw " + definitions.get(0));
    } catch (IOException | URISyntaxException | CsvException e) {
      e.printStackTrace();
    }
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
      winLabel.setText("WIN");
      completeGame();
      return;
    }
    timer -= 0.1;
    // When user runs out of time they are notified that they have lost
    if (timer <= 0.5) {
      timeValue.setText("0");
      win = 2;
      winLabel.setText("LOST");
      completeGame();
      return;
    }

    // Adjust double and display changing time
    timeValue.setText(String.format("%.0f%n", timer));
  }

  /**
   * This method sets the parameters on the case when the game have completed. Text to Speech is
   * also implemented here to notify the player if they have won or lost
   */
  private void completeGame() {

    // Set parameters to appropriate values upon completion of game
    canvas.setDisable(true);
    startButton.setDisable(false);
    startButton.setOpacity(0.74);
    timeline.stop();
    predictTimeline.stop();
    // Change the START button to PLAY AGAIN instead
    startLabel.setText("Restart");
    // Make save button visible and active so user can save their image
    saveButton.setDisable(false);
    saveButton.setOpacity(0.74);
    eraseButton.setDisable(true);
    eraseButton.setOpacity(0.3);
    clearButton.setDisable(true);
    clearButton.setOpacity(0.3);
    drawButton.setDisable(true);
    drawButton.setOpacity(0.3);

    speakResults();
    // Resetting the game
    restart = true;
    menuButton.setDisable(false);
    menuButton.setOpacity(0.74);

    hintButton.setDisable(true);
    hintButton.setOpacity(0.3);
    wordLabel.setText(currentWord);
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
      wordLabel.setText("...");
      menuButton.setDisable(false);
      menuButton.setOpacity(0.74);
      timeline.stop();
      predictTimeline.stop();
      predictionList.setText("");
      winLabel.setText("");
      definitionLabel.setText("Definition:");
      hintLabel.setText("Hint:");
      // Reset the canvas
      onClear();
      initialize();
      startLabel.setText("START");
      timeValue.setText(String.valueOf(75 - (timeLevel * 15)));
      return;
    }

    this.selectWord();

    // Enable drawings when user is ready
    menuButton.setDisable(true);
    menuButton.setOpacity(0.3);
    canvas.setDisable(false);
    startButton.setDisable(true);
    startButton.setOpacity(0.3);
    // Start the timer
    timeline.stop();
    predictTimeline.stop();
    winLabel.setText("");
    // Reset the canvas
    setupCanvas(predictionList, drawButton, eraseButton, clearButton);
    hintButton.setDisable(false);
    hintButton.setOpacity(0.74);
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
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    predictionList.clear();
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
    super.onSave(startButton, saveButton, menuButton, canvas);
  }

  /**
   * This method activates when the menu button is pressed. The button is only available once the
   * game finishes and takes the user back to the game mode selection.
   *
   * @param start The start button
   */
  @FXML
  private void onSwitchModeSelect(MouseEvent start) throws IOException {
    super.onSwitchModeSelect(start, sessionStreak);
  }

  /**
   * This method is called when the user press the HINT button. The method prints out additional
   * definitions, otherwise it will state no hint available.
   */
  @FXML
  private void onHint() {
    if (definitions.size() == 1) {
      hintButton.setDisable(true);
      hintButton.setOpacity(0.3);
      hintLabel.setText("No more definitions available...");
      return;
    }
    // Prints out hint and disable the hint button
    hintLabel.setText(definitions.get(1));
    hintButton.setDisable(true);
    hintButton.setOpacity(0.3);
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
  /** This method plays the sound effect for the hint button in the mode */
  @FXML
  private void playHint() {
    sound.playHint();
  }
}

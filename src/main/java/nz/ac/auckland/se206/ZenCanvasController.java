package nz.ac.auckland.se206;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import ai.djl.translate.TranslateException;
import com.opencsv.exceptions.CsvException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class ZenCanvasController extends MainGameController {

  private static List<String> colourList;

  private static void generateColourList() {
    // Neutral Tones
    colourList.add("#f5f5f5");
    colourList.add("#737373");
    colourList.add("#262626");
    // 400 Tones
    // Orange and yellow
    colourList.add("#f87171");
    colourList.add("#fb923c");
    colourList.add("#fbbf24");
    colourList.add("#facc15");
    colourList.add("#a3e635");
    // Green and blue
    colourList.add("#4ade80");
    colourList.add("#34d399");
    colourList.add("#2dd4bf");
    colourList.add("#22d3ee");
    colourList.add("#38bdf8");
    colourList.add("#60a5fa");
    // Purple and pink
    colourList.add("#818cf8");
    colourList.add("#a78bfa");
    colourList.add("#c084fc");
    colourList.add("#e879f9");
    colourList.add("#f472b6");
    colourList.add("#fb7185");
    // Neutral Tones
    colourList.add("#d4d4d4");
    colourList.add("#404040");
    colourList.add("#171717");
    // 600 Tones
    // Orange and yellow
    colourList.add("#dc2626");
    colourList.add("#ea580c");
    colourList.add("#d97706");
    colourList.add("#ca8a04");
    // Green and blue
    colourList.add("#65a30d");
    colourList.add("#16a34a");
    colourList.add("#059669");
    colourList.add("#0d9488");
    colourList.add("#0891b2");
    // Purple and pink
    colourList.add("#0284c7");
    colourList.add("#2563eb");
    colourList.add("#4f46e5");
    colourList.add("#7c3aed");
    colourList.add("#9333ea");
    colourList.add("#c026d3");
    colourList.add("#db2777");
    colourList.add("#e11d48");
  }

  @FXML private Canvas canvas;
  @FXML private Label wordLabel;
  @FXML private Label startLabel;
  @FXML private Rectangle startButton;
  @FXML private Rectangle saveButton;
  @FXML private Rectangle returnButton;
  @FXML private Rectangle currentColour;
  @FXML private Rectangle currentThickness;
  @FXML private ImageView drawButton;
  @FXML private ImageView eraseButton;
  @FXML private ImageView clearButton;
  @FXML private Slider thicknessSlider;
  @FXML private TextArea predictionList;
  private GraphicsContext graphic;
  private DoodlePrediction model;
  private Timeline timeline;
  private Timeline predictTimeline;
  private BufferedImage image;
  private Boolean restart;
  private Boolean erase;
  private Boolean active;
  private int count;
  private TextToSpeech speech;
  private double currentX;
  private double currentY;
  private Color currentColourValue = Color.BLACK;
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
    // Loads all the colours
    colourList = new ArrayList<>();
    generateColourList();
    // Load the current user

    // This chunk sets the initial conditions for all labels and things you can interact with
    thicknessSlider.setDisable(true);
    graphic = canvas.getGraphicsContext2D();
    speech = new TextToSpeech();
    restart = false;
    erase = false;
    active = false;
    // This chunk sets the initial conditions for all labels and things you can interact with
    saveButton.setDisable(true);
    drawButton.setOpacity(0.3);
    drawButton.setDisable(true);
    eraseButton.setOpacity(0.3);
    eraseButton.setDisable(true);
    clearButton.setOpacity(0.3);
    clearButton.setDisable(true);

    // Fill canvas with white
    graphic = canvas.getGraphicsContext2D();
    graphic.setFill(Color.WHITE);
    graphic.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    graphic.setStroke(currentColourValue);
    // save coordinates when mouse is pressed on the canvas
    canvas.setOnMousePressed(
        e -> {
          currentX = e.getX();
          currentY = e.getY();
        });
    // This chunk sets the brush properties
    canvas.setOnMouseDragged(
        e -> {
          active = true;
          // Brush size (you can change this, it should not be too small or too large).
          final double size = thicknessSlider.getValue() / 5;
          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;
          // This is the colour of the brush.
          graphic.setStroke(currentColourValue);
          graphic.setLineWidth(size);

          graphic.strokeLine(currentX, currentY, x, y);

          // update the coordinates
          currentX = x;
          currentY = y;
        });
    model = new DoodlePrediction();

    canvas.setDisable(true);

    // Set timelines for timer and predict
    timeline =
        new Timeline(
            new KeyFrame(
                Duration.millis(100),
                e -> {
                  try {
                    checkThickness();
                    predict();
                  } catch (TranslateException modelException) {
                    modelException.printStackTrace();
                  }
                }));
    timeline.setCycleCount(999999999);
    predictTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {}));
  }

  /**
   * This method is called constantly, and changes the thickness of the brush based on the slider.
   */
  private void checkThickness() {
    // This chunk sets the brush properties
    if (thicknessSlider.getValue() == 0) {
      changeThickness(0.1);
    } else {
      // Brush size (you can change this, it should not be too small or too large).
      changeThickness(thicknessSlider.getValue() / 5);
    }
    currentThickness.scaleYProperty().set(thicknessSlider.getValue() / 100);
  }

  /**
   * This method is called constantly, and changes the thickness of the brush based on the slider.
   *
   * @param thickness The thickness of the brush.
   */
  private void changeThickness(double thickness) {
    // save coordinates when mouse is pressed on the canvas
    canvas.setOnMousePressed(
        e -> {
          currentX = e.getX();
          currentY = e.getY();
        });
    // This chunk sets the brush properties
    canvas.setOnMouseDragged(
        e -> {
          final double x = e.getX() - thickness / 2;
          final double y = e.getY() - thickness / 2;
          graphic.setLineWidth(thickness);
          graphic.setStroke(currentColourValue);
          graphic.strokeLine(currentX, currentY, x, y);

          // update the coordinates
          currentX = x;
          currentY = y;
        });
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
    // If the player wants to stop the game
    if (active) {
      thicknessSlider.setDisable(true);
      active = false;
      timeline.stop();
      predictTimeline.stop();
      startLabel.setText("Start");
      saveButton.setDisable(false);
      canvas.setDisable(true);
      return;
    }

    // Start a new game after completion
    if (restart) {
      wordLabel.setText("");
      returnButton.setDisable(false);
      timeline.stop();
      predictTimeline.stop();
      // Reset the canvas
      onClear();
      initialize();
      startLabel.setText("Start");
    }

    this.selectWord();

    thicknessSlider.setDisable(false);
    // Enable drawings when user is ready
    canvas.setDisable(false);
    // Start the timer
    timeline.stop();
    predictTimeline.stop();
    // Reset the canvas
    setupCanvas(predictionList, drawButton, eraseButton, clearButton);
    startTimer();
  }

  /** This method starts the timer operation for counter and prediction */
  private void startTimer() {
    active = true;
    restart = true;
    startLabel.setText("Stop");
    timeline.play();
    predictTimeline.play();
  }

  /** This method selects the word and calls text to speech to say the word */
  private void selectWord() {
    try {
      User user = User.getUser(User.currentUser);
      // Initiate and set up wordSelector
      WordSelector wordSelector = new WordSelector(User.getWordsLevel());
      wordSelector.getCurrentWordList().removeAll(User.getWordHistory());
      // Get a random word
      String randomWord = wordSelector.getRandomWord();
      wordLabel.setText(randomWord);
      currentWord = randomWord.replaceAll(" ", "_");
      // Record the selected word in user word history
      System.out.println(user);
      user.setWordHistory(currentWord);
      User.saveUser(user);
      // Speak the random word
      this.speak("Please draw " + randomWord);
    } catch (IOException | URISyntaxException | CsvException e) {
      e.printStackTrace();
    }
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
   * This method activates when the erase/draw button is pressed. It allows the user to switch
   * between erase and draw during the game.
   */
  @FXML
  private void onErase() {

    if (erase) {
      // For drawing brush
      drawButton.setOpacity(1);
      drawButton.setDisable(true);
      eraseButton.setOpacity(0.3);
      eraseButton.setDisable(false);
      currentColourValue = (Color) currentColour.getFill();
    } else {
      // For eraser tool
      drawButton.setOpacity(0.3);
      drawButton.setDisable(false);
      eraseButton.setOpacity(1);
      eraseButton.setDisable(true);
      currentColourValue = Color.WHITE;
    }
    erase = !erase;
  }

  /** This method sets the colour to be 0. */
  @FXML
  private void onClickColour0() {
    setColour(colourList.get(0));
  }

  /** This method sets the colour to be 1. */
  @FXML
  private void onClickColour1() {
    setColour(colourList.get(1));
  }

  /** This method sets the colour to be 2. */
  @FXML
  private void onClickColour2() {
    setColour(colourList.get(2));
  }

  /** This method sets the colour to be 3. */
  @FXML
  private void onClickColour3() {
    setColour(colourList.get(3));
  }

  /** This method sets the colour to be 4. */
  @FXML
  private void onClickColour4() {
    setColour(colourList.get(4));
  }

  /** This method sets the colour to be 5. */
  @FXML
  private void onClickColour5() {
    setColour(colourList.get(5));
  }

  /** This method sets the colour to be 6. */
  @FXML
  private void onClickColour6() {
    setColour(colourList.get(6));
  }

  /** This method sets the colour to be 7. */
  @FXML
  private void onClickColour7() {
    setColour(colourList.get(7));
  }

  /** This method sets the colour to be 8. */
  @FXML
  private void onClickColour8() {
    setColour(colourList.get(8));
  }

  /** This method sets the colour to be 9. */
  @FXML
  private void onClickColour9() {
    setColour(colourList.get(9));
  }

  /** This method sets the colour to be 10. */
  @FXML
  private void onClickColour10() {
    setColour(colourList.get(10));
  }

  /** This method sets the colour to be 11. */
  @FXML
  private void onClickColour11() {
    setColour(colourList.get(11));
  }

  /** This method sets the colour to be 12. */
  @FXML
  private void onClickColour12() {
    setColour(colourList.get(12));
  }

  /** This method sets the colour to be 13. */
  @FXML
  private void onClickColour13() {
    setColour(colourList.get(13));
  }

  /** This method sets the colour to be 14. */
  @FXML
  private void onClickColour14() {
    setColour(colourList.get(14));
  }

  /** This method sets the colour to be 15. */
  @FXML
  private void onClickColour15() {
    setColour(colourList.get(15));
  }

  /** This method sets the colour to be 16. */
  @FXML
  private void onClickColour16() {
    setColour(colourList.get(16));
  }

  /** This method sets the colour to be 17. */
  @FXML
  private void onClickColour17() {
    setColour(colourList.get(17));
  }

  /** This method sets the colour to be 18. */
  @FXML
  private void onClickColour18() {
    setColour(colourList.get(18));
  }

  /** This method sets the colour to be 19. */
  @FXML
  private void onClickColour19() {
    setColour(colourList.get(19));
  }

  /** This method sets the colour to be 20. */
  @FXML
  private void onClickColour20() {
    setColour(colourList.get(20));
  }

  /** This method sets the colour to be 21. */
  @FXML
  private void onClickColour21() {
    setColour(colourList.get(21));
  }

  /** This method sets the colour to be 22. */
  @FXML
  private void onClickColour22() {
    setColour(colourList.get(22));
  }

  /** This method sets the colour to be 23. */
  @FXML
  private void onClickColour23() {
    setColour(colourList.get(23));
  }

  /** This method sets the colour to be 24. */
  @FXML
  private void onClickColour24() {
    setColour(colourList.get(24));
  }

  /** This method sets the colour to be 25. */
  @FXML
  private void onClickColour25() {
    setColour(colourList.get(25));
  }

  /** This method sets the colour to be 26. */
  @FXML
  private void onClickColour26() {
    setColour(colourList.get(26));
  }

  /** This method sets the colour to be 27. */
  @FXML
  private void onClickColour27() {
    setColour(colourList.get(27));
  }

  /** This method sets the colour to be 28. */
  @FXML
  private void onClickColour28() {
    setColour(colourList.get(28));
  }

  /** This method sets the colour to be 29. */
  @FXML
  private void onClickColour29() {
    setColour(colourList.get(29));
  }

  /** This method sets the colour to be 30. */
  @FXML
  private void onClickColour30() {
    setColour(colourList.get(30));
  }

  /** This method sets the colour to be 31. */
  @FXML
  private void onClickColour31() {
    setColour(colourList.get(31));
  }

  /** This method sets the colour to be 32. */
  @FXML
  private void onClickColour32() {
    setColour(colourList.get(32));
  }

  /** This method sets the colour to be 33. */
  @FXML
  private void onClickColour33() {
    setColour(colourList.get(33));
  }

  /** This method sets the colour to be 34. */
  @FXML
  private void onClickColour34() {
    setColour(colourList.get(34));
  }

  /** This method sets the colour to be 35. */
  @FXML
  private void onClickColour35() {
    setColour(colourList.get(35));
  }

  /** This method sets the colour to be 36. */
  @FXML
  private void onClickColour36() {
    setColour(colourList.get(36));
  }

  /** This method sets the colour to be 37. */
  @FXML
  private void onClickColour37() {
    setColour(colourList.get(37));
  }

  /** This method sets the colour to be 38. */
  @FXML
  private void onClickColour38() {
    setColour(colourList.get(38));
  }

  /** This method sets the colour to be 39. */
  @FXML
  private void onClickColour39() {
    setColour(colourList.get(39));
  }

  /**
   * This method sets the colour to be the one selected by the user.
   *
   * @param hexCode The hex code of the colour selected by the user.
   */
  private void setColour(String hexCode) {
    // Set the colour of the colour picker to the colour selected by the user.
    currentColourValue = Color.web(hexCode);
    currentColour.setFill(Color.web(hexCode));
  }

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    // Fill canvas with white
    graphic = canvas.getGraphicsContext2D();
    graphic.setFill(Color.WHITE);
    graphic.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    graphic.setStroke(currentColourValue);
  }

  /**
   * This method activates when the menu button is pressed. The button is only available once the
   * game finishes and takes the user back to the start Box.
   *
   * @param start The start button
   */
  @FXML
  private void onSwitchStart(MouseEvent start) throws IOException {
    System.out.println("Switching to start screen");
    // Get scene that the button event is in
    Parent root =
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/gameMode.fxml")));
    Stage stage = (Stage) ((Node) start.getSource()).getScene().getWindow();
    // Change scene to zen mode
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
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
   * This method call on another thread to operate the prediction function. It gets the top 10
   * results every 100 milliseconds and prints out those results every second. It does not activate
   * until the user starts drawing. Upon victory the result is also printed.
   *
   * @throws TranslateException If there is an error in reading the input/output of the DL model.
   */
  private void predict() throws TranslateException {

    // Initiates new task object
    Task<Void> backgroundTask =
        new Task<>() {

          @Override
          protected Void call() throws Exception {

            count++;

            if (!active) {
              // Do not initiate machine learning if player have not drawn anything
              return null;
            }
            Platform.runLater(() -> image = getCurrentSnapshot(canvas));
            // Get list of predictions based on image
            List<Classification> predictionResults = model.getPredictions(image, 10);
            StringBuilder predictions = new StringBuilder();
            for (Classification predictionResult : predictionResults) {
              predictions.append(String.format("%s%n", predictionResult.getClassName()));
            }
            // Format _ out of printing strings
            final String tempPredictions = predictions.toString().replaceAll("_", " ");
            Platform.runLater(
                () -> {
                  // Print every second using count variable
                  if (count >= 10) {
                    predictionList.setText(tempPredictions);
                    count = 0;
                  }
                });
            // Prints out result upon victory
            return null;
          }
        };
    // Run the separate thread
    Thread predictionThread = new Thread(backgroundTask);
    predictionThread.start();
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

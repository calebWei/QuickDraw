package nz.ac.auckland.se206;

import static nz.ac.auckland.se206.User.getConfidenceLevel;
import static nz.ac.auckland.se206.User.getLongestStreak;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import com.opencsv.exceptions.CsvException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class MainGameController {

  protected int count;
  protected BufferedImage image;
  protected int win;
  protected String currentWord;
  protected double currentX;
  protected double currentY;
  protected GraphicsContext graphic;
  protected boolean erase;
  protected TextToSpeech speech;
  protected User user;
  protected int timeLevel;
  protected double timer;
  protected int sessionStreak = 0;
  protected Timeline timeline;
  protected Timeline predictTimeline;
  protected boolean restart;
  protected boolean active;
  protected DoodlePrediction model;
  protected int accuracyLevel;
  protected double thermometerReading;

  /**
   * This method activates when the menu button is pressed. The button is only available once the
   * game finishes and takes the user back to the start Box.
   *
   * @param start The start button
   */
  protected void onSwitchModeSelect(MouseEvent start, int sessionStreak) throws IOException {
    // Record streaks achievements
    User user = User.getUser(User.currentUser);
    if (sessionStreak > getLongestStreak()) {
      user.setLongestStreak((sessionStreak));
    }
    if (sessionStreak >= 15) {
      user.incrementFifteenGameStreaks();
    } else if (sessionStreak >= 10) {
      user.incrementTenGameStreaks();
    } else if (sessionStreak >= 5) {
      user.incrementFiveGameStreaks();
    }
    saveStats();

    System.out.println("Switch to game mode selection");
    // Load the game mode selection
    Parent root =
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/gameMode.fxml")));
    Stage stage = (Stage) ((Node) start.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /** This method selects the word and calls text to speech to say the word */
  protected void selectWord(Label wordLabel) {
    try {
      // Initiate and set up wordSelector
      WordSelector wordSelector = new WordSelector(User.getWordsLevel());
      wordSelector.getCurrentWordList().removeAll(User.getWordHistory());
      // Get a random word
      String randomWord = wordSelector.getRandomWord();
      wordLabel.setText(randomWord);
      currentWord = randomWord.replaceAll(" ", "_");
      // Record the selected word in user word history
      user.setWordHistory(currentWord);
      User.saveUser(user);
      // Speak the random word
      speak("Please draw " + randomWord);
    } catch (IOException | URISyntaxException | CsvException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method saves the user's stats to the user's profile
   *
   * @throws IOException If the user's profile cannot be found
   */
  private void saveStats() throws IOException {
    // Gets the stats and saves it to the profile JSON file
    User.getWordHistory().clear();
    User.getWordHistory().addAll(WordSelector.getWordHistory());
    User.saveUser(User.getUser(User.currentUser));
  }

  /**
   * Save the current snapshot on a bitmap file.
   *
   * @throws IOException If the image cannot be saved.
   */
  protected void onSave(
      Rectangle startButton, Rectangle saveButton, Rectangle returnButton, Canvas canvas)
      throws IOException {

    startButton.setDisable(true);
    saveButton.setDisable(true);
    returnButton.setDisable(true);
    FileChooser fileChooser = new FileChooser();
    // Set directory as home for any user
    fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    File file = fileChooser.showSaveDialog(new Stage());
    // Allows user to exit saving process
    if (file == null) {
      startButton.setDisable(false);
      saveButton.setDisable(false);
      returnButton.setDisable(false);
      return;
    }
    String filePath = file.getAbsolutePath();
    // Set the extension to .bmp if not already, then save as .bmp
    if (!filePath.endsWith(".png")) {
      file = new File(filePath + ".png");
      ImageIO.write(getCurrentSnapshot(canvas), "png", file);
    }
    startButton.setDisable(false);
    saveButton.setDisable(false);
    returnButton.setDisable(false);
  }

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  protected BufferedImage getCurrentSnapshot(Canvas canvas) {

    final Image snapshot = canvas.snapshot(null, null);
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);
    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
    final Graphics2D graphics = imageBinary.createGraphics();
    graphics.drawImage(image, 0, 0, null);
    // To release memory we dispose.
    graphics.dispose();
    return imageBinary;
  }

  /**
   * This method checks the required probability of the predicted words depending on the set
   * Confidence Level
   *
   * @param classifications List of predictions based on drawing
   * @return True if the win condition for probability is satisfied
   */
  protected boolean checkProbability(
      List<Classification> classifications, int currentLevel, int confidenceLevel) {
    // Iterates only for the winnable options
    for (int i = 0; i < (-currentLevel) + 4; i++) {
      double probability = classifications.get(i).getProbability() * 100;
      // More than 1% for EASY
      if (confidenceLevel == 1) {
        if (probability >= 1) {
          return true;
        }
      }
      // More than 10% for Medium
      if (confidenceLevel == 2) {
        if (probability >= 10) {
          return true;
        }
      }
      // More than 25% for Hard
      if (confidenceLevel == 3) {
        if (probability >= 25) {
          return true;
        }
      }
      // More than 50% for Master
      if (confidenceLevel == 4) {
        if (probability >= 50) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * This method checks if the image is within the top 1, 2 or 3 predictions of the ML depending on
   * the level
   *
   * @param classifications List of predictions based on drawing
   * @return True if won, False if Lost
   */
  protected boolean isWin(List<Classification> classifications, int currentLevel)
      throws IOException {

    for (int i = 0; i < (-currentLevel) + 4; i++) {
      // Check if user has win only for top 3 options
      if (classifications.get(i).getClassName().equals(currentWord)) {
        if (checkProbability(classifications, currentLevel, getConfidenceLevel())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * This method call on another thread to operate the prediction function. It gets the top 10
   * results every 100 milliseconds and prints out those results every second. It does not activate
   * until the user starts drawing. Upon victory the result is also printed.
   */
  protected void predict(
      boolean active,
      Canvas canvas,
      DoodlePrediction model,
      TextArea predictionList,
      Slider thermometer,
      int accuracyLevel) {

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
            List<Classification> predictionResults = model.getPredictions(image, 20);
            StringBuilder predictions = new StringBuilder();
            for (int i = 0; i < 10; i++) {
              if (i == 0 || i == 1 || i == 2) {
                predictions.append(
                    String.format(
                        "%s %.1f%%%n",
                        predictionResults.get(i).getClassName(),
                        predictionResults.get(i).getProbability() * 100));
              } else {
                predictions.append(String.format("%s%n", predictionResults.get(i).getClassName()));
              }
              // Get thermometer reading if the current word is in the top 20 predictions,
              // thermometer max out at 40%
              if (predictionResults.get(i).getClassName().equals(currentWord)) {
                thermometerReading = predictionResults.get(i).getProbability();
              }
            }
            // Set the thermometer value
            switch (User.getConfidenceLevel()) {
              case 1:
                thermometer.setValue(thermometerReading * 10000);
                break;
              case 2:
                thermometer.setValue(thermometerReading * 1000);
                break;
              case 3:
                thermometer.setValue(thermometerReading * 400);
                break;
              case 4:
                thermometer.setValue(thermometerReading * 200);
                break;
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
            if (isWin(predictionResults, accuracyLevel)) {
              Platform.runLater(() -> predictionList.setText(tempPredictions));
              win = 1;
            }
            return null;
          }
        };
    // Run the separate thread
    Thread predictionThread = new Thread(backgroundTask);
    predictionThread.start();
  }

  /**
   * This method activates when the erase/draw button is pressed. It allows the user to switch
   * between erase and draw during the game.
   */
  protected void onErase(ImageView drawButton, ImageView eraseButton, Canvas canvas) {

    if (erase) {
      // For drawing brush
      drawButton.setOpacity(1);
      drawButton.setDisable(true);
      eraseButton.setOpacity(0.3);
      eraseButton.setDisable(false);
      // save coordinates when mouse is pressed on the canvas
      canvas.setOnMousePressed(
          e -> {
            currentX = e.getX();
            currentY = e.getY();
          });
      canvas.setOnMouseDragged(
          e -> {
            active = true;
            // Brush size.
            final double size = 5.0;
            final double x = e.getX() - size / 2;
            final double y = e.getY() - size / 2;
            // This is the colour of the brush.
            graphic.setFill(Color.BLACK);
            graphic.setLineWidth(size);
            if (win == 0) {
              graphic.strokeLine(currentX, currentY, x, y);

              // update the coordinates
              currentX = x;
              currentY = y;
            }
          });
    } else {
      // For eraser tool
      drawButton.setOpacity(0.3);
      drawButton.setDisable(false);
      eraseButton.setOpacity(1);
      eraseButton.setDisable(true);
      // save coordinates when mouse is pressed on the canvas
      canvas.setOnMousePressed(
          e -> {
            currentX = e.getX();
            currentY = e.getY();
          });
      canvas.setOnMouseDragged(
          e -> {
            // Eraser size.
            final double size = 15.0;
            final double x = e.getX() - size / 2;
            final double y = e.getY() - size / 2;
            // Acts like eraser by painting white.
            graphic.setFill(Color.WHITE);
            //            graphic.setLineWidth(size);
            if (win == 0) {
              graphic.fillOval(x, y, size, size);
            }
          });
    }
    erase = !erase;
  }

  /** This method speaks the results of the game. */
  protected void speakResults() {
    // Text to speech upon game over
    if (win == 1) {
      speak("You Win!");
      // Update stats/achievements
      try {
        // Adding win stats
        user.setGamesWon(User.getGamesWon() + 1);
        saveStats();
        // Check time taken to win and update achievement stats
        User user = User.getUser(User.currentUser);
        double currentTime = 75 - (timeLevel * 15) - timer;
        if (currentTime <= 5) {
          user.incrementGamesWonUnderFive();
        } else if (currentTime <= 15) {
          user.incrementGamesWonUnderFifteen();
        } else if (currentTime <= 30) {
          user.incrementGamesWonUnderThirty();
        }
        saveStats();
      } catch (IOException e) {
        e.printStackTrace();
      }
      // Increment streak
      sessionStreak++;
    }
    if (win == 2) {
      speak("You Lost!");
      // Adding loss stats
      try {
        user.setGamesLost(User.getGamesLost() + 1);
        saveStats();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      // Reset streak
      sessionStreak = 0;
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

  /** This method sets up the canvas for the game. */
  protected void setupCanvas(
      TextArea predictionList, ImageView drawButton, ImageView eraseButton, ImageView clearButton) {
    predictionList.setText("");
    // Reset the canvas
    drawButton.setDisable(true);
    drawButton.setOpacity(1);
    eraseButton.setDisable(false);
    eraseButton.setOpacity(0.3);
    clearButton.setDisable(false);
    clearButton.setOpacity(1);
  }

  /** This method resets the canvas on game completion. */
  protected void resetCanvas(
      Canvas canvas,
      Rectangle startButton,
      Label startLabel,
      Rectangle saveButton,
      ImageView eraseButton,
      ImageView drawButton,
      ImageView clearButton) {
    // Set parameters to appropriate values upon completion of game
    canvas.setDisable(true);
    startButton.setDisable(false);
    timeline.stop();
    predictTimeline.stop();
    // Change the START button to PLAY AGAIN instead
    startLabel.setText("Play Again");
    // Make save button visible and active so user can save their image
    saveButton.setDisable(false);
    saveButton.setVisible(true);
    eraseButton.setDisable(true);
    eraseButton.setOpacity(0.3);
    clearButton.setDisable(true);
    clearButton.setOpacity(0.3);
    drawButton.setDisable(true);
    drawButton.setOpacity(0.3);
  }

  /**
   * This method initialises the setup of the game.
   *
   * @param canvas The canvas that the game will be played on
   * @param saveButton The button that allows the user to save their image
   * @param drawButton The button that allows the user to draw
   * @param eraseButton The button that allows the user to erase
   * @param clearButton The button that allows the user to clear the canvas
   * @param timeValue The label that displays the time remaining
   * @throws ModelException This exception is thrown if the model is not found
   * @throws IOException This exception is thrown if the file is not found
   */
  protected void initialiseSetup(
      Canvas canvas,
      Rectangle saveButton,
      ImageView drawButton,
      ImageView eraseButton,
      ImageView clearButton,
      Label timeValue)
      throws ModelException, IOException {

    // Load the current user
    user = User.getUser(User.currentUser);

    // Get difficulty levels
    accuracyLevel = User.getAccuracyLevel();
    timeLevel = user.getTimeLevel();
    User.getConfidenceLevel();

    // Set up the canvas
    graphic = canvas.getGraphicsContext2D();
    speech = new TextToSpeech();
    restart = false;
    erase = false;
    active = false;
    // Sets up the buttons
    saveButton.setDisable(true);
    saveButton.setVisible(false);
    drawButton.setOpacity(0.3);
    drawButton.setDisable(true);
    eraseButton.setOpacity(0.3);
    eraseButton.setDisable(true);
    clearButton.setOpacity(0.3);
    clearButton.setDisable(true);

    // Set up the timer
    timeValue.setText(String.valueOf(75 - (timeLevel * 15)));

    count = 0;
    win = 0;
    // Fill canvas with white
    graphic = canvas.getGraphicsContext2D();
    graphic.setFill(Color.WHITE);
    graphic.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    graphic.setFill(Color.BLACK);
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
          final double size = 6.0;
          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;
          // This is the colour of the brush.
          graphic.setFill(Color.BLACK);
          graphic.setLineWidth(size);

          if (win == 0) {
            graphic.strokeLine(currentX, currentY, x, y);

            // update the coordinates
            currentX = x;
            currentY = y;
          }
        });
    model = new DoodlePrediction();

    canvas.setDisable(true);
  }
}

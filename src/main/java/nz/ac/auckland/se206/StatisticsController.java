package nz.ac.auckland.se206;

import static nz.ac.auckland.se206.User.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class StatisticsController implements Initializable {

  @FXML private Label titleLabel;
  @FXML private Label gamesPlayedLabel;
  @FXML private Label gamesWonLabel;
  @FXML private Label gamesLostLabel;
  @FXML private Label fastestWinLabel;
  @FXML private Label longestStreakLabel;
  @FXML private Label difficultyLabel;
  @FXML private Label wordsEncounteredLabel;
  private SoundPlayer sound;

  /**
   * This method switch the scene to the Start box page
   *
   * @param start Activates when the Return button is pressed
   */
  @FXML
  private void onSwitchStart(MouseEvent start) {
    System.out.println("Switch to start box");
    // Get scene that the button event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to start box
    try {
      scene.setRoot(App.loadFxml("startBox"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method switch the scene to the achievements page
   *
   * @param start Activates when the Return button is pressed
   */
  @FXML
  private void onSwitchAchievements(MouseEvent start) {
    System.out.println("Switch to achievements");
    // Get scene that the button event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to achievements
    try {
      scene.setRoot(App.loadFxml("achievements"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** This method displays the stats of the user in the label */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    sound = new SoundPlayer();
    try {
      titleLabel.setText(String.format("%s's Statistics", getUsername()));
      // Fill the statistics labels
      gamesPlayedLabel.setText(String.format("%d games", getGamesWon() + getGamesLost()));
      gamesWonLabel.setText(String.format("%d games", getGamesWon()));
      gamesLostLabel.setText(String.format("%d games", getGamesLost()));
      // Display the fastest win time
      fastestWinLabel.setText(String.format("%.2f seconds", getBestTime()));
      longestStreakLabel.setText(String.format("%d streaks", getLongestStreak()));
      wordsEncounteredLabel.setText(String.join(", ", getWordHistory()));
      // display words difficulty
      switch (User.getWordsLevel()) {
          // easy
        case 1:
          difficultyLabel.setText("Easy");
          break;
          // medium
        case 2:
          difficultyLabel.setText("Medium");
          break;
          // hard
        case 3:
          difficultyLabel.setText("Hard");
          break;
          // master
        case 4:
          difficultyLabel.setText("Master");
          break;
      }
    } catch (IOException e) {
      // Catch block
      throw new RuntimeException(e);
    }
  }
  /** This method plays the sound effect for buttons in the application */
  @FXML
  private void playPop() {
    sound.playPop();
  }
}

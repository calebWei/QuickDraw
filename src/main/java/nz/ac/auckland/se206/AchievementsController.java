package nz.ac.auckland.se206;

import static nz.ac.auckland.se206.User.getUsername;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class AchievementsController implements Initializable {

  @FXML private Label titleLabel;
  @FXML private Label thirtySecondsLabel;
  @FXML private Label fifteenSecondsLabel;
  @FXML private Label fiveSecondsLabel;
  @FXML private Label thirtySecondsBadge;
  @FXML private Label fifteenSecondsBadge;
  @FXML private Label fiveSecondsBadge;
  @FXML private Label fifteenGamesLabel;
  @FXML private Label tenGamesLabel;
  @FXML private Label fiveGamesLabel;
  @FXML private Label fifteenGamesBadge;
  @FXML private Label tenGamesBadge;
  @FXML private Label fiveGamesBadge;
  @FXML private Label secretAchievementLabel;
  @FXML private Label secretAchievementBadge;
  private SoundPlayer sound;

  /**
   * This method switch the scene to the Start Menu page
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
   * This method switch the scene to the statistics page
   *
   * @param start Activates when the Return button is pressed
   */
  @FXML
  private void onSwitchStats(MouseEvent start) {
    System.out.println("Switch to statistics");
    // Get scene that the button event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to statistics
    try {
      scene.setRoot(App.loadFxml("statistics"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** This method displays the achievement of the user in the label */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    sound = new SoundPlayer();
    // Display achievements
    try {
      // Set title
      titleLabel.setText(String.format("%s's Achievements", getUsername()));
      // Set games won under x  amount of time
      thirtySecondsLabel.setText(String.format("%d times", User.getGamesWonUnderThirty()));
      thirtySecondsBadge.setText(String.format("x %d", User.getGamesWonUnderThirty()));
      fifteenSecondsLabel.setText(String.format("%d times", User.getGamesWonUnderFifteen()));
      fifteenSecondsBadge.setText(String.format("x %d", User.getGamesWonUnderFifteen()));
      fiveSecondsLabel.setText(String.format("%d times", User.getGamesWonUnderFive()));
      fiveSecondsBadge.setText(String.format("x %d", User.getGamesWonUnderFive()));
      // Set game streaks achievements
      fiveGamesLabel.setText(String.format("%d times", User.getFiveGameStreaks()));
      fiveGamesBadge.setText(String.format("x %d", User.getFiveGameStreaks()));
      tenGamesLabel.setText(String.format("%d times", User.getTenGameStreaks()));
      tenGamesBadge.setText(String.format("x %d", User.getTenGameStreaks()));
      fifteenGamesLabel.setText(String.format("%d times", User.getFifteenGameStreaks()));
      fifteenGamesBadge.setText(String.format("x %d", User.getFifteenGameStreaks()));
      // Secret Achievement
      if (getUsername().equals("Nas-ty")) {
        secretAchievementBadge.setText("x 1");
        secretAchievementLabel.setText("Hi Nasser!");
      } else if (getUsername().equals("mr-V")) {
        secretAchievementBadge.setText("x 1");
        secretAchievementLabel.setText("Hi Valerio!");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  /** This method plays the sound effect for buttons in the application */
  @FXML
  private void playPop() {
    sound.playPop();
  }
}

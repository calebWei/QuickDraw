package nz.ac.auckland.se206;

import static nz.ac.auckland.se206.User.getUsername;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

/**
 * This is the controller for the starting scene when the user run the program. The scene contains a
 * button that allows the user to start a new game. This process takes the user to the scene for the
 * canvas.
 *
 * @author Tuan Le
 */
public class BoxController {
  private SoundPlayer sound;
  @FXML private Label startLabel;

  /**
   * This method switch the scene to the Start box page
   *
   * @param start Activates when the Return button is pressed
   */
  @FXML
  private void onSwitchGameMode(MouseEvent start) {
    System.out.println("Switch to canvas");
    // Get scene that the button event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to canvas
    try {
      scene.setRoot(App.loadFxml("gameMode"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method switches the scene to statistics page
   *
   * @param start When the Profile button is pushed
   */
  @FXML
  private void onSwitchStatistics(MouseEvent start) {
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

  /**
   * This method switches the scene to settings page
   *
   * @param start When the Settings button is pushed
   */
  @FXML
  private void onSwitchSettings(MouseEvent start) {
    System.out.println("Switch to settings");
    // Get scene that the button event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to settings
    try {
      scene.setRoot(App.loadFxml("settings"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method switches the scene to mainMenu page
   *
   * @param start When the Logout button is pushed
   */
  @FXML
  private void onSwitchLogOut(MouseEvent start) {
    System.out.println("Switch to Profile Selection");
    // Get scene that the button event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to main menu
    try {
      scene.setRoot(App.loadFxml("profileSelect"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method switches the scene to mainMenu page
   *
   * @param start When the Logout button is pushed
   */
  @FXML
  private void onSwitchMainMenu(MouseEvent start) {
    System.out.println("Switch to main menu");
    // Get scene that the button event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to canvas
    try {
      scene.setRoot(App.loadFxml("mainMenu"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** This method displays the welcome message to the user */
  public void initialize() {
    sound = new SoundPlayer();
    try {
      startLabel.setText("Welcome, " + getUsername() + "!");
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

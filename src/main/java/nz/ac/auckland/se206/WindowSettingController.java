package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class WindowSettingController {
  private SoundPlayer sound;

  /** This class initialize the scene with the sound player for sound effects */
  public void initialize() {
    sound = new SoundPlayer();
  }
  /**
   * This method switch the scene to the Start Menu page
   *
   * @param start Activates when the Return button is pressed
   */
  @FXML
  private void onSwitchStart(MouseEvent start) {
    System.out.println("Switch to start box");
    // Get scene that the button event is in
    Rectangle button = (Rectangle) start.getSource();
    Scene scene = button.getScene();
    // Change scene to start box
    try {
      scene.setRoot(App.loadFxml("startBox"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method switch the scene to the difficulty setting page
   *
   * @param start Activates when the Level Select button is pressed
   */
  @FXML
  private void onSwitchDifficulty(MouseEvent start) {
    // Get scene that the button event is in
    Rectangle button = (Rectangle) start.getSource();
    Scene scene = button.getScene();
    // Change scene to canvas
    try {
      scene.setRoot(App.loadFxml("difficulty"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method switches the scene to the Login page and also deletes the account
   *
   * @param start Activates when the Delete Account button is pressed
   */
  @FXML
  private void onDeleteAccount(MouseEvent start) {
    System.out.println("Switch to main menu");
    // Get scene that the button event is in
    Rectangle button = (Rectangle) start.getSource();
    Scene scene = button.getScene();
    // Change scene to mainMenu
    try {
      User.deleteUser(User.currentUser);
      User.dereferenceUser();
      scene.setRoot(App.loadFxml("mainMenu"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** This method switches clears the user history */
  @FXML
  private void onClearHistory() {
    // Clears the user history
    try {
      User.clearUserHistory(User.currentUser);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method switches the scene to the Login page
   *
   * @param start Activates when the Logout button is pressed
   */
  @FXML
  private void onSwitchMainMenu(ActionEvent start) {
    System.out.println("Switch to main menu");
    // Get scene that the button event is in
    Button button = (Button) start.getSource();
    Scene scene = button.getScene();
    // Change scene to canvas
    try {
      scene.setRoot(App.loadFxml("mainMenu"));
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

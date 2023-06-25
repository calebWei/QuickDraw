package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class MainMenuController {
  private SoundPlayer sound;

  /** This class initialize the scene with the sound player for sound effects */
  public void initialize() {
    sound = new SoundPlayer();
  }
  /**
   * This method switch the scene to the Start page
   *
   * @param start Activates when the Login button is pressed
   */
  @FXML
  private void onSwitchSelectProfile(MouseEvent start) {
    System.out.println("Switch to profile selection");
    // Get scene that the mouse event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to profile select
    try {

      scene.setRoot(App.loadFxml("profileSelect"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method switch the scene to the Register page
   *
   * @param start Activates when the Register button is pressed
   */
  @FXML
  private void onSwitchNew(MouseEvent start) {
    System.out.println("Switch to register");
    // Get scene that the mouse event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to profile select
    try {
      scene.setRoot(App.loadFxml("register"));
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

package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class LandingPageController {

  /**
   * This method switch the scene to the Main Menu
   *
   * @param start Activates when the mainMenu button is pressed
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
}

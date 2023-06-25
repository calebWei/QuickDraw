package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameModeController {

  @FXML private Stage stage;
  @FXML private Scene scene;
  @FXML private Parent root;
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
   * This method switch the scene to the classic mode
   *
   * @param start Activates when the classic button is pressed
   */
  @FXML
  private void onSwitchClassic(MouseEvent start) throws IOException {
    System.out.println("Switch to classic mode");
    // Load the hidden scene
    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/canvas.fxml")));
    stage = (Stage) ((Node) start.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * This method switch the scene to the zen mode
   *
   * @param start Activates when the zen button is pressed
   */
  @FXML
  private void onSwitchZen(MouseEvent start) throws IOException {
    System.out.println("Switch to zen mode");
    // Get scene that the button event is in
    Parent root =
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/zenCanvas.fxml")));
    Stage stage = (Stage) ((Node) start.getSource()).getScene().getWindow();
    // Change scene to zen mode
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * This method switch the scene to the hidden mode
   *
   * @param start Activates when the hidden button is pressed
   */
  @FXML
  private void onSwitchHidden(MouseEvent start) throws IOException {
    System.out.println("Switch to hidden mode");
    // Load the hidden scene
    root =
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/hiddenCanvas.fxml")));
    stage = (Stage) ((Node) start.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * This method switch the scene to the streak mode
   *
   * @param start Activates when the streak button is pressed
   */
  @FXML
  private void onSwitchStreak(MouseEvent start) throws IOException {
    System.out.println("Switch to streak mode");
    // Load the hidden scene
    root =
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/streakCanvas.fxml")));
    stage = (Stage) ((Node) start.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
  /** This method plays the sound effect for buttons in the application */
  @FXML
  private void playPop() {
    sound.playPop();
  }
}

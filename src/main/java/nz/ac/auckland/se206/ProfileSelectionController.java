package nz.ac.auckland.se206;

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class ProfileSelectionController {

  @FXML private ImageView user1;
  @FXML private ImageView user2;
  @FXML private ImageView user3;
  @FXML private ImageView user4;
  @FXML private ImageView user5;
  @FXML private ImageView user6;
  @FXML private Label name1;
  @FXML private Label name2;
  @FXML private Label name3;
  @FXML private Label name4;
  @FXML private Label name5;
  @FXML private Label name6;
  private SoundPlayer sound;

  /**
   * This method is called by the FXMLLoader when initialization is complete
   *
   * @throws IOException if the file is not found
   */
  public void initialize() throws IOException {
    sound = new SoundPlayer();
    // Loads the profile data from the file
    String[] icons = new String[] {"ghost", "cactus", "mug", "bulb", "chick", "pineapple"};
    ImageView[] images = new ImageView[] {user1, user2, user3, user4, user5, user6};
    Label[] names = new Label[] {name1, name2, name3, name4, name5, name6};

    // Finding all the files in the directory
    File file = new File("src/main/resources/users/");
    File[] files = file.listFiles();
    assert files != null;

    // Looping through all the files and setting the images and names
    for (File f : files) {
      for (int i = 0; i < 6; i++) {
        // If the file is a directory, then it is a profile
        if (f.getName().equals(icons[i] + ".json")) {
          User.getUser(icons[i]);
          images[i].setDisable(false);
          images[i].setOpacity(1);
          // Setting the name of the profile
          names[i].setText(User.getUsername());
        }
      }
    }
  }

  /**
   * This method is called when the user clicks on the profile icon
   *
   * @param start the mouse event
   */
  @FXML
  private void onClickGhost(MouseEvent start) {
    // Get scene that the button event is in
    ImageView button = (ImageView) start.getSource();
    Scene scene = button.getScene();
    try {
      // Loading all user information
      User.currentUser = "ghost";
      // Change scene to canvas
      scene.setRoot(App.loadFxml("startBox"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is called when the user clicks on the profile icon
   *
   * @param start the mouse event
   */
  @FXML
  private void onClickCactus(MouseEvent start) {
    // Get scene that the button event is in
    ImageView button = (ImageView) start.getSource();
    Scene scene = button.getScene();
    try {
      // Loading all user information
      User.currentUser = "cactus";
      // Change scene to canvas
      scene.setRoot(App.loadFxml("startBox"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is called when the user clicks on the profile icon
   *
   * @param start the mouse event
   */
  @FXML
  private void onClickMug(MouseEvent start) {
    // Get scene that the button event is in
    ImageView button = (ImageView) start.getSource();
    Scene scene = button.getScene();
    try {
      // Loading all user information
      User.currentUser = "mug";
      // Change scene to canvas
      scene.setRoot(App.loadFxml("startBox"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is called when the user clicks on the profile icon
   *
   * @param start the mouse event
   */
  @FXML
  private void onClickBulb(MouseEvent start) {
    // Get scene that the button event is in
    ImageView button = (ImageView) start.getSource();
    Scene scene = button.getScene();
    try {
      // Loading all user information
      User.currentUser = "bulb";
      // Change scene to canvas
      scene.setRoot(App.loadFxml("startBox"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is called when the user clicks on the profile icon
   *
   * @param start the mouse event
   */
  @FXML
  private void onClickChick(MouseEvent start) {
    // Get scene that the button event is in
    ImageView button = (ImageView) start.getSource();
    Scene scene = button.getScene();
    try {
      // Loading all user information
      User.currentUser = "chick";
      // Change scene to canvas
      scene.setRoot(App.loadFxml("startBox"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is called when the user clicks on the profile icon
   *
   * @param start the mouse event
   */
  @FXML
  private void onClickPineapple(MouseEvent start) {
    // Get scene that the button event is in
    ImageView button = (ImageView) start.getSource();
    Scene scene = button.getScene();
    try {
      // Loading all user information
      User.currentUser = "pineapple";
      // Change scene to canvas
      scene.setRoot(App.loadFxml("startBox"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method switch the scene to the Main Menu
   *
   * @param start Activates when the mainMenu button is pressed
   */
  @FXML
  private void onSwitchMainMenu(MouseEvent start) {
    System.out.println("Switch to main menu");
    // Get scene that the mouse event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to main menu
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
  /** This method plays the sound effect for character selections in the application */
  @FXML
  private void playCharacter() {
    sound.playCharacter();
  }
}

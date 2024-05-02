package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		stage.setResizable(false);

		Group root = new Group();

		Label scoresheet = new Label("SCORE: 0");
		scoresheet.getStyleClass().add("counter-label");
		Label missed = new Label("MISSED: 0");
		missed.getStyleClass().add("counter-label");
		Label bullets = new Label("AMMO: 0");
		bullets.getStyleClass().add("counter-label");
		Label magazines = new Label("MAG: 0");
		magazines.getStyleClass().add("counter-label");
		Label hotkey = new Label("<E> FOR EXIT");
		hotkey.getStyleClass().add("hotkey-label");
		HBox infoContainer = new HBox(scoresheet, missed, bullets, magazines, hotkey);
		infoContainer.setSpacing(10);
		infoContainer.setAlignment(Pos.CENTER);
		scoresheet.setVisible(false);
		missed.setVisible(false);
		bullets.setVisible(false);
		magazines.setVisible(false);
		hotkey.setVisible(false);
		

		Scene scene = new Scene(root, 1024, 500);
		Font.loadFont(getClass().getResourceAsStream("/resources/font.ttf"), 12);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		String targetMousePointerImageFile = "/resources/target.png";
		Image targetImage;
		try {
			targetImage = new Image(getClass().getResource(targetMousePointerImageFile).toURI().toString());
			ImageView targetImageView = new ImageView(targetImage);
			targetImageView.setFitWidth(30);
			targetImageView.setFitHeight(30);
			ImageCursor targetCursor = new ImageCursor(targetImage);
			scene.setCursor(targetCursor);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		stage.setTitle("Hunting");
		stage.setScene(scene);

		Game g = new Game(1024, 500, 10, scoresheet, bullets, missed, hotkey, magazines);
		root.getChildren().addAll(g, infoContainer);

		stage.show();

		Animation t = new Animation(g);
		t.start();

	}

}
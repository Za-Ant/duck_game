package application;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

public class Game extends Group {
	private ArrayList<Bird> birds;
	private Gun gun;
	private String input = "";
	final int HspriteW = 50;
	final int HspriteH = 81;
	final int DspriteW = 45;
	final int DspriteH = 45;
	private double max_width, max_height;
	final int MAX_DUCK = 5;
	final int START_BULLET = 10;
	final int START_CLIP = 1;
	private int SCORE = 0;
	private int BULLETS = START_BULLET;
	private int CLIPS = START_CLIP;
	private int MISSED = 0;
	private final int MAX_CLIPS = 999;
	private Label scoresheet;
	private Label bullets;
	private Label missed;
	private Label hotkey;
	private Label magazines;

	private ImageView[] duckSprites;
	private ImageView[] duckSpritesFlip;
	private ImageView duckShooted;
	private ImageView duckShootedFlip;
	private ImageView[] gunSprites;
	private final int duck_sprites_count = 4;
	private final int gun_sprites_count = 1;

	private MediaPlayer backgroundMusic;
	private MediaPlayer backgroundGameOverMusic;
	private MediaPlayer backgroundVideo;

	private MediaPlayer gunShotSound;
	private MediaPlayer hitSound;
	private MediaPlayer gunReloadSound;
	private MediaPlayer addMagazineSound;

	private boolean game_over_state = false;
	private VBox gameOverBox;
	private Label gameOverLabel;
	private Label gameOverActionsLabel;
	private ImageView gameOverImage;

	public Game(int w, int h, int enem, Label scoresheet, Label bullets, Label missed, Label hotkey, Label magazines) {
		max_width = w;
		max_height = h;
		this.scoresheet = scoresheet;
		this.bullets = bullets;
		this.missed = missed;
		this.hotkey = hotkey;
		this.magazines = magazines;

		String musicFile = "/resources/musik_back.mp3";
		Media music;
		try {
			music = new Media(getClass().getResource(musicFile).toURI().toString());
			backgroundMusic = new MediaPlayer(music);
			backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
			backgroundMusic.play();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		String musicGameOverFile = "/resources/musik_back.mp3";
		
		Media musicGameOver;
		try {
			musicGameOver = new Media(getClass().getResource(musicGameOverFile).toURI().toString());
			backgroundGameOverMusic = new MediaPlayer(musicGameOver);
			backgroundGameOverMusic.setCycleCount(MediaPlayer.INDEFINITE);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		String videoFile = "/resources/1.mp4";
		Media media;
		try {
			media = new Media(getClass().getResource(videoFile).toURI().toString());
			backgroundVideo = new MediaPlayer(media);
			backgroundVideo.setCycleCount(MediaPlayer.INDEFINITE);
			backgroundVideo.play();
			MediaView mediaView = new MediaView(backgroundVideo);
			mediaView.setFitWidth(w);
			getChildren().add(mediaView);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		setOnKeyPressed(evt -> onKeyPressed(evt));

		loadResources();

		birds = new ArrayList<>();

		gun = new Gun(gun_sprites_count, HspriteW, HspriteH, max_width, max_height, gunSprites);
		getChildren().add(gun);
		scoresheet.setVisible(true);
		bullets.setVisible(true);
		missed.setVisible(true);
		hotkey.setVisible(true);
		magazines.setVisible(true);
		initGamgeOverScreen();

		setFocusTraversable(true);
		setFocused(true);
		setOnMouseClicked(evt -> onClick(evt));
		setOnMouseMoved(evt -> onMoved(evt));
	}

	private void initGamgeOverScreen() {
		gameOverLabel = new Label("WOOOOOW");
		gameOverLabel.setStyle("-fx-text-fill: white; -fx-font-size: 80;-fx-font-family:\"PixelGameFont\"");
		gameOverActionsLabel = new Label("RESTART \"R\"");
		gameOverActionsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10;-fx-font-family:\"PixelGameFont\"");
		gameOverBox = new VBox(gameOverLabel, gameOverActionsLabel);

		gameOverBox.setLayoutX(max_width / 2 - 395 / 2);
		gameOverBox.setLayoutY(max_height / 2 - 100);
		gameOverBox.setAlignment(Pos.TOP_CENTER);

		String gameOverImageFile = "/resources/over.png";
		Image gameOverImg = new Image(getClass().getResource(gameOverImageFile).toExternalForm());
		gameOverImage = new ImageView(gameOverImg);

		gameOverBox.setVisible(false);
		gameOverLabel.setVisible(false);
		gameOverActionsLabel.setVisible(false);
		gameOverImage.setVisible(false);

		getChildren().addAll(gameOverBox, gameOverImage);
		gameOverImage.toBack();
		gameOverBox.toBack();

	}

	private void showGameOverScreen() {
		gameOverImage.toFront();
		gameOverBox.toFront();
		gameOverBox.setVisible(true);
		gameOverLabel.setVisible(true);
		gameOverActionsLabel.setVisible(true);
		gameOverImage.setVisible(true);
	
		gun.setVisible(false);
		hotkey.setVisible(false);
	
		try {
			if (backgroundMusic != null) {
				backgroundMusic.stop();
			}
			if (backgroundGameOverMusic != null) {
				backgroundGameOverMusic.stop();
				backgroundGameOverMusic.play();
			}
		} catch (Exception eC) {
	
		}
	}

	private Object restartGame() {

		getChildren().removeAll(birds);

		for (int i = 0; i < birds.size(); i++) {
			birds.remove(i);
		}

		gameOverImage.toBack();
		gameOverBox.toBack();
		gameOverBox.setVisible(false);
		gameOverLabel.setVisible(false);
		gameOverActionsLabel.setVisible(false);
		gameOverImage.setVisible(false);
		gun.setVisible(true);
		hotkey.setVisible(true);

		BULLETS = START_BULLET;
		CLIPS = START_CLIP;
		SCORE = 0;
		MISSED = 0;
		game_over_state = false;

		try {
			if (backgroundGameOverMusic != null) {
				backgroundGameOverMusic.stop();
			}
			if (backgroundMusic != null) {
				backgroundMusic.stop();
				backgroundMusic.play();
			}
		} catch (Exception eC) {

		}

		return null;
	}

	private void loadResources() {
		duckSprites = new ImageView[duck_sprites_count];
		duckSpritesFlip = new ImageView[duck_sprites_count];
		for (int i = 0; i < duck_sprites_count; i++) {
			String duckImageFrameFile = "/resources/b" + (i + 1) + ".png";
			Image originalImage;
			try {
				originalImage = new Image(getClass().getResource(duckImageFrameFile).toURI().toString(),
						DspriteW, DspriteH, false, false);
				duckSprites[i] = new ImageView(originalImage);
				originalImage = flipImage(originalImage);
				duckSpritesFlip[i] = new ImageView(originalImage);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		String duckShootedImageFile = "/resources/boom.png";
		Image shootedImage;
		try {
			shootedImage = new Image(getClass().getResource(duckShootedImageFile).toURI().toString(), DspriteW,
					DspriteH, false, false);
			duckShooted = new ImageView(shootedImage);
			shootedImage = flipImage(shootedImage);
			duckShootedFlip = new ImageView(shootedImage);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		gunSprites = new ImageView[gun_sprites_count];
		for (int i = 0; i < gun_sprites_count; i++) {
			String gunImageFrameFile = "/resources/gn" + (i + 1) + ".png";
			Image originalImage;
			try {
				originalImage = new Image(getClass().getResource(gunImageFrameFile).toURI().toString(),
						HspriteW, HspriteH, false, false);
				gunSprites[i] = new ImageView(originalImage);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		gunShotSound = loadSoundFx("/resources/gunshot.wav");
		addMagazineSound = loadSoundFx("/resources/add_magazine.wav");
		gunReloadSound = loadSoundFx("/resources/gun_reload_sound.mp3");
		hitSound = loadSoundFx("/resources/hit.mp3");
	}

	private Image flipImage(Image originalImage) {
		int width = (int) originalImage.getWidth();
		int height = (int) originalImage.getHeight();
		WritableImage flippedImage = new WritableImage(width, height);
	
		PixelReader pixelReader = originalImage.getPixelReader();
		PixelWriter pixelWriter = flippedImage.getPixelWriter();
	
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixelWriter.setArgb(width - x - 1, y, pixelReader.getArgb(x, y));
			}
		}
	
		return flippedImage;
	}

	private MediaPlayer loadSoundFx(String musicFile) {
		MediaPlayer mediaPlayer;
		Media sound;
		try {
			sound = new Media(getClass().getResource(musicFile).toURI().toString());
			mediaPlayer = new MediaPlayer(sound);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		return mediaPlayer;
	}

	public void update(double deltaTime) {

		switch (input) {
		case "r":
		case "R":
			if (game_over_state) {
				restartGame();
			}
			break;

		case "e":
		case "E":
			if (!game_over_state) {
				game_over_state = true;
				showGameOverScreen();
			}
		}
		input = "";
		createBird();
		moveBird(deltaTime / 1000000000);
		removeBirds();
		updateCounters();
	}

	private void updateCounters() {
		scoresheet.setText("SCORE: " + SCORE);
		bullets.setText("BULLETS: " + BULLETS);
		missed.setText("MISSED: " + MISSED);
		magazines.setText("CLIPS: " + CLIPS);
	}

	private void hit() {
		SCORE += 1;

		gunShotSound.stop();
		gunShotSound.play();
		
		hitSound.stop();
		hitSound.play();

		if (SCORE % 10 == 0) {
			CLIPS++;
			CLIPS = CLIPS > MAX_CLIPS ? MAX_CLIPS : CLIPS;
			if (addMagazineSound != null) {
				addMagazineSound.stop();
				addMagazineSound.play();
			}
		}
	}
	
	private void reload() {
		BULLETS = START_BULLET;
		CLIPS--;
		if (gunReloadSound != null) {
			gunReloadSound.stop();
			gunReloadSound.play();
		}
	}
	
	private void createBird() {
		if (game_over_state) {
			return;
		}
		if (birds.size() < MAX_DUCK) {
			if (Math.random() < 0.3) {
				Bird bird = new Bird(duck_sprites_count, DspriteW, DspriteW, HspriteW,
						HspriteH, max_width, max_height, duckSprites, duckSpritesFlip, duckShooted,
						duckShootedFlip);

				birds.add(bird);
				getChildren().add(bird);
			}
		}
	}

	private void moveBird(double delta) {
		for (Bird b : birds) {
			b.move(delta);
		}
	}

	private void removeBirds() {
		int s = birds.size() - 1;
		for (int i = 0; i < birds.size(); i++) {
			Bird bird = birds.get(i);
			if (bird.getState() == EBirdStatus.Out_of_view) {
				getChildren().remove(bird);
				
				while (s > i && birds.get(s).getState() == EBirdStatus.Out_of_view) s--;
				
				if (s > i)
					Collections.swap(birds, i, s--);
			}
		}
		
		if (birds.size() > 0) {
			while (birds.get(birds.size() - 1).getState() == EBirdStatus.Out_of_view) {
				birds.remove(birds.size() - 1);
			}
		}
	}
	
	private void onMoved(MouseEvent evt) {
		double mouseX = evt.getX();
		double angle = Math.toDegrees(Math.atan2(gun.customY, mouseX - gun.customX));
		gun.setRotate(90 - angle);
	}

	private void onKeyPressed(KeyEvent evt) {
		input = evt.getCode().toString();
	}

	private void onClick(MouseEvent event) {
		double click_x = event.getX();
		double click_y = event.getY();
		shotAction(click_x, click_y);
	}

	private void shotAction(double click_x, double click_y) {
		if (BULLETS > 0) {
			BULLETS--;

			gunShotSound.stop();
			gunShotSound.play();
			boolean found = false;
			
			for (Bird b : birds) {
				if (b.state == EBirdStatus.Fired && b.state == EBirdStatus.Out_of_view) continue;
				if (b.getBoundsInParent().contains(click_x, click_y)) {
					b.state = EBirdStatus.Fired;
					hit();
					found = true;
					break;
				}
			}
			
			if (!found) {
				MISSED++;
			}
			
		} else if (CLIPS > 0) {
			reload();
		} else {
			game_over_state = true;
			showGameOverScreen();
		}
	}

}

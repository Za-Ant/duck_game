package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.util.HashMap;
import java.lang.Runnable;

enum EBirdStatus {
	Flying,
	Fired,
	Out_of_view,
}


public class Bird extends ImageView {
	private ImageView[] duckSprites;
	private ImageView duckShooted;
	private HashMap<EBirdStatus, Runnable> draws;
	double x, y, max_width, max_height, speed;
	int active_image = 0;
	EBirdStatus state = EBirdStatus.Flying;
	private int sprites_count;
	private Timeline t;
	boolean missed = false;

	public Bird(int sprites_count, double w, double h, double hero_w, double hero_h, double maxw, double maxh,
			ImageView[] duckSprites, ImageView[] duckSpritesFlip, ImageView duckShooted, ImageView duckShootedFlip) {
		super();
		max_width = maxw;
		max_height = maxh;
		this.sprites_count = sprites_count;
		do {
			speed = (int) (-5 + Math.random() * 11) * 30;
		} while (speed == 0);

		this.duckSprites = (speed > 0 ? duckSprites : duckSpritesFlip);
		this.duckShooted = (speed > 0 ? duckShooted : duckShootedFlip);

		setImage(duckSprites[0].getImage());
		draws = new HashMap<>();
		draws.put(EBirdStatus.Flying, () -> drawFlying());
		draws.put(EBirdStatus.Fired, () -> drawFired());
		draws.put(EBirdStatus.Out_of_view, () -> drawOutOfView());

		if (speed < 0) {
			setLayoutX(max_width);
		} else {
			setLayoutX(1);
		}
		setLayoutY(70 + (int) (Math.random() * (max_height - hero_h - h - 100)));
	}

	public void move(double deltaTime) {
		setLayoutX(getLayoutX() + speed * deltaTime);
		setLayoutY(getLayoutY() - 5 + (int) (Math.random() * 10));
		if ((getLayoutX() < 0) || (getLayoutX() > max_width)) {
			state = EBirdStatus.Out_of_view;
			missed = true;
		}
		draws.get(state).run();
	}
	
	private void drawFlying() {
		active_image = (active_image + 1) % sprites_count;
		setImage(duckSprites[active_image].getImage());
	}
	
	private void drawFired() {
		setImage(duckShooted.getImage());
		speed = 0;
		t = new Timeline(new KeyFrame(Duration.millis(100), e -> {}));
		t.setCycleCount(10);
		t.setOnFinished(e -> end());
		t.play();
	}
	
	private void drawOutOfView() {
		setImage(null);
	}

	private void end() {
		t.stop();
		state = EBirdStatus.Out_of_view;
	}

	public EBirdStatus getState() {
		return state;
	}

}

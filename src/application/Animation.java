package application;

import javafx.animation.AnimationTimer;

public class Animation extends AnimationTimer {
	private Game game;
	private long lastNanoTime;

	public Animation(Game g) {
		game = g;
	}

	public void handle(long now) {
		game.update(now - lastNanoTime);
		lastNanoTime = now;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

	}

	public void start() {
		lastNanoTime = System.nanoTime();
		super.start();
	}

}

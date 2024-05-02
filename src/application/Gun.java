package application;

import javafx.scene.image.ImageView;
import java.util.HashMap;
import java.lang.Runnable;

enum GunStates {
    Standby,
    Firing,
    Out_of_ammo,
}

public class Gun extends ImageView {
    private ImageView[] customGunSprites;
    private HashMap<GunStates, Runnable> draws;
    double customX, customY, customMaxWidth, customMaxHeight;
    int customActiveImage = 0;
    GunStates customState = GunStates.Standby;

    public Gun(int spritesCount, double width, double height, double maxWidth, double maxHeight,
            ImageView[] heroSprites) {
        super();
        draws = new HashMap<>();
        draws.put(GunStates.Standby, () -> drawStandby());

        customMaxWidth = maxWidth;
        customMaxHeight = maxHeight;
        this.customGunSprites = heroSprites;
        setImage(heroSprites[0].getImage());
        setLayoutX(maxWidth / 2 - width / 2);
        setLayoutY(maxHeight - height + 30);
        customX = getLayoutX();
        customY = getLayoutY();
    }

    public void move(double deltaTime) {
        draws.get(customState).run();
    }

    private void drawStandby() {
        setImage(customGunSprites[customActiveImage].getImage());
    }
}

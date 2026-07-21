package ru.uliana;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DekstopLauncher {
    public  static void main (String[] args){
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Битва героев: Кристалл власти");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);

        new Lwjgl3Application(new HeroesGame(), config);
    }

}

package com.jjjackson.framework;

public interface Game {
    public Input getInput();

    public FileIO getFileIo();

    public Graphics getGraphics();

    public Audio getAudio();

    public void setScreen(Screen screen);

    public Screen getCurrentScreen();

    public Screen getStartScreen();
}

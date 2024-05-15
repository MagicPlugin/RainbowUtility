package com.magicpowered.rainbowutility.Storage;

public class Message {
    public String chat;
    public String title;
    public String subTitle;
    public int fadeIn;
    public int stay;
    public int fadeOut;
    public String actionBar;

    public Message(String chat, String actionBar, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        this.chat = chat;
        this.actionBar = actionBar;
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }
}

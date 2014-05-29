package chatterbox.core;

import models.User;

import java.util.ArrayList;

public class ChatRoom {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private ArrayList<User> onlineUsers;

    private User owner;

    private String name;

    private String topic;

    private boolean open;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ChatRoom(User owner, String name, boolean isPublic) {
        this(owner, name, "//TODO: Set topic name", isPublic);
    }

    public ChatRoom(User owner, String name, String topic, boolean isPublic){
        onlineUsers = new ArrayList<User>();

        this.owner = owner;
        this.name = name;
        this.topic = topic;
        this.open = isPublic;
    }



    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public ArrayList<User> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(ArrayList<User> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    public void roomMessage(String message){

        for(int i = 0; i < onlineUsers.size(); i++){
            User user = onlineUsers.get(i);
            user.sendMessage(message);
        }

    }



    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================




}

package net.artux.columba.data.model;

import java.io.Serializable;
import java.util.List;

public class Channel implements Serializable {

    private String uid;
    private String title;
    private List<String> usersUIDs;
    private String lastMessage = "";

    public Channel() {
    }

    public Channel(String uid, String title, List<String> usersUIDs) {
        this.uid = uid;
        this.title = title;
        this.usersUIDs = usersUIDs;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getUsersUIDs() {
        return usersUIDs;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}

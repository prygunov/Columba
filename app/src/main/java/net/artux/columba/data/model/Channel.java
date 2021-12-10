package net.artux.columba.data.model;

import java.io.Serializable;
import java.util.List;

public class Channel implements Serializable {

    private String uid;
    private String title;
    private String icon;
    private List<String> usersUIDs;
    private String lastMessageId;

    public Channel() {
    }

    public Channel(String uid, String title, List<String> usersUIDs) {
        this.uid = uid;
        this.title = title;
        this.usersUIDs = usersUIDs;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
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

    public void setUsersUIDs(List<String> usersUIDs) {
        this.usersUIDs = usersUIDs;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public String getIcon(){
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

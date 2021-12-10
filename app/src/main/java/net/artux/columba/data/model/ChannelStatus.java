package net.artux.columba.data.model;

public class ChannelStatus {

    private String channelId;
    private String userId;
    private String username;

    public ChannelStatus() {
    }

    public ChannelStatus(String channelId, String userId, String username) {
        this.channelId = channelId;
        this.userId = userId;
        this.username = username;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}

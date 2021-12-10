package net.artux.columba.data.model;

public class Request {

    private String nickname;
    private String uid;
    private String privateKey;

    public Request(String nickname, String uid, String privateKey) {
        this.nickname = nickname;
        this.uid = uid;
        this.privateKey = privateKey;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUid() {
        return uid;
    }

    public String getPrivateKey() {
        return privateKey;
    }

}

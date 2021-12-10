package net.artux.columba.data.model;

public class ShareKey {

    private String uid;
    private String privateKey;

    public ShareKey(String uid, String privateKey) {
        this.uid = uid;
        this.privateKey = privateKey;
    }

    public String getUid() {
        return uid;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}

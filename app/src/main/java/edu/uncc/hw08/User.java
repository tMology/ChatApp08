package edu.uncc.hw08;

public class User {

    public String uid, name;
    public boolean isOnline;

    public User(){

    }

//    public User(String id, String displayName){
//        this.uid = id;
//        this.name = displayName;
//    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline(){
        return isOnline();

    }
    public void setOnline(boolean online) {
        isOnline = online;
    }


}

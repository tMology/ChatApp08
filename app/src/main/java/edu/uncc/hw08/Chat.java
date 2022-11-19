package edu.uncc.hw08;


import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {
    public String chatId;
    public Message lastMsg;
    public ArrayList<String> userNames, userIds;

    public Chat() {
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Message getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(Message lastMsg) {
        this.lastMsg = lastMsg;
    }

    public ArrayList<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(ArrayList<String> userNames) {
        this.userNames = userNames;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }
}
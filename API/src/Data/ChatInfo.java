package Data;

import Roles.User;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatInfo implements Serializable
{
    private String name;
    private long lastMessage;
    ImageView chatImage;
    private int amountOfUsers;
    private boolean ifNewChat = false;
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<Message> messages = new ArrayList<Message>();
    private byte[] key = null;
    private boolean areMessagesDecrypted = false;
    int index;
    String chatPhotoName = "";
    String chatPhotoPath = "";
    boolean ifEnabledChat = true;
    boolean isGroupChat = false;
    ArrayList<String> moderatorUsernames = new ArrayList<>();

    public boolean isGroupChat()
    {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat)
    {
        isGroupChat = groupChat;
    }

    public ArrayList<String> getModeratorUsernames()
    {
        return moderatorUsernames;
    }

    public void setModeratorUsernames(ArrayList<String> moderatorUsernames)
    {
        this.moderatorUsernames = moderatorUsernames;
    }

    public boolean isIfEnabledChat()
    {
        return ifEnabledChat;
    }

    public void setIfEnabledChat(boolean ifEnabledChat)
    {
        this.ifEnabledChat = ifEnabledChat;
    }

    public String getChatPhotoPath()
    {
        return chatPhotoPath;
    }

    public void setChatPhotoPath(String chatPhotoPath)
    {
        this.chatPhotoPath = chatPhotoPath;
    }

    public String getChatPhotoName()
    {
        return chatPhotoName;
    }

    public void setChatPhotoName(String chatPhotoName)
    {
        this.chatPhotoName = chatPhotoName;
    }

    public boolean areMessagesDecrypted()
    {
        return areMessagesDecrypted;
    }

    public void setMessagesToDecrypted()
    {
        areMessagesDecrypted = true;
    }

    public byte[] getKey()
    {
        return key;
    }

    public void setKey(byte[] key)
    {
        this.key = key;
    }

    public ChatInfo(String chatName, long lastMessage, int amountOfUsers, int chatIndex)
    {
        this.name = chatName;
        this.lastMessage = lastMessage;
       // this.chatImage = chatImage;
        this.amountOfUsers = amountOfUsers;
        this.index = chatIndex;
        //this.users = users;
    }

    public ChatInfo(ChatInfo chatToCopy)
    {
        this.name = chatToCopy.name;
        this.lastMessage = chatToCopy.lastMessage;
        this.amountOfUsers = chatToCopy.amountOfUsers;
        this.index = chatToCopy.index;
        this.ifNewChat = chatToCopy.ifNewChat;
        this.users = new ArrayList<>(chatToCopy.users);
    }

    public String getName()
    {
        return name;
    }

    public void setChatName(String chatName)
    {
        this.name = chatName;
    }

    public long getTimeOfLastMessage()
    {
        return lastMessage;
    }

    public void setLastMessage(long lastMessage)
    {
        this.lastMessage = lastMessage;
    }

    public ImageView getChatImage()
    {
        return chatImage;
    }

    public void setChatImage(ImageView chatImage)
    {
        this.chatImage = chatImage;
    }

    public int getAmountOfUsers()
    {
        return amountOfUsers;
    }

    public void setAmountOfUsers(int amountOfUsers)
    {
        this.amountOfUsers = amountOfUsers;
    }

    public boolean isIfNewChat()
    {
        return ifNewChat;
    }

    public void setIfNewChat(boolean ifNewChat)
    {
        this.ifNewChat = ifNewChat;
    }

    public int getChatIndex()
    {
        return index;
    }

    public void setChatIndex(int chatIndex)
    {
        this.index = chatIndex;
    }

    public ArrayList<User> getUsers()
    {
        return users;
    }

    public void setUsers(ArrayList<User> users)
    {
        this.users = users;
    }

    public ArrayList<Message> getMessages()
    {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages)
    {
        this.messages = messages;
    }
}

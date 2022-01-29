package Roles;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable
{

    private String username;
    private String name;
    String photoName;
    private ImageView imageView;
    ArrayList<User>userBlackList = new ArrayList<User>();

    public User(String username, String name, String photoName)
    {
        this.username = username;
        this.name = name;
        this.photoName = photoName;
        //imageView.autosize();
    }

    public ArrayList<User> getUserBlackList()
    {
        return userBlackList;
    }

    public void setUserBlackList(ArrayList<User> userBlackList)
    {
        this.userBlackList = userBlackList;
    }

    public String getPhotoName()
    {
        return photoName;
    }

    public void setPhotoName(String photoName)
    {
        this.photoName = photoName;
    }
//    public User (User userToCopy)
//    {
//        username = userToCopy.username;
//        name = userToCopy.name;
//
//    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername(){return username;}

    public ImageView getImage()
    {
        return imageView;
    }
}

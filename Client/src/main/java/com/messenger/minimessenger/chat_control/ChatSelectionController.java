package com.messenger.minimessenger.chat_control;

import Data.*;
import Roles.*;
import com.messenger.minimessenger.JavaPostgreSql;
import com.messenger.minimessenger.Main;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class ChatSelectionController
{
    public static void initializeChats(ListView<Node> listView, ArrayList<ChatInfo> chats1)
    {
        ArrayList<ChatInfo> chats = new ArrayList<ChatInfo>(chats1);

        if (chats.size() == 0)
            return;

        listView.getItems().clear();
        int size = chats.size();

        for (int i = 0; i < size; i++)
        {

            long max = Integer.MIN_VALUE;
            int indexOfMaxChat = Integer.MIN_VALUE;
            for (int j=0; j < chats.size();j++)
            {
                if (max < chats.get(j).getTimeOfLastMessage())
                {
                    max = chats.get(j).getTimeOfLastMessage();
                    indexOfMaxChat = j;
                }
            }

            String chatName = getNameOfChat(chats.get(indexOfMaxChat));


            Label user = new Label(chatName);
            ImageView userImage = chats.get(indexOfMaxChat).getChatImage();
            userImage.setFitWidth(40);
            userImage.setFitHeight(40);
            user.setGraphic(userImage);
            listView.getItems().add(user);

            chats.remove(indexOfMaxChat);
        }
    }

    public static void addNewChat(ListView<Node> listView, ChatInfo chat)
    {
        String chatName = getNameOfChat(chat);
        Label newChat = new Label(chatName);

        ImageView userImage = chat.getChatImage();
        userImage.setFitWidth(40);
        userImage.setFitHeight(40);
        newChat.setGraphic(userImage);

        listView.getItems().add(0, newChat);
    }

    public static String getNameOfChat(ChatInfo chat)
    {
        String chatName = chat.getName();
        if(!chat.isGroupChat())
        {
            Scanner scanner = new Scanner(chat.getName());
            String readTo;
            while (scanner.hasNextLine())
            {
                readTo = scanner.nextLine();
                if (!readTo.equals(Main.user.getUsername()))
                {
                    User userToGetName = JavaPostgreSql.getUserByUsername(readTo);

                    if (userToGetName.getUsername().isEmpty())
                    {
                        chatName = readTo;
                        break;
                    }

                    chatName = userToGetName.getName();
                }
            }
        }
        else
        {
            chatName = chat.getName();
        }
        chatName+="\n"+new Date(chat.getTimeOfLastMessage());

        return chatName;
    }
}

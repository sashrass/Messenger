package com.messenger.minimessenger.Threads;

import Data.ChatInfo;
import Data.Message;
import Utils.Encrypter;
import com.messenger.minimessenger.JavaPostgreSql;
import javafx.scene.Node;
import javafx.scene.control.ListView;;

import java.util.ArrayList;

public class MessagesDownloader extends Thread
{

    private ArrayList<ChatInfo> chats;
    public static volatile boolean isReady = false;
    ListView<Node>chatChoser;

    public MessagesDownloader(ArrayList<ChatInfo> chats,  ListView<Node>chatChoser)
    {
        this.chats = chats;
        this.chatChoser = chatChoser;
    }

    @Override
    public void run()
    {
        chatChoser.setDisable(true);
        for (int i=0; i < chats.size();i++)
        {
            if (chats.get(i).getMessages().size() != 0 ||
                    chats.get(i).getKey() == null)
                continue;

            ArrayList<Message> messages = JavaPostgreSql.getMessages(chats.get(i).getChatIndex());
            chats.get(i).setMessages(messages);

            for (int j=0; j < chats.get(i).getMessages().size();j++)
            {
                Encrypter.decryptMessage(chats.get(i).getMessages().get(j),
                        chats.get(i).getKey());
            }
        }
        chatChoser.setDisable(false);
        isReady = true;
    }
}

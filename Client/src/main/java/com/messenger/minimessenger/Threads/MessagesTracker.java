package com.messenger.minimessenger.Threads;

import Data.ChatInfo;
import Data.Message;
import Utils.CompareChats;
import Utils.Encrypter;
import Utils.Notifications;
import com.dropbox.core.DbxException;
import com.messenger.minimessenger.AfterLoginUser;
import com.messenger.minimessenger.JavaPostgreSql;
import com.messenger.minimessenger.Main;
import com.messenger.minimessenger.chat_control.ChatSelectionController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class MessagesTracker extends Thread
{
    ListView<Node> chatChoser;
    GridPane messagesPane;
    ArrayList<ChatInfo> chats;
    ScrollPane messagesPaneScroll;

    public static volatile boolean isConnectionFree = true;
    public static boolean isProgrammRun = true;
    public static int chatWithhighestID = Integer.MIN_VALUE;
    public static volatile boolean isConClosed = true;

    private static String urlDB = "jdbc:postgresql://hattie.db.elephantsql.com:5432/lwlcihpm";
    private static String userDB = "lwlcihpm";
    private static String passwordDB = "iY50AxqQnrgVDtRrqNhvj_DqrUrfpCeA";

    @Override
    public void run()
    {
        //isConClosed = false;
        /*
        Connection con;
        try
        {
            con = DriverManager.getConnection(urlDB, userDB, passwordDB);
        } catch (SQLException e)
        {
            e.printStackTrace();
            return;
        }*/


        for (int i = 0; i < chats.size(); i++)
        {
            if (chats.get(i).getChatIndex() > chatWithhighestID)
                chatWithhighestID = chats.get(i).getChatIndex();
        }
        long lastMessage = -1;
        if (chats.size() != 0)
            lastMessage = chats.get(0).getTimeOfLastMessage();
        System.out.println("last message = " + lastMessage);
        System.out.println("chat with highest iD:" + chatWithhighestID);

        while (isProgrammRun)
        {
            if (isConnectionFree)
            {
                ArrayList<ChatInfo> newChats = JavaPostgreSql.getNewChats(chatWithhighestID);

                for (int i = 0; i < newChats.size(); i++)
                {
                    isConClosed = true;
                    JavaPostgreSql.getAllUsersInChat(newChats.get(i));
                    AfterLoginUser.setPhotoOfChat(newChats.get(i));

                    lastMessage = newChats.get(i).getTimeOfLastMessage();
                    chatWithhighestID = newChats.get(i).getChatIndex();

                    chatChoser.setDisable(true);
                    int finalI = i;
                    final boolean[] isAdded = {false};
                    Platform.runLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ChatSelectionController.addNewChat(chatChoser, newChats.get(finalI));
                            AfterLoginUser.selectedChatIndex++;
                            isAdded[0] = true;
                            System.out.println("addd" + isAdded[0]);
                        }
                    });

                    while (!isAdded[0])
                    {
                        try
                        {
                            Thread.sleep(500);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        System.out.println(isAdded[0]);
                    }
                    System.out.println("chat added to chatselection");

                    if (!JavaPostgreSql.getKeyForChat(newChats.get(i)))
                    {
                        JavaPostgreSql.getAllUsersInChat(newChats.get(i));
                        newChats.get(i).setMessages(new ArrayList<Message>());
                        System.out.println("Chat key not set");
                    } else
                    {
                        newChats.get(i).setMessages(JavaPostgreSql.getMessages(newChats.get(i).getChatIndex()));
                        for (int j = 0; j < newChats.get(i).getMessages().size(); j++)
                            Encrypter.decryptMessage(newChats.get(i).getMessages().get(j),
                                    newChats.get(i).getKey());
                    }

                    if (AfterLoginUser.isGroupChat(newChats.get(i)))
                        newChats.get(i).setGroupChat(true);

                    chats.add(newChats.get(i));
                    chats.sort(new CompareChats());
                }
                isConClosed = false;
                chatChoser.setDisable(false);
                /*
                try
                {

                    if (rs.next())
                    {
                        if (rs.isFirst())
                            startIndexofAddedChats = 0;

                        startIndexofAddedChats++;

                        System.out.println("In new chat creator THREAD 3");
                        String chatName = rs.getString("chatname");
                        long lastmessageOfNewChat = rs.getLong("lastmess");
                        int amountOfUsers = rs.getInt("numbers");
                        int chatID = rs.getInt("idchat");

                        ChatInfo chatToAdd = new ChatInfo(chatName, lastmessageOfNewChat, amountOfUsers, chatID);

                        con.close();
                        isConClosed = true;
                        JavaPostgreSql.getAllUsersInChat(chatToAdd);
                        AfterLoginUser.setPhotoOfChat(chatToAdd);

                        lastMessage = lastmessageOfNewChat;
                        chatWithhighestID = chatID;

                        chatChoser.setDisable(true);
                        Platform.runLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ChatSelectionController.addNewChat(chatChoser,chatToAdd);
                                AfterLoginUser.selectedChatIndex++;
                            }
                        });

                        chats.add(chatToAdd);
                        chats.sort(new CompareChats());
                        isConClosed = false;
                        con = DriverManager.getConnection(urlDB, userDB, passwordDB);




                    }


                    if (startIndexofAddedChats !=-1)
                    {
                        con.close();
                        isConClosed = true;
                        for (int i=0; i < startIndexofAddedChats;i++)
                        {
                            System.out.println("chat index to gey key: " + chats.get(i).getChatIndex());

                            AfterLoginUser.setPhotoOfChat(chats.get(i));
                            if (!JavaPostgreSql.getKeyForChat(chats.get(i)))
                            {
                                JavaPostgreSql.getAllUsersInChat(chats.get(i));
                                chats.get(i).setMessages(new ArrayList<Message>());
                                System.out.println("Chat key not set");
                            }
                            else
                            {
                                chats.get(i).setMessages(JavaPostgreSql.getMessages(chats.get(i).getChatIndex()));
                                for (int j=0; j < chats.get(i).getMessages().size();j++)
                                    Encrypter.decryptMessage(chats.get(i).getMessages().get(j),
                                            chats.get(i).getKey());
                            }
                            chatChoser.setDisable(false);
                        }
                        isConClosed = false;
                        con = DriverManager.getConnection(urlDB, userDB, passwordDB);
                    }
                } catch (SQLException e)
                {
                    e.printStackTrace();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                */

                Object[] requestedKeys = JavaPostgreSql.checkRequestedKeys(Main.user.getUsername());

                if (requestedKeys != null)
                {
                    int chatID = (int) requestedKeys[0];

                    for (int i = 0; i < chats.size(); i++)
                        if ((chats.get(i).getChatIndex() == chatID) && (chats.get(i).getKey() != null))
                        {
                            String usernameToReceive = (String) requestedKeys[1];
                            //con.close();
                            isConClosed = true;
                            JavaPostgreSql.transferKey(chats.get(i), usernameToReceive);
                            isConClosed = false;
                            //con = DriverManager.getConnection(urlDB, userDB, passwordDB);
                        }
                }


                if (AfterLoginUser.indexesOfChatsWithoutKeys.size() != 0)
                {
                    //try
                    //{
                        /*String getKeyQuery = "SELECT key FROM transfer_keys\n"+
                                "WHERE username = " + '?'+ " AND chat_id = " + '?';
                        pst = con.prepareStatement(getKeyQuery);*/

                    //pst.setString(1, Main.user.getUsername());
                    isConClosed = true;
                    for (int i = 0; i < AfterLoginUser.indexesOfChatsWithoutKeys.size(); i++)
                    {
                        System.out.println("no key");
                        int index = AfterLoginUser.indexesOfChatsWithoutKeys.get(i);

                        if (JavaPostgreSql.getKeyForChat(chats.get(index)))
                            AfterLoginUser.indexesOfChatsWithoutKeys.remove(index);
                            /*

                            pst.setInt(2, chats.get(index).getChatIndex());
                            ResultSet rs = pst.executeQuery();

                            if (rs.next())
                            {
                                con.close();
                                isConClosed = true;
                                JavaPostgreSql.getKeyForChat(chats.get(index));
                                isConClosed = false;
                                con = DriverManager.getConnection(urlDB, userDB, passwordDB);
                                AfterLoginUser.indexesOfChatsWithoutKeys.remove(index);
                            }
                        }
                    } catch (SQLException throwables)
                    {
                        throwables.printStackTrace();
                    }*/
                    }
                    isConClosed = false;
                }
                try
                {
                    ArrayList<Message> newMessages = JavaPostgreSql.getNewMessages(lastMessage);

                    for (int j = 0; j < newMessages.size(); j++)
                    {
                        System.out.println("IN ADDING NEW MESSAGE next()");
                        if (newMessages.get(j).getSenderUsername().equals(Main.user.getUsername()))
                        {
                            lastMessage = newMessages.get(j).getTime();
                            System.out.println("My message - doing nothing");
                            continue;
                        }

                        int chatIndexInChoser = -1;
                        for (int i = 0; i < chats.size(); i++)
                        {
                            if (chats.get(i).getChatIndex() == newMessages.get(j).getMessage_chat_id())
                            {
                                chatIndexInChoser = i;
                                break;
                            }
                        }

                        String textmessage = newMessages.get(j).getText();
                        String attachments = newMessages.get(j).getAttachments();
                        String username = newMessages.get(j).getSenderUsername();
                        String name = newMessages.get(j).getSenderName();
                        long idmessage = newMessages.get(j).getTime();
                        int idchat = newMessages.get(j).getMessage_chat_id();


                        if (Main.stg.isIconified())
                        {
                            Message mess = new Message(textmessage, "", "",
                                    name, 0, 0);
                            Encrypter.decryptMessage(mess, chats.get(chatIndexInChoser).getKey());
                            System.out.println("notif");
                            Notifications.showMessage(mess);
                        }

                        if ((chatIndexInChoser != -1) && chats.get(chatIndexInChoser).getKey() == null)
                            continue;

                        if (chatIndexInChoser != -1)
                        {
                            int finalChatIndexInChoser = chatIndexInChoser;
                            System.out.println("ChatIndexOnChoser: " + chatIndexInChoser);
                            final boolean[] iAmEnd = {false};
                            //con.close();
                            isConClosed = true;
                            Platform.runLater(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    System.out.println("IN ADDING NEW MESSAGE run()");
                                    chats.get(finalChatIndexInChoser).setLastMessage(idmessage);
//                                            System.out.println(chats.get(finalChatIndexInChoser).getTimeOfLastMessage());

                                    if (chats.get(finalChatIndexInChoser).getMessages().size() != 0)
                                    {
                                        try
                                        {
//                                                Message messageToAdd = new Message(
//                                                        rs.getString("textmessage"),
//                                                        rs.getString("attachments"),
//                                                        rs.getString("username"),
//                                                        rs.getString("name"),
//                                                        rs.getLong("idmessage"),
//                                                        rs.getInt("idchat"));

                                            Message messageToAdd = new Message(
                                                    textmessage,
                                                    attachments,
                                                    username,
                                                    name,
                                                    idmessage,
                                                    idchat);

                                            Encrypter.decryptMessage(messageToAdd,
                                                    chats.get(finalChatIndexInChoser).getKey());

                                            if (messageToAdd.getAttachments().length() != 0)
                                            {
                                                if (AfterLoginUser.getFileExtensionByFilename(messageToAdd.getAttachments()).equals("png")
                                                        || AfterLoginUser.getFileExtensionByFilename(messageToAdd.getAttachments()).equals("jpg")
                                                        || AfterLoginUser.getFileExtensionByFilename(messageToAdd.getAttachments()).equals("jpeg"))
                                                {
                                                    ImageView photoToMessage = (ImageView) AfterLoginUser.getPhotoOfMessage(messageToAdd);
                                                    messageToAdd.setImage(photoToMessage);
                                                }
                                            }
                                            chats.get(finalChatIndexInChoser).getMessages().add(messageToAdd);

                                        } catch (IOException e)
                                        {
                                            e.printStackTrace();
                                        } catch (DbxException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }

                                    int oldSelectedChatIndex = AfterLoginUser.selectedChatIndex;

                                    if (finalChatIndexInChoser == AfterLoginUser.selectedChatIndex)
                                        AfterLoginUser.selectedChatIndex = -1;
                                    AfterLoginUser.isNeedToShowMessages = false;
                                    ChatInfo chatToRelocate = chats.get(finalChatIndexInChoser);
                                    chatChoser.getItems().remove(finalChatIndexInChoser);

                                    ChatSelectionController.addNewChat(chatChoser, chatToRelocate);

                                    chats.sort(new CompareChats());

                                    if (AfterLoginUser.selectedChatIndex == -1)
                                    {
                                        System.out.println("Setting selectedChatIndex to 0");
                                        chatChoser.getSelectionModel().selectFirst();
                                        AfterLoginUser.selectedChatIndex = 0;
                                    }

                                    AfterLoginUser.isNeedToShowMessages = true;


                                    if (finalChatIndexInChoser > oldSelectedChatIndex && AfterLoginUser.selectedChatIndex != -3)
                                    {
                                        System.out.println("finalChatIndexInChoser > oldSelectedChatIndex");
                                        //int choicedChatIndex = chatChoser.getSelectionModel().getSelectedIndex();
                                        //chatChoser.getSelectionModel().select(choicedChatIndex+1);
                                        AfterLoginUser.selectedChatIndex++;
                                    }
                                    iAmEnd[0] = true;
                                }
                            });
                            while (!iAmEnd[0])
                            {
                                System.out.println("not end");
                                Thread.sleep(500);
                            }
                            isConClosed = false;
                            //con = DriverManager.getConnection(urlDB, userDB, passwordDB);

                            chatIndexInChoser = 0;
                        } else
                            break;


                        //Thread.sleep(1000);
                        System.out.println(chatIndexInChoser + " ::: " + AfterLoginUser.selectedChatIndex);
                        if (chatIndexInChoser == AfterLoginUser.selectedChatIndex)
                        {

                            //String message = rs.getString("textmessage");
                            //if (!rs.getString("username").equals(Main.user.getUsername()))
                            //{
                            int finalChatIndexInChoser1 = chatIndexInChoser;
                            Platform.runLater(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    System.out.println("Adding new mess to pane");
                                    Message messageToShow = chats.get(finalChatIndexInChoser1).
                                            getMessages().get(chats.get(finalChatIndexInChoser1).getMessages().size() - 1);
                                    Text messageText;
//                                            String message = null;
//                                            try
//                                            {
//                                                message = rs.getString("textmessage");
//                                            } catch (SQLException e)
//                                            {
//                                                e.printStackTrace();
//                                            }
                                    System.out.println("I-m adding message to selected chat");

                                    String nameText = messageToShow.getSenderUsername() + "\n\n";

                                    if (messageToShow.getAttachments().length() == 0)
                                        messageText = new Text(nameText + messageToShow.getText());
                                    else
                                        messageText = new Text(nameText + messageToShow.getText() + '\n');

                                    TextFlow textFlow = new TextFlow();
                                    textFlow.setStyle("-fx-background-color:DODGERBLUE;-fx-background-radius:10");
                                    textFlow.setTextAlignment(TextAlignment.JUSTIFY);
                                    messageText.setFill(Color.WHITE);
                                    messageText.setFont(Font.font("System", 12));
                                    textFlow.getChildren().add(messageText);

                                    if (messageToShow.getAttachments().length() != 0)
                                    {
                                        if (AfterLoginUser.getFileExtensionByFilename(messageToShow.getAttachments()).equals("png")
                                                || AfterLoginUser.getFileExtensionByFilename(messageToShow.getAttachments()).equals("jpg")
                                                || AfterLoginUser.getFileExtensionByFilename(messageToShow.getAttachments()).equals("jpeg"))
                                        {
                                            ImageView image = messageToShow.getImage();

                                            image.setFitWidth(100);
                                            image.setFitHeight(200);
                                            image.setSmooth(true);

                                            image.setOnMouseClicked(new EventHandler<MouseEvent>()
                                            {
                                                @Override
                                                public void handle(MouseEvent mouseEvent)
                                                {
                                                    ImageView imageCopy = new ImageView(image.getImage());
                                                    AfterLoginUser.showFullSizeImage(imageCopy);
                                                }
                                            });

                                            textFlow.setTextAlignment(TextAlignment.CENTER);
                                            textFlow.setMinHeight(220);
                                            textFlow.getChildren().add(image);
                                        } else
                                        {
                                            GridPane gridPane = new GridPane();
                                            gridPane.getChildren().clear();
                                            gridPane.addRow(1);
                                            gridPane.addColumn(0);
                                            System.out.println("In sendMessage(): rows - " + gridPane.getRowCount() +
                                                    " : columns - " + gridPane.getColumnCount());

                                            ImageView imageView = new ImageView(new Image("fileImage.jpg"));
                                            imageView.setFitHeight(30);
                                            imageView.setFitWidth(30);
                                            Message message = messageToShow;
                                            imageView.setOnMouseClicked(new EventHandler<MouseEvent>()
                                            {
                                                @Override
                                                public void handle(MouseEvent mouseEvent)
                                                {
                                                    //Runtime.getRuntime().exec("explorer.exe / select," + path);
                                                    //message.setFileAttachment(new File("a"));
                                                    //message.
                                                    try
                                                    {
                                                        message.setFileAttachment((File) AfterLoginUser.getPhotoOfMessage(message));
                                                    } catch (IOException e)
                                                    {
                                                        e.printStackTrace();
                                                    } catch (DbxException e)
                                                    {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            gridPane.add(imageView, 0, 1);
                                            gridPane.add(new Label(message.getAttachments()), 1, 1);
                                            textFlow.getChildren().add(gridPane);
                                        }
                                    }


                                    messagesPane.addRow(messagesPane.getRowCount());
                                    messagesPane.add(textFlow, 0, messagesPane.getRowCount());
                                    messagesPane.layout();
                                    messagesPaneScroll.layout();
                                    messagesPaneScroll.setVvalue(1);

                                    AfterLoginUser.isNeedToShowMessages = false;
                                    chatChoser.getSelectionModel().selectFirst();
                                    AfterLoginUser.isNeedToShowMessages = true;
                                }
                            });
//                                try
//                                {
//                                    Thread.sleep(500);
//                                } catch (InterruptedException e)
//                                {
//                                    e.printStackTrace();
//                                }
                            //}
                        }
                        lastMessage = newMessages.get(j).getTime();
                    }

                } catch (Throwable e)
                {
                    e.printStackTrace();
                }

                try
                {
                    long amount = JavaPostgreSql.getCountOfBlackListIamIn();

                    System.out.println("amount: " + amount + " size: " + AfterLoginUser.usersIamInBlacklist.size());

                    if (amount != AfterLoginUser.usersIamInBlacklist.size())
                    {
                        isConClosed = true;
                        ArrayList<String> newUsernamesIamInBlackListOf = new ArrayList<>();

                        newUsernamesIamInBlackListOf = JavaPostgreSql.getUsersWhereIamInBlacklist(Main.user.getUsername());


                        ArrayList<Integer> indexesOfNewFalseChats = new ArrayList<>();

                        for (int i = 0; i < newUsernamesIamInBlackListOf.size(); i++)
                        {
                            for (int j = 0; j < chats.size(); j++)
                            {
                                if (chats.get(j).isGroupChat())
                                    continue;

                                boolean isFound = false;
                                for (int k = 0; k < 2; k++)
                                {
                                    if ((chats.get(i).getUsers().size() == 0) && !chats.get(i).isIfNewChat())
                                        JavaPostgreSql.getAllUsersInChat(chats.get(i));

                                    if (newUsernamesIamInBlackListOf.get(i).equals(chats.get(j).getUsers().get(k).getUsername()))
                                    {
                                        chats.get(j).setIfEnabledChat(false);
                                        indexesOfNewFalseChats.add(j);
                                        isFound = true;
                                    }
                                }
                                if (isFound)
                                    break;
                            }
                        }

                        for (int i = 0; i < chats.size(); i++)
                        {
                            if (indexesOfNewFalseChats.contains(i))
                                continue;

                            chats.get(i).setIfEnabledChat(true);
                        }

                        AfterLoginUser.usersIamInBlacklist = newUsernamesIamInBlackListOf;

//                            while(newUsernamesIamInBlackListOf.size() != AfterLoginUser.usersIamInBlacklist.size())
//                            {
//                                for (int i=0; i < newUsernamesIamInBlackListOf.size();i++)
//                                {
//                                    boolean isFound = false;
//                                    for (int j=0; j < AfterLoginUser.usersIamInBlacklist.size();j++)
//                                    {
//                                        if (newUsernamesIamInBlackListOf.get(i)
//                                                .equals(AfterLoginUser.usersIamInBlacklist.get(j)))
//                                        {
//                                            isFound = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!isFound)
//                                    {
//
//                                    }
//                                }
//                            }
                        isConClosed = false;
                    }
                } catch (Throwable throwables)
                {
                    throwables.printStackTrace();
                }

                try
                {
                    for (int i=0; i < chats.size();i++)
                    {
                        Object[]newPhotoNameOfChat =
                                JavaPostgreSql.checkPhotoUpdate(chats.get(i).getChatIndex(),
                                        chats.get(i).getChatPhotoName());

                        if (newPhotoNameOfChat != null)
                        {
                            for (int j=0; j < chats.size();j++)
                                if ((int) newPhotoNameOfChat[0] == chats.get(j).getChatIndex())
                                {
                                    System.out.println("new photo");
                                    isConClosed = true;
                                    chats.get(j).setChatPhotoName((String)newPhotoNameOfChat[1]);
                                    AfterLoginUser.setPhotoOfChat(chats.get(j));

                                    final boolean[] isEnd = {false};
                                    Platform.runLater(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            int oldSelectedChatIndex = AfterLoginUser.selectedChatIndex;
                                            AfterLoginUser.selectedChatIndex = -1;
                                            ChatSelectionController.initializeChats(chatChoser, chats);
                                            AfterLoginUser.selectedChatIndex = oldSelectedChatIndex;
                                            isEnd[0] = true;
                                        }
                                    });

                                    while (!isEnd[0])
                                    {
                                        Thread.sleep(100);
                                    }
                                    isConClosed = false;
                                }
                        }
                    }
                }
                catch(Throwable throwable)
                {
                    throwable.printStackTrace();
                }

                try
                {
                    for (int i=0; i < chats.size();i++)
                    {
                        if (chats.get(i).getUsers().size() == 0)
                            continue;

                        int actualCountOfUsersInChat =
                                JavaPostgreSql.getActualCountOfusersInChat(chats.get(i).getChatIndex());

                        System.out.println("actual count: " + actualCountOfUsersInChat + " : "
                                + chats.get(i).getUsers().size());

                        if (actualCountOfUsersInChat != chats.get(i).getUsers().size())
                        {
                            isConClosed = true;
                            JavaPostgreSql.getAllUsersInChat(chats.get(i));
                            boolean amIStillInChat = false;

                            for (int j=0; j < chats.get(i).getUsers().size();j++)
                                if (Main.user.getUsername().equals(chats.get(i).getUsers().get(j)))
                                {
                                    amIStillInChat = true;
                                    break;
                                }
                            if (!amIStillInChat)
                            {
                                chats.remove(i);
                                int finalI = i;
                                boolean[] isEnd = {false};
                                Platform.runLater(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        chatChoser.getItems().remove(finalI);
                                        if (AfterLoginUser.selectedChatIndex > finalI)
                                            AfterLoginUser.selectedChatIndex--;
                                        chatChoser.getSelectionModel().select(AfterLoginUser.selectedChatIndex);
                                        isEnd[0] = true;
                                    }
                                });

                                while (!isEnd[0])
                                {
                                    Thread.sleep(100);
                                }

                                int maxIndexChat = Integer.MIN_VALUE;
                                for (int j=0; j < chats.size();j++)
                                {
                                    if (chats.get(j).getChatIndex() > maxIndexChat)
                                        maxIndexChat = chats.get(j).getChatIndex();
                                }
                                chatWithhighestID = maxIndexChat;
                                isConClosed = false;
                            }
                        }

                    }
                }
                catch(Throwable throwable)
                {
                    throwable.printStackTrace();
                }

                try
                {
                    for (int i=0; i < chats.size();i++)
                    {
                        String newChatName = JavaPostgreSql.checkNewChatName(chats.get(i).getChatIndex(),
                                chats.get(i).getName());
                        if (!newChatName.isEmpty())
                        {
                            chats.get(i).setChatName(newChatName);
                            final boolean [] isEnd = {false};

                            Platform.runLater(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    isConClosed = true;
                                    int oldSelectedChatIndex = AfterLoginUser.selectedChatIndex;
                                    AfterLoginUser.selectedChatIndex = -1;
                                    ChatSelectionController.initializeChats(chatChoser, chats);
                                    chatChoser.getSelectionModel().select(oldSelectedChatIndex);
                                    AfterLoginUser.selectedChatIndex = oldSelectedChatIndex;
                                    isEnd[0] = true;
                                }
                            });

                            while (!isEnd[0])
                            {
                                Thread.sleep(100);
                            }
                        }
                    }
                }
                catch(Throwable throwable)
                {
                    throwable.printStackTrace();
                }

                try
                {
                    System.out.println("Sleep");
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            } else
            {
                System.out.println("Resource is not free");
                JavaPostgreSql.disconnectTracker();
                isConClosed = true;
                while (!isConnectionFree && isProgrammRun)
                {
                    try
                    {
                        System.out.println("Connection still not free");
                        Thread.sleep(500);

                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

                if (isProgrammRun)
                {
                    JavaPostgreSql.connectTracker();
                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    isConClosed = false;
                }
            }
            isConClosed = true;
        }

    }


    public MessagesTracker(ListView<Node> chatChoser, GridPane messagesPane,
            ArrayList<ChatInfo> chats, ScrollPane scrollPane)
        {
            this.chatChoser = chatChoser;
            this.messagesPane = messagesPane;
            this.chats = chats;
            this.messagesPaneScroll = scrollPane;
        }
    }


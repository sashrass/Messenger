package com.messenger.minimessenger;

/*import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;*/

import CustomExceptions.UserNotFoundException;
import Data.ChatInfo;
import Data.Message;
import Roles.User;
import Utils.CompareMessages;
import Utils.Encrypter;
import com.caucho.hessian.client.HessianProxyFactory;
import com.messenger.minimessenger.Threads.MessagesTracker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.MessengerAPI;
//import Messages.*;

import java.net.MalformedURLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaPostgreSql
{

    private static String urlDB = "jdbc:postgresql://hattie.db.elephantsql.com:5432/lwlcihpm";
    private static String userDB = "lwlcihpm";
    private static String passwordDB = "iY50AxqQnrgVDtRrqNhvj_DqrUrfpCeA";

    static MessengerAPI serverAPI;
    static HessianProxyFactory factory;

    static
    {
        factory = new HessianProxyFactory();
        String url = "http://127.0.0.1:8080";
        try
        {
            serverAPI = (MessengerAPI) factory.create(MessengerAPI.class, url + "/"+"basicAPI");
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean writeToDatabase(String userName, String userEmail, String userPassword)
    {

        String name = userName;
        String email = userEmail;
        String pass = userPassword;
        return false;

    }

    public static void readFromDatabase()
    {
        String query = "SELECT email FROM users WHERE name = 'ferfc'";

        try (Connection con = DriverManager.getConnection(JavaPostgreSql.urlDB, JavaPostgreSql.userDB, JavaPostgreSql.passwordDB);
             PreparedStatement pst = con.prepareStatement(query))
        {

            ResultSet rs;
            //pst.executeUpdate();
            rs = pst.executeQuery();
            String str;
            rs.next();
            Object obj = (Object) rs.getObject(1);

            if (obj instanceof String)
            {
                str = (String) obj;
                System.out.println(str);
            } else
                System.out.println("Not String");


            //System.out.println("Sucessfully created.");

        } catch (SQLException ex)
        {

            Logger lgr = Logger.getLogger(JavaPostgreSql.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static boolean registerUser(String username, String name, String email, String password)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        boolean result = serverAPI.registerUser(username,name, email, password);
        MessagesTracker.isConnectionFree = true;

        return result;

    }

    public static String checkIfUserExists(String email)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        String result = serverAPI.checkIfUserExists(email);
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static boolean isUserExists(String username)
    {

        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        boolean result = serverAPI.isUserExists(username);
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static ArrayList<ChatInfo> getChats(String username) throws UserNotFoundException
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        ArrayList<ChatInfo>chats = serverAPI.getChats(username);
        MessagesTracker.isConnectionFree = true;

        return chats;
    }

    public static ArrayList<Message> getMessages(int chatIndex)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        ArrayList<Message> messages = serverAPI.getMessages(chatIndex);
        messages.sort(new CompareMessages());

        MessagesTracker.isConnectionFree = true;

        return messages;

    }

    public static boolean sendMessage(Message message)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        Message messageLite = new Message(message.getText(),message.getAttachments(),
                message.getSenderUsername(), message.getSenderName(), message.getTime(),
                message.getMessage_chat_id());
        boolean result = serverAPI.sendMessage(messageLite);
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static int getLatestMessageIndex(String username)
    {
        return 0;
    }

    public static User getUserByEmail(String email)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        User user = serverAPI.getUserByEmail(email);
        MessagesTracker.isConnectionFree = true;

        return user;
    }

    public static boolean addNewChat(ChatInfo chatToAdd)
    {
        System.out.println("Adding chat: " + chatToAdd.getName());
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        ChatInfo chatLite = new ChatInfo(chatToAdd.getName(), chatToAdd.getTimeOfLastMessage(),
                chatToAdd.getAmountOfUsers(), chatToAdd.getChatIndex());
        chatLite.setChatPhotoName(chatToAdd.getChatPhotoName());

        int chatIndex = serverAPI.addNewChat(chatLite);
        if (chatIndex!= -1)
            chatToAdd.setChatIndex(chatIndex);
        MessagesTracker.isConnectionFree = true;

        return chatIndex != -1;
    }

    public static boolean addUsersToChat(ArrayList<User> users, ChatInfo chat)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        ArrayList<String>usernames = new ArrayList<>();
        for (int i=0; i < users.size();i++)
            usernames.add(users.get(i).getUsername());

        boolean result = serverAPI.addUsersToChat(usernames, chat.getChatIndex());
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static boolean getAllUsersInChat(ChatInfo chat)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        ArrayList<User>usersInChat = serverAPI.getAllUsersInChat(chat.getChatIndex());

        if (usersInChat != null)
            chat.setUsers(usersInChat);

        MessagesTracker.isConnectionFree = true;

        return usersInChat != null;

    }

    public static User getUserByUsername(String username)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        User user = serverAPI.getUserByUsername(username);
        MessagesTracker.isConnectionFree = true;

        return user;
    }

    public static boolean getKeyForChat(ChatInfo chat)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        byte[] key = serverAPI.getKeyForChat(chat.getChatIndex(), Main.user.getUsername());
        if (key != null)
        {
            chat.setKey(key);
            Encrypter.writeKeyToFile(chat);
            System.out.println("key written");
        }

        MessagesTracker.isConnectionFree = true;

        return key != null;
    }

    public static boolean transferKey(ChatInfo chat, String transferTo)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        boolean result = serverAPI.transferKey(chat.getKey(), transferTo, chat.getChatIndex());
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static boolean requestKey(ChatInfo chat)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        ArrayList<String>usernames = new ArrayList<>();
        for (int i=0; i < chat.getUsers().size();i++)
            usernames.add(chat.getUsers().get(i).getUsername());

        boolean result = serverAPI.requestKey(chat.getChatIndex(), usernames, Main.user.getUsername());
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static String getPhotoNameOfChat(ChatInfo chat)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        String result = serverAPI.getPhotoNameOfChat(chat.getChatIndex());
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static String getUserPhotoName(String username)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        String result = serverAPI.getUserPhotoName(username);
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static void updateProfilePhoto(User user)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        serverAPI.updateProfilePhoto(user.getUsername(), user.getPhotoName());
        MessagesTracker.isConnectionFree = true;
    }

    public static boolean addUserToBlacklist(String usernameToBlock)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        boolean result = serverAPI.addUserToBlacklist(usernameToBlock, Main.user.getUsername());
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static ArrayList<String> getBlackList(String username)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        ArrayList<String> blackList = serverAPI.getBlackList(username);
        MessagesTracker.isConnectionFree = true;

        return blackList;
    }

    public static boolean removeUserFromBlacklist(String usernameToRemove)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        boolean result = serverAPI.removeUserFromBlacklist(usernameToRemove, Main.user.getUsername());
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static ArrayList<String> getUsersWhereIamInBlacklist(String username)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        ArrayList<String> usernamesList = serverAPI.getUsersWhereIamInBlacklist(username);
        MessagesTracker.isConnectionFree = true;
        return usernamesList;
    }

    public static boolean deleteMessage(Message message)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        boolean result = serverAPI.deleteMessage(message.getTime());

        MessagesTracker.isConnectionFree = true;
        return result;
    }

    public static boolean editMessage(Message message)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        boolean result = serverAPI.editMessage(message.getText(), message.getTime());
        MessagesTracker.isConnectionFree = true;

        return result;

    }

    public static boolean isUserAccountActive(String email)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        boolean result = serverAPI.isUserAccountActive(email);
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static void deactivateAccount(String username)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        serverAPI.deactivateAccount(username);
        MessagesTracker.isConnectionFree = true;
    }

    public static String getPasswordByUsername(String username)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed) ;

        String password = serverAPI.getPasswordByUsername(username);
        MessagesTracker.isConnectionFree = true;

        return password;
    }

    public static boolean updateChatImageInDB(String photoName, int idChat)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        boolean result = serverAPI.updateChatPhotoName(photoName, idChat);
        MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static ArrayList<String> getCreatorAndModeratorsOfChat(int idChat)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        ArrayList<String>moderators = serverAPI.getCreatorAndModeratorsOfChat(idChat);
        MessagesTracker.isConnectionFree = true;
        return moderators;
    }

    public static void deleteUserFromChat(String username, int idChat)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        serverAPI.deleteUserFromChat(username, idChat);
        MessagesTracker.isConnectionFree = true;
    }
    public static boolean changeRoleOfUserInChat(String username, int idChat, int role)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        boolean result = serverAPI.changeRoleOfUserInChat(username, idChat, role);
        MessagesTracker.isConnectionFree = true;
        return result;
    }

    public static boolean changeNameOfChat(int chatID, String newName)
    {
        MessagesTracker.isConnectionFree = false;
        while (!MessagesTracker.isConClosed);

        boolean result = serverAPI.changeNameOfChat(chatID, newName);
        MessagesTracker.isConnectionFree = true;
        return result;
    }

    public static ArrayList<ChatInfo> getNewChats(int chatWithHighestID)
    {
        //MessagesTracker.isConnectionFree = false;
        //while (!MessagesTracker.isConClosed) ;
        ArrayList<ChatInfo> newChats = serverAPI.getNewChats(chatWithHighestID, Main.user.getUsername());
        //MessagesTracker.isConnectionFree = true;

        return newChats;
    }

    public static Object[] checkRequestedKeys(String username)
    {
        //MessagesTracker.isConnectionFree = false;
        //while (!MessagesTracker.isConClosed);

        Object[]result = serverAPI.checkRequestedKeys(username);
        //MessagesTracker.isConnectionFree = true;

        return result;
    }

    public static Object[] checkPhotoUpdate(int idChat, String currentPhotoName)
    {
        Object[] result = serverAPI.checkPhotoUpdate(idChat, currentPhotoName);
        return result;
    }

    public static String checkNewChatName(int chatIndex, String currentChatName)
    {
        String newName = serverAPI.checkNewChatName(chatIndex, currentChatName);
        return newName;
    }

    public static ArrayList<Message> getNewMessages(long idOfLastMessage)
    {
        return serverAPI.getNewMessages(idOfLastMessage, Main.user.getUsername());
    }

    public static int getCountOfBlackListIamIn()
    {
        return serverAPI.getCountOfBlackListIamIn(Main.user.getUsername());
    }

    public static int getActualCountOfusersInChat(int idChat)
    {
        return serverAPI.getActualAmountOfUsersInChat(idChat);
    }


    public static boolean connectTracker()
    {
        return serverAPI.connectTracker();
    }

    public static boolean disconnectTracker()
    {
        return serverAPI.disconnectTracker();
    }


    //public void trackNewMessages(ListView<Node> chatChoser, )
}


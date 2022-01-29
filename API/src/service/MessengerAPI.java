package service;

import Data.ChatInfo;
import Data.Message;
import Roles.User;

import java.util.ArrayList;

public interface MessengerAPI
{
    boolean registerUser(String username, String name, String email, String password);
    String checkIfUserExists(String email);
    boolean isUserExists(String username);
    ArrayList<ChatInfo> getChats(String username);
    ArrayList<Message> getMessages(int chatIndex);
    boolean sendMessage(Message message);
    User getUserByEmail(String email);
    int addNewChat(ChatInfo chatToAdd);
    boolean addUsersToChat(ArrayList<String> users, int chatIndex);
    ArrayList<User> getAllUsersInChat(int chatIndex);
    User getUserByUsername(String username);
    byte[] getKeyForChat(int chatIndex, String mainUsername);
    boolean transferKey(byte[]key, String transferTo, int chatIndex);
    boolean requestKey(int chatIndex, ArrayList<String>users, String mainUsername);
    String getPhotoNameOfChat(int chatIndex);
    String getUserPhotoName(String username);
    void updateProfilePhoto(String username, String photoName);
    boolean addUserToBlacklist(String usernameToBlock, String mainUsername);
    ArrayList<String> getBlackList(String username);
    boolean removeUserFromBlacklist(String usernameToRemove, String mainUsername);
    ArrayList<String> getUsersWhereIamInBlacklist(String username);
    boolean deleteMessage(long idMessage);
    boolean editMessage(String newText, long idMessage);
    boolean isUserAccountActive(String email);
    void deactivateAccount(String username);
    String getPasswordByUsername(String username);
    boolean updateChatPhotoName(String photoname, int idChat);
    ArrayList<String> getCreatorAndModeratorsOfChat(int idChat);
    void deleteUserFromChat(String username, int idChat);
    boolean changeRoleOfUserInChat(String username, int idChat, int role);
    boolean changeNameOfChat(int chatID, String newName);

    String checkNewChatName(int chatIndex, String currentChatName);
    ArrayList<ChatInfo> getNewChats(int chatWithHighestID, String mainUsername);
    Object[] checkRequestedKeys(String username);
    ArrayList<Message> getNewMessages(long idmessage, String username);
    int getCountOfBlackListIamIn(String mainUsername);
    Object[] checkPhotoUpdate(int idChat, String currentPhotoName);
    int getActualAmountOfUsersInChat(int idChat);
    boolean connectTracker();
    boolean disconnectTracker();
}

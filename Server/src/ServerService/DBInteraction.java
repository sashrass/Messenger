package ServerService;

import Data.ChatInfo;
import Data.Message;
import Roles.User;
import com.caucho.hessian.server.HessianServlet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.MessengerAPI;

import java.sql.*;
import java.util.ArrayList;

public class DBInteraction extends HessianServlet implements MessengerAPI
{

    private static String urlDB = "jdbc:postgresql://hattie.db.elephantsql.com:5432/lwlcihpm";
    private static String userDB = "lwlcihpm";
    private static String passwordDB = "iY50AxqQnrgVDtRrqNhvj_DqrUrfpCeA";

    //Connection con;

    private static Connection con = null;

    static
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        try
        {
            con = DriverManager.getConnection(urlDB, userDB, passwordDB);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean registerUser(String username, String name, String email, String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

        String query = "INSERT INTO users(username, name, email, password, photo) VALUES(?, ?, ?, ?, ?)";

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB,userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, username);
            pst.setString(2, name);
            pst.setString(3, email);
            pst.setString(4, passwordEncoder.encode(password));
            pst.setString(5, "");

            pst.executeUpdate();
            pst.close();
            con.close();
            return true;
        } catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public String checkIfUserExists(String email)
    {
        //BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        String query = "SELECT password FROM users\n" +
                "WHERE email = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
             {
             PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, email);
            ResultSet rs;
            //pst.executeUpdate();
            rs = pst.executeQuery();
            String str;
            rs.next();
            Object obj = (Object) rs.getObject(1);

            if (obj instanceof String)
            {
                str = (String) obj;
                //System.out.println(str);
                pst.close();
                con.close();
                return str;
            }

        } catch (SQLException ex)
        {
            ex.printStackTrace();
            return "";
        }
        return "";
    }

    @Override
    public boolean isUserExists(String username)
    {
        String query = "SELECT username FROM users\n" +
                "WHERE username = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);

            ResultSet rs;

            rs = pst.executeQuery();

            if (rs.next())
            {
                String data = rs.getString("username").trim();
                System.out.println(rs.getString("username").length());
                System.out.println(username.length());
                if (data.equals(username))
                {
                    //System.out.println(rs.getString("username") + ":" + username);
                    pst.close();
                    con.close();

                    return true;
                } else
                {
                    //System.out.println("No");
                    pst.close();
                    con.close();

                    return false;
                }
            }


        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<ChatInfo> getChats(String username)
    {
        ArrayList<ChatInfo> chats = new ArrayList<ChatInfo>();
        String query = "SELECT DISTINCT chatuser.username, chatname, chatuser.idchat, NumberOfUsers.Numbers, MaxNumb.LastMess\n" +
                "\n" +
                "FROM users, messages, chatuser, chats \tINNER JOIN (SELECT chatuser.idchat AS GG, COUNT(*) AS Numbers\n" +
                "\t\t\t\t\t\t\t\t\t\t\tFROM chatuser, chats\n" +
                "\t\t\t\t\t\t\t\t\t\t\tWHERE chats.idchat = chatuser.idchat\n" +
                "\t\t\t\t\t\t\t\t\t\t\tGROUP BY chatuser.idchat)AS NumberOfUsers ON chats.idchat = GG\n" +
                "\t\t\t\t\t\t\t\t\t\tINNER JOIN (SELECT  idchat, MAX(idmessage) AS LastMess \n" +
                "\t\t\t\t\t\t\t\t\t\t\tFROM messages\n" +
                "\t\t\t\t\t\t\t\t\t\t\tGROUP BY idchat)  AS  MaxNumb ON chats.idchat = MaxNumb.idchat\n" +
                "WHERE users.username = chatuser.username AND chats.idchat = chatuser.idchat AND  users.username = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }


        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);

            ResultSet rs;
            //pst.executeUpdate();
            rs = pst.executeQuery();

            while (rs.next())
            {
                String chatName = rs.getString("chatname");
                int amountOfUsersInChat = rs.getInt("numbers");
                long timeOfLastMessage = rs.getLong("lastmess");
                int index = rs.getInt("idchat");

                chats.add(new ChatInfo(chatName, timeOfLastMessage, amountOfUsersInChat, index));
            }

            pst.close();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return chats;
    }

    @Override
    public ArrayList<Message> getMessages(int chatIndex)
    {
        String query = "SELECT * FROM messages WHERE messages.idchat =" + "?::integer";

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            ArrayList<Message> messages = new ArrayList<Message>();

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, Integer.toString(chatIndex));
            ResultSet rs = pst.executeQuery();

            while (rs.next())
            {
                String text = rs.getString("textmessage");
                long time = rs.getLong("idmessage");
                String username = rs.getString("username");
                String attachments = rs.getString("attachments");
                //System.out.println(attachments);
                messages.add(new Message(text, attachments, username, username, time, chatIndex));
            }

            pst.close();
            con.close();

            return messages;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean sendMessage(Message message)
    {
        String query = "INSERT INTO messages \n" +
                "VALUES (?, ?, ?, ? ,?)";

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setLong(1, message.getTime());
            pst.setString(2, message.getText());
            pst.setString(3, message.getAttachments());
            pst.setInt(4, message.getMessage_chat_id());
            pst.setString(5, message.getSenderUsername());

            pst.executeUpdate();
            pst.close();
            con.close();

            return true;

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();

            return false;
        }
    }

    @Override
    public User getUserByEmail(String email)
    {
        String query = "SELECT users.username, users.name, users.photo FROM users\n" +
                "WHERE users.email = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next())
            {
                String username = rs.getString("username");
                String name = rs.getString("name");
                String photoName = rs.getString("photo");
                pst.close();
                con.close();

                return new User(username,
                        name, photoName);
            }

            pst.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return new User("", "", "");
    }

    @Override
    public int addNewChat(ChatInfo chatToAdd)
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        Connection con;
        try
        {
            con = DriverManager.getConnection(urlDB, userDB, passwordDB);
        } catch (SQLException e)
        {
            e.printStackTrace();
            return -1;
        }

        for (int i = 0; i < 3; i++)
        {
            String getIDforNewChatQuery = "select chats.idchat from chats order by chats.idchat desc limit 1";
            String query = "INSERT INTO chats\n" +
                    "VALUES (?,?,?)";

            try
            {
                PreparedStatement pst = con.prepareStatement(getIDforNewChatQuery);
                ResultSet rs = pst.executeQuery();
                int chatIndex = 1;
                if (rs.next())
                    chatIndex = rs.getInt("idchat") + 1;

                pst = con.prepareStatement(query);
                pst.setInt(1, chatIndex);
                pst.setString(2, chatToAdd.getName());
                pst.setString(3, chatToAdd.getChatPhotoName());
                pst.executeUpdate();

                pst.close();

                chatToAdd.setChatIndex(chatIndex);
                con.close();

                return chatIndex;
            } catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }
        }

        try
        {
            con.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public boolean addUsersToChat(ArrayList<String> users, int chatIndex)
    {
        String query = "INSERT INTO chatuser\n" +
                "VALUES (?,?)";

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);

            for (int i = 0; i < users.size(); i++)
            {
                pst.setString(1, users.get(i));
                pst.setInt(2, chatIndex);
                pst.executeUpdate();
            }

            pst.close();
            con.close();

            return true;

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();

            return false;
        }
    }

    @Override
    public ArrayList<User> getAllUsersInChat(int chatIndex)
    {
        String allUsersInChatQuery = "SELECT username, name, photo FROM users\n" +
                "WHERE username = ANY\n" +
                "(SELECT username FROM chatuser\n" +
                "\t  WHERE idchat = " + '?' + ")";

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }


        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(allUsersInChatQuery);
            pst.setInt(1, chatIndex);
            ResultSet rs = pst.executeQuery();
            ArrayList<User> usersInChat = new ArrayList<User>();

            while (rs.next())
            {
                String usernameOfUserInChat = rs.getString("username");
                String nameOfUserInChat = rs.getString("name");
                String photoName = rs.getString("photo");

                usersInChat.add(new User(usernameOfUserInChat, nameOfUserInChat,
                        photoName));
                System.out.println(usernameOfUserInChat + " : " + nameOfUserInChat);
            }

            pst.close();
            con.close();

            return usersInChat;

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();

            return null;
        }
    }

    @Override
    public User getUserByUsername(String username)
    {
        String query = "SELECT users.username, users.name, users.photo FROM users\n" +
                "WHERE users.username = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next())
            {
                String name = rs.getString("name");
                String photoName = rs.getString("photo");
                pst.close();
                con.close();

                return new User(username,
                        name, photoName);
            }

            pst.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return new User("", "", "");
    }

    @Override
    public byte[] getKeyForChat(int chatIndex, String mainUsername)
    {
        String getKeyQuery = "SELECT key FROM transfer_keys\n" +
                "WHERE username = " + '?' + " AND chat_id = " + '?';

        String deleteQuery = "DELETE FROM transfer_keys\n" +
                "WHERE username = " + '?' + " AND chat_id = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }


        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(getKeyQuery);
            pst.setString(1, mainUsername);
            pst.setInt(2, chatIndex);
            ResultSet rs = pst.executeQuery();

            if (rs.next())
            {
                Integer[] key = (Integer[]) rs.getArray("key").getArray();
                byte[] byteKey = new byte[16];

                for (int i = 0; i < 16; i++)
                    byteKey[i] = key[i].byteValue();


                pst = con.prepareStatement(deleteQuery);
                pst.setString(1, mainUsername);
                pst.setInt(2, chatIndex);

                pst.executeUpdate();

                pst.close();
                con.close();

                return byteKey;

            } else
            {
                con.close();

                return null;
            }


        } catch (SQLException throwables)
        {
            throwables.printStackTrace();

            return null;
        }

    }

    @Override
    public boolean transferKey(byte[]key, String transferTo, int chatIndex)
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        Byte[] byteObjects = new Byte[16];

        for (int i = 0; i < 16; i++)
        {
            byteObjects[i] = key[i];
        }

        String query = "INSERT INTO transfer_keys\n" +
                "VALUES (?,?,?)";
        String deleteQuery = "DELETE FROM request_key\n" +
                "WHERE username = " + '?' + " AND chat_id = " + '?';

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, transferTo);
            pst.setInt(2, chatIndex);
            Array array = con.createArrayOf("INTEGER", byteObjects);
            pst.setArray(3, array);

            pst.executeUpdate();

            pst = con.prepareStatement(deleteQuery);
            pst.setString(1, transferTo);
            pst.setInt(2, chatIndex);

            pst.executeUpdate();

            pst.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();

            return false;
        }

        return true;
    }

    @Override
    public boolean requestKey(int chatIndex, ArrayList<String>users, String mainUsername)
    {
        String query = "INSERT INTO request_key\n" +
                "VALUES (?,?,?)";

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            for (int i = 0; i < users.size(); i++)
            {
                pst.setString(1, mainUsername);
                pst.setInt(2, chatIndex);
                pst.setString(3, users.get(i));

                pst.executeUpdate();
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();

            return false;
        }

        return true;
    }

    @Override
    public String getPhotoNameOfChat(int chatIndex)
    {
        String query = "SELECT photo FROM chats\n" +
                "WHERE idchat = " + '?';
        String photoName = "";

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, chatIndex);

            ResultSet rs = pst.executeQuery();

            if (rs.next())
            {
                photoName = rs.getString("photo");
            }

            pst.close();
            con.close();

            return photoName;

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return "error";
    }

    @Override
    public String getUserPhotoName(String username)
    {
        String query = "SELECT photo FROM users\n" +
                "WHERE username = " + '?';
        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }


        String photoName = "";

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);

            ResultSet rs = pst.executeQuery();

            if (rs.next())
                photoName = rs.getString("photo");

            pst.close();
            con.close();

            return photoName;
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return "error";
    }

    @Override
    public void updateProfilePhoto(String username, String photoName)
    {
        String query = "UPDATE users\n" +
                "SET photo = " + '?' + '\n' +
                "WHERE username = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, photoName);
            pst.setString(2, username);

            pst.executeUpdate();

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean addUserToBlacklist(String usernameToBlock, String mainUsername)
    {
        String query = "INSERT INTO black_list\n" +
                "VALUES (?,?)";

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, mainUsername);
            pst.setString(2, usernameToBlock);

            pst.executeUpdate();
            pst.close();
            con.close();

            return true;
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return false;
    }

    @Override
    public ArrayList<String> getBlackList(String username)
    {
        String query = "SELECT * FROM black_list\n" +
                "WHERE username = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        ArrayList<String> blackList = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);

            ResultSet rs = pst.executeQuery();

            while (rs.next())
            {
                blackList.add(rs.getString("locked"));
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return blackList;
    }

    @Override
    public boolean removeUserFromBlacklist(String usernameToRemove, String mainUsername)
    {
        String query = "DELETE FROM black_list\n" +
                "WHERE username = " + '?' + " AND locked = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, mainUsername);
            pst.setString(2, usernameToRemove);

            pst.executeUpdate();
            con.close();

            return true;
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return false;
    }

    @Override
    public ArrayList<String> getUsersWhereIamInBlacklist(String username)
    {
        String query = "SELECT username FROM black_list\n" +
                "WHERE locked = " + '?';

        ArrayList<String>usernames = new ArrayList<>();

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);

            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                usernames.add(rs.getString("username"));
            }

            pst.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

       return usernames;
    }

    @Override
    public boolean deleteMessage(long idMessage)
    {
        String query = "DELETE FROM messages\n" +
                "WHERE idmessage = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB,userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setLong(1, idMessage);

            pst.executeUpdate();
            con.close();

            return true;
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean editMessage(String newText, long idMessage)
    {
        String query = "UPDATE messages\n" +
                "SET textmessage = " + '?' + "\n" +
                "WHERE idmessage = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, newText);
            pst.setLong(2, idMessage);

            pst.executeUpdate();

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();

            return false;
        }

        return true;
    }

    @Override
    public boolean isUserAccountActive(String email)
    {
        String query = "SELECT is_active FROM users\n" +
                "WHERE email = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, email);

            ResultSet rs = pst.executeQuery();

            if (rs.next())
            {
                if (!rs.getBoolean("is_active"))
                {
                    con.close();
                    return false;
                }
            }
            pst.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void deactivateAccount(String username)
    {
        String query = "UPDATE users\n" +
                "SET is_active = false" + '\n' +
                "WHERE username = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);

            pst.executeUpdate();

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getPasswordByUsername(String username)
    {
        String password = "";
        String query = "SELECT password FROM users\n" +
                "WHERE username = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);

            ResultSet rs = pst.executeQuery();

            if (rs.next())
            {
                password = rs.getString("password");
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return password;
    }

    @Override
    public boolean updateChatPhotoName(String photoname, int idChat)
    {
        String query = "UPDATE chats\n"+
                "SET photo  = " + '?'+"\n"+
                "WHERE idchat = " + '?';

        try(Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, photoname);
            pst.setInt(2, idChat);

            pst.executeUpdate();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<String> getCreatorAndModeratorsOfChat(int idChat)
    {
        ArrayList<String> moderators = new ArrayList<>();

        String query = "SELECT username, role from chatuser\n"+
                "WHERE (role = 1 OR role = 2) AND idchat = " +'?';

        try(Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, idChat);

            ResultSet rs = pst.executeQuery();

            while (rs.next())
            {
                int role = rs.getInt("role");
                String username = rs.getString("username");

                if (role == 2)
                    moderators.add(0, username);
                else
                    moderators.add(username);
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return moderators;
    }

    @Override
    public void deleteUserFromChat(String username, int idChat)
    {
        String query = "DELETE FROM chatuser\n"+
                "WHERE idchat = " + '?' + " AND username = " + '?';

        try(Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, idChat);
            pst.setString(2, username);

            pst.executeUpdate();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean changeRoleOfUserInChat(String username, int idChat, int role)
    {
        String query = "UPDATE chatuser\n"+
                "SET role = " + '?'+
                "WHERE idchat = " + '?' + " AND username = " + '?';

        try(Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, role);
            pst.setInt(2, idChat);
            pst.setString(3, username);
            pst.executeUpdate();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean changeNameOfChat(int chatID, String newName)
    {
        String query = "UPDATE chats\n"+
                "SET chatname = " + '?'+
                "WHERE idchat = " + '?';

        try(Connection con = DriverManager.getConnection(urlDB, userDB,passwordDB))
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, newName);
            pst.setInt(2, chatID);

            pst.executeUpdate();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<ChatInfo> getNewChats(int chatWithhighestID, String mainUsername)
    {
        String checkNewChatsQuery = "SELECT DISTINCT chatuser.username, chatname, chatuser.idchat, NumberOfUsers.Numbers, MaxNumb.LastMess\n" +
                "\n" +
                "FROM users, messages, chatuser, chats \tINNER JOIN (SELECT chatuser.idchat AS GG, COUNT(*) AS Numbers\n" +
                "\t\t\t\t\t\t\t\t\t\t\tFROM chatuser, chats\n" +
                "\t\t\t\t\t\t\t\t\t\t\tWHERE chats.idchat = chatuser.idchat\n" +
                "\t\t\t\t\t\t\t\t\t\t\tGROUP BY chatuser.idchat)AS NumberOfUsers ON chats.idchat = GG\n" +
                "\t\t\t\t\t\t\t\t\t\tINNER JOIN (SELECT  idchat, MAX(idmessage) AS LastMess \n" +
                "\t\t\t\t\t\t\t\t\t\t\tFROM messages\n" +
                "\t\t\t\t\t\t\t\t\t\t\tGROUP BY idchat)  AS  MaxNumb ON chats.idchat = MaxNumb.idchat\n" +
                "WHERE users.username = chatuser.username AND chats.idchat = chatuser.idchat AND chats.idchat >"+ chatWithhighestID +
                "  AND  users.username = " + '?';
        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }


        ArrayList<ChatInfo> newChats = new ArrayList<>();

        PreparedStatement pst = null;
        try
        {
            pst = con.prepareStatement(checkNewChatsQuery);
            pst.setString(1, mainUsername);

            ResultSet rs = pst.executeQuery();

            while (rs.next())
            {
                String chatName = rs.getString("chatname");
                long lastmessageOfNewChat = rs.getLong("lastmess");
                int amountOfUsers = rs.getInt("numbers");
                int chatID = rs.getInt("idchat");

                newChats.add(new ChatInfo(chatName, lastmessageOfNewChat, amountOfUsers, chatID));
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        try
        {
            pst.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return newChats;
    }

    @Override
    public Object[] checkRequestedKeys(String username)
    {
        Object [] intAndString = null;
        String query = "SELECT * FROM request_key\n"+
                "WHERE receive_from = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try
        {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next())
            {
                intAndString = new Object[2];
                intAndString[0] = rs.getInt("chat_id");
                intAndString[1] = rs.getString("username");

                pst.close();
                return intAndString;
            }

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return intAndString;
    }

    @Override
    public ArrayList<Message> getNewMessages(long idmessage, String username)
    {
        ArrayList<Message> newMessages = new ArrayList<>();

        String checkLatestMessageQuery = "SELECT idmessage, textmessage,attachments, idchat, messages.username, users.name from messages, users\n" +
                "WHERE idmessage >" + '?' + "and idchat = ANY\n" +
                "(SELECT idchat from chatuser\n" +
                "WHERE username =" + '?' + ") AND users.username = messages.username";

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try (PreparedStatement pst = con.prepareStatement(checkLatestMessageQuery))
        {
            pst.setLong(1, idmessage);
            pst.setString(2, username);
            ResultSet rs =  pst.executeQuery();

            while (rs.next())
            {
                System.out.println("new mess detected");
                String textmessage = rs.getString("textmessage");
                String attachments = rs.getString("attachments");
                String senderUsername = rs.getString("username");
                String senderName = rs.getString("name");
                long messageID = rs.getLong("idmessage");
                int idchat = rs.getInt("idchat");

                newMessages.add(new Message(textmessage, attachments, senderUsername, senderName,
                        messageID, idchat));
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return newMessages;
    }

    @Override
    public int getCountOfBlackListIamIn(String mainUsername)
    {
        int count = -1;
        String queryForCount = "SELECT COUNT(*) AS amount from black_list\n"
                + "WHERE black_list.locked = " + '?';

        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        try(PreparedStatement pst = con.prepareStatement(queryForCount))
        {
            pst.setString(1, mainUsername);
            ResultSet rs = pst.executeQuery();

            if (rs.next())
                count = rs.getInt("amount");


        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return count;
    }

    @Override
    public Object[] checkPhotoUpdate(int idChat, String currentPhotoName)
    {
        Object [] newPhotoName = null;

        String query  ="SELECT photo\n"+
                "FROM chats\n"+
                "WHERE idChat = " + '?';

        try(PreparedStatement pst = con.prepareStatement(query))
        {
            pst.setInt(1, idChat);
            ResultSet rs = pst.executeQuery();

            if (rs.next())
            {
                String updatedPhotoName = rs.getString("photo");
                if (!updatedPhotoName.equals(currentPhotoName))
                {
                    newPhotoName = new Object[2];
                    newPhotoName[0] = idChat;
                    newPhotoName[1] = updatedPhotoName;
                }
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return newPhotoName;
    }

    @Override
    public int getActualAmountOfUsersInChat(int idChat)
    {
        int amount = 0;
        String query = "SELECT COUNT(*) as amount from chatuser\n"+
                "WHERE idchat = " + '?';

        try (PreparedStatement pst = con.prepareStatement(query))
        {
            pst.setInt(1, idChat);
            ResultSet rs = pst.executeQuery();

            if (rs.next())
                amount = rs.getInt("amount");

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return amount;
    }

    public String checkNewChatName(int chatIndex, String currentChatName)
    {
        String newName = "";
        String query = "SELECT chatname FROM chats\n"+
        "WHERE idchat = " + '?';

        try(PreparedStatement pst = con.prepareStatement(query))
        {

            pst.setInt(1, chatIndex);
            ResultSet rs = pst.executeQuery();

            if (rs.next())
            {
                if (!currentChatName.equals(rs.getString("chatname")))
                {
                    newName = rs.getString("chatname");
                }
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return newName;
    }

    @Override
    public boolean connectTracker()
    {
        try
        {
            con = DriverManager.getConnection(urlDB, userDB, passwordDB);
        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        System.out.println("con connected");
        return true;
    }

    @Override
    public boolean disconnectTracker()
    {
        try
        {
            con.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        System.out.println("con disconnected");
        return true;
    }


}

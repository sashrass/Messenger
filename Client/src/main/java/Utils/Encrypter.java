package Utils;

import Data.ChatInfo;
import Data.Message;
import com.messenger.minimessenger.Main;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

public class Encrypter
{

    private static Cipher cipher;
    private static Cipher decipher;
    static
    {
        try
        {
            System.out.println("in static -> try");
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
    }


    private static SecretKeySpec secretKeySpec;

    /*
    private Encrypter()
    {
        System.out.println("In encrypter constr");
        try
        {
            cipher =
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
    }
*/
    public static void createKeyForNewChat(ChatInfo chat)
    {
        try
        {
            FileWriter fw =null;
            File file= new File("C:\\serviceMM\\" + Main.user.getUsername() + "\\secretkeys.txt");

            if (file.exists())
                fw = new FileWriter("C:\\serviceMM\\" + Main.user.getUsername() + "\\secretkeys.txt", true);
            else
            {
                file.getParentFile().mkdirs();
                fw = new FileWriter("C:\\serviceMM\\" + Main.user.getUsername() + "\\secretkeys.txt");
            }

            PrintWriter writer = new PrintWriter(fw);

            String key = "";
            byte [] keyInBytes = new byte[16];
            new Random().nextBytes(keyInBytes);
            chat.setKey(keyInBytes);

            for (int i=0; i < keyInBytes.length;i++)
            {
                key+= keyInBytes[i] + " ";
            }
            System.out.println(key);

            writer.println(chat.getChatIndex());
            writer.println(key);
            writer.close();
            fw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void encryptMessage(Message messageToEncrypt, byte [] encryptionKey)
    {
        try
        {
            secretKeySpec = new SecretKeySpec(encryptionKey, "AES");

            byte[] stringByte = messageToEncrypt.getText().getBytes();
            byte[] encryptedByte;

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);

            //String returnString = new String(encryptedByte, "ISO-8859-1"); //ISO-8859-1

            messageToEncrypt.setText(Base64.getEncoder().encodeToString(encryptedByte));
        } catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        } catch (BadPaddingException e)
        {
            e.printStackTrace();
        } catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
    }

    public static void decryptMessage(Message messageToDecrypt, byte [] encryptionKey)
    {
        /*
        try
        {
            byte[]encryptionKey = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};
            secretKeySpec = new SecretKeySpec(encryptionKey, "AES");

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            String strToEncrypt = "Привет";
            byte[]strBytes = strToEncrypt.getBytes();
            byte[]encrByte;
            encrByte = cipher.doFinal(strBytes);
            String encodedString1 = new String(encrByte, "ISO-8859-1");
            System.out.println(encodedString1);
            String encodedStringBase64 = Base64.getEncoder().encodeToString(encrByte);
            System.out.println("Base64" + encodedStringBase64);

            //byte[]encryptionKey = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};

            // = "MV3vv71177+9bRMqbe+/vc+E77+9Se+/ve+/vQ==";




            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] strToDecrypt = Base64.getDecoder().decode(encodedStringBase64);
            byte[] cipherText = decipher.doFinal(strToDecrypt);
            String finalString = new String(cipherText);
            System.out.println(finalString);
        }*/
        try
        {
//            for (int i=0; i < 16;i++)
//                System.out.println(encryptionKey[i]);
            secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] EncryptedByte = Base64.getDecoder().decode(messageToDecrypt.getText());
            String decryptedString;

            byte[] decryption;

            decryption = decipher.doFinal(EncryptedByte);
            decryptedString = new String(decryption);

            messageToDecrypt.setText(decryptedString);

        } catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        } catch (BadPaddingException e)
        {
            e.printStackTrace();
        } catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
    }


    public static void getKeysOfChats(ArrayList<ChatInfo> chats)
    {
        try
        {
            File file= new File("C:\\serviceMM\\" + Main.user.getUsername() + "\\secretkeys.txt");

            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader("C:\\serviceMM\\" + Main.user.getUsername() + "\\secretkeys.txt"));

            String key;
            byte[] encryptionKey = new byte[16];

            int count=0;
            while ((key = reader.readLine()) != null)
            {
                    for (int i = 0; i < chats.size(); i++)
                        if (key.length() < 11 && (chats.get(i).getChatIndex() == Integer.parseInt(key)))
                        {
                            byte[] readyKey = new byte[16];
                            key = reader.readLine();
                            String[] bytes = key.split(" ");
                            for (int j = 0; j < 16; j++)
                                readyKey[j] = Byte.parseByte(bytes[j]);
                            chats.get(i).setKey(readyKey);

                            break;
                        }
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void writeKeyToFile(ChatInfo chat)
    {
        try
        {
            FileWriter fw = null;
            File file = new File("C:\\serviceMM\\" + Main.user.getUsername() + "\\secretkeys.txt");

            if (file.exists())
                fw = new FileWriter("C:\\serviceMM\\" + Main.user.getUsername() + "\\secretkeys.txt", true);
            else
            {
                file.getParentFile().mkdirs();
                fw = new FileWriter("C:\\serviceMM\\" + Main.user.getUsername() + "\\secretkeys.txt");
            }

            PrintWriter writer = new PrintWriter(fw);

            String key = "";
            for(int i=0; i < 16;i++)
            {
                key+=chat.getKey()[i] + " ";
            }

            writer.println(chat.getChatIndex());
            writer.println(key);
            writer.close();
            fw.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

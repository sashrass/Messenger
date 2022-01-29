package Data;

import javafx.scene.image.ImageView;

import java.io.File;
import java.io.Serializable;

public class Message implements Serializable
{
    private long time;
    private String text;
    private String attachments;
    private ImageView image;
    private String senderUsername;
    private String senderName;
    private int message_chat_id;
    File fileAttachment = null;

    public File getFileAttachment()
    {
        return fileAttachment;
    }

    public void setFileAttachment(File fileAttachment)
    {
        this.fileAttachment = fileAttachment;
    }

    public Message(String text, String attachments, String senderUsername,
                   String senderName, long time, int message_chat_id)
    {
        this.text = text;
        this.attachments = attachments;
        this.senderUsername = senderUsername;
        this.senderName = senderName;
        this.time = time;
        this.message_chat_id = message_chat_id;
    }

    public String getText()
    {
        return text;
    }

    public String getAttachments()
    {
        return attachments;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void setAttachments(String attachments)
    {
        this.attachments = attachments;
    }

    public String getSenderUsername()
    {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername)
    {
        this.senderUsername = senderUsername;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public int getMessage_chat_id()
    {
        return message_chat_id;
    }

    public void setMessage_chat_id(int message_chat_id)
    {
        this.message_chat_id = message_chat_id;
    }

    public ImageView getImage()
    {
        return image;
    }

    public void setImage(ImageView image)
    {
        this.image = image;
    }
}

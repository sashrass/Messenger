package Utils;

import Data.ChatInfo;

import java.util.Comparator;

public class CompareChats implements Comparator<ChatInfo>
{
    @Override
    public int compare(ChatInfo o1, ChatInfo o2)
    {
        return (int) (o2.getTimeOfLastMessage() - o1.getTimeOfLastMessage());
    }

}

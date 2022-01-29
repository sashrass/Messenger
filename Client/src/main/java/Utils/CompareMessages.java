package Utils;

import Data.Message;
import java.util.Comparator;

public class CompareMessages implements Comparator<Message>
{
    @Override
    public int compare(Message o1, Message o2)
    {
        return (int) (o1.getTime() - o2.getTime());
    }
}

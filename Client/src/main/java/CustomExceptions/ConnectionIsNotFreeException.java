package CustomExceptions;

public class ConnectionIsNotFreeException extends Exception
{
    public ConnectionIsNotFreeException(String message)
    {
        super(message);
    }

    @Override
    public void printStackTrace(){super.printStackTrace();}
}

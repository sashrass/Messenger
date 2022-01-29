package CustomExceptions;

public class NoUsersException extends Exception
{
        public NoUsersException(String message)
        {
            super(message);
        }

        @Override
        public void printStackTrace()
        {
            super.printStackTrace();
        }
}

package Intellidrone;

public class InvalidImageException extends Exception
{
    private String error;
    
    public InvalidImageException()
    {
        super();
        error = "Unknown";
    }
    
    public InvalidImageException(String errorMessage)
    {
        super(errorMessage);
        error = errorMessage;
    }
    
    public String getError()
    {
        return(error);
    }
}

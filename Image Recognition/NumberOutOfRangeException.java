
public class NumberOutOfRangeException extends Exception
{
    private String error;
    
    public NumberOutOfRangeException()
    {
        super();
        error = "Unknown";
    }
    
    public NumberOutOfRangeException(String errorMessage)
    {
        super(errorMessage);
        error = errorMessage;
    }
    
    public String getError()
    {
        return(error);
    }
}

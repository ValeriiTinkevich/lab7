package common.exceptions;

public class NotAuthorizedException extends Exception{
    public NotAuthorizedException() {
        super("User is not authorized");
    }
}

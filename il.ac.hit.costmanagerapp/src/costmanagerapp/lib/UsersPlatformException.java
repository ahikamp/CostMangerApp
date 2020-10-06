package costmanagerapp.lib;

public class UsersPlatformException extends Exception {


    public UsersPlatformException() {
        super();
    }

    public UsersPlatformException(String message) {
        super(message);
    }

    public UsersPlatformException(String message, Throwable cause) {
        super(message, cause);
    }
}
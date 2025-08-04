package api.giybat.uz.exp;

public class AppBadException extends Throwable {
    public AppBadException(String usernameAlreadyExists) {
        super(usernameAlreadyExists);
    }
}

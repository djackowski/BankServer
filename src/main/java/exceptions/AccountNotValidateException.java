package exceptions;

public class AccountNotValidateException extends Exception {
    public AccountNotValidateException() {
        super("Account is not correct");
    }
}

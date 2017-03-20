package net.c0f3.labs.database.mongodb.ssh;

/**
 * Created by KostaPC on 2017-03-21.
 *
 */
public class MongoSshException extends Exception {
    public MongoSshException() {
    }

    public MongoSshException(String message) {
        super(message);
    }

    public MongoSshException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongoSshException(Throwable cause) {
        super(cause);
    }

    public MongoSshException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

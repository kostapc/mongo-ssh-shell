package net.c0f3.labs.database.mongodb.ssh;

import net.c0f3.labs.database.mongodb.MongoConnectionWrapper;
import org.junit.Test;

import java.io.File;

/**
 * Created by KostaPC on 2017-03-21.
 *
 */
public class WrappedMongoSshExample {

    @Test
    public void usageExample() throws MongoSshException {
        MongoConnectionWrapper connection = new MongoConnectionWrapper(
                "conf"+ File.separator+"mongo.properties"
        );

        MongoSshShell sshShell = new MongoSshShell(connection);
        String result = sshShell.invoke(
                "user_function_name",
                "param 1","param 2",3,4
        );
        System.out.println("execution result: \n=====================\n"+result+"\n=====================\n");
    }

}

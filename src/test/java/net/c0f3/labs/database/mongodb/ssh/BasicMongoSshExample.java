package net.c0f3.labs.database.mongodb.ssh;

import net.c0f3.labs.database.mongodb.MongoConnectionWrapper;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by KostaPC on 2017-03-21.
 *
 */
public class BasicMongoSshExample {

    @Test
    public static void test(String[] args) throws IOException {

        MongoConnectionWrapper connection = new MongoConnectionWrapper(
                "conf"+ File.separator+"mongo.properties"
        );

        final SSHClient ssh = new SSHClient();
        ssh.loadKnownHosts();

        ssh.connect(connection.getSshHost());
        try {
            //net.c0f3.labs.database.mongodb.ssh.authPublickey(System.getProperty("user.name"));
            ssh.authPassword(connection.getSshLogin(), connection.getSshPassword());
            try (Session session = ssh.startSession()) {
                final Command cmd = session.exec(buildCommand(
                            connection,
                        "user_function_name",
                        "param 1","param 2",3,4
                ));
                System.out.println(IOUtils.readFully(cmd.getInputStream()).toString());
                cmd.join(5, TimeUnit.SECONDS);
                System.out.println("\n** exit status: " + cmd.getExitStatus());
            }
        } finally {
            ssh.disconnect();
        }
    }

    private static String buildCommand(MongoConnectionWrapper connection, String methodName, Object... params) {
        // mongo -u tester -p p4ss test -eval 'db.loadServerScripts(); f_winner("SMS Fortuna","TEST 4","Mon Mar 06 19:27:00 MSK 2017 ","Mon Mar 06 09:27:00 MSK 2017","Mon Mar 06 19:26:00 MSK 2017");'
        String[] quotedParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            quotedParams[i] = "\""+params[i]+"\"";
        }
        String command = String.format(
            "mongo -u %s -p %s %s -eval 'db.loadServerScripts(); %s(%s);'",
            connection.getMongoDBUser(), connection.getMongoDBPassword(), connection.getMongoDBDB(),
            methodName, String.join(",", quotedParams)
        );
        return command;
    }

}

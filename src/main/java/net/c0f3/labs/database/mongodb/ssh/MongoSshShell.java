package net.c0f3.labs.database.mongodb.ssh;

import net.c0f3.labs.database.mongodb.MongoConnectionWrapper;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by KostaPC on 2017-03-21.
 *
 */
public class MongoSshShell {

    private static final Log LOG = LogFactory.getLog(MongoSshShell.class);

    private final MongoConnectionWrapper connection;

    public MongoSshShell(MongoConnectionWrapper connectionWrapper) {
        this.connection = connectionWrapper;
    }

    public String invoke(String methodName, Object... params) throws MongoSshException {
        final SSHClient ssh = new SSHClient();

        try {
            ssh.loadKnownHosts();
            ssh.connect(connection.getSshHost());
            //ssh.authPublickey(System.getProperty("user.name"));
            ssh.authPassword(connection.getSshLogin(), connection.getSshPassword());

            try (Session session = ssh.startSession()) {
                String commandString = buildCommand(
                        connection, methodName, params
                );
                if (LOG.isDebugEnabled()) {
                    LOG.debug("going to execute command: " + commandString);
                }
                final Session.Command cmd = session.exec(commandString);
                String executionResult = IOUtils.readFully(cmd.getInputStream()).toString();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("execution result: " + executionResult);
                }
                cmd.join(5, TimeUnit.SECONDS);
                return executionResult;
            } catch (IOException e) {
                throw new MongoSshException(e);
            } finally {
                ssh.disconnect();
            }

        } catch (IOException e1) {
            throw new MongoSshException(e1);
        }
    }

    private static String buildCommand(MongoConnectionWrapper connection, String methodName, Object... params) {
        // mongo -u login -p password host/database_name -eval 'db.loadServerScripts(); method_name("param1", "param2", 3);'
        String[] quotedParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            if(params[i]==null) {
                quotedParams[i] = null;
                continue;
            }
            if (params[i] instanceof Number) {
                quotedParams[i] = params[i].toString();
                continue;
            }
            quotedParams[i] = "\"" + params[i].toString() + "\"";
        }
        return String.format(
                "mongo -u %s -p %s %s/%s -eval 'db.loadServerScripts(); %s(%s);'",
                connection.getMongoDBUser(), connection.getMongoDBPassword(),
                connection.getMongoDBUrl(),  connection.getMongoDBDB(),
                methodName, String.join(",", quotedParams)
        );
    }
}

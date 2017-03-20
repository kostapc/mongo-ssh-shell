package net.c0f3.labs.database.mongodb;

import com.mongodb.Mongo;
import net.c0f3.labs.database.mongodb.ssh.MongoSshException;
import net.c0f3.labs.database.mongodb.ssh.MongoSshShell;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import javax.annotation.Resource;

/**
 * Created by KostaPC on 2017-03-21.
 *
 */
public class MongoTemplateEx extends MongoTemplate {

    private static final Log LOG = LogFactory.getLog(MongoTemplateEx.class);

    @Resource
    MongoSshShell mongoSshShell;

    public MongoTemplateEx(Mongo mongo, String databaseName) {
        super(mongo, databaseName);
    }

    public MongoTemplateEx(Mongo mongo, String databaseName, UserCredentials userCredentials) {
        super(mongo, databaseName, userCredentials);
    }

    public MongoTemplateEx(MongoDbFactory mongoDbFactory) {
        super(mongoDbFactory);
    }

    public MongoTemplateEx(MongoDbFactory mongoDbFactory, MongoConverter mongoConverter) {
        super(mongoDbFactory, mongoConverter);
    }

    public String callFunction(String functionName, Object... params) {
        try {
            return mongoSshShell.invoke(functionName, params);
        } catch (MongoSshException e) {
            LOG.error(e);
            return null;
        }
    }
}

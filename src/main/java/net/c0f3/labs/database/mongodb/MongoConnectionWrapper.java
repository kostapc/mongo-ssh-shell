package net.c0f3.labs.database.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;

/**
 * 06.03.2017
 * KostaPC
 */
public class MongoConnectionWrapper {

    public static final String MONGO_DB_URL = "default.mongodb.uri";
    public static final String MONGO_DB_DB = "default.mongodb.database";
    public static final String MONGO_DB_USER = "default.mongodb.user";
    public static final String MONGO_DB_PASSWORD = "default.mongodb.password";

    public static final String MONGO_SSH_HOST = "default.mongodb.ssh.host";
    public static final String MONGO_SSH_LOGIN = "default.mongodb.ssh.login";
    public static final String MONGO_SSH_PASSWORD = "default.mongodb.ssh.password";


    private String sshHost;
    private String sshLogin;
    private String sshPassword;

    private String mongoDBUrl;
    private String mongoDBDB;
    private String mongoDBUser;
    private String mongoDBPassword;

    public MongoConnectionWrapper(String propertiesFile) {
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(Paths.get(propertiesFile)));
            init(properties);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public MongoConnectionWrapper() {
        init(System.getProperties());
    }

    private void init(Properties properties) {
        this.mongoDBUrl = properties.getProperty(MONGO_DB_URL);
        this.mongoDBDB = properties.getProperty(MONGO_DB_DB);
        this.mongoDBUser = properties.getProperty(MONGO_DB_USER);
        this.mongoDBPassword = properties.getProperty(MONGO_DB_PASSWORD);
        System.out.println(String.format(
                "URL: %s; DB: %s, USER: %s; PASS: %s",
                mongoDBUrl, mongoDBDB, mongoDBUser, mongoDBPassword
        ));
        this.sshHost = properties.getProperty(MONGO_SSH_HOST);
        this.sshLogin = properties.getProperty(MONGO_SSH_LOGIN);
        this.sshPassword = properties.getProperty(MONGO_SSH_PASSWORD);
        System.out.println(String.format(
                "SSH connection: ssh://%s:%s@%s",
                sshLogin, sshPassword, sshHost
        ));
    }

    public String getMongoDBUrl() {
        return mongoDBUrl;
    }

    public String getMongoDBDB() {
        return mongoDBDB;
    }

    public String getMongoDBUser() {
        return mongoDBUser;
    }

    public String getMongoDBPassword() {
        return mongoDBPassword;
    }

    public String getSshHost() {
        return sshHost;
    }

    public String getSshLogin() {
        return sshLogin;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public String getConnectionString() {
        final String user = getMongoDBUser();
        final String url;
        if (user != null) {
            if(getMongoDBUser().length()==0 && getMongoDBPassword().length()==0) {
                url = String.format(
                        "mongodb://%s/%s",
                        getMongoDBUrl(),
                        getMongoDBDB()
                );
            } else {
                url = String.format(
                        "mongodb://%s:%s@%s/%s",
                        getMongoDBUser(),
                        getMongoDBPassword(),
                        getMongoDBUrl(),
                        getMongoDBDB()
                );
            }
        } else {
            url = String.format("mongodb://%s/%s",
                    getMongoDBUrl(),
                    getMongoDBDB()
            );
        }
        return url;
    }

    private MongoClient client = null;

    public MongoDatabase connect() throws UnknownHostException {
        MongoDatabase db;
        try {
            ServerAddress serverAddress = new ServerAddress(getMongoDBUrl());
            MongoCredential credential = MongoCredential.createCredential(
                    getMongoDBUser(),
                    getMongoDBDB(),
                    getMongoDBPassword().toCharArray()
            );
            client = new MongoClient(
                    Collections.singletonList(serverAddress),
                    Collections.singletonList(credential),
                    new MongoClientOptions.Builder().serverSelectionTimeout(500).build()
            );
            db = client.getDatabase(getMongoDBDB());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return db;
    }

    public MongoClient getClient() {
        if(client==null) {
            throw new IllegalStateException("first you must call connect");
        }
        return client;
    }
}

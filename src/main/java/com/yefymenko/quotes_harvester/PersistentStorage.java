package com.yefymenko.quotes_harvester;

import org.slf4j.*;

import java.sql.*;
import java.util.Collection;

public class PersistentStorage implements QuoteWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentStorage.class);
    private static PersistentStorage INSTANCE;
    private final String url;
    private final String database;
    private final String user;
    private final String password;

    private PersistentStorage(Settings.DbSettings dbSettings) {
        String url = dbSettings.getDbUrl();
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        this.url = url;
        String normalizedDbName = dbSettings.getDbName().toLowerCase();
        if (!normalizedDbName.equals(dbSettings.getDbName())) {
            LOGGER.warn("Database name {} is not normalized to lower case. Will normalize to {}",
                    dbSettings.getDbName(), normalizedDbName);
        }
        this.database = normalizedDbName;
        this.user = dbSettings.getDbUser();
        this.password = dbSettings.getDbPassword();
        createIfNotExists();
    }

    public synchronized static PersistentStorage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PersistentStorage(Settings.getInstance().getDbSettings());
        }
        return INSTANCE;
    }

    public void writeQuotes(Collection<Quote> quotes) {
        if (quotes.size() == 0) {
            return;
        }
        try (Connection connection = getConnection()) {
            connection.setCatalog(database);
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(getInsertQuoteSql());
            for (Quote quote : quotes) {
                statement.setTimestamp(1, java.sql.Timestamp.valueOf(quote.getTime()));
                statement.setBigDecimal(2, quote.getBid());
                statement.setBigDecimal(3, quote.getAsk());
                statement.setString(4, quote.getExchange());
                statement.setString(5, quote.getName());
                statement.addBatch();
            }
            int[] updatedCount = statement.executeBatch();
            connection.commit();
            LOGGER.info("Quotes have been successfully written to database. Quotes count: {}", updatedCount.length);
        } catch (SQLException exc) {
            LOGGER.error("Exception occurred during saving quotes. The application will be terminated", exc);
            System.exit(1);
        }
    }

    private String getInsertQuoteSql() {
        return "INSERT QUOTES (TIME, BID, ASK, EXCHANGE, NAME) VALUES (?,?,?,?,?)";
    }

    private void createIfNotExists() {
        try (Connection con = getConnection(); Statement statement = con.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);
            statement.executeUpdate("USE " + database);
            statement.executeUpdate(getCreateTableQuoteSql());
        } catch (SQLException exc) {
            LOGGER.error("Exception occurred while creating database. The application will be terminated.", exc);
            System.exit(1);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url + "?allowPublicKeyRetrieval=true&useSSL=false", user, password);
    }

    private String getCreateTableQuoteSql() {
        return "CREATE TABLE IF NOT EXISTS QUOTES (" +
                "ID int(11) NOT NULL AUTO_INCREMENT, " +
                "TIME TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "BID decimal(18, 6) NOT NULL, " +
                "ASK decimal(18, 6) NOT NULL, " +
                "EXCHANGE varchar(20) NOT NULL, " +
                "NAME varchar(20) NOT NULL, " +
                "PRIMARY KEY (ID), " +
                "KEY QUOTES_TIME_IDX (TIME) USING BTREE," +
                "KEY QUOTES_SYNTHETIC_IDX (NAME) USING BTREE" +
                ") ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8";
    }
}

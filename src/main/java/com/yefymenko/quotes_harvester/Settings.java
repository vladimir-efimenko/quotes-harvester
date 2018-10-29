package com.yefymenko.quotes_harvester;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.*;

public final class Settings {
    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    private static Settings INSTANCE;

    private int flushPeriod;
    private Collection<Instrument> instruments;
    private String dbUrl;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private DbSettings dbSettings;

    private Settings() {
        instruments = new ArrayList<>();
        dbSettings = new DbSettings();
    }

    /**
     * Flush periods in seconds
     */
    public int getFlushPeriod() {
        return flushPeriod;
    }

    public Collection<Instrument> getInstruments() {
        return instruments;
    }

    public DbSettings getDbSettings() {
        return dbSettings;
    }

    /**
     * Reads 'settings.yaml' file and returns an instance of Settings class.
     *
     * @return an instance of Settings class if successfully read; otherwise, null.
     */
    public static synchronized Settings getInstance() {
        if (INSTANCE == null) {
            try {
                File file = new File("settings.yaml");
                if (!file.exists()) {
                    LOGGER.warn("Cannot find 'settings.yaml file'");
                }
                INSTANCE = Settings.readFromStream(new FileInputStream("settings.yaml"));
            } catch (Exception exc) {
                LOGGER.warn("Exception occurred during reading of settings.yaml file.", exc);
            }
        }

        return INSTANCE;
    }

    /**
     * Reads settings from input stream in yaml format
     */
    public static Settings readFromStream(InputStream is) throws InvalidObjectException {
        Settings settings = new Settings();
        Yaml yaml = new Yaml();
        Map<String, Object> settingsMap = yaml.load(is);

        Map<String, Object> db = (Map<String, Object>) settingsMap.get("db");
        settings.dbUrl = (String) db.get("url");
        if (settings.dbUrl == null) {
            throw new InvalidObjectException("Database url cannot be null");
        }
        settings.dbName = (String) db.get("database");
        if (settings.dbName == null) {
            throw new InvalidObjectException("Database name cannot be null");
        }
        settings.dbUser = (String) db.get("user");
        if (settings.dbUrl == null) {
            throw new InvalidObjectException("Database user cannot be null");
        }
        settings.dbPassword = (String) db.get("password");
        if (settings.dbPassword == null) {
            throw new InvalidObjectException("Database password cannot be null");
        }
        settings.flushPeriod = (Integer) settingsMap.get("flush_period_s");
        if (settings.flushPeriod == 0) {
            LOGGER.warn("FlushPeriod cannot be 0. Will reset to 1.");
            settings.flushPeriod = 1;
        }

        HashSet<String> addedInstrumentNames = new HashSet<>();

        for (Map<String, Object> instrumentMap : (Collection<Map<String, Object>>) settingsMap.get("instruments")) {
            Instrument instrument = Instrument.parse((String) instrumentMap.get("instrument"));
            instrument.setName((String) instrumentMap.get("name"));

            if (addedInstrumentNames.contains(instrument.getName())) {
                throw new InvalidObjectException("Instrument name has to be unique. Duplicate found: " + instrument.getName());
            }
            if (instrumentMap.containsKey("depends")) {
                SyntheticInstrument synthInstrument = new SyntheticInstrument(instrument);
                for (String value : (Collection<String>) instrumentMap.get("depends")) {
                    boolean added = synthInstrument.addDependency(Instrument.parse(value));
                    if (!added) {
                        String warningMsg = "The same dependency instrument {} for the synthetic {} has already been added";
                        LOGGER.warn(warningMsg, value, instrument);
                    }
                }
                instrument = synthInstrument;
            }

            settings.instruments.add(instrument);
            addedInstrumentNames.add(instrument.getName());
        }
        return settings;
    }

    public class DbSettings {
        private DbSettings() {
        }

        public String getDbUrl() {
            return Settings.this.dbUrl;
        }

        public String getDbName() {
            return Settings.this.dbName;
        }

        public String getDbUser() {
            return Settings.this.dbUser;
        }

        public String getDbPassword() {
            return Settings.this.dbPassword;
        }
    }
}

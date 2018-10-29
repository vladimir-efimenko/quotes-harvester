package com.yefymenko.quotes_harvester;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class SettingsTest {

    @Test
    public void settingsTest() throws Exception {
        File testSettingsFile = new File("src/test/resources/test-settings.yaml");

        Settings settings = Settings.readFromStream(new FileInputStream(testSettingsFile));
        Settings.DbSettings dbSettings = settings.getDbSettings();

        assertEquals("Url", "jdbc:mysql://localhost:3306/", dbSettings.getDbUrl());
        assertEquals("Database", "quotesHarvester", dbSettings.getDbName());
        assertEquals("User", "user", dbSettings.getDbUser());
        assertEquals("Password", "password", dbSettings.getDbPassword());
        assertEquals("FlushPeriod", 10, settings.getFlushPeriod());
        assertEquals("Instruments size", 4, settings.getInstruments().size());
    }
}

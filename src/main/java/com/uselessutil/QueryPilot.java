package com.uselessutil;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uselessutil.model.QueryPilotConfig;
import com.uselessutil.model.ScriptConfig;

public class QueryPilot {
	public static void main(String[] args) {
		String configFile = System.getProperty("config", "config.json");
		ObjectMapper mapper = new ObjectMapper();

		try {
			QueryPilotConfig config = mapper.readValue(new File(configFile), QueryPilotConfig.class);
			boolean ignoreError = config.isIgnoreError();
			List<ScriptConfig> scripts = config.getScripts();

			for (ScriptConfig scriptConfig : scripts) {
				String driver = scriptConfig.getDriver();
				String jdbcConnection = scriptConfig.getJdbcConnection();
				String username = scriptConfig.getUsername();
				String password = scriptConfig.getPassword();
				List<String> sqlFiles = scriptConfig.getSqlscripts();

				loadDatabaseDriver(driver);
				for (String file : sqlFiles) {
					executeScript(jdbcConnection, username, password, file, ignoreError);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadDatabaseDriver(String driver) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.out.println("Database driver not found: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void executeScript(String jdbcConnection, String username, String password, String filePath,
			boolean ignoreError) {
		try (Connection conn = DriverManager.getConnection(jdbcConnection, username, password);
				Statement stmt = conn.createStatement();) {

				Path path = Paths.get(filePath);
				String sql = Files.readString(path, StandardCharsets.UTF_8);
				try {
					if (!sql.trim().isEmpty()) {
						stmt.execute(sql);
					}
				} catch (Exception e) {
					if (!ignoreError) {
						throw e; // Re-throw the exception if not ignoring errors
					} else {
						System.out.println("Error executing SQL: " + sql);
						System.out.println("Error Message: " + e.getMessage());
					}
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.uselessutil.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScriptConfig {
    private String driver;
    private String jdbcConnection;
    private String username;
    private String password;
    private List<String> sqlscripts;
}

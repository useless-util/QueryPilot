package com.uselessutil.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueryPilotConfig {
	 private boolean ignoreError;
	 private List<ScriptConfig> scripts;
}

package com.nehades.services.databases.dto;

import java.util.HashMap;
import java.util.Map;

public record IgniteTableDto(String cacheName, String tableName, Map<String, String> fieldMap) { }

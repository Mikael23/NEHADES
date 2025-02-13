package com.nehades.services.databases.dto;

import net.minidev.json.JSONArray;

import java.util.List;

public record ResponseDto (JSONArray jsonArray, List<String> idList){}

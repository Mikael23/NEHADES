package com.nehades.dto;

import java.util.List;

public record QueryDto(String query, List<String> hierarchy,String partnerId) {}

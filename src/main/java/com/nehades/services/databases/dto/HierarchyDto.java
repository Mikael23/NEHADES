package com.nehades.services.databases.dto;

import com.nehades.services.databases.Databases;

import javax.xml.crypto.Data;
import java.util.List;

public record HierarchyDto(String partnerId, List<String>fieldsHierarchy, Databases source) { }

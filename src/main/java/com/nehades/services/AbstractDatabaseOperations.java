package com.nehades.services;

import com.nehades.services.databases.dto.ResponseDto;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AbstractDatabaseOperations {
    public ResponseDto extractResult(ResultSet rs) throws SQLException {
        List<String> idList = new ArrayList<>();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            StringBuilder result = new StringBuilder("Row: ");
            JSONObject jsonObject = new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = rs.getObject(i);
                jsonObject.put(columnName, value);
                jsonArray.add(jsonObject);
                if (columnName.equals("_id")) {
                    idList.add(value.toString());
                }
            }
        }
        return new ResponseDto(jsonArray, idList);
    }

}



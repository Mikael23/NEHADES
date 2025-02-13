package com.nehades.services;

import com.nehades.services.databases.*;
import com.nehades.services.databases.dto.ResponseDto;
import com.nehades.services.sqlparsesservice.SqlParserService;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    private final MongoService mongoService;
    private final SqlParserService sqlParserService;
    private final List<InsertSearchService> list;
    private final IgniteService igniteService;

    public SearchService(MongoService mongoService, SqlParserService sqlParserService, List<InsertSearchService> list, IgniteService igniteService) {
        this.mongoService = mongoService;
        this.sqlParserService = sqlParserService;
        this.list = list;
        this.igniteService = igniteService;
    }

    //Now for test I perfofm search only in ingite
//    public List<ResponseDto> search(String partnerId, String query,List<String>hiearachy) throws Exception {
//        List<ResponseDto>responseDtoList = new LinkedList<>();
//        Map<String, Map<String, Map<String, Integer>>> stringMapMap = sqlParserService.parseSQLQuery(query,hiearachy);
//        ResponseDto responseDto = igniteService.performSearch(partnerId, query);
//        for (InsertSearchService insertSearchService : list) {
//            ResponseDto responseDto = insertSearchService.performSearch(partnerId, query);
//            responseDtoList.add(responseDto);
//        }
 //       mongoService.updateAccessLogAsync(partnerId,responseDtoList.getFirst().idList());
   //     return responseDtoList;
//    }
}

package com.performance.domain.service;

import java.util.List;

import lombok.Data;

@Data
public class GoogleSpreadSheetRowResponse {

    private String range;
    private String majorDimension;
    private List<String[]> values;

}

package com.performance.domain.service;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleSpreadSheetRowResponse {

    private String range;
    private String majorDimension;
    private List<String[]> values;

}

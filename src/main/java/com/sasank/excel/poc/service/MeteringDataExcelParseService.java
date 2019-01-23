package com.sasank.excel.poc.service;

import com.sasank.excel.poc.domain.dto.MonthlyMeterData;

import java.util.Map;
import java.util.Set;

public interface MeteringDataExcelParseService {

    Set<MonthlyMeterData> getParsedMeteringData(String filename);

}

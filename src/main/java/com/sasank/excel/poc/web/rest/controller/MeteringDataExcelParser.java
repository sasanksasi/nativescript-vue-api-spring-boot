package com.sasank.excel.poc.web.rest.controller;

import com.sasank.excel.poc.domain.dto.MonthlyMeterData;
import com.sasank.excel.poc.service.MeteringDataExcelParseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class MeteringDataExcelParser {
    private static Logger logger = LogManager.getLogger(MeteringDataExcelParser.class);
    @Autowired
    MeteringDataExcelParseService _meteringDataExcelParseService;

    @Autowired
    private ResourceLoader _resourceLoader;

    @Value("${data.file.path}")
    private String _meteringDataExcelName;

    @GetMapping("/meter-data/excel")
    public Set<MonthlyMeterData> getParsedMonthlyMeteringData() {
        logger.info("==> getParsedMonthlyMeteringData() : Fetching Monthly Meter Data on file " + _meteringDataExcelName);
        Set<MonthlyMeterData> regionalMonthlyMeteringData = null;
        try {
            regionalMonthlyMeteringData = _meteringDataExcelParseService
                    .getParsedMeteringData(_resourceLoader.getResource("classpath:" + _meteringDataExcelName).getFile().getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("<== getParsedMonthlyMeteringData() : Fetched Monthly Meter Data on file " + _meteringDataExcelName);
        return regionalMonthlyMeteringData;
    }
}

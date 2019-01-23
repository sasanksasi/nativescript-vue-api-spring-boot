package com.sasank.excel.poc.service.implementation;

import com.sasank.excel.poc.domain.dto.MonthlyCount;
import com.sasank.excel.poc.domain.dto.MonthlyMeterData;
import com.sasank.excel.poc.service.MeteringDataExcelParseService;
import com.sasank.excel.poc.util.DateUtils;
import com.sasank.excel.poc.util.ExcelFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class MeteringDataExcelParseServiceImp implements MeteringDataExcelParseService {

    private static Logger logger = LogManager.getLogger(MeteringDataExcelParseServiceImp.class);

    public static <T> Collector<T, ?, T> toSingleObjectFromList() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() == 0) {
                        return null;
                    }
                    return list.get(0);
                }
        );
    }

    @Override
    public Set<MonthlyMeterData> getParsedMeteringData(String filename) {
        logger.info("==> getParsedMeteringData() : Parsing EXCEL File :"+filename);
        Set<MonthlyMeterData> regionalMonthlyMeteringData = new HashSet<>();
        try {
            ExcelFileReader.processAllSheetsToRows(filename,
                    (int rowNumber, List<String> values) -> {
                        if (rowNumber != 0) {
                            double monthCount = 0;
                            String region = values.get(0);
                            String month = DateUtils.getMonth(values.get(1)).getName();
                            Set<MonthlyCount> monthlyCountSet = null;
                            MonthlyCount monthlyCount = null;

                            MonthlyMeterData monthlyMeteringData = regionalMonthlyMeteringData
                                    .parallelStream()
                                    .filter(data -> data.getRegion().equals(region))
                                    .collect(toSingleObjectFromList());

                            if (monthlyMeteringData != null) {
                                monthlyCountSet = monthlyMeteringData.getMonthlyCount();


                                if (monthlyCountSet != null)
                                    monthlyCount = monthlyCountSet.parallelStream()
                                            .filter(data -> data.getMonth().equals(month))
                                            .collect(toSingleObjectFromList());


                                if (monthlyCount != null)
                                    monthCount = monthlyCount.getCount();
                            } else {

                                monthlyCountSet = new HashSet<>();
                            }


                            for (int i = 2; i < values.size() - 1; i++)
                                if (values.get(i) != null)
                                    monthCount += Double.valueOf(values.get(i));

                            if (monthlyCount != null)
                                monthlyCount.setCount(monthCount);
                            else
                                monthlyCountSet.add(new MonthlyCount(month, monthCount));

                            if (monthlyMeteringData == null)
                                regionalMonthlyMeteringData.add(new MonthlyMeterData(region, monthlyCountSet));
                        }
                        return true;
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("==> getParsedMeteringData() : Parsed EXCEL File :"+filename);
        return regionalMonthlyMeteringData;
    }
}

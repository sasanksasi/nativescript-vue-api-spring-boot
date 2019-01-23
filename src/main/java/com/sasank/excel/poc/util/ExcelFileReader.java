package com.sasank.excel.poc.util;

import com.sasank.excel.poc.util.excel.XlsxRowCallback;
import com.sasank.excel.poc.util.excel.XlsxSheetToRowsHandler;
import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.Iterator;

public class ExcelFileReader {
    public static void processFirstSheet(String filename, XlsxRowCallback xlsxRowCallback) throws Exception {
        try (OPCPackage pkg = OPCPackage.open(filename, PackageAccess.READ)) {
            XSSFReader xssfReader = new XSSFReader(pkg);
            XMLReader parser = fetchSheetParser(xssfReader, xlsxRowCallback);

            // process the first sheet
            try (InputStream sheet = xssfReader.getSheetsData().next()) {
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
            }
        }
    }

    public static void processAllSheetsToRows(String filename, XlsxRowCallback xlsxRowCallback) throws Exception {
        try (OPCPackage pkg = OPCPackage.open(filename, PackageAccess.READ)) {
            XSSFReader xssfReader = new XSSFReader(pkg);
            XMLReader parser = fetchSheetParser(xssfReader, xlsxRowCallback);

            Iterator<InputStream> sheets = xssfReader.getSheetsData();
            while (sheets.hasNext()) {
                try (InputStream sheet = sheets.next()) {
                    InputSource sheetSource = new InputSource(sheet);
                    parser.parse(sheetSource);
                }
            }
        }
    }
    public static void processAllSheetsToRows(InputStream filename, XlsxRowCallback xlsxRowCallback) throws Exception {
        try (OPCPackage pkg = OPCPackage.open(filename)) {
            XSSFReader xssfReader = new XSSFReader(pkg);
            XMLReader parser = fetchSheetParser(xssfReader, xlsxRowCallback);

            Iterator<InputStream> sheets = xssfReader.getSheetsData();
            while (sheets.hasNext()) {
                try (InputStream sheet = sheets.next()) {
                    InputSource sheetSource = new InputSource(sheet);
                    parser.parse(sheetSource);
                }
            }
        }
    }

    private static XMLReader fetchSheetParser(XSSFReader xssfReader, XlsxRowCallback xlsxRowCallback) throws Exception {
        XMLReader parser = SAXHelper.newXMLReader();
        ContentHandler handler = new XlsxSheetToRowsHandler(xssfReader, xlsxRowCallback);
        parser.setContentHandler(handler);
        return parser;
    }
}

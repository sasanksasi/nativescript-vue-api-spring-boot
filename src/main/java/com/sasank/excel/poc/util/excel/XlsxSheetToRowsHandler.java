/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.sasank.excel.poc.util.excel;

import com.sasank.excel.poc.util.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * XML handler for transforming a sheet into rows. Uses an {@link XlsxRowCallback} to publish identified rows.
 */
public final class XlsxSheetToRowsHandler extends DefaultHandler {

    // global variables
    private final XlsxRowCallback _callback;
    private final StylesTable _stylesTable;
    private final SharedStringsTable _sharedStringTable;
    private final List<String> _rowValues;
    // variables used to hold information about the current visited cells
    private final StringBuilder _value;
    // variables used to hold information about the current rows
    private int _rowNumber;
    private boolean _inCell;
    private boolean _inFormula;
    private int _columnNumber;
    private XssfDataType _dataType;
    private int _formatIndex;
    private String _formatString;
    public XlsxSheetToRowsHandler(XSSFReader xssfReader, XlsxRowCallback callback)
            throws Exception {
        _callback = callback;

        _sharedStringTable = xssfReader.getSharedStringsTable();
        _stylesTable = xssfReader.getStylesTable();

        _value = new StringBuilder();
        _rowValues = new ArrayList<String>();
        _rowNumber = -1;
        _inCell = false;
        _inFormula = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("row".equals(qName)) {
            // element is a row

            // excel row numbers are 1-based
            int rowNumber = Integer.parseInt(attributes.getValue("r"));
            rowNumber = rowNumber - 1;

            while (_rowNumber + 1 < rowNumber) {
                // empty lines are not skipped, so dispatch empty lines
                _rowNumber++;
                List<String> emptyValues = Collections.emptyList();
                _callback.row(_rowNumber, emptyValues);
            }
            _rowNumber = rowNumber;
        } else if ("c".equals(qName)) {
            // element is a cell

            _inCell = true;

            final String r = attributes.getValue("r");
            int firstDigit = -1;
            for (int c = 0; c < r.length(); ++c) {
                if (Character.isDigit(r.charAt(c))) {
                    firstDigit = c;
                    break;
                }
            }
            _columnNumber = nameToColumn(r.substring(0, firstDigit));

            // Set up defaults.
            _dataType = XssfDataType.NUMBER;
            _formatIndex = -1;
            _formatString = null;

            final String cellType = attributes.getValue("t");
            if ("b".equals(cellType)) {
                _dataType = XssfDataType.BOOL;
            } else if ("e".equals(cellType)) {
                _dataType = XssfDataType.ERROR;
            } else if ("inlineStr".equals(cellType)) {
                _dataType = XssfDataType.INLINESTR;
            } else if ("s".equals(cellType)) {
                _dataType = XssfDataType.SSTINDEX;
            } else if ("str".equals(cellType)) {
                _dataType = XssfDataType.FORMULA;
            }

            final String cellStyleStr = attributes.getValue("s");
            if (cellStyleStr != null) {
                // It's a number, but almost certainly one
                // with a special style or format
                final int styleIndex = Integer.parseInt(cellStyleStr);
                final XSSFCellStyle style = _stylesTable.getStyleAt(styleIndex);


                if (_dataType == XssfDataType.NUMBER) {
                    this._formatIndex = style.getDataFormat();
                    this._formatString = style.getDataFormatString();
                    if (this._formatString == null) {
                        this._formatString = BuiltinFormats.getBuiltinFormat(this._formatIndex);
                    }
                }
            }
        } else if (_inCell && "f".equals(qName)) {
            // skip the actual formula line
            _inFormula = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("row".equals(qName)) {
            // element was a row
            boolean next = _callback.row(_rowNumber, _rowValues);
            if (!next) {
                throw new XlsxStopParsingException();
            }
            _rowValues.clear();
        } else if ("c".equals(qName)) {
            // element was a cell

            _inCell = false;

            while (_rowValues.size() < _columnNumber) {
                _rowValues.add(null);
            }

            _rowValues.add(createValue());
            _value.setLength(0);
        } else if (_inFormula && "f".equals(qName)) {
            // skip the actual formula line
            _inFormula = false;
        }
    }

    private String createValue() {
        if (_value.length() == 0) {
            return null;
        }

        switch (_dataType) {

            case BOOL:
                final char first = _value.charAt(0);
                return first == '0' ? "false" : "true";
            case ERROR:
                return _value.toString();
            case FORMULA:
                return _value.toString();
            case INLINESTR:
                final XSSFRichTextString rtsi = new XSSFRichTextString(_value.toString());
                return rtsi.toString();
            case SSTINDEX:
                final String sstIndex = _value.toString();
                final int idx = Integer.parseInt(sstIndex);
                final RichTextString item = _sharedStringTable.getItemAt(idx);
                return item.getString();
            case NUMBER:
                final String numberString = _value.toString();
                if (_formatString != null) {
                    final DataFormatter formatter = getDataFormatter();
                    if (HSSFDateUtil.isADateFormat(_formatIndex, _formatString)) {
                        final Date date = DateUtil.getJavaDate(Double.parseDouble(numberString));
                        return DateUtils.createDateFormat().format(date);
                    }
                    return formatter.formatRawCellContents(Double.parseDouble(numberString), _formatIndex, _formatString);
                } else {
                    if (numberString.endsWith(".0")) {
                        // xlsx only stores doubles, so integers get ".0" appended
                        // to them
                        return numberString.substring(0, numberString.length() - 2);
                    }
                    return numberString;
                }
            default:
                return "";
        }
    }

    private DataFormatter getDataFormatter() {
        return new DataFormatter();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (_inCell && !_inFormula) {
            _value.append(ch, start, length);
        }
    }

    /**
     * Converts an Excel column name like "C" to a zero-based index.
     *
     * @param name
     * @return Index corresponding to the specified name
     */
    private int nameToColumn(String name) {
        int column = -1;
        for (int i = 0; i < name.length(); ++i) {
            int c = name.charAt(i);
            column = (column + 1) * 26 + c - 'A';
        }
        return column;
    }

    private enum XssfDataType {
        BOOL,
        ERROR,
        FORMULA,
        INLINESTR,
        SSTINDEX,
        NUMBER,
    }
}

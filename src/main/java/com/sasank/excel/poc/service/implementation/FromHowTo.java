package com.sasank.excel.poc.service.implementation;

import com.sasank.excel.poc.util.ExcelFileReader;

import java.util.List;

public class FromHowTo {
    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    /*private static class SheetHandler extends DefaultHandler {
        private final SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;
        private boolean inlineStr;
        private final LruCache<Integer,String> lruCache = new LruCache<>(50);

        private static class LruCache<A,B> extends LinkedHashMap<A, B> {
            private final int maxEntries;

            public LruCache(final int maxEntries) {
                super(maxEntries + 1, 1.0f, true);
                this.maxEntries = maxEntries;
            }

            @Override
            protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
                return super.size() > maxEntries;
            }
        }

        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }

        @Override
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            // c => cell
            if(name.equals("c")) {
                // Print the cell reference
                System.out.print(attributes.getValue("r") + " - ");
                // Figure out if the value is an index in the SST
                String cellType = attributes.getValue("t");
                nextIsString = cellType != null && cellType.equals("s");
                inlineStr = cellType != null && cellType.equals("inlineStr");
            }
            // Clear contents cache
            lastContents = "";
        }

        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if(nextIsString) {
                Integer idx = Integer.valueOf(lastContents);
                lastContents = lruCache.get(idx);
                if (lastContents == null && !lruCache.containsKey(idx)) {
                    lastContents = new  XSSFRichTextString(sst.getEntryAt(idx)).toString();
                    lruCache.put(idx, lastContents);
                }
                nextIsString = false;
            }

            // v => contents of a cell
            // Output after we've seen the string contents
            if(name.equals("v") || (inlineStr && name.equals("c"))) {
                System.out.println(lastContents);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException { // NOSONAR
            lastContents += new String(ch, start, length);
        }
    }*/
    public static void main(String[] args) throws Exception {

       /* FromHowTo howto = new FromHowTo();
        System.out.println(Character.isDigit('.'));
        List<String> list = new ArrayList<>();
        SheetHandler sheetHandler = new SheetHandler() {

            @Override
            public void startElement(String uri, String localName, String name, Attributes attributes) {
                // c => cell
                if ("row".equals(name)) {
                    System.out.println("New Row Started");
                }
                else if (name.equals("c")) {
                    // Print the cell reference
                    System.out.print(attributes.getValue("r") + " - ");
                    // Figure out if the value is an index in the SST
                    String cellType = attributes.getValue("t");
                    nextIsString = cellType != null && cellType.equals("s");
                    inlineStr = cellType != null && cellType.equals("inlineStr");
                }
                // Clear contents cache
                lastContents = "";
            }

            @Override
            public void endElement(String uri, String localName, String name) {
                // Process the last contents as required.
                // Do now, as characters() may be called more than once
               if (nextIsString) {
                    Integer idx = Integer.valueOf(lastContents);
                    lastContents = lruCache.get(idx);
                    if (lastContents == null && !lruCache.containsKey(idx)) {
                        lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                        lruCache.put(idx, lastContents);
                    }

                    nextIsString = false;
                }

                // v => contents of a cell
                // Output after we've seen the string contents
                if (name.equals("v") || (inlineStr && name.equals("c"))) {
                    System.out.println(lastContents);
                }
            }
        };*/
        /*ExcelFileReader.processAllSheetsToRows("/Users/sasankm/Documents/NativeScriptBackEnd/ns-vue-excel-poc/src/main/resources/static/tou.xlsx",
                (int rowNumber, List<String> values) -> {
                    if (values.isEmpty())
                        return false;
                    System.out.print("\n" + rowNumber + "  ");
                    values.forEach(e -> System.out.print(e + "  "));
                    return true;
                });*/
        //ExcelReader.processAllSheets("/Users/sasankm/Documents/NativeScriptBackEnd/ns-vue-excel-poc/src/main/resources/static/tou.xlsx", sheetHandler);
    }


}
package com.sasank.excel.poc.util;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class SheetHandler extends DefaultHandler {
    public SharedStringsTable sst;
    public final LruCache<Integer, String> lruCache = new LruCache<>(50);
    public String lastContents;
    public boolean nextIsString;
    public boolean inlineStr;

    public SheetHandler() {  }

    public void setSharedStringsTable(SharedStringsTable sst) {
        this.sst = sst;
    }

    public abstract void startElement(String uri, String localName, String name, Attributes attributes);

    public abstract void endElement(String uri, String localName, String name);

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException { // NOSONAR
        lastContents += new String(ch, start, length);
    }

    public static class LruCache<A, B> extends LinkedHashMap<A, B> {
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

}

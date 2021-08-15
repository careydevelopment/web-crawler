package us.careydevelopment.util.bot.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Reads a page just once and gets a single piece of info from it
 */
public class WebPageReader {
    private static final Logger LOG = LoggerFactory.getLogger(WebPageReader.class);

    private String url;
    
    
    public static void main(String... args) {
        String url = "https://www.insider.com/billy-crystal-weed-gummies-stoned-during-mri-taco-bell-2021-8";
        WebPageReader wpr = new WebPageReader(url);
                
        try {
            String contents = wpr.getFullContents();
            //System.err.println(contents);
            MetaParser metaParser = new MetaParser(contents);
            String meta = metaParser.getMetaByProperty("og:image");
            String thumbnail = WebParser.getAttributeFromLine(meta, "content");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public WebPageReader(String url) {
        this.url = url;
    }
    
    
    public List<String> fetchLinesByKeyword(String keyword) throws IOException, MalformedURLException {
        LOG.debug("Getting lines by keyword " + keyword + " for URL " + url);
                
        ConnectionHelper connectionHelper = new ConnectionHelper(url);
        URLConnection conn = connectionHelper.getBasicConnection();
                
        List<String> urlData = new ArrayList<String>();
                
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            //urlData = reader.lines().collect(Collectors.joining("\n"));
            Stream<String> stream = reader.lines();
            urlData = stream.filter(line -> {
                return (line.indexOf(keyword) > -1);
            })
            .collect(Collectors.toList());
        }
                
        return urlData;
    }
    
    
    public String fetchFirstLineByKeyword(String keyword) throws IOException, MalformedURLException {
        List<String> lines = fetchLinesByKeyword(keyword);
        String returnValue = "";
                
        if (lines != null && lines.size() > 0) {
            returnValue = lines.get(0);
        } 
                
        return returnValue;
    }
        
        
    public List<String> fetchLinesByFrontAndBackDelimiters(String frontDelimiter, String backDelimiter) throws IOException, MalformedURLException {
        List<String> urlData = new ArrayList<String>();
                
        ConnectionHelper connectionHelper = new ConnectionHelper(url);
        URLConnection conn = connectionHelper.getBasicConnection();
                
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String contents = reader.lines().collect(Collectors.joining("\n"));
                        
            int startingPoint = contents.indexOf(frontDelimiter);
            while (startingPoint > -1) {
                int endingPoint = contents.indexOf(backDelimiter, startingPoint + 1);
                
                if (endingPoint  > -1) {
                    String element = contents.substring(startingPoint, endingPoint);
                    //LOG.debug("element is " + element);
                    urlData.add(element);
                    startingPoint = contents.indexOf(frontDelimiter, endingPoint + 1);
                } else {
                    break;
                }
            }
        }
                 
        return urlData;
    }
    
    
    public String getFullContents() throws IOException, MalformedURLException {
        String contents = "";
                
        ConnectionHelper connectionHelper = new ConnectionHelper(url);
        URLConnection conn = connectionHelper.getBasicConnection();
                
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            contents = reader.lines().collect(Collectors.joining("\n"));
        }
                 
        return contents;
    }
        
        
    public List<String> getElementContents(String elementName) throws IOException, MalformedURLException {
        List<String> contents = new ArrayList<String>();
                
        ConnectionHelper connectionHelper = new ConnectionHelper(url);
        URLConnection conn = connectionHelper.getBasicConnection();
                
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String fullPage = reader.lines().collect(Collectors.joining("\n"));
                        
            int startingPoint = WebParser.findPointAfterElement(fullPage, elementName, 0); 

            while (startingPoint > -1) {
                int endingPoint = WebParser.findPointBeforeClosingElement(fullPage, elementName, startingPoint);
                
                if (endingPoint  > -1) {
                    String element = fullPage.substring(startingPoint, endingPoint);
                    //LOG.debug("element is " + element);
                    contents.add(element);
                    startingPoint = WebParser.findPointAfterElement(fullPage, elementName, endingPoint + elementName.length());
                } else {
                    break;
                }
            }
        }
                 
        return contents;
    }
}
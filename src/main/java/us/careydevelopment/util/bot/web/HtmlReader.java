package us.careydevelopment.util.bot.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For reading and parsing entire web pages to get multiple pieces of info
 */
public class HtmlReader {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlReader.class);
        
    private String contents;
        
    public HtmlReader(String contents) {
        this.contents = contents;
    }
    
    
    public String getTitle() {
        return getElementValue("title");
    }
    
    
    public String getElementValue(String elementName) {
        String startTag = WebParser.transformElementNameToStartingElement(elementName);
        String endTag = WebParser.transformElementNameToEndingElement(elementName);
                
        String value = "";
        int start = contents.indexOf(startTag);
        
        if (start > -1) {
            int end = contents.indexOf(endTag, start + 1);
            if (end > -1) {
                start += startTag.length();
                value = contents.substring(start, end);
            }
        }
                
        return value;
    }
    
    
    public String getEverythingBetweenTwoString(String startString, String endString) {
        String everything = "";
                
        int start = contents.indexOf(startString);
        if (start > -1) {
            int end = contents.indexOf(endString, start + 1);
            if (end > -1) {
                start = start + startString.length();
                everything = contents.substring(start, end);
            }
        }
                
        return everything;
    }
    
    
    public String getAttributeFromLineWithToken(String token, String attribute) {
        String value = "";
                
        try (BufferedReader br = new BufferedReader(new StringReader(contents))) {
            List<String> lines = br.lines().filter(line -> {
                return (line.indexOf(token) > -1);
            }).collect(Collectors.toList());

        for (String line : lines) {
            if (line.indexOf(attribute) > -1) {
                value = WebParser.getAttributeFromLine(line, attribute);
                break;
                                }
            }
        } catch (IOException ie) {
            LOG.error("Problem getting attribute!", ie);
        }
                
        return value;
    }
    
    
    public String getContents() {
        return contents;
    }
    
    
    public String getJsonValue(String jsonPropertyName) {
        String value = "";
                
        jsonPropertyName = "\"" + jsonPropertyName + "\"";
        int loc = contents.indexOf(jsonPropertyName);
        if (loc > -1) {
            loc += jsonPropertyName.length();
            int colonLoc = contents.indexOf(":", loc);
            if (colonLoc > -1) {
                int firstQuoteLoc = contents.indexOf("\"", colonLoc);
    
                if (firstQuoteLoc > -1) {
                    int secondQuoteLoc = contents.indexOf("\"", firstQuoteLoc + 1);
                    
                    if (secondQuoteLoc > -1) {
                        value = contents.substring(firstQuoteLoc + 1, secondQuoteLoc);
                    }
                }
            }
        }
                
        return value;
    }
}
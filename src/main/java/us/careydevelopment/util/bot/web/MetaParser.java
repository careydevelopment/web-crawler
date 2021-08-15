package us.careydevelopment.util.bot.web;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses contents of an HTML page and retrieves meta element data
 */
public class MetaParser {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlReader.class);
    
    private static final String META_SEARCH = "<meta";
    
    private String contents;
    private List<String> metas = new ArrayList<>();
    
    public MetaParser(String contents) {
        this.contents = contents;
        findMetas();
    }
    
    
    private void findMetas() {
        int currentIndex = 0;
        int metaLoc = contents.indexOf(META_SEARCH, currentIndex);
        
        while (metaLoc > -1) {
            int endingPoint = findMetaEnd(metaLoc);
            String meta = contents.substring(metaLoc, endingPoint + 1);
            metas.add(meta);
            
            currentIndex = endingPoint + 1;
            metaLoc = contents.indexOf(META_SEARCH, currentIndex);
        }
    }
    
    
    private int findMetaEnd(int startingPoint) {
        int metaEnd = -1;
        
        int unclosedTag = contents.indexOf(">", startingPoint + 1);
        int closedTag = contents.indexOf("/>", startingPoint + 1);
        int nextTag = contents.indexOf("<", startingPoint + 1);
        
        if (nextTag == -1 || unclosedTag < nextTag || closedTag < nextTag) {
            if (closedTag < unclosedTag) {
                metaEnd = closedTag + 1;
            } else {
                metaEnd = unclosedTag;
            }
        }
        
        return metaEnd;
    }
    
    
    public String getMetaByProperty(String property) {
        String meta = null;
        
        if (property != null) {
            for (String m : metas) {
                String prop = WebParser.getAttributeFromLine(m, "property");
                
                if (property.equals(prop)) {
                    meta = m;
                    break;
                }
            }            
        }
        
        return meta;
    }
}

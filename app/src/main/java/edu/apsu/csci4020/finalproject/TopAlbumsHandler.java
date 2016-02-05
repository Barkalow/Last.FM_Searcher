package edu.apsu.csci4020.finalproject;
/**
 * Created by ACOFF on 11/27/2015.
 *
 * XML parser for the information returned by the Last.FM API. Essentially checks XML flags and grabs the needed data from them.
 */

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class TopAlbumsHandler extends DefaultHandler {

    boolean inAlbum = false;
    boolean inTitle = false;
    boolean inLink = false;
    int names = 1;
    int urls = 1;
    StringBuilder titleBuffer;
    StringBuilder urlBuffer;
    ArrayList<Item> finalResult;
    Item currentItem;

    public TopAlbumsHandler() {
        finalResult = new ArrayList<Item>();
    }

    public ArrayList<Item> getResults() {
        return finalResult;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if (localName.equalsIgnoreCase("album")) {
            inAlbum = true;
            titleBuffer = new StringBuilder();
            urlBuffer = new StringBuilder();
            currentItem = new Item();
        } else if (inAlbum && localName.equalsIgnoreCase("name")) {
            inTitle = true;
            names++;
        } else if (inAlbum && localName.equalsIgnoreCase("url")) {
            inLink = true;
            urls++;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (inAlbum) {
            if (inTitle) {
                if((names % 2) == 0){
                    titleBuffer.append(ch, start, length);
                }
            } else if (inLink) {
                if((urls % 2) == 0){
                    urlBuffer.append(ch, start, length);
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (localName.equalsIgnoreCase("album")) {
            inAlbum = false;
            finalResult.add(currentItem);
        } else if (inAlbum && localName.equalsIgnoreCase("name")) {
            inTitle = false;
            currentItem.setTitle(titleBuffer.toString().replace("\n", " "));
        } else if (inAlbum && localName.equalsIgnoreCase("url")) {
            inLink = false;
            currentItem.setUrl(urlBuffer.toString().trim());
        }
    }

}

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

public class SimilarArtistsHandler extends DefaultHandler {

    boolean inArtist = false;
    boolean inTitle = false;
    boolean inLink = false;
    StringBuilder titleBuffer;
    StringBuilder urlBuffer;
    ArrayList<Item> finalResult;
    Item currentArtist;

    public SimilarArtistsHandler() {
        finalResult = new ArrayList<Item>();
    }

    public ArrayList<Item> getResults() {
        return finalResult;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if (localName.equalsIgnoreCase("artist")) {
            inArtist = true;
            titleBuffer = new StringBuilder();
            urlBuffer = new StringBuilder();
            currentArtist = new Item();
        } else if (inArtist && localName.equalsIgnoreCase("name")) {
            inTitle = true;
        } else if (inArtist && localName.equalsIgnoreCase("url")) {
            inLink = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (inArtist) {
            if (inTitle) {
                titleBuffer.append(ch, start, length);
            } else if (inLink) {
                urlBuffer.append(ch, start, length);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (localName.equalsIgnoreCase("artist")) {
            inArtist = false;
            finalResult.add(currentArtist);
        } else if (inArtist && localName.equalsIgnoreCase("name")) {
            inTitle = false;
            currentArtist.setTitle(titleBuffer.toString().replace("\n", " "));
        } else if (inArtist && localName.equalsIgnoreCase("url")) {
            inLink = false;
            currentArtist.setUrl("http://" + urlBuffer.toString().trim());
        }
    }

}


package fr.ensicaen.ecole.fluxrss.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.ensicaen.ecole.fluxrss.bdd.Bdd_RSS;

public class MyRSSsaxHandler extends DefaultHandler {

    private String url = null ;// l'URL du flux RSS à parser
    // Ensemble de drapeau permettant de savoir où en est le parseur dans le flux XML
    private boolean inTitle = false ;
    private boolean inDescription = false ;
    private boolean inItem = false ;
    private boolean inDate = false ;
    // L'image référencée par l'attribut url du tag <enclosure>
    private Bitmap image = null ;
    private String imageURL = null ;
    // Le titre, la description et la date extraits du flux RSS
    private StringBuffer title = new StringBuffer();
    private StringBuffer description = new StringBuffer();
    private StringBuffer date = new StringBuffer();
    private int numItem = 0; // Le numéro de l'item à extraire du flux RSS
    private int numItemMax = - 1; // Le nombre total d’items dans le flux RSS
    private List<Item> listItem ;
    private Context c;

    public MyRSSsaxHandler() {
        super();
        listItem = new ArrayList<>();
    }


    public void setUrl(String url){
        this.url = url;
    }

    public void processFeed(){
        try {
            numItem = 0; //A modifier pour visualiser un autre item
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler((ContentHandler) this);
            InputStream inputStream = new URL(url).openStream();
            reader.parse(new InputSource(inputStream));
            image = getBitmap(imageURL);
            //numItemMax = numItem;
        }catch(Exception e) {
            Bdd_RSS bdd = new Bdd_RSS(c , "article.db",null, 1);
            listItem = bdd.getAllItem();
            Log.e("smb116rssview", "processFeed Exception" + e.getMessage());
        }
    }

    @Override
    public void characters(char ch[], int start, int length){
        String chars = new String(ch).substring(start, start+length);
        if(inTitle){
            title = new StringBuffer(chars);
            inTitle = false;
        }
        if(inDate){
            date = new StringBuffer(chars);
            inDate = false;
        }
        if(inDescription){
            description = new StringBuffer(chars);
            inDescription = false;
        }
        if(inItem){
            numItemMax ++;
            numItem ++;
            inItem = false;
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("item")) {
            inItem = true;
        }
        if(qName.equals("title")){
            inTitle=true;
        }
        if(qName.equals("pubDate")){
            inDate=true;
        }
        if(qName.equals("media:description")){
            inDescription = true;
        }
        if(qName.equals("media:content")){
            imageURL = attributes.getValue("url");

        }


    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if(qName.equals("item")) {
            Item item = new Item(numItem, title.toString(), description.toString(), date.toString(), getBitmap(imageURL), imageURL);
            listItem.add(item);
        }
    }

    // récupération d'une image sous format Bitmap à partir d'une url
    public Bitmap getBitmap(String imageURL)
    {
        try {
            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //  connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    public String getNumber() {
        return String.valueOf(numItemMax);
    }

    public List<Item> getListItem() {
        return listItem;
    }
}

package fr.ensicaen.ecole.fluxrss.model;

import android.graphics.Bitmap;

public class Item {
    private int numItem;
    private String title;
    private String description;
    private String date;
    private Bitmap image;
    private String imageUrl;

    public Item(int numItem, String title, String description, String date, Bitmap image, String imageUrl){
        this.numItem = numItem;
        this.title = title;
        this.description = description;
        this.date = date;
        this.image = image;
        this.imageUrl = imageUrl;
    }

    public int getNumItem() {
        return numItem;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    @Override
    public String toString(){
        return this.title + this.numItem;
    }
}

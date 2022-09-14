package fr.ensicaen.ecole.fluxrss.bdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.ensicaen.ecole.fluxrss.model.Item;

public class Bdd_RSS extends SQLiteOpenHelper {

    private static final String TABLE_RSS = "flux_rss_bdd";
    private static final String idItem = "idItem";
    private static final String title = "title";
    private static final String description = "description";
    private static final String date = "date";
    private static final String image = "image";

    private SQLiteDatabase bdd;

    private static final String CREATE_RSS =  "CREATE TABLE " + TABLE_RSS + " ("
            + " idArticle"
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " title VARCHAR(50),"
            + " description VARCHAR(1000),"
            + " date VARCHAR(20),"
            + " image VARCHAR(100));";

    public Bdd_RSS(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RSS + ";");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_RSS);
        System.out.println("Fin de la creation des table");
    }

    public void open(){
        System.out.println("open database");
        bdd = this.getWritableDatabase();
        onCreate(bdd);
        onUpgrade(bdd, 1, 1);
    }

    public void close(){
        bdd.close();
    }

    public void insertItem(String title2, String description2, String date2, String url){
        bdd = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(title, title2);
        contentValues.put(description, description2);
        contentValues.put(date, date2);
        contentValues.put(image, url);

        System.out.println("insertion d'item dans la bdd");
        bdd.insert(TABLE_RSS, null, contentValues);
    }

    public Item readDatabase(int id){
        Cursor c = bdd.query(TABLE_RSS, new String[]{idItem, title, description, date, image}, "idItem = " + id, null,null,null,null);

        Bitmap imageBitMap = getBitmap(c.getString(4));

        return new Item(Integer.valueOf(c.getString(0)),c.getString(1), c.getString(2), c.getString(3), imageBitMap, c.getString(4));
    }

    public int countRow(){
        Cursor c = bdd.rawQuery("SELECT * FROM "+ TABLE_RSS, null);
        return c.getCount();
    }

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

    public List<Item> getAllItem(){
        List<Item> listItem = new ArrayList<>();
        for(int i = 0; i < countRow(); i++){
            Item item = readDatabase(i);
            listItem.add(item);
        }
        return listItem;
    }
}

package fr.ensicaen.ecole.fluxrss;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fr.ensicaen.ecole.fluxrss.bdd.Bdd_RSS;
import fr.ensicaen.ecole.fluxrss.model.DownloadRssTask;
import fr.ensicaen.ecole.fluxrss.model.Item;
import fr.ensicaen.ecole.fluxrss.model.MyRSSsaxHandler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public MyRSSsaxHandler handler;
    TextView titleId;
    TextView dateId;
    ImageView imageId;
    TextView descriptionId;
    Button buttonPreced;
    Button buttonSuivant;
    int index =0;
    int indexMax;

    Bdd_RSS bdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bdd = new Bdd_RSS(this, "article.db", null, 1);
        bdd.open();

        handler = new MyRSSsaxHandler();
        handler.setUrl("https://www.lemonde.fr/international/rss_full.xml ");
        Toast.makeText(this,"chargement image :"+handler.getNumber(), Toast.LENGTH_LONG).show();
        new DownloadRssTask(this).execute(handler);

        titleId = findViewById(R.id.imageTitle);
        dateId = findViewById(R.id.imageDate);
        imageId = findViewById(R.id.imageDisplay);
        descriptionId = findViewById(R.id.imageDescription);
        buttonPreced = findViewById(R.id.buttonPrev);
        buttonSuivant = findViewById(R.id.buttonNext);
        
        buttonPreced.setOnClickListener(this);
        buttonSuivant.setOnClickListener(this);
    }

    public void insertInDb(String title, String desc, String date, String url){
        bdd.insertItem(title, desc, date, url);
    }

    public void setDisplay(Item item, int max){
        titleId.setText(item.getTitle());
        dateId.setText(item.getDate());
        imageId.setImageBitmap(item.getImage());
        descriptionId.setText(item.getDescription());
        indexMax = max;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonPrev:
                if(index != 0) {
                    index--;
                }
                break;
            case R.id.buttonNext:
                if(index != indexMax-1) {
                    index++;
                }
                break;
        }
        Item item = handler.getListItem().get(index);
        setDisplay(item, indexMax);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.actualityId){
            Toast.makeText(this, "Actualité", Toast.LENGTH_LONG).show();
            handler.setUrl("https://www.lemonde.fr/rss/une.xml");
        }
        else if(id == R.id.economicId){
            Toast.makeText(this, "Economie", Toast.LENGTH_LONG).show();
            handler.setUrl("https://www.lemonde.fr/economie/rss_full.xml");
        }
        else if(id == R.id.sportId){
            Toast.makeText(this, "Sport", Toast.LENGTH_LONG).show();
            handler.setUrl("https://www.lemonde.fr/sport/rss_full.xml");
        }
        new DownloadRssTask(this).execute(handler);
        Toast.makeText(this, "Fin du chargement", Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }
}
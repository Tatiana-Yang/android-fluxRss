package fr.ensicaen.ecole.fluxrss.model;

import android.os.AsyncTask;

import fr.ensicaen.ecole.fluxrss.MainActivity;

public class DownloadRssTask extends AsyncTask<MyRSSsaxHandler, Void, MyRSSsaxHandler> {
    private MainActivity monActivity;
    public DownloadRssTask(MainActivity monActivity){
        this.monActivity = monActivity;
    }

    protected MyRSSsaxHandler doInBackground(MyRSSsaxHandler... handler){
        handler[0].processFeed();
        return handler[0];
    }
    /** The system calls this to perform work in the UI thread and delivers
     * the result from doInBackground() */
    protected void onPostExecute(MyRSSsaxHandler handler)
    {
        Item item = handler.getListItem().get(0);

        for(Item it : handler.getListItem()){
            monActivity.insertInDb(it.getTitle(), it.getDescription(), it.getDate(), it.getImageUrl());
        }
        monActivity.setDisplay(item, handler.getListItem().size());

    }
}
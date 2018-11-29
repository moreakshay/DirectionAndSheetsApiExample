package moreakshay.com.gpssheettask.helpers;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.Sheets;

public class AsyncFetcher extends AsyncTask<Void, Void, String> {

    public interface Listener{
        void executeInBackground();
        void executionComplete();
    }

    Context context;
    GoogleAccountCredential credential;
    Sheets sheets;
    Listener listener;

    public AsyncFetcher(Listener listener){
        this.listener = listener;
    }

    public AsyncFetcher(Context context, GoogleAccountCredential credential, Sheets sheets, Listener listener) {
        this.context = context;
        this.credential = credential;
        this.sheets = sheets;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        listener.executeInBackground();
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.executionComplete();
    }

}

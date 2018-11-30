package moreakshay.com.gpssheettask.helpers;

import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import moreakshay.com.gpssheettask.MapsActivity;

public class SheetsHelper{

    public interface Listener{
        void sheetsFetched();
        void sheetsRequestAuthorization(UserRecoverableAuthIOException e);
        void sheetFailed();
    }

    private Sheets sheets;
    private int directionColumnSize;
    private Listener listener;
    private final String spreadsheetId = "1-PeQnzOktZKK74MgI_DuN351wt6h8oAlMVl3nV_1Jqs";

    public SheetsHelper(Listener listener, Sheets sheets) {
        this.listener = listener;
        this.sheets = sheets;
    }

    public void getSheets(){
        new AsyncFetcher(new AsyncFetcher.Listener() {
            @Override
            public void executeInBackground() {
                try {
                    String range = "Sheet1!C1:C";

                    ValueRange result = sheets.spreadsheets().values().get(spreadsheetId, range).execute();
                    List<List<Object>> values = result.getValues();
                    directionColumnSize = values.size() + 2;
                } catch (UserRecoverableAuthIOException e) {
                    listener.sheetsRequestAuthorization(e);
                } catch (Exception e) {
                    listener.sheetFailed();
//            cancel(true);
                    e.printStackTrace();
                }
            }

            @Override
            public void executionComplete() {
                listener.sheetsFetched();
            }
        }).execute();
    }

    public void writeSheet(final Object time, final Object source, final List<Object> directions){
        new AsyncFetcher(new AsyncFetcher.Listener() {
            @Override
            public void executeInBackground() {
                try {
                    String timeAndSourceRange = "A"+ directionColumnSize+":B" +directionColumnSize;
                    ValueRange valueRange = new ValueRange();
                    valueRange.setValues(
                            Arrays.asList(
                                    Arrays.asList(time, source)
                            ));

                    sheets.spreadsheets().values().update(spreadsheetId, timeAndSourceRange, valueRange)
                            .setValueInputOption("RAW")
                            .execute();

                    String directionColumnRange = "C"+directionColumnSize+":C";
                    ValueRange directionValueRange = new ValueRange();

                    List<List<Object>> objects = new ArrayList<>();
                    ArrayList<Object> objects1;
                    for(Object o: directions){
                        objects1 = new ArrayList<>();
                        objects1.add(o);
                        objects.add(objects1);
                    }

                    directionValueRange.setValues(objects);
                    sheets.spreadsheets().values().update(spreadsheetId, directionColumnRange, directionValueRange)
                            .setValueInputOption("RAW")
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void executionComplete() {

            }
        }).execute();

    }

}

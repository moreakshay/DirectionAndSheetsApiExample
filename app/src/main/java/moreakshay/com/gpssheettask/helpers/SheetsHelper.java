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

    private MapsActivity mapsActivity;
    private Sheets sheets;
    private int directionColumnSize;

    private final String spreadsheetId = "1-PeQnzOktZKK74MgI_DuN351wt6h8oAlMVl3nV_1Jqs";

    public SheetsHelper(MapsActivity mapsActivity, Sheets sheets) {
        this.mapsActivity = mapsActivity;
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
                    for(int i=0; i<values.size(); i++){
                        List<Object> cells = values.get(i);
                        for(int j=0; j<cells.size(); j++){
                            Object cell = cells.get(j);
                            Log.d("SHEET", cell.toString());
                        }
                    }
                } catch (UserRecoverableAuthIOException e) {
                    mapsActivity.startActivityForResult(e.getIntent(), MapsActivity.REQUEST_AUTHORIZATION);
                } catch (Exception e) {
//            cancel(true);
                    e.printStackTrace();
                }
            }

            @Override
            public void executionComplete() {

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
                    for(Object o: directions){
                        ArrayList<Object> objects1 = new ArrayList<>();
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

   /* @Override
    public void executeInBackground() {
        try {

            String range = "Sheet1!C1:C";

            ValueRange result = sheets.spreadsheets().values().get(spreadsheetId, range).execute();
            int numRows = result.getValues() != null ? result.getValues().size() : 0;
            System.out.printf("%d rows retrieved.", numRows);
            List<List<Object>> values = result.getValues();
            directionColumnSize = values.size() + 2;
            for(int i=0; i<values.size(); i++){
                List<Object> cells = values.get(i);
                for(int j=0; j<cells.size(); j++){
                    Object cell = cells.get(j);
                    Log.d("tag", cell.toString());
                }
            }

            ValueRange valueRange = new ValueRange();
            Object c1 = "C6";
            Object b2 = "C7";
            valueRange.setValues(
                    Arrays.asList(
                            Arrays.asList(c1),
                            Arrays.asList(b2)));

            *//*sheets.spreadsheets().values().update(spreadsheetId, "C"+ cSize+":C", valueRange)
                    .setValueInputOption("RAW")
                    .execute();*//*

        } catch (UserRecoverableAuthIOException e){
            mapsActivity.startActivityForResult(e.getIntent(), MapsActivity.REQUEST_AUTHORIZATION);
        } catch (Exception e) {
//            cancel(true);
            e.printStackTrace();
        }
    }

    @Override
    public void executionComplete() {

    }*/
}

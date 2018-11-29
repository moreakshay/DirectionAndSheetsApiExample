package moreakshay.com.gpssheettask;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.Arrays;
import java.util.List;

import moreakshay.com.gpssheettask.helpers.AsyncFetcher;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements AsyncFetcher.Listener{

    Button done;
    static final int REQUEST_PERMISSION_LOCATION = 990;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };
    private GoogleAccountCredential mCredential;
    private Sheets sheets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        done = findViewById(R.id.button_done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new AsyncFetcher(MainActivity.this).execute();
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
        done.setEnabled(false);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        validation();

    }

    private void validation() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (! isDeviceOnline()) {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        } else if(mCredential.getSelectedAccountName() == null){
            chooseAccount();
        } else if(!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            EasyPermissions.requestPermissions(this, "This app needs access to Location",
                    REQUEST_PERMISSION_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }else {
            getSharedPreferences("myAccountName",Context.MODE_PRIVATE).edit()
                    .putString(PREF_ACCOUNT_NAME,mCredential.getSelectedAccountName()).apply();
            done.setEnabled(true);
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

//    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getSharedPreferences("myAccountName",Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                validation();
            } else {
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account.",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
                } else {
                    validation();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getSharedPreferences("myAccountName",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        validation();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    validation();
                }
                break;
            case REQUEST_PERMISSION_GET_ACCOUNTS:
                if (resultCode == RESULT_OK) {
                    validation();
                }
                break;
            case REQUEST_PERMISSION_LOCATION:
                if (resultCode == RESULT_OK) {
                    validation();
                }
                break;

            /*case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;*/
        }
    }

    void getSheets(){
        try {
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            sheets = new Sheets
                    .Builder(httpTransport,jsonFactory,mCredential)
                    .setApplicationName(getString(R.string.app_name)).build();

            String range = "Sheet1!A1";
            final String spreadsheetId = "1-PeQnzOktZKK74MgI_DuN351wt6h8oAlMVl3nV_1Jqs";

            ValueRange result = sheets.spreadsheets().values().get(spreadsheetId, range).execute();
            int numRows = result.getValues() != null ? result.getValues().size() : 0;
            System.out.printf("%d rows retrieved.", numRows);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void executeInBackground() {
        try {
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            sheets = new Sheets
                    .Builder(httpTransport,jsonFactory,mCredential)
                    .setApplicationName(getString(R.string.app_name)).build();

            String range = "Sheet1!C1:C";
            final String spreadsheetId = "1-PeQnzOktZKK74MgI_DuN351wt6h8oAlMVl3nV_1Jqs";

            ValueRange result = sheets.spreadsheets().values().get(spreadsheetId, range).execute();
            int numRows = result.getValues() != null ? result.getValues().size() : 0;
            System.out.printf("%d rows retrieved.", numRows);
            List<List<Object>> values = result.getValues();
            int cSize = values.size() + 2;
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

            /*sheets.spreadsheets().values().update(spreadsheetId, "C"+ cSize+":C", valueRange)
                    .setValueInputOption("RAW")
                    .execute();*/


        } catch (UserRecoverableAuthIOException e){
            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        } catch (Exception e) {
//            cancel(true);
            e.printStackTrace();
        }
    }

    @Override
    public void executionComplete() {

    }

    private class FetchAttendance extends AsyncTask<Void, Void, String> {

        private Sheets sheets = null;
        private Context context;

        private String mDate;
        Exception mError;

        FetchAttendance(GoogleAccountCredential credential,Context context ,String mDate){
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            sheets = new Sheets
                    .Builder(httpTransport,jsonFactory,mCredential)
                    .setApplicationName(getString(R.string.app_name)).build();

//            this.mDate = mDate;
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
//            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String range = "Sheet1!C1:C";
                final String spreadsheetId = "1-PeQnzOktZKK74MgI_DuN351wt6h8oAlMVl3nV_1Jqs";

                ValueRange result = sheets.spreadsheets().values().get(spreadsheetId, range).execute();
                int numRows = result.getValues() != null ? result.getValues().size() : 0;
                System.out.printf("%d rows retrieved.", numRows);
                List<List<Object>> values = result.getValues();
                int cSize = values.size() + 2;
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

                sheets.spreadsheets().values().update(spreadsheetId, "C"+ cSize+":C", valueRange)
                        .setValueInputOption("RAW")
                        .execute();


            } catch (UserRecoverableAuthIOException e){
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (Exception e) {
                mError = e;
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try
            {

            }catch (Exception e)
            {
                mError = e;
                cancel(true);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
//            progressDialog.hide();
            if (mError != null) {
                if (mError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mError)
                                    .getConnectionStatusCode());
                } else if (mError instanceof UserRecoverableAuthIOException) {

                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(context, mError.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Request Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

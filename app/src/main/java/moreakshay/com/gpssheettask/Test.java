package moreakshay.com.gpssheettask;

import android.util.Log;

import com.directions.route.GoogleParser;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import moreakshay.com.gpssheettask.helpers.AsyncFetcher;


public class Test {


    /*void testRoute() {
        AsyncFetcher asyncFetcher = new AsyncFetcher(new AsyncFetcher.Listener() {
            @Override
            public void executeInBackground() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL("https://script.google.com/macros/s/AKfycbzPIsh-Qy1o-6RG9sdlheBJ53ivyLuidvds4kEKzWbtSZ0Cqk0/exec");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                        Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                    }
                    String result = buffer.toString();
                    Route route = new Gson().fromJson(result, Route.class);
                    route.getLegs().get(0).getSteps().get(0).getHtmlInstructions();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void executionComplete() {

            }
        });
        asyncFetcher.execute();
    }*/

    void testRoute(final String url) {
        AsyncFetcher asyncFetcher = new AsyncFetcher(new AsyncFetcher.Listener() {
            @Override
            public void executeInBackground() {
                ArrayList result = new ArrayList();

                try {
                    result = (new GoogleParser(url)).parse();

                    Log.d("fetched", "fetched");

                } catch (RouteException var4) {
                    var4.printStackTrace();
                }
            }

            @Override
            public void executionComplete() {

            }
        });
        asyncFetcher.execute();
    }

    public void testList() {
        List<List<String>> lists = Arrays.asList(
                Arrays.asList("F1"),
                Arrays.asList("F2"),
                Arrays.asList("F3")
        );

        List<List<String>> total = new ArrayList<>();
        List<String> stringList1 = new ArrayList<>();
        stringList1.add("f1");
        ArrayList<String> stringList2 = new ArrayList<>();
        stringList2.add("f2");
        ArrayList<String> stringList3 = new ArrayList<>();
        stringList3.add("f3");

        total.add(stringList1);
        total.add(stringList2);
        total.add(stringList3);

        Log.d("list", "comething");
    }


}


/*String url = "https://api.simbabeer.com/api/test/test/get_map_response?url=https://maps.googleapis.com/maps/api/directions/json?origin=19.134122,72.869662%26destination=19.112688,72.861171%26key=AIzaSyCmFbhEs4f1FO5ipvLinxKbHzFupDpdlAs";
        new Test().testRoute(url);*/
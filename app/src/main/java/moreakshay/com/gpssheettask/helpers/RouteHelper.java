package moreakshay.com.gpssheettask.helpers;

import android.graphics.Color;
import android.util.Log;

import com.directions.route.GoogleParser;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Segment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class RouteHelper {

    public interface Listener{
        void onRouteFetched(List<Route> routes);
    }

    ArrayList<Object> directions;
    List<Listener> listeners = new ArrayList<>();

    public void regiesterListener(Listener listener){
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener){
        listeners.remove(listener);
    }


    public PolylineOptions getPolylineOptions(Route route){
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.BLUE);
            polyOptions.width(5);
            List<LatLng> path = route.getPoints();
            polyOptions.addAll(path);

        List<Segment> segments = route.getSegments();
        directions = new ArrayList<>();
        for(Segment segment: segments){
            directions.add(segment.getInstruction());
            Log.d("SEGMENT", segment.getInstruction());
        }

        return polyOptions;
    }

    public List<Object> getDirections(){
        if(directions != null && !directions.isEmpty()){
            return directions;
        } else {
            return null;
        }
    }

    /*public List<Route> getRoutes(final String url){
        ArrayList result;
        new AsyncFetcher(new AsyncFetcher.Listener() {
            @Override
            public void executeInBackground() {

                try {
                    result = (new GoogleParser(url)).parse();
                } catch (RouteException var4) {
                    var4.printStackTrace();
                }
            }

            @Override
            public void executionComplete() {

            }
        }).execute();
        return result;
    }*/

    public void getRoute(final String url){
        AsyncFetcher asyncFetcher = new AsyncFetcher(new AsyncFetcher.Listener() {
            ArrayList result = new ArrayList();
            @Override
            public void executeInBackground() {

                try {
                    result = (new GoogleParser(url)).parse();
                    Log.d("fetched", "fetched");


                } catch (RouteException var4) {
                    var4.printStackTrace();
                }
            }

            @Override
            public void executionComplete() {
                for(Listener listener: listeners){
                    listener.onRouteFetched(result);
                }
            }
        });
        asyncFetcher.execute();
    }
}

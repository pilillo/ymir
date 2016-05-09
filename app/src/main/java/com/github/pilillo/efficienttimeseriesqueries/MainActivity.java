package com.github.pilillo.efficienttimeseriesqueries;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeMap;

import com.github.pilillo.ymir.model.realmio.RealmIO;
import com.github.pilillo.ymir.timeseries.TSManager;

public class MainActivity extends AppCompatActivity {

    private TSManager tsm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // preload the data in backgroud so that the user does not notice any delay
        JSONRetrievalTask t = new JSONRetrievalTask();
        t.execute((Void) null);
    }

    public class JSONRetrievalTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            tsm = TSManager.getInstance();

            try {
                loadAllDataFromFile(getResources().openRawResource(R.raw.smart_metering_json));
                for(String key : tsm.getTimeseries().keySet()){
                    System.out.println("Created timeserie for "+key+" with "+tsm.getTimeseries().get(key).size()+" entries");
                }

                RealmIO rio = RealmIO.getInstance(getApplicationContext(), true);
                tsm.serializeToDatabase(rio);
                System.out.println("Serialized to database!");

                tsm.serializeFromDatabase(rio);
                for(String key : tsm.getTimeseries().keySet()){
                    System.out.println("Found timeserie for " + key + " with " + tsm.getTimeseries().get(key).size() + " entries");
                }
            }catch(Exception e){ e.printStackTrace(); }
            return true;
        }

    }

    public boolean loadAllDataFromFile(InputStream is) throws IOException, JSONException {
        boolean result = false;

        String jsonString = "";

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            jsonString = sb.toString();

        } finally {
            br.close();
        }

        /*
        In this example, we parse a local file, used to indicate consumption in different tariffs, with the following format:
        { "status": { "executionTime": 3899, "description": "", "code": 200 },
          "data": {"smartMeterData": [
                        {"dateAndTime": "1420845300000", "consumptionHT": 0, "consumptionNT": 0.1 },
                   ]},
          "messages": {} }
        */

        // parse the resulting JSON payload
        JSONObject jsonObj = new JSONObject(jsonString);
        JSONArray messages = jsonObj.getJSONArray("messages");
        JSONArray dataSamples = jsonObj.getJSONObject("data").getJSONArray("smartMeterData");

        if(messages.length() == 0) {
            result = true;  // everything went allright

            JSONObject entry;
            // use the header to parse the tariffs available to the smart meter
            if (dataSamples.length() > 0) {
                entry = (JSONObject) dataSamples.get(0);

                Iterator<String> keyIterator = entry.keys();
                while (keyIterator.hasNext()) {
                    String key = (String) keyIterator.next();

                    if (!key.equals("dateAndTime")) {
                        //LinkedHashMap<String,Double> series = new LinkedHashMap<String, Double>();
                        TreeMap<Long, Double> series = new TreeMap<>();
                        //System.out.print("Putting series for "+key+": "+ tsm.getTimeseries().keySet().size()+", now has ");
                        tsm.getTimeseries().put(key, series);
                        //System.out.println(tsm.getTimeseries().keySet().size());
                    }
                }
            }

            // parse all entries and add the values to the local memory store
            for (int i = 0; i < dataSamples.length(); i++) {
                entry = (JSONObject) dataSamples.get(i);
                // append the value to each existing timeseries
                for (String t : tsm.getTimeseries().keySet()) {
                    Long timestamp = Long.parseLong(entry.getString("dateAndTime"));
                    tsm.addToDataStore(t,                       // timeseries name (string)
                                        timestamp,              // timestamp (long)
                                        entry.getDouble(t));    // value (double)
                    //System.out.println("Added entry for "+t+": ("+timestamp+", "+tsm.getTimeseries().get(t).get(timestamp)+")");
                }
            }
        }

        return result;
    }
}

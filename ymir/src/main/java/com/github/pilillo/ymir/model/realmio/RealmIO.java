package com.github.pilillo.ymir.model.realmio;

import android.content.Context;

import com.github.pilillo.ymir.model.TimeserieSerializer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RealmIO
    implements TimeserieSerializer
{

    private static RealmIO instance;
    private Realm realm;


    private RealmIO(Context context, boolean overwrite){

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();

        // remove the realm previously created for this context (activity)
        if(overwrite) Realm.deleteRealm(realmConfiguration);

        // create a new realm (space where all realm objects are mantained)
        realm = Realm.getInstance(realmConfiguration);
    }

    public static RealmIO getInstance(Context context, boolean overwrite){
        if(instance == null){
            instance = new RealmIO(context, overwrite);
        }
        return instance;
    }

    public void createTransaction(){
        realm.beginTransaction();
    }

    public void commitTransaction(){
        realm.commitTransaction();
    }

    public RealmObject getRealmObjectFromClass(Class oClass){
        return realm.createObject(oClass);
    }

    public RealmObject getRealmObjectFromJson(Class oClass, JSONObject obj){
        return realm.createObjectFromJson(oClass, obj);
    }

    public Realm getRealm(){
        return realm;
    }

    public RealmResults getAllEntriesByClass(Class oClass){
        // query: SELECT * FROM oClass
        return realm.where(oClass).findAll();
    }

    public void removeQueryResult(RealmResults resultingEntries){
        // remove all resultingEntries from the collection resultingEntries (clear)
        resultingEntries.removeAll(resultingEntries);
    }

    @Override
    public void serializeTimeserie(String name, SortedMap<Long,Double> timeserie){

        // initiate transaction on the database
        realm.beginTransaction();

        // create a timeserie object
        RealmTimeserie ts = realm.createObject(RealmTimeserie.class);
        ts.setTsName(name);

        RealmDataPoint dp = null;
        for(Long timestamp : timeserie.keySet()){
            dp = realm.createObject(RealmDataPoint.class);
            dp.setTimestamp(timestamp);
            dp.setValue(timeserie.get(timestamp));
            ts.getDataPoints().add(dp);
        }
        // commit resulting objects on the database
        realm.commitTransaction();
    }

    @Override
    public TreeMap<Long, Double> getTimeserieFromDatabase(String name) {

        TreeMap<Long,Double> sortedTimeserie = new TreeMap<>();

        RealmResults<RealmTimeserie> ts = realm.where(RealmTimeserie.class).equalTo("tsName", name).findAll();

        // there is only 1 timeserie with that name (UNIQUE)
        if(ts.size() > 0){
            for(RealmDataPoint dp : ts.get(0).getDataPoints()){
                sortedTimeserie.put( dp.getTimestamp(), dp.getValue() );
            }
        }

        return sortedTimeserie;
    }

    @Override
    public List<String> getTimeseriesNames(){
        RealmResults<RealmTimeserie> rts = realm.where(RealmTimeserie.class).findAll();

        ArrayList<String> names = new ArrayList<>();

        for(RealmTimeserie ts : rts){
            names.add( ts.getTsName() );
        }

        return names;
    }

}

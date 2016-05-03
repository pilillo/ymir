package org.altervista.monacchi.efficienttimeseriesqueries.model.realmio;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmTimeserie extends RealmObject {
    private String tsName;
    private RealmList<RealmDataPoint> dataPoints;

    public void setTsName(String tsName){
        this.tsName = tsName;
    }

    public String getTsName(){
        return this.tsName;
    }

    public RealmList<RealmDataPoint> getDataPoints(){
        return this.dataPoints;
    }

    public void setDataPoints(RealmList<RealmDataPoint> dataPoints){
        this.dataPoints = dataPoints;
    }
}

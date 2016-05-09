package com.github.pilillo.ymir.model.realmio;

import io.realm.RealmObject;

public class RealmDataPoint extends RealmObject {
    private Long timestamp;
    private Double value;

    public Long getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(Long timestamp){
        this.timestamp = timestamp;
    }

    public Double getValue(){
        return value;
    }

    public void setValue(Double value){
        this.value = value;
    }
}

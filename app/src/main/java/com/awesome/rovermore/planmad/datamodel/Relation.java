package com.awesome.rovermore.planmad.datamodel;

import com.google.gson.annotations.SerializedName;

public class Relation {

    @SerializedName("@id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

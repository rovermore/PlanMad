package com.awesome.rovermore.planmad.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventFeed {

    @SerializedName("@context")
    private EventContext eventContext;
    @SerializedName("@graph")
    private List<Graph> graphList = new ArrayList<>();

    public EventContext getEventContext() {
        return eventContext;
    }

    public void setEventContext(EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public List<Graph> getGraphList() {
        return graphList;
    }

    public void setGraphList(List<Graph> graphList) {
        this.graphList = graphList;
    }
}

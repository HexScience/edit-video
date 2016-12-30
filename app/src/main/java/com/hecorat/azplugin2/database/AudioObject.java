package com.hecorat.azplugin2.database;

/**
 * Created by Bkmsx on 12/13/2016.
 */

public class AudioObject {
    public int id, projectId;
    public String path, startTime, endTime, left,
            orderInList, volume, volumePreview;

    public AudioObject(){}

    public AudioObject(int projectId, String path, String startTime,
                       String endTime, String left, String order,
                       String volume, String volumePreview){
        this.projectId = projectId;
        this.path = path;
        this.startTime = startTime;
        this.endTime = endTime;
        this.left = left;
        this.orderInList = order;
        this.volume = volume;
        this.volumePreview = volumePreview;
    }
}

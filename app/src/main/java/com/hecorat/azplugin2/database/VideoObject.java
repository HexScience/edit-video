package com.hecorat.azplugin2.database;

/**
 * Created by Bkmsx on 12/13/2016.
 */

public class VideoObject {
    public int id;
    public String path, startTime, endTime, left,
            orderInList, volume, volumePreview,
            leftSide, rightSide, bottomSide, topSide;

    public VideoObject(){}

    public VideoObject(int projectId, String path, String startTime,
                       String endTime, String left, String orderInList,
                        String volume, String volumePreview){
        this.path = path;
        this.startTime = startTime;
        this.endTime = endTime;
        this.left = left;
        this.orderInList = orderInList;
        this.volume = volume;
        this.volumePreview = volumePreview;
    }
}

package com.hecorat.editvideo.database;

/**
 * Created by Bkmsx on 12/13/2016.
 */

public class VideoObject {
    public int id, projectId;
    public String path, startTime, endTime, left, orderInList;

    public VideoObject(){}

    public VideoObject(int projectId, String path, String startTime,
                       String endTime, String left, String orderInList){
        this.projectId = projectId;
        this.path = path;
        this.startTime = startTime;
        this.endTime = endTime;
        this.left = left;
        this.orderInList = orderInList;
    }
}

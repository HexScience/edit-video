package com.hecorat.editvideo.database;

/**
 * Created by Bkmsx on 12/13/2016.
 */

public class AudioObject {
    public int id, projectId;
    public String path, startTime, endTime, left, order;

    public AudioObject(){}

    public AudioObject(int projectId, String path, String startTime,
                       String endTime, String left, String order){
        this.projectId = projectId;
        this.path = path;
        this.startTime = startTime;
        this.endTime = endTime;
        this.left = left;
        this.order = order;
    }
}

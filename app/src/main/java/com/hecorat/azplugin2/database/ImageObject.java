package com.hecorat.azplugin2.database;

/**
 * Created by Bkmsx on 12/13/2016.
 */

public class ImageObject {
    public int id;
    public String path, left, right, inLayoutImage, orderInLayout, orderInList;
    public String x, y, scale, rotation;

    public ImageObject(){}

    public ImageObject(int projectId, String path, String left, String right,
                String inLayoutImage, String orderInLayout, String orderInList,
                String x, String y, String scale, String rotation) {
        this.path = path;
        this.left = left;
        this.right = right;
        this.inLayoutImage = inLayoutImage;
        this.orderInLayout = orderInLayout;
        this.orderInList = orderInList;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = rotation;
    }
}

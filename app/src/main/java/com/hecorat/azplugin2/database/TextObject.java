package com.hecorat.azplugin2.database;

/**
 * Created by Bkmsx on 12/13/2016.
 */

public class TextObject {
    public int id, projectId;
    public String text, left, right, inLayoutImage, orderInLayout, orderInList;
    public String x, y, scale, rotation, size, fontPath, fontColor, boxColor, fontId;

    public TextObject(){}

    public TextObject(int projectId, String text, String left, String right,
                      String inLayoutImage, String orderInLayout, String orderInList,
                      String x, String y, String scale, String rotation, String size,
                      String fontPath, String fontColor, String boxColor) {
        this.projectId = projectId;
        this.text = text;
        this.left = left;
        this.right = right;
        this.inLayoutImage = inLayoutImage;
        this.orderInLayout = orderInLayout;
        this.orderInList = orderInList;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = rotation;
        this.size = size;
        this.fontPath = fontPath;
        this.fontColor = fontColor;
        this.boxColor = boxColor;
    }
}

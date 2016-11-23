package com.hecorat.editvideo.export;

import android.media.Image;

import java.util.ArrayList;

/**
 * Created by TienDam on 11/22/2016.
 */

public class ImageFilter {

    public static String getFilter(String input, String output, ArrayList<ExportTask.ImageHolder> listImage, int order){
        String filter="";
        for (int i=0; i<listImage.size(); i++){
            ExportTask.ImageHolder image = listImage.get(i);
            int index = i+order;
            String in = i==0?input:"[out"+index+"]";
            String out = i==listImage.size()-1?output:"[out"+(index+1)+"];";
            filter += prepareImage(image, index);
            filter += addImage(in, out, image, index);
        }
        return filter;
    }

    private static String prepareImage(ExportTask.ImageHolder image, int index){
        String filter = "["+index+":v]scale="+image.width+":"
                    +image.height+",rotate="+image.rotate+":c=none:ow=rotw("+image.rotate
                    +"):oh=roth("+image.rotate+")[ov"+index+"];";
        return filter;
    }

    private static String addImage(String input, String output, ExportTask.ImageHolder image, int index){
        String filter= input+"[ov"+index+"]overlay="+image.x
                    +":"+image.y+":enable='between=(t,"+image.startTime+","
                    +image.endTime+")'"+output;
        return filter;
    }
}

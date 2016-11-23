package com.hecorat.editvideo.export;

import com.hecorat.editvideo.timeline.ExtraTimeLine;

import java.util.ArrayList;

/**
 * Created by TienDam on 11/22/2016.
 */

public class ImageFilter {
    public static String filter="";
    public static String getFilter(String input, String output, ArrayList<ExportTask.ImageHolder> listImage){
        filterImage(listImage);
        addImage(input, output, listImage);
        return filter;
    }

    private static void filterImage(ArrayList<ExportTask.ImageHolder> listImage){
        for (int i=0; i<listImage.size(); i++){
            ExportTask.ImageHolder image = listImage.get(i);
            filter+="["+(i+1)+":v]scale="+image.width+":"
                    +image.height+",rotate="+image.rotate+":c=none:ow=rotw("+image.rotate
                    +"):oh=roth("+image.rotate+")[ov"+(i+1)+"];";
        }
    }

    private static void addImage(String input, String output, ArrayList<ExportTask.ImageHolder> listImage){
        for (int i=0; i<listImage.size(); i++){
            ExportTask.ImageHolder image = listImage.get(i);
            filter+= (i==0?input:"[out"+(i+1)+"]")+"[ov"+(i+1)+"]overlay="+image.x
                    +":"+image.y+":enable='between=(t,"+image.startTime+","
                    +image.endTime+")'"+(i==listImage.size()-1?output:"[out"+(i+1)+"]");
        }
    }
}

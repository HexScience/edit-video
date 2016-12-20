package com.hecorat.editvideo.export;

import com.hecorat.editvideo.timeline.ExtraTL;

import java.util.ArrayList;

/**
 * Created by TienDam on 11/22/2016.
 */

public class ImageFilter {

    public static String getFilter(String input, String output, ArrayList<ExtraTL> listImage, int order){
        String filter="";
        for (int i=0; i<listImage.size(); i++){
            ImageHolder image = listImage.get(i).imageHolder;
            int index = i+order;
            String in = i==0?input:"[out"+index+"]";
            String out = i==listImage.size()-1?output:"[out"+(index+1)+"];";
            filter += prepareImage(image, index);
            filter += addImage(in, out, image, index);
        }
        return filter;
    }

    private static String prepareImage(ImageHolder image, int index){
        String filter = "["+index+":v]scale="+image.width+":"
                    +image.height+",rotate="+image.rotate+":c=none:ow=rotw("+image.rotate
                    +"):oh=roth("+image.rotate+")[ov"+index+"];";
        return filter;
    }

    private static String addImage(String input, String output, ImageHolder image, int index){
        String filter= input+"[ov"+index+"]overlay="+image.x
                    +":"+image.y+":enable='between=(t,"+image.startInTimeLineMs +","
                    +image.endInTimeLineMs +")'"+output;
        return filter;
    }
}

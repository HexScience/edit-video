package com.hecorat.editvideo.export;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by TienDam on 11/23/2016.
 */

public class TextFilter {
    public static String getFilter(String input, String output,
                                   ArrayList<ExportTask.TextHolder> listText){
        String filter="";
        for (int i=0; i<listText.size(); i++){
            ExportTask.TextHolder text = listText.get(i);
            String in = i==0? input:"[out"+i+"]";
            String out = i==listText.size()-1?output:"[out"+(i+1)+"];";
            filter += "color=black:100x100[c];[c]"+in+"scale2ref[ct][mv];"+
                    "[ct]drawtext=fontfile="+text.fontPath+":textfile="+text.textFile+
                    ":fontsize="+text.size+":fontcolor="+text.fontColor+":box=1"+
                    ":boxcolor="+text.boxColor+",split[text][alpha];[text][alpha]alphamerge"+
                    ",rotate="+text.rotate+":ow=rotw("+text.rotate+")"+
                    ":oh=roth("+text.rotate+")"+":c=none[txta];"+
                    "[mv][txta]overlay=x='min(0,-H*sin("+text.rotate+"))+"+text.x+"'"+
                    ":y='min(0,W*sin("+text.rotate+"))+"+text.y+"'"+
                    ":enable='between=(t,"+text.startTime+","+text.endTime+")'"+
                    ":shortest=1"+out;
        }
        return filter;
    }
}

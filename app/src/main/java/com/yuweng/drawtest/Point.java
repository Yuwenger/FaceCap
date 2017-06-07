package com.yuweng.drawtest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuweng on 2017/6/6.
 */

public class Point {
    final static int LINE_TYPE_1 = 1;
    final static int LINE_TYPE_2 = 2;
    final static int LINE_TYPE_3 = 3;
    final static int LINE_TYPE_4 = 4;
    final static double PI = 3.1415926;

    public double x;
    public double y;
    public Point(double x,double y){
        this.x = x;
        this.y = y;
    }

    public static List<Point> getLinePoints(Point start_point, Point end_point){
        int line_type;
        double step_ = 5;
        double x1 = start_point.x;
        double y1 = start_point.y;
        double x2 = end_point.x;
        double y2 = end_point.y;

        double delta_y;
        double delta_x;
        double length;
        double angle;
        double step = step_;


        List<Point> list = new ArrayList<Point>();
        if(start_point.x<= end_point.x && start_point.y>=end_point.y){
            line_type = LINE_TYPE_1;
        }else if(start_point.x>=end_point.x && start_point.y>=end_point.y){
            line_type = LINE_TYPE_2;
        }else if(start_point.x>= end_point.x && start_point.y<= end_point.y){
            line_type = LINE_TYPE_3;
        }else{
            line_type = LINE_TYPE_4;
        }

        switch (line_type){
            case LINE_TYPE_1:
                delta_y = y1-y2;
                delta_x = x2-x1;
                length = Math.sqrt(delta_x*delta_x+delta_y*delta_y);
                angle = Math.acos(delta_x/length);
                for(double len = 0;len<=length;len+=step){
                    double new_x = x1+len*Math.cos(angle);
                    double new_y = y1-len*Math.sin(angle);
                    Point p = new Point(new_x,new_y);
                    list.add(p);
                }
                return list;
            case LINE_TYPE_2:
                delta_y = y1-y2;
                delta_x = x1-x2;
                length = Math.sqrt(delta_x*delta_x+delta_y*delta_y);
                angle = Math.acos(delta_x/length);
                for(double len = 0;len<=length;len+=step){
                    double new_x = x1-len*Math.cos(angle);
                    double new_y = y1-len*Math.sin(angle);
                    Point p = new Point(new_x,new_y);
                    list.add(p);
                }
                return list;
            case LINE_TYPE_3:
                delta_y = y2-y1;
                delta_x = x1-x2;
                length = Math.sqrt(delta_x*delta_x+delta_y*delta_y);
                angle = Math.acos(delta_x/length);
                for(double len = 0;len<=length;len+=step){
                    double new_x = x1-len*Math.cos(angle);
                    double new_y = y1+len*Math.sin(angle);
                    Point p = new Point(new_x,new_y);
                    list.add(p);
                }
                return list;
            case LINE_TYPE_4:
                delta_y = y2-y1;
                delta_x = x2-x1;
                length = Math.sqrt(delta_x*delta_x+delta_y*delta_y);
                angle = Math.acos(delta_x/length);
                for(double len = 0;len<=length;len+=step){
                    double new_x = x1+len*Math.cos(angle);
                    double new_y = y1+len*Math.sin(angle);
                    Point p = new Point(new_x,new_y);
                    list.add(p);
                }
                return list;
        }

        return null;
    }
}

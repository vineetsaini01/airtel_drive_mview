package com.newmview.wifi.helper;

import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;


public class DirectionPath {
    //final static int diffe=100;
    private static final String TAG ="DirectionPath" ;

    public static Point getNextPoint(String currentDirection, String lastDirection, Point lastPoint, int diff)
    {
    /*   int absDif=Math.abs(old_diff);
       int  diff=absDif*1000;*/
   //     int diff=old_diff*10;
     //   Log.i(TAG,"Diff here finally "+diff +" ol diff "+old_diff);
        diff=30;
        
        Point point=new Point();
        if(!TextUtils.isEmpty(currentDirection) && !TextUtils.isEmpty(lastDirection))
        {
            //Anticlockwise
            
            if(currentDirection.equalsIgnoreCase("NW") && lastDirection.equalsIgnoreCase("N"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y- diff;


            }

            else
            if(currentDirection.equalsIgnoreCase("W") && lastDirection.equalsIgnoreCase("NW"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y- diff;


            }
            else
            if(currentDirection.equalsIgnoreCase("SW") && lastDirection.equalsIgnoreCase("W"))
            {

                point.x=lastPoint.x- diff;
                point.y=lastPoint.y- diff;


            }
            else
            if(currentDirection.equalsIgnoreCase("S") && lastDirection.equalsIgnoreCase("SW"))
            {

                point.x=lastPoint.x- diff;
                point.y=lastPoint.y- diff;


            }
            else
            if(currentDirection.equalsIgnoreCase("SE") && lastDirection.equalsIgnoreCase("S"))
            {

                point.x=lastPoint.x- diff;
                point.y=lastPoint.y+ diff;


            }
            else
            if(currentDirection.equalsIgnoreCase("E") && lastDirection.equalsIgnoreCase("SE"))
            {

                point.x=lastPoint.x- diff;
                point.y=lastPoint.y+ diff;


            }

            /*else
            if(currentDirection.equalsIgnoreCase("SE") && lastDirection.equalsIgnoreCase("N"))
            {

                point.x=lastPoint.x-diff;
                point.y=lastPoint.y+diff;


            }
          */
            else
            if(currentDirection.equalsIgnoreCase("NE") && lastDirection.equalsIgnoreCase("E"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y+ diff;


            }
            else
            if(currentDirection.equalsIgnoreCase("N") && lastDirection.equalsIgnoreCase("NE"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y+ diff;


            }




            //clockwise
           else if(lastDirection.equalsIgnoreCase("NW") && currentDirection.equalsIgnoreCase("N"))
            {

                point.x=lastPoint.x- diff;
                point.y=lastPoint.y+ diff;


            }
           else if(lastDirection.equalsIgnoreCase("N") && currentDirection.equalsIgnoreCase("NE"))
            {
                point.x=lastPoint.x- diff;
                point.y=lastPoint.y+ diff;


            }
            else if(lastDirection.equalsIgnoreCase("NE") && currentDirection.equalsIgnoreCase("E"))
            {
                point.x=lastPoint.x- diff;
                point.y=lastPoint.y+ diff;


            }
            else
            if(lastDirection.equalsIgnoreCase("E") && currentDirection.equalsIgnoreCase("SE"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y- diff;


            }
            else
            if(lastDirection.equalsIgnoreCase("SE") && currentDirection.equalsIgnoreCase("S"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y- diff;


            }
            else
            if(lastDirection.equalsIgnoreCase("S") && currentDirection.equalsIgnoreCase("SW"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y+ diff;


            }
            else
            if(lastDirection.equalsIgnoreCase("SW") && currentDirection.equalsIgnoreCase("W"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y+ diff;


            }
            else
            if(lastDirection.equalsIgnoreCase("W") && currentDirection.equalsIgnoreCase("NW"))
            {

                point.x=lastPoint.x- diff;
                point.y=lastPoint.y+ diff;


            }
            if(lastDirection.equalsIgnoreCase("SS") && currentDirection.equalsIgnoreCase("SS"))
            {

                point.x=lastPoint.x;
                point.y=lastPoint.y+ diff;


            }
           else if(lastDirection.equalsIgnoreCase("NN") && currentDirection.equalsIgnoreCase("NN"))
            {

                point.x=lastPoint.x;
                point.y=lastPoint.y- diff;


            }
           /* else if(lastDirection.equalsIgnoreCase("N") && currentDirection.equalsIgnoreCase("N"))
            {

                point.x=lastPoint.x;
                point.y=lastPoint.y-diff;


            }*/
         else   if(lastDirection.equalsIgnoreCase("EE") && currentDirection.equalsIgnoreCase("EE"))
            {

                point.x=lastPoint.x- diff;
                point.y=lastPoint.y;


            }
            else   if(lastDirection.equalsIgnoreCase("E") && currentDirection.equalsIgnoreCase("E"))
            {

                point.x=lastPoint.x- diff;
                point.y=lastPoint.y+ diff;


            }
            else   if(lastDirection.equalsIgnoreCase("W") && currentDirection.equalsIgnoreCase("W"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y+ diff;


            }
            else   if(lastDirection.equalsIgnoreCase("WW") && currentDirection.equalsIgnoreCase("WW"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y;


            }
           else if(lastDirection.equalsIgnoreCase("SW") && currentDirection.equalsIgnoreCase("SW"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y- diff;


            }
            else if(lastDirection.equalsIgnoreCase("NW") && currentDirection.equalsIgnoreCase("NW"))
            {

                point.x=lastPoint.x+ diff;
                point.y=lastPoint.y+ diff;


            }
            else if(lastDirection.equalsIgnoreCase("NE") && currentDirection.equalsIgnoreCase("NE"))
            {

                point.x=lastPoint.x- diff;
                point.y=lastPoint.y+ diff;


            }
            else if(lastDirection.equalsIgnoreCase("SE") && currentDirection.equalsIgnoreCase("SE"))
            {

                point.x=lastPoint.x- diff;
                point.y=lastPoint.y- diff;


            }
            else
            {
                point=null;
            }



        }
        if(point!=null) {
            Log.i(TAG,"X point "+point.x +" y point "+point.y);
            if (point.x <= CoordsBuilder.startX || point.x >= CoordsBuilder.endX || point.y <= CoordsBuilder.startY || point.y > CoordsBuilder.endY)
                return null;
            else
                return point;
        }
        return point;

    }
}

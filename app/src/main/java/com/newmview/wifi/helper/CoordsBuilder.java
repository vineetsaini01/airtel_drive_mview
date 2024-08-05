package com.newmview.wifi.helper;

import android.graphics.Point;
import android.util.Log;

import com.newmview.wifi.bean.RectCoords;

import java.text.DecimalFormat;

public class CoordsBuilder {

    private static final String TAG = "CoordsBuilder" ;
    public static final int startX=30;
    public static final int startY=30;
     public   static final int endY=1500;
     public static final int endX=980;
    private Point[] points_G1;
    private static final RectCoords G1_pointsList=addG1Points();
    private static final RectCoords G2_pointsList=addG2Points();
    private static final RectCoords G3_pointsList=addG3Points();
    private static final RectCoords G4_pointsList=addG4Points();
    private static final RectCoords G5_pointsList=addG5Points();
    private static final RectCoords G6_pointsList=addG6Points();
    private static final RectCoords G7_pointsList=addG7Points();
    private static final RectCoords G8_pointsList=addG8Points();
    
    public static String getGridId(int pX,int pY)
    {


       if(pointExistsInGrid(G1_pointsList.getX(),G1_pointsList.getY(),G1_pointsList.getX1(),G1_pointsList.getY1(),pX,pY))
       return "G1";
       else if(pointExistsInGrid(G2_pointsList.getX(),G2_pointsList.getY(),G2_pointsList.getX1(),
            G2_pointsList.getY1(),pX,pY))
        return "G2";

       else if(pointExistsInGrid(G3_pointsList.getX(),G3_pointsList.getY(),G3_pointsList.getX1(),
               G3_pointsList.getY1(),pX,pY))
           return "G3";

       else if(pointExistsInGrid(G4_pointsList.getX(),G4_pointsList.getY(),G4_pointsList.getX1(),
               G4_pointsList.getY1(),pX,pY))
           return "G4";

       else if(pointExistsInGrid(G5_pointsList.getX(),G5_pointsList.getY(),G5_pointsList.getX1(),
               G5_pointsList.getY1(),pX,pY))
           return "G5";

       else if(pointExistsInGrid(G6_pointsList.getX(),G6_pointsList.getY(),G6_pointsList.getX1(),
               G6_pointsList.getY1(),pX,pY))
           return "G6";

       else if(pointExistsInGrid(G7_pointsList.getX(),G7_pointsList.getY(),G7_pointsList.getX1(),
               G7_pointsList.getY1(),pX,pY))
           return "G7";

       else if(pointExistsInGrid(G8_pointsList.getX(),G8_pointsList.getY(),G8_pointsList.getX1(),
               G8_pointsList.getY1(),pX,pY))
           return "G8";

       else return null;


    }
    public static boolean pointExistsInGrid(int x, int y, int x1, int y1, int pointX, int pointY )
    {
        boolean exists=false;
        if((pointX>x && pointX<x1) && (pointY>y && pointY<y1)) {
            Log.i(TAG,"X is "+x +" y is "+y +" x1 is "+x1 +" y1 is "+y1+" pontx "+pointX +" pointy "+pointY);
            exists = true;
        }

        return exists;
        
    }

    public static String newGetGridId(int startX, int startY, int endX, int endY, int pointX, int pointY)
    {
        for(int j = 0; j < 4; j++)
        {
            int y1 = startY + (j)*((endY - startY)/4);
            int y2 = startY + (j+1)*((endY - startY)/4);
            for (int i = 0; i<2; i++)
            {
                int x1 = startX + (i)*((endX - startX)/2);
                int x2 = startX + (i+1)*((endX - startX)/2);
                if((pointX >= x1 && pointX <= x2) && (pointY >= y1 && pointY <= y2)) {
                    int gridID = 2 * j + i + 1;
                    return "G" + gridID;
                }
            }
        }
        return null;
    }

    public static String getQuadId(int startX, int startY, int endX, int endY, float wifiX, float wifiY, int pointX, int pointY)
    {
        if((pointX > startX && pointX <= wifiX) && (pointY > startY && pointY <= wifiY)) {
            return "Quad1";
        }
        else if((pointX >= wifiX && pointX < endX) && (pointY > startY && pointY <= wifiY)) {
            return "Quad2";
        }
        else if((pointX >= wifiX && pointX < endX) && (pointY >= wifiY && pointY < endY)) {
            return "Quad3";
        }
        else if((pointX > startX && pointX <= wifiX) && (pointY >= wifiY && pointY < endY)) {
            return "Quad4";
        }

        return null;
    }

    public static String getQuadIdForNearAnFarPoints(int startX, int startY, int endX, int endY, float wifiX, float wifiY, int pointX, int pointY)
    {
        if((pointX >= startX && pointX <= wifiX) && (pointY >= startY && pointY <= wifiY)) {
            return "Quad1";
        }
        else if((pointX >= wifiX && pointX <= endX) && (pointY >= startY && pointY <= wifiY)) {
            return "Quad2";
        }
        else if((pointX >= wifiX && pointX <= endX) && (pointY >= wifiY && pointY <= endY)) {
            return "Quad3";
        }
        else if((pointX >= startX && pointX <= wifiX) && (pointY >= wifiY && pointY <= endY)) {
            return "Quad4";
        }

        return null;
    }

    private static RectCoords addG1Points()
    {
        
       RectCoords rectCoords=new RectCoords();
       rectCoords.setX(40);
       rectCoords.setY(40);
       rectCoords.setX1(410);
       rectCoords.setY1(440);
        
        return rectCoords;
    }

    private static RectCoords addG2Points()
    
    {
        
        RectCoords rectCoords=new RectCoords();
        rectCoords.setX(410);
        rectCoords.setY(40);
        rectCoords.setX1(880);
        rectCoords.setY1(440);

        return rectCoords;
    }
    private static RectCoords addG3Points()

    {
        
        RectCoords rectCoords=new RectCoords();
        rectCoords.setX(40);
        rectCoords.setY(440);
        rectCoords.setX1(410);
        rectCoords.setY1(840);

        return rectCoords;
    }


    private static RectCoords addG4Points()

    {
        
        RectCoords rectCoords=new RectCoords();
        rectCoords.setX(410);
        rectCoords.setY(440);
        rectCoords.setX1(880);
        rectCoords.setY1(880);

        return rectCoords;
    }


    private static RectCoords addG5Points()

    {
        
        RectCoords rectCoords=new RectCoords();
        rectCoords.setX(40);
        rectCoords.setY(840);
        rectCoords.setX1(410);
        rectCoords.setY1(1240);

        return rectCoords;
    }
    private static RectCoords addG6Points()

    {
        
        RectCoords rectCoords=new RectCoords();
        rectCoords.setX(410);
        rectCoords.setY(840);
        rectCoords.setX1(880);
        rectCoords.setY1(1240);

        return rectCoords;
    }
    private static RectCoords addG7Points()

    {
        
        RectCoords rectCoords=new RectCoords();
        rectCoords.setX(40);
        rectCoords.setY(1240);
        rectCoords.setX1(410);
        rectCoords.setY1(1640);

        return rectCoords;
    }
    private static RectCoords addG8Points()

    {
        
        RectCoords rectCoords=new RectCoords();
        rectCoords.setX(40);
        rectCoords.setY(1240);
        rectCoords.setX1(880);
        rectCoords.setY1(1640);

        return rectCoords;
    }


    public static String getDistanceBetweenPoints(float wX, float wY, double x, double y) {
        double distance;
        String ds;
        distance=Math.sqrt( Math.pow((wX/100-x/100),2) +( Math.pow((wY/100-y/100),2)));
       // distance=Math.sqrt(  ((wX/100)-(x/100))*((wX/100)-(x/100)) +( ((wY/100)-(y/100))*((wY/100)-(y/100)) ));
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
       ds= decimalFormat.format(distance);
        return ds;
    }
    public static String getDistanceBetweenPointsN(float wX, float wY, double x, double y) {
        double distance;
        String ds;
        distance=Math.sqrt( Math.pow((wX-x),2) +( Math.pow((wY-y),2)));

        Log.i(TAG,"Distance in double is "+distance);
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        ds=decimalFormat.format(distance);
        return ds;
    }


}

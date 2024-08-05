package com.newmview.wifi.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.mview.airtel.R;
//import com.mcpsinc.mview.bean.Hull;

import com.newmview.wifi.activity.FinalWifiHeatMapActivity;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.MarkerTestResults;
import com.newmview.wifi.bean.WifiHeatMapPoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.Vector;
import com.newmview.wifi.other.Utils;

public class CustomHeatMapView extends AppCompatImageView {
    private List<Vector2> mPositions = new ArrayList<Vector2>(100);
    private List<Vector2> markersHistory = new ArrayList<Vector2>(100);
   private List<MarkerTestResults> mainResultsHistory=new ArrayList<>();
    private static final String TAG = "CustomHeatMapView";
    private Context context;
    private Point[] bluePoints;
    private Point[] excellentPoints;
    private Point[] fairPoints, poorPoints;
    private ArrayList<WifiHeatMapPoints> goodPointsList = new ArrayList<>();
    private ArrayList<WifiHeatMapPoints> fairPointsList = new ArrayList<>();
    private ArrayList<WifiHeatMapPoints> excellentPointsList = new ArrayList<>();
    private ArrayList<WifiHeatMapPoints> poorPointsList = new ArrayList<>();
    private ArrayList<Coordinate[]> fairPointsHullList = new ArrayList<>();
    private ArrayList<Coordinate[]> excellentPointsHullList = new ArrayList<>();
    private ArrayList<Coordinate[]> goodPointsHullList = new ArrayList<>();
    private ArrayList<Coordinate[]> poorPointsHullList = new ArrayList<>();
    private String text;
    private Canvas canvas;
    private Paint paint;
    private Paint polyPaint;
    private ArrayList<String> quadIdYellowList = new ArrayList<>();
    private Coordinate[] fairPointsHull;
    private Coordinate[] excellentPointsHull, goodPointsHull, poorPointsHull;
    private int wifiX, wifiY;
    private Paint textPaint;
    private Bitmap markerBitmap;
    private Paint markerPaint;
    private DB_handler db_handler;
    private String mapId;
    private Vector2 mBitmapDimensions;
    private boolean alreadyExists;
    //  private Hull hull;

    public CustomHeatMapView(Context context) {
        super(context);
        this.context = context;
        initialize();

    }

    public CustomHeatMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialize();
        //init();
    }

    private void initialiseTextPaint() {
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40f);
        textPaint.setFakeBoldText(true);
    }

    private void initialize() {
        paint = new Paint();
        paint.setTextSize(30f);
        paint.setColor(Color.BLACK);
        initializePolyPaint();
        initialiseTextPaint();
        initialiseMarkerPaint();
        markerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pointer);
        db_handler=new DB_handler(MviewApplication.ctx);


        mBitmapDimensions = new Vector2(markerBitmap.getWidth(), markerBitmap.getHeight(),
                0,false,-1);

    }

    private void initialiseMarkerPaint() {
        markerPaint = new Paint();
        markerPaint.setColor(Color.BLACK);
    }

    public void initializeHull() {
//hull=new Hull(new Coordinate[]{});
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.i(TAG,"Action is "+event.getAction() +" event x "+event.getX() +" y "+event.getY());
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                this.actionDown(event);
                invalidate();

                return true;


        }
        return false;
    }
    public Vector2 getLatestTestAreaCoords()
    {
      if(mPositions!=null)
      {
          if(mPositions.size()>0)
          {
              return mPositions.get(mPositions.size()-1);
          }
      }

        return null;
    }

    private Bitmap getBitmap(Canvas canvas) {
        try {
            java.lang.reflect.Field field=Canvas.class.getDeclaredField("mBitmap");
            field.setAccessible(true);
            return (Bitmap)field.get(canvas);
        }
        catch (Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }

    public void setMapId(String mapId) {
        this.mapId=mapId;
    }

    public void setIdToLatestMarker(String id, float x, float y) {
        boolean idFound=false;
        Log.i(TAG,"Id being set is "+id +" for x "+x +" and y "+y);
        if(mPositions!=null)
        {
            if(mPositions.size()>0) {
                for(int i=0;i<mPositions.size();i++)
                {
                    Log.i(TAG,"Id being checked for x "+mPositions.get(i).x +" y "+mPositions.get(i).y);
                    if(x==mPositions.get(i).x && y==mPositions.get(i).y)
                    {
                        MarkerTestResults testResults=new MarkerTestResults();
                        mPositions.get(mPositions.size() - 1).id =
                                Integer.parseInt(id);
                        float finalX = mPositions.get(0).x;
                        float finalY = mPositions.get(0).y;
                        markersHistory.add(new Vector2(finalX,
                                finalY, 0, false,Integer.parseInt(id)));
                        testResults.setId(id);
                        testResults.setMarkerResults(markersHistory);
                        testResults.setTouched(false);
                        testResults.setX(finalX);
                        testResults.setY(finalY);
                        mainResultsHistory.add(testResults);

                        break;
                    }
                }

            }
        }

    }

    public void clearMarkers() {
        markersHistory.clear();
        mainResultsHistory.clear();
        mPositions.clear();
        invalidate();
    }

    public static final class Vector2 {
        public Vector2(float x, float y,int color,boolean touched,int id) {
            this.x = x;
            this.y = y;
            this.color=color;
            this.touched=touched;
            this.id=id;
        }

        public  float x;
        public  float y;
        public final int color;
        public int id;
        public boolean touched;
    }


    private void actionDown(MotionEvent event) {
        final float posX = event.getX();
        final float posY = event.getY();
        Bitmap bmp=getBitmap(this.canvas);
        int color=0;
        if(bmp!=null) {
            color = bmp.getPixel((int) event.getX(), (int) event.getY());
        }

        Log.i(TAG,"Color on touch "+color);
               /* mPositions.add(new Vector2(event.getX() ,
                        event.getY(),color,true));*/
        float finalX=posX - mBitmapDimensions.x / 2;
        float finalY=posY - mBitmapDimensions.y / 2;

       /* mPositions.add(new Vector2(posX - mBitmapDimensions.x / 2,
                posY - mBitmapDimensions.y / 2,0,false,-1);*/

        if(mainResultsHistory!=null) {
            if (mainResultsHistory.size() > 0) {
                for (int j = 0; j < mainResultsHistory.size(); j++) {
                    MarkerTestResults marker = mainResultsHistory.get(j);
                    float historyX = marker.x;
                    float historyY = marker.y;
                    Log.i(TAG, "History X " + historyX + " history Y " + historyY + " touch X " + finalX + " touch Y " + finalY);
                    if ((historyX - 50 <= finalX && finalX <= historyX + 50)
                            && (historyY - 50 <= finalY && finalY <= historyY + 50)) {
                        // if(inViewInBounds(markerBitmap,event.getX(),event.getY()))

                        mainResultsHistory.get(j).setTouched(true);
                        alreadyExists=true;

                    }
                    else
                    {
                        mainResultsHistory.get(j).setTouched(false);
                    }
                }
            }
        }
        if(!alreadyExists) {
            if (mPositions != null) {
                mPositions.clear();
            }
            mPositions.add(new Vector2(finalX,
                    finalY, color, false, -1));

        }
        else {
            alreadyExists=false;
        }
    //    onTouchOldMethod(finalX,finalY);









    }
   /* private boolean inViewInBounds(View view, int x, int y, Bitmap bitmap) {
       *//* view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);*//*
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas=canvas;
        Log.i(TAG,"Lets draw..");



        for(int i=0;i<excellentPointsHullList.size();i++)
        {
            drawHull(canvas, context.getResources().getColor(R.color.green),excellentPointsHullList.get(i));
        }
         for(int i=0;i<goodPointsHullList.size();i++)
        {
            drawHull(canvas, context.getResources().getColor(R.color.light_green),goodPointsHullList.get(i));
        }
        for(int i=0;i<fairPointsHullList.size();i++)
        {
            drawHull(canvas, context.getResources().getColor(R.color.yellow),fairPointsHullList.get(i));
        }
        for(int i=0;i<poorPointsHullList.size();i++)
        {
        drawHull(canvas, context.getResources().getColor(R.color.red),poorPointsHullList.get(i));
        }
        drawLabels(excellentPointsList);
        drawLabels(goodPointsList);
        drawLabels(fairPointsList);
        drawLabels(poorPointsList);




        for (int i = 0; i < mPositions.size(); i++) {
            Vector2 pos = mPositions.get(i);
            //   Log.i(TAG,"Drawing "+markerBitmap +" pos x "+pos.x +" y "+pos.y);
            if(pos.color!=0) {
                markerPaint.setColor(pos.color);
                canvas.drawBitmap(markerBitmap, pos.x, pos.y, markerPaint);
            }
            else {
                canvas.drawBitmap(markerBitmap, pos.x, pos.y, null);
            }

        }
for(int j=0;j<mainResultsHistory.size();j++)
{
  //   List<Vector2> markersHistory=mainResultsHistory.get(j).getMarkerResults();

        MarkerTestResults marker = mainResultsHistory.get(j);
    String id=marker.getId();
    int idNum=Integer.parseInt(id);
    canvas.drawBitmap(markerBitmap, marker.x, marker.y, markerPaint);
    Log.i(TAG,"Id is "+id +" and touched is "+marker.isTouched() +" i is "+j);
        if (marker.isTouched()) {
            // if(inViewInBounds(markerBitmap,event.getX(),event.getY()))

            if(context instanceof FinalWifiHeatMapActivity)
            {
                ((FinalWifiHeatMapActivity)context).
                        showTestResultAlert(idNum);
            }

        }



   /* List<Vector2> markersHistory=mainResultsHistory.get(j).getMarkerResults();
     String id=mainResultsHistory.get(j).getId();
     int idNum=Integer.parseInt(id);
     boolean touched=mainResultsHistory.get(j).isTouched();
     for (int i = 0; i < markersHistory.size(); i++) {
        Vector2 pos = markersHistory.get(i);
       // int id=pos.id;
        Log.i(TAG,"Id is "+id +" and touched is "+touched +" i is "+i);
        if(touched)
        {
            if(context instanceof FinalWifiHeatMapActivity)
            {
                ((FinalWifiHeatMapActivity)context).
                        showTestResultAlert(idNum);
            }
        }


    }*/

}


    }

    private void drawLabels(ArrayList<WifiHeatMapPoints> pointsList) {
        for(int i=0;i<pointsList.size();i++)
        {
            String label=pointsList.get(i).getLabel();
            if(Utils.checkifavailable(label))
            {
                canvas.drawText(label,pointsList.get(i).getPoints().get(0).x,
                        pointsList.get(i).getPoints().get(0).y,textPaint);
            }

        }
    }

    private void drawHull(Canvas canvas,int color,Coordinate[] points) {
        {
            // line at minimum...
            if(points==null)
            {
                return;
            }
            Path polyPath = new Path();
            polyPaint.setColor(color);
            if(color==getResources().getColor(R.color.red))
            {
                polyPaint.setAlpha(120);
            }
            else
            {
                polyPaint.setAlpha(180);
            }
           // polyPaint.setAlpha(120);
String label=null;
            if(points.length==1)
            {

//label=points[0].label;
                polyPath.addCircle(points[0].x,points[0].y,
                        5,Path.Direction.CCW);
                canvas.drawPath(polyPath,polyPaint);
                if(Utils.checkifavailable(label))

                {

                    canvas.drawText(label,points[0].x,points[0].y,this.textPaint);
                }
            }
            else  if (points.length == 2) {

                polyPath.moveTo(points[0].x, points[0].y);
                polyPath.lineTo(points[1].x, points[1].y);
                canvas.drawPath(polyPath,polyPaint);
/*
                for(int i=0;i<points.length;i++)
                { label=points[i].label;
                    if(Utils.checkifavailable(label))

                    {

                        canvas.drawText(label,points[i].x,points[i].y,this.textPaint);
                    }
                }
*/

                // return;
            }
            else {
              /*  for(int x = 0; x < points.length-1; x++)
                    canvas.drawLine(points[x].x, points[x].y, points[x+1].x, points[x+1].y, polyPaint);


                canvas.drawLine(points[0].x, points[0].y, points[points.length-1].x,
                        points[points.length-1].y, polyPaint); //this is the last line.


*/

                Point prevPoint = null;
                polyPath.moveTo(points[0].x, points[0].y);
                int i, len;
                len = points.length;
              //  Log.i(TAG,"Length of points "+len);
                for (i = 0; i < len; i++) {
                    label=points[i].label;
                  //  Log.i(TAG,"Fair Label for i "+i +" is "+label);
                    polyPath.lineTo(points[i].x, points[i].y);
/*
                    if(Utils.checkifavailable(label))

                    {

                        canvas.drawText(label,points[i].x,points[i].y,this.textPaint);
                    }
*/
                }
                polyPath.lineTo(points[0].x, points[0].y);


                canvas.drawPath(polyPath, polyPaint);
            }
            polyPath.close();
        }
    }



    public void setExcellentPoints(List<WifiHeatMapPoints> excellentPoints)
    {
        if(excellentPoints!=null)
        {
            if(excellentPoints.size()>0)
            {

                //   Log.i(TAG,"Red points length "+poorPoints.length);
                excellentPointsList.addAll(excellentPoints);
                Coordinate[] coordArray=new Coordinate[excellentPoints.size()];

                for(int i=0;i<excellentPoints.size();i++)
                {
                    // Log.i(TAG,"X at lg point "+poorPoints[i].x +" for i "+i);
                    coordArray[i]=new Coordinate();
                    coordArray[i].x=excellentPoints.get(i).getPoints().get(0).x;
                    coordArray[i].y=excellentPoints.get(i).getPoints().get(0).y;
                    coordArray[i].label=excellentPoints.get(i).getLabel();
                }
                excellentPointsHull = solve(coordArray);
                excellentPointsHullList.add(excellentPointsHull);
                invalidate();
            }
        }
        //this.poorPoints=poorPoints;

        Log.i(TAG,"Red points size "+poorPointsList.size());

    }

    public void setGoodPoints(List<WifiHeatMapPoints> goodPoints)
    {
        if(goodPoints!=null)
        {
            if(goodPoints.size()>0)
            {

                //   Log.i(TAG,"Red points length "+poorPoints.length);
                goodPointsList.addAll(goodPoints);
                Coordinate[] coordArray=new Coordinate[goodPoints.size()];

                for(int i=0;i<goodPoints.size();i++)
                {
                    // Log.i(TAG,"X at lg point "+poorPoints[i].x +" for i "+i);
                    coordArray[i]=new Coordinate();
                    coordArray[i].x=goodPoints.get(i).getPoints().get(0).x;
                    coordArray[i].y=goodPoints.get(i).getPoints().get(0).y;
                    coordArray[i].label=goodPoints.get(i).getLabel();
                }
                goodPointsHull = solve(coordArray);
                goodPointsHullList.add(goodPointsHull);
                invalidate();
            }
        }
        //this.poorPoints=poorPoints;

        Log.i(TAG,"Red points size "+poorPointsList.size());

    }

    public void setFairPoints(List<WifiHeatMapPoints> fairPoints)
    {
        if(fairPoints!=null)
        {
            if(fairPoints.size()>0)
            {

                //   Log.i(TAG,"Red points length "+poorPoints.length);
                fairPointsList.addAll(fairPoints);
                Coordinate[] coordArray=new Coordinate[fairPoints.size()];

                for(int i=0;i<fairPoints.size();i++)
                {
                    // Log.i(TAG,"X at lg point "+poorPoints[i].x +" for i "+i);
                    coordArray[i]=new Coordinate();
                    coordArray[i].x=fairPoints.get(i).getPoints().get(0).x;
                    coordArray[i].y=fairPoints.get(i).getPoints().get(0).y;
                    coordArray[i].label=fairPoints.get(i).getLabel();
                    Log.i(TAG,"Label for i "+i +" is "+fairPoints.get(i).getLabel());
                }
                fairPointsHull = solve(coordArray);
                fairPointsHullList.add(fairPointsHull);
                invalidate();
            }
        }
        //this.poorPoints=poorPoints;

        Log.i(TAG,"Red points size "+poorPointsList.size());

    }

    public void setPoorPoints(List<WifiHeatMapPoints> poorPoints)
    {
        if(poorPoints!=null)
        {
            if(poorPoints.size()>0)
            {

                //   Log.i(TAG,"Red points length "+poorPoints.length);
                poorPointsList.addAll(poorPoints);
                Coordinate[] coordArray=new Coordinate[poorPoints.size()];
               // Log.i(TAG," coordArrayLength "+ );
                for(int i=0;i<poorPoints.size();i++)
                {
                    Log.i(TAG," i is "+i );
                    // Log.i(TAG,"X at lg point "+poorPoints[i].x +" for i "+i);
                    coordArray[i]=new Coordinate();
                    coordArray[i].x=poorPoints.get(i).getPoints().get(0).x;
                    coordArray[i].y=poorPoints.get(i).getPoints().get(0).y;
                    coordArray[i].label=poorPoints.get(i).getLabel();
                }
                poorPointsHull = solve(coordArray);
                poorPointsHullList.add(poorPointsHull);
                invalidate();
            }
        }
        //this.poorPoints=poorPoints;

        Log.i(TAG,"Red points size "+poorPointsList.size());

    }


/*
    public void setPoorPoints(Point[] poorPoints)
    {
if(poorPoints!=null)
{
    if(poorPoints.length>0)
    {
        Log.i(TAG,"Red points length "+poorPoints.length);
        poorPointsList.add(poorPoints);
        Coordinate[] coordArray=new Coordinate[poorPoints.length];

        for(int i=0;i<poorPoints.length;i++)
        {
            Log.i(TAG,"X at lg point "+poorPoints[i].x +" for i "+i);
            coordArray[i]=new Coordinate();
            coordArray[i].x=poorPoints[i].x;
            coordArray[i].y=poorPoints[i].y;
        }
        poorPointsHull = solve(coordArray);
        poorPointsHullList.add(poorPointsHull);
        invalidate();
    }
}
        //this.poorPoints=poorPoints;

        Log.i(TAG,"Red points size "+poorPointsList.size());

    }
*/
    public int getRedPointsSize()
    {
        return poorPointsList.size();
    }

    public int getYellowPoints() {
        return fairPointsList.size();
    }
    public int getBluePoints() {
        return goodPointsList.size();
    }
    public int getGreenPoints() {
        return excellentPointsList.size();
    }

    private void drawPoly(Canvas canvas, int color, Point[] points) {
        // line at minimum...
        if(points==null)
        {
            return;
        }
        Path polyPath = new Path();
        polyPaint.setColor(color);
        if(color==getResources().getColor(R.color.red))
        {
            polyPaint.setAlpha(120);
        }
        else
        {
            polyPaint.setAlpha(180);
        }

        if(points.length==1)
        {


            polyPath.addCircle(points[0].x,points[0].y,5,Path.Direction.CCW);
            canvas.drawPath(polyPath,polyPaint);
        }
      else  if (points.length == 2) {
            polyPath.moveTo(points[0].x, points[0].y);
            polyPath.lineTo(points[1].x, points[1].y);
            canvas.drawPath(polyPath,polyPaint);
           // return;
        }
        else {


       /* float radius=100.0f;
        CornerPathEffect cornerPathEffect=
                new CornerPathEffect(radius);
        polyPaint.setPathEffect(cornerPathEffect);*/

            // path
            //  Path polyPath = new Path();
            Point prevPoint = null;
            polyPath.moveTo(points[0].x, points[0].y);
            int i, len;
            len = points.length;
            for (i = 0; i < len; i++) {
                polyPath.lineTo(points[i].x, points[i].y);

              /*  if (i == 0) {
                    polyPath.moveTo(points[i].x, points[i].y);
                } else {
                    float midX = (prevPoint.x + points[i].x) / 2;
                    float midY = (prevPoint.y + points[i].y) / 2;

                    if (i == 1) {
                        polyPath.lineTo(midX, midY);
                    } else {
                        polyPath.quadTo(prevPoint.x, prevPoint.y, midX, midY);
                    }
                }
                prevPoint = points[i];*/
            }
            polyPath.lineTo(points[0].x, points[0].y);

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            polyPaint.setBlendMode(BlendMode.COLOR_BURN);
        }*/
//https://developer.android.com/reference/android/graphics/BlendMode
            // draw
            canvas.drawPath(polyPath, polyPaint);
        }
        polyPath.close();
    }

    private void initializePolyPaint() {
         polyPaint = new Paint();
         polyPaint.setStrokeWidth(40f);
         polyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        polyPaint.setStrokeJoin(Paint.Join.ROUND);
        polyPaint.setStrokeMiter(40);
        polyPaint.setAntiAlias(true);
        polyPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setText(String text) {
        this.text=text;
        invalidate();
    }

    public void setPoints(Point[] points) {
    }

    public Coordinate [] solve(Coordinate[] arr){
        int N = arr.length;
        if (N <= 1){
            return arr;
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int value = 0; //minimum value
        for(int x = 0; x < arr.length; x++){
            if (arr[x].y < minY || (arr[x].y== minY && arr[x].x < minX)){
                minY = arr[x].y;
                minX = arr[x].x;
                value = x;
            }
        }
        //You got the starting point, now you have to sort everything by the y coordinate!

        for(int x = 0; x < arr.length; x++){
            if (x == value)
                continue;
            if (arr[x].x == minX){
                arr[x].setAngle(0); //fix 0 -> 90 WAIT .. THIS SHOULD BE 90 FIX?
            }
            else{
                arr[x].setAngle(Math.atan2((double)(arr[x].y-minY), (double)(arr[x].x - minX)));
            }
        }
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Arrays.sort(arr);

        Stack<Coordinate> Q = new Stack<Coordinate>();
        Q.push(arr[0]);
        Q.push(arr[1]);
        for(int x = 2; x < N; x++){
            Q.push(arr[x]);
            if (N == 3) //odd..
                break;
            while(true){
                //check to see if arr[0]arr[1] vector angle with arr[1][2] is a right
                if (Q.size() < 3)
                    break;
                Coordinate p2 = Q.pop();
                Coordinate p1 = Q.pop();
                Coordinate p0 = Q.pop();
                Vector p0p1 = new Vector(p1.x-p0.x, p1.y-p0.y);
                Vector p1p2 = new Vector(p2.x-p1.x, p2.y-p1.y);
                if (p0p1.turnsLeft(p1p2)){
                    Q.push(p0);
                    Q.push(p1);
                    Q.push(p2);
                    break;
                }
                else{
                    //remove the last candidate
                    Q.push(p0);
                    Q.push(p2);
                }
            }
        }

        int size = Q.size();
        Coordinate [] hull = new Coordinate[size];
        for(int x = 0; x < size; x++) {
            hull[x] = Q.pop();

        }
        Log.i(TAG,"Value in conves hull "+ Arrays.toString(hull));

        return hull;
    }

    public void setWifiCoords(int wifiCoordsX, int wifiCoordsY) {
        this.wifiY=wifiCoordsY;
        this.wifiX=wifiCoordsX;
    }


    private void onTouchOldMethod(float finalX, float finalY) {
        if(mainResultsHistory!=null)
        {
            if(mainResultsHistory.size()>0)
            {
                for(int j=0;j<mainResultsHistory.size();j++)
                {
                    List<Vector2> markersHistory=mainResultsHistory.get(j).getMarkerResults();
                    String id=mainResultsHistory.get(j).getId();
                    int idNum=Integer.parseInt(id);
                    Log.i(TAG,"Check for id "+id );
                    if (markersHistory.size() > 0) {
                        for (int i = 0; i < markersHistory.size(); i++) {
                            Vector2 marker = markersHistory.get(i);
                            float historyX = marker.x;
                            float historyY = marker.y;
                            Log.i(TAG,"History X "+historyX +" history Y "+historyY +" touch X "+finalX +" touch Y "+finalY);
                            if ((historyX-50 <= finalX && finalX<= historyX+50)
                                    && (historyY-50 <= finalY &&  finalY<=historyY+50)) {
                                // if(inViewInBounds(markerBitmap,event.getX(),event.getY()))

                                mainResultsHistory.get(j).setTouched(true);

                                alreadyExists = true;

                                Log.i(TAG, "Yes touched marker at x " + historyX + " and y " + historyY);
                                break;
                            } else {
                                mainResultsHistory.get(j).setTouched(false);

                            }
                        }

                    }

                }

            }

        }
    }
    }

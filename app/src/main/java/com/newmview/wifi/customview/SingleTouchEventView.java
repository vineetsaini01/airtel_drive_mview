package com.newmview.wifi.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;
import com.mview.airtel.R;
import com.newmview.wifi.activity.CreateWalkMapActivity;
import com.newmview.wifi.bean.FloatPoint;
import com.newmview.wifi.bean.WifiHeatMapPoints;
import com.newmview.wifi.bean.WifiModel;
import com.newmview.wifi.helper.CoordsBuilder;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.other.WifiConfig;

import java.util.ArrayList;
import java.util.List;

import static com.newmview.wifi.other.WifiConfig.getColorForWifiSignalStrength;
import static com.newmview.wifi.other.WifiConfig.getConnectedWifiDetails;

public class SingleTouchEventView extends ImageView {
    private static final String TAG = "SingleTouchEventView";
    private final Context context;
    private final Bitmap markerBitmap;
    private final Vector2 markerBitmapDimensions;
    private Paint paint = new Paint();
    List<WifiHeatMapPoints> points = new ArrayList<WifiHeatMapPoints>();
    List<Point> markerPoints = new ArrayList<Point>();
    private int color;
    private int radius = 20;

    private List<Vector2> mPositions = new ArrayList<Vector2>(100);
    private String wifiCoordX;
    private String wifiCoordY;
    private boolean newStepDetected=true;
    private List<ActivityTransition> activityTransitionList;
    private Paint textPaint;
    private int stepsCount;
    private boolean walking;

    public SingleTouchEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        markerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pointer);

        markerBitmapDimensions = new Vector2(markerBitmap.getWidth(), markerBitmap.getHeight());
initialiseTextPaint();
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    private void setColor(int color) {
        this.color = color;
    }

    private void setRadius(int radius) {
        this.radius = radius;
    }
    private static String toActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.WALKING:
                return "WALKING";
            default:
                return "UNKNOWN";
        }
    }

    private static String toTransitionType(int transitionType) {
        switch (transitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "ENTER";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "EXIT";
            default:
                return "UNKNOWN";
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//Log.i(TAG,"Points size "+points.size());
        for (int i = 0; i < points.size(); i++) {
            WifiHeatMapPoints wifiHeatMapPoints = points.get(i);
            Point p=wifiHeatMapPoints.getPoints().get(0);
            String label=wifiHeatMapPoints.getLabel();
            Point p1=null;
            if(i>0)
             p1=points.get(i-1).getPoints().get(0);
            //paint.setColor(wifiHeatMapPoints.getColor());
/*paint.setColor(doGradient(i * 5, 0, 100,
        wifiHeatMapPoints.getColor(), 0xffff0000));*/
           /* RadialGradient gradient1 = new
                    RadialGradient(p.x, p.y, (float)(radius * 0.85),
                    new int[] { Color.argb((int)(1 * 255),
                            0, 0, 0),
                            Color.argb(0, 0, 0, 0) },
                    null, Shader.TileMode.CLAMP);*/

            RadialGradient gradient = new RadialGradient
                    (p.x, p.y,radius,
                            wifiHeatMapPoints.getColor(),
                            Color.argb(0, 0, 0, 0),
                            Shader.TileMode.CLAMP);

            paint.setShader(gradient);
           /* drawPolygon(canvas,p.x,p.y,120,5,
                    60,false,paint);*/
           canvas.drawCircle(p.x, p.y, radius+20, paint);
if(Utils.checkifavailable(label))

{
    
    canvas.drawText(label,p.x,p.y,this.textPaint);
}

           /* LinearGradient grad;
            if(i>0) {
                grad = new LinearGradient(p.x, p.y, p1.x, p1.y,
                        wifiHeatMapPoints.getColor()
                        ,  Color.argb(0, 0, 0, 0), Shader.TileMode.CLAMP);
               paint.setStrokeWidth(2*radius);
                paint.setStyle(Paint.Style.FILL);
                paint.setShader(grad);
                canvas.drawLine(p.x, p.y, p1.x, p1.y, paint);

            }*/



            
        }
        for (int i = 0; i < mPositions.size(); i++) {
           Vector2 pos = mPositions.get(i);
         //   Log.i(TAG,"Drawing "+markerBitmap +" pos x "+pos.x +" y "+pos.y);
            canvas.drawBitmap(markerBitmap, pos.x, pos.y, null);
        }
        invalidate();
    }

    private void  initialiseTextPaint() {
         textPaint=new Paint();
         textPaint.setColor(Color.BLACK);
         textPaint.setTextSize(40f);
         textPaint.setFakeBoldText(true);
    }

    private void drawPolygon(Canvas mCanvas, float x, float y, float radius, float sides, float startAngle, boolean anticlockwise, Paint paint) {

        if (sides < 3) { return; }

        float a = ((float) Math.PI *2) / sides * (anticlockwise ? -1 : 1);
        mCanvas.save();
        mCanvas.translate(x, y);
        mCanvas.rotate(startAngle);
        Path path = new Path();
        path.moveTo(radius, 0);
        for(int i = 1; i < sides; i++) {
            path.lineTo(radius * (float)
                    Math.cos(a * i), radius * (float) Math.sin(a * i));
        }
        path.close();
        paint.setColor(context.getResources().getColor(R.color.black_color));
        paint.setStrokeWidth(12);
        Log.i(TAG,"draw polygon");
        mCanvas.drawPath(path, paint);
       mCanvas.restore();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(newStepDetected) {
                    this.actionDown(event);
                }
                else
                {
                //    Utils.showToast(context, Constants.NO_MOVEMENT);
                }
                 break;

            case MotionEvent.ACTION_MOVE:  // a pointer was moved
               // if(newStepDetected)
                //this.actionMove(event);
               // else
               // Utils.showToast(context, Constants.NO_MOVEMENT);
                break;

            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL: {
                break;
            }
        }
        invalidate();
        return true;
    }

    private void actionMove(MotionEvent event) {
        if (!mPositions.isEmpty()) {
            mPositions.clear();
        }
        final float posX = event.getX();
        final float posY = event.getY();
       // mPositions.add(new Vector2(posX-markerBitmapDimensions.x, posY-markerBitmapDimensions.y));
        WifiModel wifiModel= getConnectedWifiDetails();
        if(wifiModel!=null) {
            setCirclePoint(event,wifiModel);
        }
        float y=(posY - markerBitmapDimensions.y / 2)-80;
         mPositions.add(new Vector2(posX - markerBitmapDimensions.x / 2, y));
        invalidate();
    }


    private void actionDown(MotionEvent event) {
        WifiModel wifiModel= getConnectedWifiDetails();
        if(wifiModel!=null) {
            markerPoints.clear();
            setCirclePoint(event,wifiModel);

           // markerPoints.add(p);
            invalidate();
        }
    }
public void setWifiCoords(String wifiCoordX,String wifiCoordY)
{
    this.wifiCoordX=wifiCoordX;
    this.wifiCoordY=wifiCoordY;
}
    private void setCirclePoint(MotionEvent event, WifiModel wifiModel) {
        stepsCount++;
      //  Utils.showToast(context,"Steps count "+stepsCount);
      /*  if(stepsCount%5==0)
        {
            if (CreateWalkMapActivity.WALKING)

            {
                CreateWalkMapActivity.WALKING=false;
            }
            else
            {
                Utils.showToast(context,"Please take walk steps for completing your test.");
            }
        }*/
        int signalStrength = wifiModel.getSignalStrength();
        color = getColorForWifiSignalStrength(signalStrength);
        Point p = new Point();
        WifiHeatMapPoints wifiHeatMapPoints=new WifiHeatMapPoints();
        p.x = (int) event.getX();
        p.y = (int) event.getY();
        FloatPoint fl=new FloatPoint();
        fl.setX(event.getX());
        fl.setY(event.getY());
        List<FloatPoint> floatPointList=new ArrayList<>();
        floatPointList.add(fl);

        List<Point> coordPoints=new ArrayList<>();
        coordPoints.add(p);
        wifiHeatMapPoints.setPoints(coordPoints);
        wifiHeatMapPoints.setColor(color);
        wifiHeatMapPoints.setSignalStrength(signalStrength);
        wifiHeatMapPoints.setSsidName(wifiModel.getSsidName()+"");
        wifiHeatMapPoints.setLinkSpeed(wifiModel.getLinkSpeed());
        wifiHeatMapPoints.setFloatPoints(floatPointList);
        Log.i(TAG,"Link Speed on touching.." +wifiModel.getLinkSpeed());
        wifiHeatMapPoints.setSignalStrengthColor(WifiConfig.getColorForWifiSignalStrength(signalStrength));
          wifiHeatMapPoints.setLinkSpeedColor(WifiConfig.getColorForLinkSpeed(wifiModel.getLinkSpeed()));
      //  String gridId=CoordsBuilder.getGridId(p.x,p.y);

        //String gridId=CoordsBuilder.newGetGridId(240,40,840,1100,p.x,p.y);
        Log.i(TAG,"Coord x "+wifiCoordX +"coordy "+wifiCoordY);
        if(wifiCoordX!=null && wifiCoordY!=null) {
           /* String quadId = CoordsBuilder.getQuadId(240, CoordsBuilder.startY,
                    CoordsBuilder.endX,
                    CoordsBuilder.endY, Float.parseFloat(wifiCoordX+200),
                    Float.parseFloat(wifiCoordY), p.x, p.y);*/
            String quadId = CoordsBuilder.getQuadId(CoordsBuilder.startX, CoordsBuilder.startY,
                    CoordsBuilder.endX,
                    CoordsBuilder.endY, Float.parseFloat(wifiCoordX),
                    Float.parseFloat(wifiCoordY), p.x, p.y);

            Log.i(TAG,"quadid IS "+quadId +" x is "+p.x +" y is "+p.y);
            if(quadId!=null) {
                wifiHeatMapPoints.setQuadId(quadId);
               /* if(context instanceof WifiHeatMapActivity)
                {
                    String distanceN=CoordsBuilder.
                            getDistanceBetweenPointsN(Float.parseFloat(wifiCoordX),
                                    Float.parseFloat(wifiCoordY), (double)p.x, (double)p.y);


                    ((WifiHeatMapActivity)context).showDistance(CoordsBuilder.
                            getDistanceBetweenPoints(Float.parseFloat(wifiCoordX),
                            Float.parseFloat(wifiCoordY), (double)p.x, (double)p.y) +" wihout 100 division " +
                            ""+ distanceN);
                }*/
             //   double distance=
                points.add(wifiHeatMapPoints);
                if(context instanceof CreateWalkMapActivity)
                {
                    ((CreateWalkMapActivity)context).refreshOnTouch(wifiHeatMapPoints);
                }
            }
        }
        else
        {
            wifiHeatMapPoints.setQuadId("quad");
            points.add(wifiHeatMapPoints);
            if(context instanceof CreateWalkMapActivity)
            {
                ((CreateWalkMapActivity)context).refreshOnTouch(wifiHeatMapPoints);
            }
        }

        /*if(gridId!=null) {
            wifiHeatMapPoints.setGridId(gridId);
            points.add(wifiHeatMapPoints);
        }
        else {
            Utils.showToast(context, "You can draw outside the boundary");
        }
*/




    }
 public List<WifiHeatMapPoints> getHeatMapDetails()
 {
     for(int i=0;i<points.size();i++)
     {
         Log.i(TAG,"Label while sending list "+points.get(i).getLabel());
     }
     return points;
 }
    public WifiHeatMapPoints getTouchedPoint() {
        if(points!=null)
        {


                if(points.size()>0) {
                    return points.get(points.size() - 1);
                }

        }
        return null;
    }

    public void clearData() {
        points.clear();
        invalidate();
    }

    public void setNewStepDetection() {
        /*if(context instanceof CreateWalkMapActivity)
        {
           newStepDetected= ((CreateWalkMapActivity)context).newStepDetected()
        }
   */ }

    public void addLabelAtLastPoint(String text) {
        if(points!=null) {
            if(points.size()>0) {
                points.get(points.size()-1).setLabel(text);
            }
            else
            {
                Utils.showToast(context,
                        Constants.TAKE_STEP);
            }
        }
        else
        {
            Utils.showToast(context,
                    Constants.TAKE_STEP);
        }
    }

    public void setWalkingFlag(boolean walking) {
        this.walking=walking;
    }

    public void setNextPoint(Point point) {
        WifiModel wifiModel= getConnectedWifiDetails();
        int signalStrength = wifiModel.getSignalStrength();
        color = getColorForWifiSignalStrength(signalStrength);
        Point p = new Point();
        WifiHeatMapPoints wifiHeatMapPoints=new WifiHeatMapPoints();
        p.x = point.x;
        p.y = point.y;
        FloatPoint fl=new FloatPoint();
        fl.setX(point.x);
        fl.setY(point.y);
        List<FloatPoint> floatPointList=new ArrayList<>();
        floatPointList.add(fl);

        List<Point> coordPoints=new ArrayList<>();
        coordPoints.add(p);
        wifiHeatMapPoints.setPoints(coordPoints);
        wifiHeatMapPoints.setColor(color);
        wifiHeatMapPoints.setSignalStrength(signalStrength);
        wifiHeatMapPoints.setSsidName(wifiModel.getSsidName()+"");
        wifiHeatMapPoints.setLinkSpeed(wifiModel.getLinkSpeed());
        wifiHeatMapPoints.setFloatPoints(floatPointList);
        Log.i(TAG,"Link Speed on touching.." +wifiModel.getLinkSpeed());
        wifiHeatMapPoints.setSignalStrengthColor(WifiConfig.getColorForWifiSignalStrength(signalStrength));
        wifiHeatMapPoints.setLinkSpeedColor(WifiConfig.getColorForLinkSpeed(wifiModel.getLinkSpeed()));
        //  String gridId=CoordsBuilder.getGridId(p.x,p.y);

        //String gridId=CoordsBuilder.newGetGridId(240,40,840,1100,p.x,p.y);
        Log.i(TAG,"Coord x "+wifiCoordX +"coordy "+wifiCoordY);
        if(wifiCoordX!=null && wifiCoordY!=null) {
           /* String quadId = CoordsBuilder.getQuadId(240, CoordsBuilder.startY,
                    CoordsBuilder.endX,
                    CoordsBuilder.endY, Float.parseFloat(wifiCoordX+200),
                    Float.parseFloat(wifiCoordY), p.x, p.y);*/
            String quadId = CoordsBuilder.getQuadId(CoordsBuilder.startX, CoordsBuilder.startY,
                    CoordsBuilder.endX,
                    CoordsBuilder.endY, Float.parseFloat(wifiCoordX),
                    Float.parseFloat(wifiCoordY), p.x, p.y);

            Log.i(TAG,"quadid IS "+quadId +" x is "+p.x +" y is "+p.y);
            if(quadId!=null) {
                wifiHeatMapPoints.setQuadId(quadId);
               /* if(context instanceof WifiHeatMapActivity)
                {
                    String distanceN=CoordsBuilder.
                            getDistanceBetweenPointsN(Float.parseFloat(wifiCoordX),
                                    Float.parseFloat(wifiCoordY), (double)p.x, (double)p.y);


                    ((WifiHeatMapActivity)context).showDistance(CoordsBuilder.
                            getDistanceBetweenPoints(Float.parseFloat(wifiCoordX),
                            Float.parseFloat(wifiCoordY), (double)p.x, (double)p.y) +" wihout 100 division " +
                            ""+ distanceN);
                }*/
                //   double distance=
                points.add(wifiHeatMapPoints);
                if(context instanceof CreateWalkMapActivity)
                {
                    ((CreateWalkMapActivity)context).refreshOnTouch(wifiHeatMapPoints);
                }
            }
        }
        else
        {
            wifiHeatMapPoints.setQuadId("quad");
            points.add(wifiHeatMapPoints);
            if(context instanceof CreateWalkMapActivity)
            {
                ((CreateWalkMapActivity)context).refreshOnTouch(wifiHeatMapPoints);
            }
        }
    }


    private static final class Vector2 {
        public Vector2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public final float x;
        public final float y;
    }
}
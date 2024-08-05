package com.newmview.wifi.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mview.airtel.R;

import java.util.ArrayList;


public class RectangleView extends View {

    private static final String TAG = "RectangleView";
    private  int zerothPoint,firstPoint,secondPoint,lastPoint;
    private  int rectanglesCount;
    ArrayList<Point> points;

    /**
     * point1 and point 3 are of same group and same as point 2 and point4
     */
    int groupId = -1;
    private ArrayList<ColorBall> colorballs = new ArrayList<ColorBall>();
    // array that holds the balls
    private int balID = 0;
    // variable to know what ball is being dragged
    Paint paint;
    Canvas canvas;
    ArrayList<Rect> rectArrayList=new ArrayList<>();
    private int touchedRectIndex;

    public RectangleView(Context context, int rectanglesCount, ArrayList<Point> points, int count, Paint paint, Canvas canvas) {
        super(context);
        this.rectanglesCount=rectanglesCount;
        this.points=points;

      //  points = new Point[4*rectanglesCount];

Log.i(TAG,"Ponts size in constructor "+points.size());
        this.paint = paint;
        setFocusable(true); // necessary for getting the touch events
        this.canvas = canvas;
         zerothPoint= 4 * (rectanglesCount - 1);
         firstPoint=1+(4*(rectanglesCount-1));
         secondPoint=2+(4*(rectanglesCount-1));
         lastPoint=3+(4*(rectanglesCount-1));
         Log.i(TAG,"Points are "+zerothPoint +", "+firstPoint +" , "+secondPoint +" , "+lastPoint);
    }

    public RectangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
    }

    // the method that draws the balls
    @Override
    protected void onDraw(Canvas canvas) {

        Log.i(TAG,"Last point point["+lastPoint+"] is "+points.get(lastPoint));
        Log.i(TAG,"Points in onDraw are "+zerothPoint +", "+firstPoint +" , "+secondPoint +" , "+lastPoint);

        if(points.get(lastPoint)==null) {//point4 null when user did not touch and move on screen.

            return;
        }
        int left, top, right, bottom;
       // points.get(0).x
      //  int firstPoint=(4*(rectanglesCount-1));
        left =points.get(zerothPoint).x;
        top = points.get(zerothPoint).y;
        right = points.get(zerothPoint).x;
        bottom = points.get(zerothPoint).y;
        Log.i(TAG," Before left "+left +" right "+right +" top "+top +" bottom "+bottom);
       /* for (int i = 1; i < points.size(); i++) {
            left = Math.min(left, points.get(i).x);
            top = Math.min(top, points.get(i).y);
            right = Math.max(right, points.get(i).x);
            bottom = Math.max(bottom, points.get(i).y);
        }*/
        for (int i = firstPoint; i < lastPoint; i++) {
            left = Math.min(left, points.get(i).x);
            top = Math.min(top, points.get(i).y);
            right = Math.max(right, points.get(i).x);
            bottom = Math.max(bottom, points.get(i).y);
        }
      Log.i(TAG," AFter left "+left +" right "+right +" top "+top +" bottom "+bottom);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);

        //draw stroke
        paint.setStyle(Paint.Style.STROKE);
    //    paint.setColor(Color.parseColor("#AADB1255"));
        Log.i(TAG,"Color balls size "+colorballs.size() +" points size "+points.size());
        paint.setStrokeWidth(2);
        Rect rect=new Rect();
        rect.set(
                left + colorballs.get(zerothPoint).getWidthOfBall() / 3,
                top + colorballs.get(zerothPoint).getWidthOfBall() / 3,
                right + colorballs.get(secondPoint).getWidthOfBall() / 3,
                bottom + colorballs.get(secondPoint).getWidthOfBall() / 3
        );


      /*  canvas.drawRect(
                left + colorballs.get(zerothPoint).getWidthOfBall() / 3,
                top + colorballs.get(zerothPoint).getWidthOfBall() / 3,
                right + colorballs.get(secondPoint).getWidthOfBall() / 3,
                bottom + colorballs.get(secondPoint).getWidthOfBall() / 3, paint);*/

       canvas.drawRect(rect,paint);
rectArrayList.add(rect);

        //fill the rectangle
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);

       /* canvas.drawRect(
                left + colorballs.get(0).getWidthOfBall() / 2,
                top + colorballs.get(0).getWidthOfBall() / 2,
                right + colorballs.get(2).getWidthOfBall() / 2,
                bottom + colorballs.get(2).getWidthOfBall() / 2, paint);*/

        //draw the corners
        BitmapDrawable bitmap = new BitmapDrawable();
        // draw the balls on the canvas
      //  paint.setColor(Color.BLUE);
        paint.setTextSize(0);
        paint.setStrokeWidth(0);
        for (int i =0; i < colorballs.size(); i ++) {
            ColorBall ball = colorballs.get(i);
            canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),
                    paint);

            canvas.drawText("" + (i+1), ball.getX(), ball.getY(), paint);
        }
    }

    // events when touching the screen


    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();
        int X = (int) event.getX();
        int Y = (int) event.getY();
int currentRectIndex=((lastPoint+1)/4);
 //touchedRectIndex= (int) getTag();
 Log.i(TAG,"View "+ this+" Touched index "+touchedRectIndex);

canvas.getClipBounds();
int localZerothPoint=(zerothPoint*((currentRectIndex-touchedRectIndex)*4));
        int localFirstPoint=(firstPoint*((currentRectIndex-touchedRectIndex)*4));
        int localSecondPoint=(secondPoint*((currentRectIndex-touchedRectIndex)*4));
        int localThirdPoint=(lastPoint*((currentRectIndex-touchedRectIndex)*4));
       /* if(x > rectLeftX && x < rectRightX && y > rectBottomY && y < rectTopY){
            *//* Trigger your action here *//*
        }*/
Log.i(TAG,"local first "+localFirstPoint +" second "+localSecondPoint +" third "+localThirdPoint);
        switch (eventaction) {

            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on
                // a ball

                if (points.get(zerothPoint) == null) {
                    //initialize rectangle.
                    points.set(zerothPoint,new Point());
                    points.get(zerothPoint).x = X;
                    points.get(zerothPoint).y = Y;

                    points.set(firstPoint,new Point());
                    points.get(firstPoint).x = X;
                    points.get(firstPoint).y = Y + 30;

                    points.set(secondPoint,new Point());
                    points.get(secondPoint).x = X + 30;
                    points.get(secondPoint).y = Y + 30;

                    points.set(lastPoint,new Point());
                    points.get(lastPoint).x = X + 30;
                    points.get(lastPoint).y = Y + 30;
                    balID = secondPoint;
                    groupId = firstPoint;
                    // declare each ball with the ColorBall class
                    int count=4*rectanglesCount;
                    Log.i(TAG,"Points size "+points.size());
                    for (int i = 0; i < points.size(); i++) {
                        Point pt = points.get(i);
                        colorballs.add(new ColorBall(getContext(), R.drawable.circle, pt,i));
                    }
                } else {
                    //resize rectangle
                    Log.i(TAG,"Resizing rec");
                   // int localFirstPoint =
                    balID = -1;
                    groupId = -1;
                    for (int i = colorballs.size()-1; i>=0; i--) {
                        ColorBall ball = colorballs.get(i);
                        // check if inside the bounds of the ball (circle)
                        // get the center for the ball
                        int centerX = ball.getX() + ball.getWidthOfBall();
                        int centerY = ball.getY() + ball.getHeightOfBall();
                        paint.setColor(Color.WHITE);
                        // calculate the radius from the touch to the center of the
                        // ball
                        double radCircle = Math
                                .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                        * (centerY - Y)));
                        Log.i(TAG,"Resize radCircle "+radCircle +" width "+ball.getWidthOfBall());

                        if (radCircle < ball.getWidthOfBall()) {

                            balID = ball.getID();

                            if (balID == firstPoint || balID == lastPoint) {
                                groupId = secondPoint;
                                Log.i(TAG,"Resize first or last ballid "+balID +" group id "+groupId);

                            } else {
                                groupId = firstPoint;
                                Log.i(TAG,"Resize else ballid "+balID +" group id "+groupId);

                            }

                            invalidate();
                            break;
                        }
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE: // touch drag with the ball


                if (balID > -1) {
                    // move the balls the same as the finger
                    Log.i(TAG,"Balli id "+balID);
                    colorballs.get(balID).setX(X);
                    colorballs.get(balID).setY(Y);

                    paint.setColor(Color.BLACK);
                    if (groupId == firstPoint) {
                       /* colorballs.get(1).setX(colorballs.get(0).getX());
                        colorballs.get(1).setY(colorballs.get(2).getY());
                        colorballs.get(3).setX(colorballs.get(2).getX());
                        colorballs.get(3).setY(colorballs.get(0).getY());*/

                        colorballs.get(firstPoint).setX(colorballs.get(zerothPoint).getX());
                        colorballs.get(firstPoint).setY(colorballs.get(secondPoint).getY());
                        colorballs.get(lastPoint).setX(colorballs.get(secondPoint).getX());
                        colorballs.get(lastPoint).setY(colorballs.get(zerothPoint).getY());
                    } else {
                        colorballs.get(zerothPoint).setX(colorballs.get(firstPoint).getX());
                        colorballs.get(zerothPoint).setY(colorballs.get(lastPoint).getY());
                        colorballs.get(secondPoint).setX(colorballs.get(lastPoint).getX());
                        colorballs.get(secondPoint).setY(colorballs.get(firstPoint).getY());
                    }

                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
                // touch drop - just do things here after dropping

                break;
        }
        // redraw the canvas
        invalidate();
        return true;

    }

    public void setIndexOfRectangleTouched(int rectangleIndex) {
        touchedRectIndex=rectangleIndex;
    }

    public void removeChildAt(int i) {
       // points.remove()
    }


    public static class ColorBall {

        Bitmap bitmap;
        Context mContext;
        Point point;
        int id;
        int  count = 0;

        public ColorBall(Context context, int resourceId, Point point, int i) {
        //    this.id = count++;
            this.id = i;
            Log.i(TAG,"Count increased to "+this.id);
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    resourceId);
            mContext = context;
            this.point = point;
        }

        public int getWidthOfBall() {
            return bitmap.getWidth();
        }

        public int getHeightOfBall() {
            return bitmap.getHeight();
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public int getX() {
            return point.x;
        }

        public int getY() {
            return point.y;
        }

        public int getID() {
            return id;
        }

        public void setX(int x) {
            point.x = x;
        }

        public void setY(int y) {
            point.y = y;
        }
    }
}


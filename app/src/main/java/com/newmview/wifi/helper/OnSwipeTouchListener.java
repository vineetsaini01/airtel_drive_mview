package com.newmview.wifi.helper;

import android.view.View;

import com.newmview.wifi.other.Utils;


import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;


;

/**
 * Created by Mehak on 10-05-2018.
 */

public class OnSwipeTouchListener implements View.OnTouchListener {
    private static float DEFAULT_MAX_SCALE = 3.0f;
    private static float DEFAULT_MID_SCALE = 1.75f;
    private static float DEFAULT_MIN_SCALE = 1.0f;
    private static int DEFAULT_ZOOM_DURATION = 200;
    private  GestureListener onGestureListener;
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private  ImageView imageView;
    private  GestureDetector gestureDetector;
    private float downX;
    private float downY;
    private int min_distance = 100;
    float saveScale = 1f;
    float minScale = 1f;
    float maxScale = 3f;
    float[] m;
    Matrix matrix;
    String contenttype;
    private int mZoomDuration = DEFAULT_ZOOM_DURATION;
    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;
    // These are set so we don't keep allocating them on the heap
    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();
    private final RectF mDisplayRect = new RectF();
    private final float[] mMatrixValues = new float[9];
    private static final int HORIZONTAL_EDGE_NONE = -1;
    private static final int HORIZONTAL_EDGE_LEFT = 0;
    private static final int HORIZONTAL_EDGE_RIGHT = 1;
    private static final int HORIZONTAL_EDGE_BOTH = 2;
    private static final int VERTICAL_EDGE_NONE = -1;
    private static final int VERTICAL_EDGE_TOP = 0;
    private static final int VERTICAL_EDGE_BOTTOM = 1;
    private static final int VERTICAL_EDGE_BOTH = 2;
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;
    private int mHorizontalScrollEdge = HORIZONTAL_EDGE_BOTH;
    private int mVerticalScrollEdge = VERTICAL_EDGE_BOTH;


    public OnSwipeTouchListener(Context ctx) {

        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    public OnSwipeTouchListener(Context ctx, String content_type) {
        contenttype=content_type;

        gestureDetector = new GestureDetector(ctx, new GestureListener(contenttype));

    }
    public OnSwipeTouchListener(Context ctx, String content_type, ImageView imageView) {
        contenttype=content_type;
        onGestureListener=new GestureListener(content_type);
        gestureDetector = new GestureDetector(ctx, onGestureListener);
        this.imageView=imageView;

    }
    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow
                (getValue(mSuppMatrix, Matrix.MSKEW_Y), 2));
    }
    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     Matrix to unpack
     * @param whichValue Which value from Matrix.M* to return
     * @return returned value
     */
    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        System.out.println("content type "+contenttype);
        if (Utils.checkifavailable(contenttype)&&(contenttype.equalsIgnoreCase("memo")||
                contenttype.equalsIgnoreCase("text")))
        {
            return gestureDetector.onTouchEvent(event);

        }
       /* else if (Utils.checkifavailable(contenttype)&&(contenttype.equalsIgnoreCase("video")||contenttype.equalsIgnoreCase("uploaded_video")||contenttype.equalsIgnoreCase("search_video")))

        {
            if(gestureDetector.onTouchEvent(event))
            {
                return  true;
            }
            return  false;

        }
*/
        else {

            gestureDetector.onTouchEvent(event);
            return v.onTouchEvent(event);
        }
    }
    public void onclickmethod() {

    }
    public void onTapMethod()
    {

    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
        Log.i("SWIPELISTENER","swipe top");
    }

    public void onSwipeBottom() {

        Log.i("SWIPELISTENER","swipe bottom");

    }
    /*public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
        mMatrixChangeListener = listener;
    }*/
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private  int SWIPE_THRESHOLD =100;
        private   int SWIPE_VELOCITY_THRESHOLD=100 ;

        public GestureListener(String contenttype) {


            if (Utils.checkifavailable(contenttype)&&(contenttype.equalsIgnoreCase("image")||contenttype.equalsIgnoreCase("uploaded_image")))
            {
                SWIPE_THRESHOLD=300;
                SWIPE_VELOCITY_THRESHOLD=300;
            }

        }

        public void onScale(float scaleFactor, float focusX, float focusY) {
            if (getScale() < mMaxScale || scaleFactor < 1f) {
               /* if (mScaleChangeListener != null) {
                    mScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
                }*/
                mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
                checkAndDisplayMatrix();
            }
        }
       /* @Override
        public boolean onSingleTapUp(MotionEvent e) {
           onTapMethod();
            Log.i("SWIPELISTENER","on tap");
            return super.onSingleTapUp(e);
        }*/

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onTapMethod();
            Log.i("SWIPELISTENER","on tap");
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {

            onclickmethod();
            Log.i("SWIPELISTENER","on down");
            return true;
        }
        public float getMinimumScale() {
            return mMinScale;
        }

        public float getMediumScale() {
            return mMidScale;
        }


        public float getMaximumScale() {
            return mMaxScale;
        }

        /*
                @Override
                public boolean onDoubleTap(MotionEvent ev) {
                    Log.i("SWIPELISTENER","on double tap");
                    try {
                        float scale = getScale();
                        float x = ev.getX();
                        float y = ev.getY();
                        if (scale < getMediumScale()) {
                            setScale(getMediumScale(), x, y, true);
                        } else if (scale >= getMediumScale() && scale < getMaximumScale()) {
                            setScale(getMaximumScale(), x, y, true);
                        } else {
                            setScale(getMinimumScale(), x, y, true);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // Can sometimes happen when getX() and getY() is called
                    }
                    return true;
                }
        */
        public void setScale(float scale, float focalX, float focalY,
                             boolean animate) {
            // Check to see if the scale is within bounds
            if (scale < mMinScale || scale > mMaxScale) {
                throw new IllegalArgumentException("Scale must be within the range of minScale and maxScale");
            }
            if (animate) {
                imageView.post(new AnimatedZoomRunnable(getScale(), scale,
                        focalX, focalY,imageView));
            } else {
                mSuppMatrix.setScale(scale, scale, focalX, focalY);
                checkAndDisplayMatrix();
            }
        }
        private int getImageViewWidth(ImageView imageView) {
            return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
        }

        private int getImageViewHeight(ImageView imageView) {
            return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
        }
        private void setImageViewMatrix(Matrix matrix) {
            imageView.setImageMatrix(matrix);
            // Call MatrixChangedListener if needed
/*
            if (mMatrixChangeListener != null) {
                RectF displayRect = getDisplayRect(matrix);
                if (displayRect != null) {
                    mMatrixChangeListener.onMatrixChanged(displayRect);
                }
            }
*/
        }

        private void checkAndDisplayMatrix() {
            if (checkMatrixBounds()) {
                setImageViewMatrix(getDrawMatrix());
            }
        }
        private Matrix getDrawMatrix() {
            mDrawMatrix.set(mBaseMatrix);
            mDrawMatrix.postConcat(mSuppMatrix);
            return mDrawMatrix;
        }
        /**
         * Helper method that maps the supplied Matrix to the current Drawable
         *
         * @param matrix - Matrix to map Drawable against
         * @return RectF - Displayed Rectangle
         */
        private RectF getDisplayRect(Matrix matrix) {
            Drawable d = imageView.getDrawable();
            if (d != null) {
                mDisplayRect.set(0, 0, d.getIntrinsicWidth(),
                        d.getIntrinsicHeight());
                matrix.mapRect(mDisplayRect);
                return mDisplayRect;
            }
            return null;
        }
        private boolean checkMatrixBounds() {
            final RectF rect = getDisplayRect(getDrawMatrix());
            if (rect == null) {
                return false;
            }
            final float height = rect.height(), width = rect.width();
            float deltaX = 0, deltaY = 0;
            final int viewHeight = getImageViewHeight(imageView);
            if (height <= viewHeight) {
                switch (mScaleType) {
                    case FIT_START:
                        deltaY = -rect.top;
                        break;
                    case FIT_END:
                        deltaY = viewHeight - height - rect.top;
                        break;
                    default:
                        deltaY = (viewHeight - height) / 2 - rect.top;
                        break;
                }
                mVerticalScrollEdge = VERTICAL_EDGE_BOTH;
            } else if (rect.top > 0) {
                mVerticalScrollEdge = VERTICAL_EDGE_TOP;
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                mVerticalScrollEdge = VERTICAL_EDGE_BOTTOM;
                deltaY = viewHeight - rect.bottom;
            } else {
                mVerticalScrollEdge = VERTICAL_EDGE_NONE;
            }
            final int viewWidth = getImageViewWidth(imageView);
            if (width <= viewWidth) {
                switch (mScaleType) {
                    case FIT_START:
                        deltaX = -rect.left;
                        break;
                    case FIT_END:
                        deltaX = viewWidth - width - rect.left;
                        break;
                    default:
                        deltaX = (viewWidth - width) / 2 - rect.left;
                        break;
                }
                mHorizontalScrollEdge = HORIZONTAL_EDGE_BOTH;
            } else if (rect.left > 0) {
                mHorizontalScrollEdge = HORIZONTAL_EDGE_LEFT;
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
                mHorizontalScrollEdge = HORIZONTAL_EDGE_RIGHT;
            } else {
                mHorizontalScrollEdge = HORIZONTAL_EDGE_NONE;
            }
            // Finally actually translate the matrix
            mSuppMatrix.postTranslate(deltaX, deltaY);
            return true;
        }

        public GestureListener() {
            super();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i("SWIPELISTENER","on fling");

            boolean result = false;

            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }


                else {
                    result = false;
                }


            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    private void onscrollmethod() {

    }
    private float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            return 0;
        }
        return delta;
    }

    class AnimatedZoomRunnable implements Runnable {

        private final float mFocalX, mFocalY;
        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;
        private ImageView mImageView;

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom,
                                    final float focalX, final float focalY, ImageView imageView) {
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
            mImageView=imageView;
        }

        @Override
        public void run() {
            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();
            onGestureListener.onScale(deltaScale, mFocalX, mFocalY);
            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
              //  Compat.postOnAnimation(mImageView, this);
            }
        }


        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
            t = Math.min(1f, t);
            t = mInterpolator.getInterpolation(t);
            return t;
        }
    }


}



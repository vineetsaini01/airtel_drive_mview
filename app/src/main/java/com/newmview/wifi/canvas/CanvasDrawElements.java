/**
 * CanvasView.java
 *
 * Copyright (c) 2014 Tomohiro IKEDA (Korilakkuma)
 * Released under the MIT license
 */

package com.newmview.wifi.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.mview.airtel.R;
import com.newmview.wifi.activity.CanvasActivity;
import com.newmview.wifi.helper.CoordsBuilder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// import android.util.Log;
// import android.widget.Toast;

/**
 * This class defines fields and methods for drawing.
 */
public class CanvasDrawElements extends View {
	private static final String TAG = "CanvasDrawElements";


	// private ScaleGestureDetector mScaleDetector;
	// private float mScaleFactor = 1.f;

	//ColorGenerator colorGenerator = ColorGenerator.MATERIAL;

	private Bitmap mBitmapBrush,mSelectBitmap;
	private Vector2 mBitmapBrushDimensions,mSelectBitmapBrushDimensions;

	private List<Vector2> mPositions = new ArrayList<Vector2>(100);
	private List<Vector2> mcPositions = new ArrayList<Vector2>(100);

	private int mapColor;
	private List<Vector2> mselectPositions = new ArrayList<Vector2>(100);
	private boolean newText;
	private float startTextX=-1;
	private float startTextY=-1;
	private Rect baserect;
	private Rect destRec,srcRec;
	//private ArraySet<Path> basePathList=new ArrayList<>();


	public void sendBitmap(Bitmap bitmap, int startX, int startY) {
		mSelectBitmap = bitmap;

		mSelectBitmapBrushDimensions = new Vector2(mSelectBitmap.getWidth(), mSelectBitmap.getHeight());

		final int posX11 = mSelectBitmap.getWidth();
		final int posY11 = mSelectBitmap.getHeight();
		srcRec = new Rect(0, 0, posX11, posY11);
		if (!mselectPositions.isEmpty()) {
			mselectPositions.clear();
		}
		//mselectPositions.add(new Vector2(posX11, posY11 ));
		mselectPositions.add(new Vector2(startX, startY ));
	//	mselectPositions.add(new Vector2(CoordsBuilder.startX, CoordsBuilder.endY ));
		//mPositions.add(new Vector2(posX - mBitmapBrushDimensions.x / 2, posY - mBitmapBrushDimensions.y / 2));
		invalidate();
	}


	public static final class Vector2 {
		public Vector2(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public final float x;
		public final float y;
	}

	// Enumeration for Mode
	public enum Mode {
		DRAW, TEXT, ERASER, LOGO, TEXT1, MAP_CIRCLE,SELECT;
	}

	// Enumeration for Drawer
	public enum Drawer {
		PEN, LINE, RECTANGLE, CIRCLE, ELLIPSE, QUADRATIC_BEZIER, QUBIC_BEZIER,MAP_CIRCLE,L_SHAPE
		,L_SHAPE_WITH_ARC;
	}

	private Context context = null;
	private Canvas canvas = null;
	private Bitmap bitmap = null;

	private List<Path> pathLists = new ArrayList<Path>();
	private List<Paint> paintLists = new ArrayList<Paint>();

	// for Eraser
	private int baseColor = Color.WHITE;

	// for Undo, Redo
	private int historyPointer = 0;

	// Flags
	private Mode mode = Mode.DRAW;
	private Drawer drawer = Drawer.PEN;
	private boolean isDown = false;

	// for Paint
	private Paint.Style paintStyle = Paint.Style.STROKE;
	private int paintStrokeColor =  Color.BLACK;
	private int paintFillColor = Color.BLACK;
	private float paintStrokeWidth = 5F;
	private int opacity = 255;
	private float blur = 0F;
	private Paint.Cap lineCap = Paint.Cap.ROUND;

	// for Text
	private String text = "Aweome Canvas";
	private Typeface fontFamily = Typeface.DEFAULT;
	private float fontSize = 32F;
	private Paint.Align textAlign = Paint.Align.RIGHT; // fixed
	private Paint textPaint = new Paint();
	private float textX = 0F;
	private float textY = 0F;

	// for Drawer
	private float startX = 500F;
	private float startY = 300F;
	private float controlX = 0F;
	private float controlY = 0F;
	private float phase = 3;
	private List<String> textList=new ArrayList<>();
	private List<Float> startXList=new ArrayList<>();
	private List<Float> startYList=new ArrayList<>();
	private List<HashMap<String,Float>> xStartList=new ArrayList<>();
	private List<HashMap<String,Float>> yStartList=new ArrayList<>();

	// private GradientBox paintBox;

	/**
	 * Copy Constructor
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CanvasDrawElements(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setup(context);

	}

	/**
	 * Copy Constructor
	 * 
	 * @param context
	 * @param attrs
	 */
	public CanvasDrawElements(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setup(context);
	}

	/**
	 * Copy Constructor
	 * 
	 * @param context
	 */
	public CanvasDrawElements(Context context) {
		super(context);
		this.setup(context);
	}

	/**
	 * Common initialization.
	 * 
	 * @param context
	 */
	private void setup(Context context) {
		this.context = context;

		// paintBox = new GradientBox();

		this.pathLists.add(new Path());
		this.paintLists.add(this.createPaint());
		this.historyPointer++;

		this.textPaint.setARGB(0, 255, 255, 255);

		mBitmapBrush = BitmapFactory.decodeResource(context.getResources(), R.drawable.wifi);

		mBitmapBrushDimensions = new Vector2(mBitmapBrush.getWidth(), mBitmapBrush.getHeight());

		 destRec = new Rect(CoordsBuilder.startX, CoordsBuilder.startY, CoordsBuilder.endX, CoordsBuilder.endY);
		// mScaleDetector = new ScaleGestureDetector(context, new
		// ScaleListener());
	}

	/** The screen half width. */
	private int SCREEN_HALF_WIDTH;

	/** The screen half height. */
	private int SCREEN_HALF_HEIGHT;

	/** The screen width. */
	private int screenWidth;

	/** The screen height. */
	private int screenHeight;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		screenWidth = MeasureSpec.getSize(widthMeasureSpec);
		screenHeight = MeasureSpec.getSize(heightMeasureSpec);

		SCREEN_HALF_WIDTH = screenWidth / 2;

		SCREEN_HALF_HEIGHT = screenHeight / 2;

		this.setMeasuredDimension(screenWidth, screenHeight);

		// paintBox.initPaintBox(screenWidth, screenHeight, SCREEN_HALF_WIDTH,
		// SCREEN_HALF_HEIGHT);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * This method creates the instance of Paint. In addition, this method sets
	 * styles for Paint.
	 * 
	 * @return paint This is returned as the instance of Paint
	 */
	//private Paint createPaintForWhiteBaseRec()

	private Paint createPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(this.paintStyle);
		paint.setStrokeWidth(this.paintStrokeWidth);
		paint.setStrokeCap(this.lineCap);
		paint.setStrokeJoin(Paint.Join.MITER); // fixed
		paint.setTextSize(this.fontSize);
		paint.setTypeface(this.fontFamily);
		paint.setColor(Color.RED);

		if (this.mode == Mode.TEXT) {
		//if (this.mode == Mode.TEXT) {
			paint.setTypeface(this.fontFamily);
			paint.setTextSize(this.fontSize);
			paint.setTextAlign(this.textAlign);
			paint.setStrokeWidth(0F);
		}
if(this.mode==Mode.MAP_CIRCLE)
{
	paint.setStyle(Paint.Style.FILL);
	paint.setColor(this.mapColor);
}
		if (this.mode == Mode.ERASER) {
			// Eraser

			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			paint.setARGB(0, 0, 0, 0);

			// paint.setColor(this.baseColor);
			// paint.setShadowLayer(this.blur, 0F, 0F, this.baseColor);
		} else {
			// Otherwise
			paint.setColor(this.paintStrokeColor);
			paint.setShadowLayer(this.blur, 0F, 0F, this.paintStrokeColor);
			paint.setAlpha(this.opacity);
		}

		return paint;
	}

	private static Path makePathDash() {
		Path p = new Path();
	//	p.quadTo();
		p.moveTo(-6, 4);
		p.lineTo(6, 4);
		p.lineTo(6, 3);
		p.lineTo(-6, 3);
		p.close();
		p.moveTo(-6, -4);
		p.lineTo(6, -4);
		p.lineTo(6, -3);
		p.lineTo(-6, -3);
		return p;
	}

	void setPathEffect(Paint paint) {

		paint.setPathEffect(new DashPathEffect(new float[] { 10, 20 }, 0));

		paint.setPathEffect(new CornerPathEffect(10));

		paint.setPathEffect(new DashPathEffect(new float[] { 10, 5, 5, 5 }, phase));

		paint.setPathEffect(new PathDashPathEffect(makePathDash(), 12, phase, PathDashPathEffect.Style.MORPH));

		// paint.setPathEffect(new ComposePathEffect(e[2], e[1]));

	}

	/**
	 * This method initialize Path. Namely, this method creates the instance of
	 * Path, and moves current position.
	 * 
	 * @param event
	 *            This is argument of onTouchEvent method
	 * @return path This is returned as the instance of Path
	 */
	private Path createPath(MotionEvent event) {
		Path path = new Path();

		// Save for ACTION_MOVE
		this.startX = event.getX();
		this.startY = event.getY();

		path.moveTo(this.startX, this.startY);

		return path;
	}

	/**
	 * This method updates the lists for the instance of Path and Paint. "Undo"
	 * and "Redo" are enabled by this method.
	 * 
	 * @param path
	 *            the instance of Path
	/* * @param paint
	 *            the instance of Paint
	 */
	private void updateHistory(Path path) {
		if (this.historyPointer == this.pathLists.size()) {
			this.pathLists.add(path);
			this.paintLists.add(this.createPaint());
			this.historyPointer++;
		} else {
			// On the way of Undo or Redo
			this.pathLists.set(this.historyPointer, path);
			this.paintLists.set(this.historyPointer, this.createPaint());
			this.historyPointer++;

			for (int i = this.historyPointer, size = this.paintLists.size(); i < size; i++) {
				this.pathLists.remove(this.historyPointer);
				this.paintLists.remove(this.historyPointer);
			}
		}
	}

	/**
	 * This method gets the instance of Path that pointer indicates.
	 * 
	 * @return the instance of Path
	 */
	private Path getCurrentPath() {
		return this.pathLists.get(this.historyPointer - 1);
	}

	/**
	 * This method draws text.
	 * 
	 * @param canvas
	 *            the instance of Canvas
	 */
	private void drawText(Canvas canvas) {
		Log.i(TAG,"size of list "+textList.size());

		Log.i(TAG,"Text list is "+textList.size());
		this.textPaint = this.createPaint();
		for(int j=0;j<textList.size();j++) {
			this.text = textList.get(j);
			if (this.text.length() <= 0) {
				return;
			}

			textPaint.setTypeface(this.fontFamily);
			textPaint.setTextSize(this.fontSize);
			textPaint.setTextAlign(this.textAlign);
			textPaint.setStrokeWidth(0F);
         this.startTextX=startXList.get(j);
         this.startTextY=startYList.get(j);

		//	if (this.mode == Mode.TEXT) {
				this.textX = this.startTextX;
				this.textY = this.startTextY;

				//this.textPaint = this.createPaint();
			//}

			float textX = this.textX;
			float textY = this.textY;

			/*Paint paintForMeasureText = new Paint();

			// Line break automatically
			float textLength = paintForMeasureText.measureText(this.text);
			float lengthOfChar = textLength / (float) this.text.length();
			float restWidth = this.canvas.getWidth() - textX; // text-align : right
			int numChars = (lengthOfChar <= 0) ? 1 : (int) Math.floor((double) (restWidth / lengthOfChar)); // The
			// number
			// line
			int modNumChars = (numChars < 1) ? 1 : numChars;
			float y = textY;*/

			/*for (int i = 0, len = this.text.length(); i < len; i += modNumChars) {
				String substring = "";

				if ((i + modNumChars) < len) {
					substring = this.text.substring(i, (i + modNumChars));
				} else {
					substring = this.text.substring(i, len);
				}

				//y = y + this.fontSize;
Log.i(TAG,"Drawing "+text +" at "+textX+","+y);
			//	canvas.drawText(substring, textX, y, this.textPaint);
			}*/
			canvas.drawText(this.text, textX, textY, this.textPaint);
		}
	}
	private boolean clickOnBitmap(Bitmap myBitmap, MotionEvent event) {
		/*float xEnd = myBitmap.or() + myBitmap.width();
		float yEnd = myBitmap.originY() + myBitmap.height();;


		if ((event.getX() >= myBitmap.originX() && event.getX() <= xEnd)
				&& (event.getY() >= myBitmap.originY() && event.getY() <= yEnd) ) {
			int pixX = (int) (event.getX() - myBitmap.originX());
			int pixY = (int) (event.getY() -  myBitmap.originY());
			if (!(myBitmap.getBitmap().getPixel(pixX, pixY) == 0)) {
				return true;
			} else {
				return false;ff
			}
		}*/
		return false;
	}
	/**
	 * This method defines processes on MotionEvent.ACTION_DOWN
	 * 
	 * @param event
	 *            This is argument of onTouchEvent method
	 */

	private void onActionDown(MotionEvent event) {
		Path path1 = this.getCurrentPath();
		/*if (event.getX() >= xOfYourBitmap && x < (xOfYourBitmap + yourBitmap.getWidth())
				&& y >= yOfYourBitmap && y < (yOfYourBitmap + yourBitmap.getHeight())) {
			//tada, if this is true, you've started your click inside your bitmap
		}*/
		switch (this.mode) {

			case MAP_CIRCLE:
                int rad=40;
               path1.addCircle(event.getX(), event.getY(), rad, Path.Direction.CCW);
				break;
		case DRAW:
		case ERASER:
			if ((this.drawer != Drawer.QUADRATIC_BEZIER) && (this.drawer != Drawer.QUBIC_BEZIER)) {
				// Oherwise
				this.updateHistory(this.createPath(event));
				this.isDown = true;
			} else {
				// Bezier
				if ((this.startX == 0F) && (this.startY == 0F)) {
					// The 1st tap
					this.updateHistory(this.createPath(event));
				} else {
					// The 2nd tap
					this.controlX = event.getX();
					this.controlY = event.getY();

					this.isDown = true;
				}
			}

			break;
		case TEXT:
			if(startX==-1)
				startX=getWidth()/2;
			else
				this.startTextX = event.getX();
			if(startY==-1)
				startY=getHeight()/2;
			else
				this.startTextY = event.getY();



			Log.i(TAG,"New text is "+newText );
			/*if(newText) {
				startXList.add(startX);
				startYList.add(startY);
				newText=false;
			}
			else
			{*/
				if(startXList.size()>0)
				{
					int lastIndex=startXList.size()-1;
					startXList.set(lastIndex,startTextX);
                    startYList.set(lastIndex,startTextY);
				}
			//}
			Log.i(TAG,"Adding x as "+startX +" and y as "+startY +" for text "+this.text);
		/*	HashMap<String,Float> xMap=new HashMap<>();
			xMap.put(text,startX);
			xStartList.add(xMap);

			HashMap<String,Float> yMap=new HashMap<>();
			yMap.put(text,startY);
			yStartList.add(xMap);
			startXList.add(startX);
			startYList.add(startY);*/
			Log.i(TAG,"Text list size for x"+startXList.size());

			break;
		default:
			break;
		}
	}

	/**
	 * This method defines processes on MotionEvent.ACTION_MOVE
	 * 
	 * @param event
	 *            This is argument of onTouchEvent method
	 */
	private void onActionMove(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (this.mode) {
		case DRAW:
		case ERASER:

			if ((this.drawer != Drawer.QUADRATIC_BEZIER) && (this.drawer != Drawer.QUBIC_BEZIER)) {
				if (!isDown) {
					return;
				}

				Path path = this.getCurrentPath();

				switch (this.drawer) {
				case PEN:
					path.lineTo(x, y);
					break;
				case LINE:
					path.reset();
					path.moveTo(this.startX, this.startY);
					path.lineTo(x, y);
					break;
				case RECTANGLE:
					path.reset();
					path.addRect(this.startX, this.startY, x, y, Path.Direction.CCW);

					float width=x-startX;
					float height=y-startY;
					//context.getResources().getDimension()
					/*width=width/100;
					height=height/100;
*/
					Log.i(TAG,"Height "+height +" width "+width +" startx  "+startX +" starty "+startY +" x "
					+x +" Y "+y);
					if(context instanceof CanvasActivity)
					{
						((CanvasActivity)context).sendHW(height,width);
					}

					break;

					case L_SHAPE:
						path.reset();
						path.moveTo(this.startX, this.startY);
						path.lineTo(startX, y);
						path.lineTo(x, y);
						//path.arcTo(x,y,startX,startY,0,-90,false);
					//	path.arcTo(startX,startY,x,y,0,-90,false);

						break;
					case L_SHAPE_WITH_ARC:
						path.reset();
						path.moveTo(this.startX, this.startY);
						path.lineTo(startX, y);
						path.lineTo(x, y);
						path.arcTo(startX,startY,x,y,0,-90,false);
						break;
						//path.arcTo();


				case CIRCLE:
					double distanceX = Math.abs((double) (this.startX - x));
					double distanceY = Math.abs((double) (this.startX - y));
					double radius = Math.sqrt(Math.pow(distanceX, 2.0) + Math.pow(distanceY, 2.0));

					path.reset();
					path.addCircle(this.startX, this.startY, (float) radius, Path.Direction.CCW);
					break;
				case ELLIPSE:
					RectF rect = new RectF(this.startX, this.startY, x, y);

					path.reset();
					path.addOval(rect, Path.Direction.CCW);
					break;

				default:
					break;
				}
			} else {
				if (!isDown) {
					return;
				}

				Path path = this.getCurrentPath();

				path.reset();
				Log.i(TAG,"Move x "+startX +" y "+startY +" control x "+controlX +" control y " +
						""+controlY);
				path.moveTo(this.startX, this.startY);
				path.quadTo(this.controlX, this.controlY, x, y);
			}

			break;
		case TEXT:
			/*this.startX = 100;
			this.startY = 100;*/
			if(startX==-1)
				startX=getWidth()/2;
			else
				this.startTextX = x;
			if(startY==-1)
				startY=getHeight()/2;
			else
				this.startTextY = y;


			/*startXList.add(x);
			startYList.add(y);*/
Log.i(TAG,"adding "+startX+" y "+startY);
			if(startXList.size()>0)
			{
				int lastIndex=startXList.size()-1;
				startXList.set(lastIndex,startTextX);
				startYList.set(lastIndex,startTextY);
			}

			break;

		case LOGO:

			final float posX = event.getX();
			final float posY = event.getY();

			if (!mPositions.isEmpty()) {
				mPositions.clear();
				mcPositions.clear();
			}
			float bx=posX - mBitmapBrushDimensions.x / 2;
			float by=posY - mBitmapBrushDimensions.y / 2;

			Log.i(TAG,"X pos is "+posX +"y pos is "+posY+
					" bitmap poss x"+bx +" bitmap y pos "+by);
			mcPositions.add(new Vector2(posX,
					posY));
			/*mcPositions.add(new Vector2(posX - mBitmapBrushDimensions.x / 2,
					posY - mBitmapBrushDimensions.y / 2));*/

			mPositions.add(new Vector2(posX - mBitmapBrushDimensions.x / 2,
					posY - mBitmapBrushDimensions.y / 2));

			invalidate();
			break;
			case SELECT:

				final float posX11 = event.getX();
				final float posY11 = event.getY();

				if (!mselectPositions.isEmpty()) {
					mselectPositions.clear();
				}
				mselectPositions.add(new Vector2(posX11, posY11 ));
				//mPositions.add(new Vector2(posX - mBitmapBrushDimensions.x / 2, posY - mBitmapBrushDimensions.y / 2));
				invalidate();
				break;
		default:
			break;
		}
	}

	/**
	 * This method defines processes on MotionEvent.ACTION_DOWN
	 * 
	 * @param event
	 *            This is argument of onTouchEvent method
	 */
	private void onActionUp(MotionEvent event) {
		if (isDown) {
			this.startX = 0F;
			this.startY = 0F;
			this.isDown = false;
		}
	}

	/**
	 * This method updates the instance of Canvas (View)
	 * 
	 * @param canvas
	 *            the new instance of Canvas
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// canvas.save();
		// canvas.scale(mScaleFactor, mScaleFactor);

		// Before "drawPath"
		canvas.drawColor(this.baseColor);

		for (int i = 0; i < mselectPositions.size(); i++) {
			Vector2 pos = mselectPositions.get(i);
			Log.i(TAG,"Drawing "+mSelectBitmap +" pos x "+pos.x +" y "+pos.y);
			if(mSelectBitmap!=null) {
				//canvas.drawBitmap(mSelectBitmap, pos.x, pos.y, null);
				//clip the bitmap to show
				final int posX11 = mSelectBitmap.getWidth();
				final int posY11 = mSelectBitmap.getHeight();

				canvas.drawBitmap(mSelectBitmap,srcRec,destRec,null);
			}
		}

		for (int i = 0; i < this.historyPointer; i++) {
			Path path = this.pathLists.get(i);

			Paint paint = this.paintLists.get(i);

			// paint.setColor(colorGenerator.getRandomColor());
Log.i(TAG,"Draw path pointers...");
if(i==0) {
	if (getBitmapValue() != null) {
		paint.setColor(Color.WHITE);
	}
	else
	{
		paint.setColor(paintLists.get(i).getColor());
}
}
			canvas.drawPath(path, paint);
		}
		if(startXList!=null)
		{
			if(startXList.size()>0)
			{
				this.drawText(canvas);
			}
		}
		this.canvas = canvas;



		for (int i = 0; i < mPositions.size(); i++) {
			Vector2 pos = mPositions.get(i);
		//	Log.i(TAG,"Drawing "+mBitmapBrush +" pos x "+pos.x +" y "+pos.y);
			canvas.drawBitmap(mBitmapBrush, pos.x, pos.y, null);

			/*canvas.drawCircle(pos.x,pos.y,5,createPaint());*/
		}
		/*for (int i = 0; i < mcPositions.size(); i++) {
			Vector2 pos = mcPositions.get(i);
			Log.i(TAG,"Drawing circle at" +" pos x "+pos.x +" y "+pos.y +" for i "+i);
			//canvas.drawBitmap(mBitmapBrush, pos.x, pos.y, null);
			canvas.drawCircle(pos.x,pos.y,15,createPaint());
		}
*/
		// canvas.restore();
	}

	public List<Vector2> getWifiCoords()

	{
		Log.i(TAG,"Returning wificoords are"+mcPositions);
		return mcPositions;
	}

	/**
	 * This method set event listener for drawing.
	 * 
	 * @param event
	 *            the instance of MotionEvent
	 * @return
	 */
private void performTouchEvents(MotionEvent event)
{
	Log.i(TAG,"Touch event is "+event.getAction() +"with mode "+this.mode);
	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.onActionDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			this.onActionMove(event);
			break;
		case MotionEvent.ACTION_UP:
			this.onActionUp(event);
			break;
		default:
			break;
	}
}
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// Let the ScaleGestureDetector inspect all events.



		// mScaleDetector.onTouchEvent(event);

		// Re draw

		this.invalidate();
		if(mode==Mode.MAP_CIRCLE) {
			return gestureDetector.onTouchEvent(event);
		}
		else
			performTouchEvents(event);
		return true;
	}
	final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
		//	performScreenTouch(e);
			performTouchEvents(e);


			return true;
		}


		@Override
		public void onLongPress(MotionEvent e) {
           /* if (imageView.isReady()) {
                PointF sCoord = imageView.viewToSourceCoord(e.getX(), e.getY());
                Toast.makeText(getApplicationContext(), "Long press: " + ((int)sCoord.x) + ", " + ((int)sCoord.y), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Long press: Image not ready", Toast.LENGTH_SHORT).show();
            }*/
		}
		@Override
		public boolean onDoubleTap(MotionEvent e) {
           /* if (imageView.isReady()) {
                PointF sCoord = imageView.viewToSourceCoord(e.getX(), e.getY());
                Toast.makeText(getApplicationContext(), "Double tap: " + ((int)sCoord.x) + ", " + ((int)sCoord.y), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Double tap: Image not ready", Toast.LENGTH_SHORT).show();
            }*/
			return true;
		}
	});

	/**
	 * This method is getter for mode.
	 * 
	 * @return
	 */
	public Mode getMode() {
		return this.mode;
	}

	/**
	 * This method is setter for mode.
	 * 
	 * @param mode
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * This method is getter for drawer.
	 * 
	 * @return
	 */
	public Drawer getDrawer() {
		return this.drawer;
	}

	/**
	 * This method is setter for drawer.
	 * 
	 * @param drawer
	 */
	public void setDrawer(Drawer drawer) {
		this.drawer = drawer;
	}

	/**
	 * This method draws canvas again for Undo.
	 * 
	 * @return If Undo is enabled, this is returned as true. Otherwise, this is
	 *         returned as false.
	 */
	public boolean undo() {
		Log.i(TAG,"History pointer "+historyPointer);
		if (this.historyPointer > 1) {
			this.historyPointer--;
			this.invalidate();

			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method draws canvas again for Redo.
	 * 
	 * @return If Redo is enabled, this is returned as true. Otherwise, this is
	 *         returned as false.
	 */
	public boolean redo() {
		if (this.historyPointer < this.pathLists.size()) {
			this.historyPointer++;
			this.invalidate();

			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method initializes canvas.
	 * 
	 * @return
	 */
	public void changePaintColor(int color)
	{
		this.paintStrokeColor=color;
		this.paintFillColor=color;
		//paint.setColor(color);
		//invalidate();
	}
	public Bitmap getBitmapValue()
	{
		return mSelectBitmap;
	}
	public void clearSelectedImage()
	{
		if (!mselectPositions.isEmpty()) {
			mselectPositions.clear();
		}
		mSelectBitmap=null;
		invalidate();
	}
	public void clear() {
		Path path = new Path();
		path.moveTo(0F, 0F);
		path.addRect(0F, 0F, 10000F, 10000F, Path.Direction.CCW);
		path.close();

		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);

		if (this.historyPointer == this.pathLists.size()) {
			this.pathLists.add(path);
			this.paintLists.add(paint);
			this.historyPointer++;
		} else {
			// On the way of Undo or Redo
			this.pathLists.set(this.historyPointer, path);
			this.paintLists.set(this.historyPointer, paint);
			this.historyPointer++;

			for (int i = this.historyPointer, size = this.paintLists.size(); i < size; i++) {
				this.pathLists.remove(this.historyPointer);
				this.paintLists.remove(this.historyPointer);
			}
		}

		this.text = "";

		// Clear
		this.invalidate();
	}

	/**
	 * This method is getter for canvas background color
	 * 
	 * @return
	 */
	public int getBaseColor() {
		return this.baseColor;
	}

	/**
	 * This method is setter for canvas background color
	 * 
	 * @param color
	 */
	public void setBaseColor(int color) {
		this.baseColor = color;
	}

	/**
	 * This method is getter for drawn text.
	 * 
	 * @return
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * This method is setter for drawn text.
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}
	public void addText(String text)
	{
		textList.add(text);
		newText=true;
		/*startTextX=getWidth()/2-700;
		startTextY=getHeight()/2;*/
		startTextX=500;
		startTextY=500;

		/*startTextX = (canvas.getWidth() >> 1);
		startTextY = (int) ((canvas.getHeight() / 2) -
				((textPaint.descent() + textPaint.ascent()) / 2)) ;*/
		startXList.add(startTextY);
		startYList.add(startTextY);
		newText=false;


	}

	/**
	 * This method is getter for stroke or fill.
	 * 
	 * @return
	 */
	public Paint.Style getPaintStyle() {
		return this.paintStyle;
	}

	/**
	 * This method is setter for stroke or fill.
	 * 
	 * @param style
	 */
	public void setPaintStyle(Paint.Style style) {
		this.paintStyle = style;
	}

	/**
	 * This method is getter for stroke color.
	 * 
	 * @return
	 */
	public int getPaintStrokeColor() {
		return this.paintStrokeColor;
	}

	/**
	 * This method is setter for stroke color.
	 * 
	 * @param color
	 */
	public void setPaintStrokeColor(int color) {
		this.paintStrokeColor = color;
	}

	/**
	 * This method is getter for fill color. But, current Android API cannot set
	 * fill color (?).
	 * 
	 * @return
	 */
	public int getPaintFillColor() {
		return this.paintFillColor;
	};

	/**
	 * This method is setter for fill color. But, current Android API cannot set
	 * fill color (?).
	 * 
	 * @param color
	 */
	public void setPaintFillColor(int color) {
		this.paintFillColor = color;
	}

	/**
	 * This method is getter for stroke width.
	 * 
	 * @return
	 */
	public float getPaintStrokeWidth() {
		return this.paintStrokeWidth;
	}

	/**
	 * This method is setter for stroke width.
	 * 
	 * @param width
	 */
	public void setPaintStrokeWidth(float width) {
		if (width >= 0) {
			this.paintStrokeWidth = width;
		} else {
			this.paintStrokeWidth = 3F;
		}
	}

	/**
	 * This method is getter for alpha.
	 * 
	 * @return
	 */
	public int getOpacity() {
		return this.opacity;
	}

	/**
	 * This method is setter for alpha. The 1st argument must be between 0 and
	 * 255.
	 * 
	 * @param opacity
	 */
	public void setOpacity(int opacity) {
		if ((opacity >= 0) && (opacity <= 255)) {
			this.opacity = opacity;
		} else {
			this.opacity = 255;
		}
	}

	/**
	 * This method is getter for amount of blur.
	 * 
	 * @return
	 */
	public float getBlur() {
		return this.blur;
	}

	/**
	 * This method is setter for amount of blur. The 1st argument is greater
	 * than or equal to 0.0.
	 * 
	 * @param blur
	 */
	public void setBlur(float blur) {
		if (blur >= 0) {
			this.blur = blur;
		} else {
			this.blur = 0F;
		}
	}
	public void setMapColor(int color)
	{
		mapColor=color;
	}

	/**
	 * This method is getter for line cap.
	 * 
	 * @return
	 */
	public Paint.Cap getLineCap() {
		return this.lineCap;
	}

	/**
	 * This method is setter for line cap.
	 * 
	 * @param cap
	 */
	public void setLineCap(Paint.Cap cap) {
		this.lineCap = cap;
	}

	/**
	 * This method is getter for font size,
	 * 
	 * @return
	 */
	public float getFontSize() {
		return this.fontSize;
	}

	/**
	 * This method is setter for font size. The 1st argument is greater than or
	 * equal to 0.0.
	 * 
	 * @param size
	 */
	public void setFontSize(float size) {
		if (size >= 0F) {
			this.fontSize = size;
		} else {
			this.fontSize = 32F;
		}
	}

	/**
	 * This method is getter for font-family.
	 * 
	 * @return
	 */
	public Typeface getFontFamily() {
		return this.fontFamily;
	}

	/**
	 * This method is setter for font-family.
	 * 
	 * @param face
	 */
	public void setFontFamily(Typeface face) {
		this.fontFamily = face;
	}

	/**
	 * This method gets current canvas as bitmap.
	 * 
	 * @return This is returned as bitmap.
	 */
	public Bitmap getBitmap() {
		this.setDrawingCacheEnabled(false);
		this.setDrawingCacheEnabled(true);

		return Bitmap.createBitmap(this.getDrawingCache());
	}

	/**
	 * This method gets current canvas as scaled bitmap.
	 * 
	 * @return This is returned as scaled bitmap.
	 */
	public Bitmap getScaleBitmap(int w, int h) {
		this.setDrawingCacheEnabled(false);
		this.setDrawingCacheEnabled(true);

		return Bitmap.createScaledBitmap(this.getDrawingCache(), w, h, true);
	}

	/**
	 * This method draws the designated bitmap to canvas.
	 * 
	 * @param bitmap
	 */
	public void drawBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;

		if (this.bitmap != null) {
			canvas.drawBitmap(this.bitmap, getWidth() / 2,
					getHeight() / 2, new Paint());
		}
		this.invalidate();
	}

	/**
	 * This method draws the designated byte array of bitmap to canvas.
	 * 
	 * @param byteArray
	 *            This is returned as byte array of bitmap.
	 */
	public void drawBitmap(byte[] byteArray) {
		this.drawBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
	}

	/**
	 * This static method gets the designated bitmap as byte array.
	 * 
	 * @param bitmap
	 * @param format
	 * @param quality
	 * @return This is returned as byte array of bitmap.
	 */
	public static byte[] getBitmapAsByteArray(Bitmap bitmap, CompressFormat format, int quality) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(format, quality, byteArrayOutputStream);

		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * This method gets the bitmap as byte array.
	 * 
	 * @param format
	 * @param quality
	 * @return This is returned as byte array of bitmap.
	 */
	public byte[] getBitmapAsByteArray(CompressFormat format, int quality) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		this.getBitmap().compress(format, quality, byteArrayOutputStream);

		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * This method gets the bitmap as byte array. Bitmap format is PNG, and
	 * quality is 100.
	 * 
	 * @return This is returned as byte array of bitmap.
	 */
	public byte[] getBitmapAsByteArray() {
		return this.getBitmapAsByteArray(CompressFormat.PNG, 100);
	}

	int gradientStyle;

	public void setGrdientStyle(int gradientStyle) {

		this.gradientStyle = gradientStyle;
	}
public void removeBaseRect()
{
	Path path = this.getCurrentPath();
//	path.rem
}
	public void drawBaseRectangle(Context context)
	{
		Path path = this.getCurrentPath();
		//co-ordinates for main rectangle
		int startX= CoordsBuilder.startX;
		int startY=CoordsBuilder.startY;
		int endY=CoordsBuilder.endY;
		int endX=CoordsBuilder.endX;

		path.addRect(startX, startY, endX,
				endY, Path.Direction.CCW);
	//	basePathList.add(path);

		//Point[] points_G1=new Point[4];
		//Grid1 coords


		/*Log.i(TAG,"Height before "+ Utils.getScreenWidth(context) +" getWidth "+Utils.getScreenHeight(context) +" currnt path "+path);
		int x=Utils.getScreenWidth(context)-200;
		int y=Utils.getScreenHeight(context)-500;



		path.addRect(40, 40, x,
				y, Path.Direction.CCW);
		float width=x-40;
		float height=y-40;
		Log.i(TAG,"Height "+height +" width "+width +" startx  "+40 +" starty "+40 +" x "
				+x +" Y "+y);*/
		invalidate();
	}

	// private class ScaleListener extends
	// ScaleGestureDetector.SimpleOnScaleGestureListener {
	// @Override
	// public boolean onScale(ScaleGestureDetector detector) {
	// mScaleFactor *= detector.getScaleFactor();
	//
	// // Don't let the object get too small or too large.
	// mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
	//
	// invalidate();
	// return true;
	// }
	// }

	public Paint getShader(int gradientType, int x, int y, Paint paint) {

		switch (gradientType) {
		case RADIEL_GRADIENT:

			RadialGradient gradient = new RadialGradient
					(x, y, this.paintStrokeWidth,
							0xFFFFFFFF, 0xFF000000,
					Shader.TileMode.REPEAT);
			paint.setDither(true);
			paint.setShader(gradient);

			return paint;

		case LINER_GRADIENT:

			paint.setAntiAlias(true);
			Shader linearGradientShader;

			linearGradientShader = new LinearGradient(0, 0, screenWidth, screenHeight, shaderColor1, shaderColor0,
					Shader.TileMode.MIRROR);

			paint.setShader(linearGradientShader);

			linearGradientShader = new LinearGradient(SCREEN_HALF_WIDTH, SCREEN_HALF_HEIGHT,
					SCREEN_HALF_WIDTH + SCREEN_HALF_WIDTH / 4, SCREEN_HALF_HEIGHT + SCREEN_HALF_HEIGHT / 4,
					shaderColor0, shaderColor1, Shader.TileMode.MIRROR);

			paint.setShader(linearGradientShader);

			return paint;

		case SWEEP_GRADIENT:

			paint.setAntiAlias(true);
			paint.setShader(new SweepGradient(SCREEN_HALF_WIDTH, SCREEN_HALF_HEIGHT, shaderColor0, shaderColor1));

			return paint;

		case COMPOSE_GRADIENT:

			RadialGradient radial_gradient = new RadialGradient(SCREEN_HALF_WIDTH, SCREEN_HALF_HEIGHT,
					this.paintStrokeWidth, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);

			int morecolors[] = new int[13];
			float hsv[] = new float[3];
			hsv[1] = 1;
			hsv[2] = 1;
			for (int i = 0; i < 12; i++) {
				hsv[0] = (360 / 12) * i;
				morecolors[i] = Color.HSVToColor(hsv);
			}
			morecolors[12] = morecolors[0];

			SweepGradient sweep_gradient = new SweepGradient(SCREEN_HALF_WIDTH, SCREEN_HALF_HEIGHT, morecolors, null);

			ComposeShader shader = new ComposeShader(sweep_gradient, radial_gradient, PorterDuff.Mode.SRC_OVER);

			paint.setDither(true);
			paint.setShader(shader);

			return paint;

		default:
			break;
		}
		return paint;

	}

	int shaderColor0 = Color.RED;
	int shaderColor1 = Color.BLUE;

	public static final int RADIEL_GRADIENT = 0;
	public static final int LINER_GRADIENT = 1;
	public static final int SWEEP_GRADIENT = 2;
	public static final int COMPOSE_GRADIENT = 4;

}

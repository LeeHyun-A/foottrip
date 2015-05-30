package com.example.trip;

import java.util.ArrayList;
import java.util.LinkedList;

import com.example.trip.GPSmap.TripGPS;

//import com.example.trip.GPSmap.TripGPS;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
// 최민영 바보 멍청이 
public class DrawingView extends View{
	//drawing path
	private static Path drawPath;
	//drawing and canvas paint
	private Paint drawPaint, canvasPaint;
	//initial color
	private int paintColor = 0xFF660000;
	//canvas
	private Canvas drawCanvas;
	//canvas bitmap
	private static Bitmap canvasBitmap;
	private static ArrayList<Bitmap> bitmaps;
	private static ArrayList<Bitmap> undoneBitmaps;
	//brush state
	private float brushSize, lastBrushSize;
	//erase state
	private boolean erase=false;
	private boolean PaintUnselected=false;

	private ImageButton paintButton;
	//Canvas Height, Width, Left, Right, Top, Bottom
	private static int CH, CW, CL, CR, CT, CB;
	//canvas size related variable
	private static double canv_cent_x, canv_cent_y;
	//private double d_left,d_right, d_up, d_down, d_leftup, d_rightup, d_right_down, d_leftdown;

	//canvas can draw this distance
	//private double Max_X,Max_Y;

	private static int bitmap_arr_size_x = 15;
	private static int bitmap_arr_size_y = 25;

	//boolean state checker
	private static boolean isReady = false;
	private static LinkedList<TripGPS> TG;

	//getter & setter
	public void setLastBrushSize(float lastSize){lastBrushSize=lastSize;}
	public float getLastBrushSize(){return lastBrushSize;}
	public boolean IsPaintUnselected(){return PaintUnselected;}

	//start
	public DrawingView(Context context, AttributeSet attrs){
		super(context, attrs);
		setupDrawing();

		bitmaps = new ArrayList<Bitmap>();
		undoneBitmaps = new ArrayList<Bitmap>();
	}

	private void setupDrawing(){
		//get drawing area setup for interaction       
		drawPath = new Path();
		drawPaint = new Paint();

		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		canvasPaint = new Paint(Paint.DITHER_FLAG);

		//Instantiate brush state
		brushSize = 10;
		lastBrushSize = brushSize;
		drawPaint.setStrokeWidth(brushSize);
	}

	public void readyToDraw(){
		CH = getHeight();
		CW = getWidth();
		if(CH==0 && CW==0) return;
		CR = getRight();
		CL = getLeft();
		CT = getTop();
		CB = getBottom();

		canv_cent_x = getCanvas_CenterX();
		canv_cent_y = getCanvas_CenterY();
		Log.d("D_CH",""+CH);
		Log.d("D_CW",""+CW);
		Log.d("D_CR",""+CR);
		Log.d("D_CL",""+CL);
		Log.d("D_CT",""+CT);
		Log.d("D_CB",""+CB);

		isReady = true;
		Log.i("readyToDraw","readyToDrawreadyToDraw");
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean _isReady) {
		isReady = _isReady;
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//view given size
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);

	}
	@Override
	protected void onDraw(Canvas canvas) {
		//draw view
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}

	protected void Drawperminute(GPSmap T_gm, int cent_x, int cent_y){
		setColor(paintColor);

		TG = T_gm.getmGPSbitmap();
		//T_gm.setmGPSbitmap(T_gm.getmGPSbitmap());
		TripGPS tem = T_gm.getmGPSbitmap().getFirst();
		TripGPS isArrange_test = T_gm.getmGPSbitmap().getLast();

		//flush all things in the canvas
		startNew();
		initiatePathNPaints();

		float iAX = getCanvas_X(cent_x -isArrange_test.getIdxX());
		float iAY = getCanvas_Y(cent_y -isArrange_test.getIdxY());

		//범위에서 벗어나는 경우,rearrange해준다.
		if(is_needtoarrange(iAX,iAY)){
			Log.d("Rearrange","리어레에에에엔지");
			arrange();
		}

		//Log.d("STARTNEW","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		Log.d("init coordin",getCanvas_CenterX()+", "+getCanvas_CenterY());
		drawPath.moveTo(getCanvas_CenterX(),getCanvas_CenterY());
		setColor(paintColor);

		for(int i=1; i<TG.size(); i++){
			tem = TG.get(i);
			float canvas_x = getCanvas_X(cent_x - tem.getIdxX());
			float canvas_y = getCanvas_Y(cent_y - tem.getIdxY());

			Log.d("#"+(i+1)+" real", tem.getIdxX()+", "+tem.getIdxY());
			Log.d("#"+(i+1)+" coordin", canvas_x+", "+canvas_y);

			drawPath.lineTo(canvas_x,canvas_y);
		}

		invalidate();
	}
	


	//재조정해줄지 확인해주는 함수
	protected boolean is_needtoarrange(float iAX, float iAY){
		if((iAX <CL ||iAY < CT) || // 왼쪽
				(iAX >CR ||iAY < CT) || // 오른쪽
				(iAX <CL ||iAY > CB) || // 왼쪽밑
				(iAX >CR ||iAY > CB)  )  // 오른쪽밑
		{
			Log.d("iAX" ,""+iAX);
			Log.d("iAY" ,""+iAY);
			return true;
		}


		return false;
	}
	//재조정해주는 함수
	protected void arrange(){
		bitmap_arr_size_y = bitmap_arr_size_y*(3/2);
		bitmap_arr_size_x = bitmap_arr_size_x*(3/2);
	}
	
	protected void setSize(int width, int height, int left, int top, Double lat, Double lng){
		CW = width;
		CH = height;
		CL = left;
		CT = top;
		canv_cent_x = lat;
		canv_cent_y = lng;
	}

	protected float getCanvas_CenterX(){
		return CL+ (CW/bitmap_arr_size_y)*(float)(bitmap_arr_size_y/2.0);


	}

	protected float getCanvas_CenterY(){
		return CT+ (CH/bitmap_arr_size_x)*(float)(bitmap_arr_size_x/2.0);
	}

	//실제로 그려줄 크기
	protected float getCanvas_X(int dif_cx){
		double cellWidth = CW/bitmap_arr_size_x;
		return (float)(canv_cent_x + (dif_cx*cellWidth));
	}

	protected float getCanvas_Y(int dif_cy){
		double cellHeight = CH/bitmap_arr_size_y;
		return (float)(canv_cent_y + (dif_cy*cellHeight));
	}


	//Setting color
	public void setColor(String newColor){
		//set color
		invalidate();
		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
	}
	public void setColor(int newColor){	//overloading setColor()
		//set color
		invalidate();
		drawPaint.setColor(newColor);
	}
	//Setting brush size
	public void setBrushSize(float newSize){
		//update size
		float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
				newSize, getResources().getDisplayMetrics());
		brushSize=pixelAmount;
		drawPaint.setStrokeWidth(brushSize);
	}
	public void setSelectedPaint(ImageButton pressedPaint){
		PaintUnselected=false;
		paintButton = pressedPaint;
	}
	public void startNew(){
		if(drawCanvas != null){
			drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

			bitmaps.clear();
			undoneBitmaps.clear();
			canvasBitmap = Bitmap.createBitmap(canvasBitmap.getWidth(), canvasBitmap.getHeight(), Bitmap.Config.ARGB_8888);
			drawCanvas.setBitmap(canvasBitmap);

			invalidate();
		}
	}
	public void initiatePathNPaints(){
		//Initiate path and paint member for remembering to do redo and undo 
		drawPath = new Path();
		drawPath.reset();
		drawPaint = new Paint();
		drawPaint.reset();
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		drawPaint.setStrokeWidth(brushSize);
		drawPaint.setColor(paintColor);
	}
}
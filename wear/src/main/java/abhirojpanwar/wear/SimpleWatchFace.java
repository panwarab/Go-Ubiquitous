package abhirojpanwar.wear;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.Log;

/**
 * Created by Abhiroj on 2/15/2017.
 */

public class SimpleWatchFace {

    private static final String TAG="SimpleWatchFaceClass";

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String DATE_FORMAT = "%02d/%02d/%d";


    Typeface NORMAL_TYPEFACE=Typeface.create(Typeface.SANS_SERIF,Typeface.NORMAL);

    Paint mTextTimePaint;
    Paint mTextDatePaint;
    Paint mTextWeatherPaint;

    float TimeOffsetX;
    float TimeOffsetY;
    float DateOffSetX;
    float DateOffsetY;
    float TempOffsetX;
    float TempOffsetY;
    Resources resources;
    Context context;
    float timeSize;
    float dateSize;
    Canvas globalcanvas;

    boolean ambient;

    private final Time time;


    public static SimpleWatchFace newInstance(Context context, float timeSize, float dateSize) {
        return new SimpleWatchFace(context, new Time(),timeSize,dateSize);
    }

    SimpleWatchFace(Context context, Time time,float timeSize,float dateSize) {
        mTextTimePaint=createTimeObject();
        mTextDatePaint=createDateObject();
        mTextWeatherPaint=createWeatherObject();
        this.context=context;
        this.timeSize=timeSize;
        this.dateSize=dateSize;
        resources=context.getResources();
        Log.i(TAG,(resources==null)?"resource is null":"resource="+resources.toString());
        this.time = time;
        calculateOffsets();
    }

    private void configureForAmbientMode()
    {
        globalcanvas.drawColor(Color.BLACK);
        mTextDatePaint.setColor(Color.WHITE);
        mTextDatePaint.setTypeface(Typeface.DEFAULT);
        mTextTimePaint.setColor(Color.WHITE);
        mTextTimePaint.setTypeface(Typeface.DEFAULT);
        mTextWeatherPaint.setTypeface(Typeface.DEFAULT);
        mTextWeatherPaint.setColor(Color.WHITE);
    }

    private void setConfigsIfVisible()
    {
        globalcanvas.drawColor(Color.parseColor("#42A5F5"));
    }

    public void draw(Canvas canvas, Rect bounds,String Hightemp,String Lowtemp,Integer weatherId,boolean checkForAmbient) {
        time.setToNow();
        globalcanvas=canvas;
        if(checkForAmbient)
        {
            // If watch is in Ambient Mode, set all the configurations for it.
            configureForAmbientMode();
        }
        else {
            setConfigsIfVisible();
        }
        String timeText = String.format( TIME_FORMAT_WITHOUT_SECONDS, time.hour, time.minute);
        canvas.drawText(timeText,bounds.centerX()-TimeOffsetX, TimeOffsetY, mTextTimePaint);

        String dateText = String.format(DATE_FORMAT, time.monthDay, (time.month + 1), time.year);
        canvas.drawText(dateText,bounds.centerX()-DateOffSetX, DateOffsetY, mTextDatePaint);

        Log.d(TAG,"Current weather conditions="+Hightemp+","+Lowtemp+","+weatherId);
        if(Hightemp!=null && Lowtemp!=null && weatherId!=null)
        {

            drawHighTemp(canvas,Hightemp,bounds);
            drawLowTemp(canvas,Lowtemp,bounds);
            if(!checkForAmbient)
            drawWeatherIcon(canvas,weatherId,bounds,Hightemp);
        }
        //For Debug, making a rough layout!!
        else{
            Log.d(TAG,"Drawing Demo");

            drawHighTemp(canvas,"22",bounds);
            drawLowTemp(canvas,"18",bounds);
            if(!checkForAmbient)
            drawWeatherIcon(canvas,502,bounds,"22");
        }
    }

    private void drawWeatherIcon(Canvas canvas,Integer weatherId,Rect bounds,String Hightemp)
    {
        float highTextSize =mTextWeatherPaint.measureText(Hightemp);
        Drawable b = context.getResources().getDrawable(IconUtility.getSmallArtResourceIdForWeatherCondition(weatherId));
        Bitmap icon = ((BitmapDrawable) b).getBitmap();
        float scaledWidth = (mTextWeatherPaint.getTextSize() / icon.getHeight()) * icon.getWidth();
        Bitmap weatherIcon = Bitmap.createScaledBitmap(icon, (int) scaledWidth, (int) mTextWeatherPaint.getTextSize(), true);
        float iconXOffset = bounds.centerX() - ((highTextSize / 2) + weatherIcon.getWidth() + 30);
        canvas.drawBitmap(weatherIcon, iconXOffset, TempOffsetY - weatherIcon.getHeight(), null);
    }
    private void drawHighTemp(Canvas canvas,String Hightemp,Rect bounds)
    {
        float highTextSize =mTextWeatherPaint.measureText(Hightemp);
        float xOffset = bounds.centerX() - (highTextSize / 2);
        mTextWeatherPaint.setColor(Color.BLACK);
        canvas.drawText(Hightemp, xOffset, TempOffsetY, mTextWeatherPaint);
    }

    private void drawLowTemp(Canvas canvas,String Lowtemp,Rect bounds)
    {
        float highTextSize =mTextWeatherPaint.measureText(Lowtemp);
        float xOffset = bounds.centerX() - (highTextSize / 2);
        mTextWeatherPaint.setColor(Color.GRAY);
        canvas.drawText(Lowtemp, bounds.centerX() + (highTextSize / 2) + 20, TempOffsetY, mTextWeatherPaint);
    }


    public void setAntiAlias(boolean antiAlias) {
        mTextDatePaint.setAntiAlias(antiAlias);
        mTextTimePaint.setAntiAlias(antiAlias);
    }

    public void setColor(int color) {
        mTextDatePaint.setColor(color);
        mTextTimePaint.setColor(color);
    }


    void calculateOffsets(){
        TimeOffsetX=mTextTimePaint.measureText("12:60")/2;
        DateOffSetX=mTextDatePaint.measureText("14/02/2017")/2;
        TimeOffsetY=resources.getDimension(R.dimen.timeoffset_y);
        DateOffsetY=resources.getDimension(R.dimen.dateoffset_y);
        TempOffsetY=resources.getDimension(R.dimen.weatheroffset_y);
    }

    Paint createTimeObject()
    {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        Log.d(TAG,"time size = "+timeSize);
        return paint;
    }

    Paint createDateObject()
    {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        Log.d(TAG,"date size = "+dateSize);
        paint.setTypeface(NORMAL_TYPEFACE);
        return paint;
    }

    Paint createWeatherObject(){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(NORMAL_TYPEFACE);
        return paint;
    }


}

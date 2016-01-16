package com.example.rizvan.myart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by Rizvan on 02.11.2015.
 */
public class ControlEditorView extends View{

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint paint, mBitmapPaint;
    private float canvasSizeHorizontal;
    private float canvasSizeVertical;
    private final int horizontalCountOfCells, verticalCountOfCells;

    private final ScaleGestureDetector scaleGestureDetector;
    private final int viewSizeHorizontal;
    private final int viewSizeVertical;
    private float mScaleFactor;

    private final GestureDetector detector;

    public ControlEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //размер поля
        horizontalCountOfCells = 38;
        verticalCountOfCells = 13;

        //в xml разметке позднее пропишем размер вьюхи равный 300dp
        viewSizeHorizontal = (int)convertDpToPixel(400, context);
        viewSizeVertical = (int)convertDpToPixel(300, context);
        mScaleFactor = 1f;//значение зума по умолчанию
        canvasSizeHorizontal = (int)(viewSizeHorizontal * mScaleFactor);//определяем размер канваса
        canvasSizeVertical = (int)(viewSizeVertical * mScaleFactor);//определяем размер канваса

        mBitmap = Bitmap.createBitmap((int) canvasSizeHorizontal, (int) canvasSizeVertical, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        //определяем параметры кисти, которой будем рисовать сетку и атомы
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xffff0505);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        //рисуем сетку
        for(int x = 0; x < horizontalCountOfCells +1; x++)
            mCanvas.drawLine((float)x* canvasSizeHorizontal / horizontalCountOfCells, 0, (float)x* canvasSizeHorizontal / horizontalCountOfCells, canvasSizeHorizontal, paint);

        for(int y = 0; y < verticalCountOfCells +1; y++)
            mCanvas.drawLine(0, (float)y* canvasSizeHorizontal / verticalCountOfCells, canvasSizeHorizontal, (float)y* canvasSizeHorizontal / verticalCountOfCells, paint);

        scaleGestureDetector=new ScaleGestureDetector(context, new MyScaleGestureListener());

        detector = new GestureDetector(context, new MyGestureListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);//зумируем канвас
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }
    //в случае касания пальем передаем управление MyScaleGestureListener
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    //унаследовались от GestureDetector.SimpleOnGestureListener, чтобы не писать пустую
    //реализацию ненужных методов интерфейса OnGestureListener
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        //обрабатываем скролл (перемещение пальца по экрану)
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            //не даем канвасу показать края по горизонтали
            if(getScrollX()+distanceX< canvasSizeHorizontal - viewSizeHorizontal && getScrollX()+distanceX>0){
                scrollBy((int)distanceX, 0);
            }
            //не даем канвасу показать края по вертикали
            if(getScrollY()+distanceY< canvasSizeVertical - viewSizeVertical && getScrollY()+distanceY>0){
                scrollBy(0, (int)distanceY);
            }
            return true;
        }

        //обрабатываем одиночный тап
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event){
            //получаем координаты ячейки, по которой тапнули
            int cellX=(int)((event.getX()+getScrollX())/mScaleFactor);
            int cellY=(int)((event.getY()+getScrollY())/mScaleFactor);
            return true;
        }
        //обрабатываем двойной тап
        @Override
        public boolean onDoubleTapEvent(MotionEvent event){
            //зумируем канвас к первоначальному виду

            mScaleFactor=1f;
            canvasSizeHorizontal = viewSizeHorizontal;
            canvasSizeVertical = viewSizeVertical;
            scrollTo(0, 0);//скролим, чтобы не было видно краев канваса.
            invalidate();//перерисовываем канвас
            return true;
        }
    }

    //унаследовались от ScaleGestureDetector.SimpleOnScaleGestureListener, чтобы не писать пустую реализацию ненужных
    //методов интерфейса OnScaleGestureListener
    private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        //обрабатываем "щипок" пальцами
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();//получаем значение зума относительно предыдущего состояния
            //получаем координаты фокальной точки - точки между пальцами
            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();

            //следим чтобы канвас не уменьшили меньше исходного размера и не допускаем увеличения больше чем в 3 раза
            if (mScaleFactor * scaleFactor > 1 && mScaleFactor * scaleFactor < 3) {
                mScaleFactor *= scaleGestureDetector.getScaleFactor();
                canvasSizeHorizontal = viewSizeHorizontal * mScaleFactor;
                canvasSizeVertical = viewSizeVertical * mScaleFactor;//изменяем хранимое в памяти значение размера канваса
                //используется при расчетах
                //по умолчанию после зума канвас отскролит в левый верхний угол.
                //Скролим канвас так, чтобы на экране оставалась
                //область канваса, над которой был жест зума
                //Для получения данной формулы достаточно школьных знаний математики (декартовы координаты).
                int scrollX = (int) ((getScrollX() + focusX) * scaleFactor - focusX);
                scrollX = Math.min(Math.max(scrollX, 0), (int) canvasSizeHorizontal - viewSizeHorizontal);
                int scrollY = (int) ((getScrollY() + focusY) * scaleFactor - focusY);
                scrollY = Math.min(Math.max(scrollY, 0), (int) canvasSizeVertical - viewSizeVertical);
                scrollTo(scrollX, scrollY);
            }
            //вызываем перерисовку принудительно
            invalidate();
            return true;
        }
    }

    void drawAtoms(int cellX, int cellY, int color, int count){
        //считаем координаты центра ячейки
        float x0=((1f/(2* horizontalCountOfCells))*viewSizeHorizontal+(1f/ horizontalCountOfCells)*cellX*viewSizeHorizontal);
        float y0=((1f/(2* verticalCountOfCells))*viewSizeVertical+(1f/ verticalCountOfCells)*cellY*viewSizeVertical);
        paint.setColor(color);
        switch (count){
            //todo non-absolute values
            case 1:
                drawAtoms(cellX, cellY, color, 0);//стираем существующие атомы
                mCanvas.drawCircle(x0, y0, 3, paint);//рисуем один атом в центре ячейки
                break;
            case 2:
                drawAtoms(cellX, cellY, color, 0);
                //рисуем пару атомов на удалении от центра ячейки
                mCanvas.drawCircle(x0-7, y0, 3, paint);
                mCanvas.drawCircle(x0+7, y0, 3, paint);
                break;
            case 3:
                drawAtoms(cellX, cellY, color, 0);
                //рисуем три атома в вершинах правильного треугольника с центром в центре ячейки
                mCanvas.drawCircle(x0 - 7, y0 + 4, 3, paint);
                mCanvas.drawCircle(x0 + 7, y0 + 4, 3, paint);
                mCanvas.drawCircle(x0, y0-8, 3, paint);
                break;
            case 4:
                drawAtoms(cellX, cellY, color, 0);
                //рисуем 4 атом в вершинах квадрата с центром в центре ячейки
                mCanvas.drawCircle(x0-7, y0-7, 3, paint);
                mCanvas.drawCircle(x0-7, y0+7, 3, paint);
                mCanvas.drawCircle(x0+7, y0+7, 3, paint);
                mCanvas.drawCircle(x0+7, y0-7, 3, paint);
                break;
            case 0:
                //устанавливаем кисти режим стирания
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                //заполнение обойденного участка
                paint.setStyle(Paint.Style.FILL);
                //рисуем большой круг, на месте которого ничего не останется
                mCanvas.drawCircle(x0, y0, 17, paint);
                //возвращаем исходные параметры
                paint.setStyle(Paint.Style.STROKE);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                break;
        }
        invalidate();//перерисовываем канвас
    }

    //переводим dp в пиксели
    public float convertDpToPixel(float dp,Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi/160f);
    }
}

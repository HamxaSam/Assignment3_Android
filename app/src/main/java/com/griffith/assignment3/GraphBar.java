package com.griffith.assignment3;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public class GraphBar extends View {
    int width;
    int height;
    private List<Float> values;
    private List<ShapeDrawable> columns;
    private List<Label> verticals_labels;
    private Paint p = new Paint();
    private int graph_width;

    public GraphBar(Context context) {
        super(context);
        init(null, 0);
    }

    public GraphBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GraphBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        columns = new LinkedList<>();
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width  = metrics.widthPixels;
        this.height = metrics.heightPixels;
        graph_width = width - ( 10 * width / 100);
        p.setColor(Color.BLACK);
        p.setTextSize(30.0f);
        verticals_labels = new LinkedList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (ShapeDrawable s : this.columns){
            s.draw(canvas);
        }
        canvas.drawLine(width - graph_width, 0, width - graph_width, width, p);
        for (Label l : verticals_labels){
            canvas.drawText(l.getLabel(), 0, l.getCoord(), p);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = widthMeasureSpec;
        int h = heightMeasureSpec;
        if (w > h)
            w = h;
        else
            h = w;
        super.onMeasure(w, h);
    }

    private float[] findMinMaxValues(){
        float min = this.values.get(0);
        float max = this.values.get(0);
        for (int i = 1 ; i < this.values.size(); ++i) {
            if (this.values.get(i) < min)
                min = this.values.get(i);
            if (this.values.get(i) > max)
                max = this.values.get(i);
        }
        return new float[]{min, max};
    }

    private void fillVerticalLabels(){
        int diff = verticals_labels.get(1).getCoord() - verticals_labels.get(0).getCoord();
        float[] minmax = findMinMaxValues();
        float diffv = minmax[1] - minmax[0];
        int step = diff / 8;
        float stepv = diffv / 8;
        for(int i  = 0; i < 7 ; ++i){
            verticals_labels.add(new Label(String.valueOf(((i + 1) * stepv)+ minmax[0]), width - ((i + 1) * step)));
        }
    }

    public void setValues(List<Float> values) {
        this.values = values;

        float[] minmax = findMinMaxValues();
        float diff = minmax[1] - minmax[0];
        float ratio_min = 5 * diff / 100;


        int w = graph_width / this.values.size();
        for (int i = 0 ; i < this.values.size(); ++i) {
            float ratio = (this.values.get(i) - minmax[0] + ratio_min) / diff;
            int bar_heigth = (int)((graph_width) * ratio);
            this.columns.add(new ShapeDrawable(new RectShape()));
            if (i % 2 == 0)
                this.columns.get(i).getPaint().setColor(Color.RED);
            else
                this.columns.get(i).getPaint().setColor(Color.BLUE);
            this.columns.get(i).setBounds((width - graph_width) + w * i, width - bar_heigth , (width - graph_width) + w * (i + 1), width );
            if (this.values.get(i) == minmax[0]){
                //min
                verticals_labels.add(new Label(String.valueOf(this.values.get(i)), width - bar_heigth));
            } else if (this.values.get(i) == minmax[1]){
                //max
                verticals_labels.add(new Label(String.valueOf(this.values.get(i)), width - bar_heigth));
            }

        }
        fillVerticalLabels();
        invalidate();
    }
}

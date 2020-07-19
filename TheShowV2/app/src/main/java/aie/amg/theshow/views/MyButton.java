package aie.amg.theshow.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.appcompat.widget.AppCompatButton;

import aie.amg.theshow.R;

public class MyButton extends AppCompatButton {
    private Context context;

    private int color;
    private float radius;
    private GradientDrawable drawable;

    public MyButton(Context context) {
        this(context, null);
    }

    public MyButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyButton, defStyleAttr, 0);
        color = typedArray.getColor(R.styleable.MyButton_buttonBackgroundColor, Color.WHITE);
        radius = typedArray.getDimension(R.styleable.MyButton_radius, 0.0f);
        typedArray.recycle();

        drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setState(new int[]{android.R.attr.state_pressed,android.R.attr.state_active});
        drawable.setCornerRadius(radius);
        setBackground(drawable);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 10, w, h, radius);
            }
        });
    }
}

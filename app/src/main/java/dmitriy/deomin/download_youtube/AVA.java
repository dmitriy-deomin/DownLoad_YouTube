package dmitriy.deomin.download_youtube;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Admin on 12.09.2016.
 */

public class AVA extends ImageView {
    public AVA(Context context) {
        super(context);
    }

    public AVA(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AVA(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Picasso.with(Main.context).load(Main.url_ava).into(this);
    }
}

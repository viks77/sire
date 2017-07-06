package com.viks.sire2;

/**
 * Created by viks on 17.31.5.
 */
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class TestActivity extends Activity {

    private int finalHeight;
    private int finalWidth;

    @Override
    public void onCreate (Bundle savedInstanceState) {

        super.onCreate (savedInstanceState);

        setContentView (R.layout.test);


        final ImageView iv = (ImageView)findViewById(R.id.clef_imageView);

        ViewTreeObserver vto = iv.getViewTreeObserver();
        vto.addOnPreDrawListener (new ViewTreeObserver.OnPreDrawListener () {
            public boolean onPreDraw() {
                iv.getViewTreeObserver().removeOnPreDrawListener(this);
                finalHeight = iv.getMeasuredHeight();
                finalWidth = iv.getMeasuredWidth();
                return true;
            }
        });    }

}

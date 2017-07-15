package com.angeloparenteapp.upcomingmovies.MyClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.widget.TextView;

import com.angeloparenteapp.upcomingmovies.R;

public class Utils {

    //Custom font for text view
    public static void setTextViewCustomFont(Context context, TextView textView){
        Typeface font = Typeface.createFromAsset(context.getAssets(), "myFont.otf");
        textView.setTypeface(font);
    }

    //Custom font for collapsing toolbar layout
    public static void setLayoutViewCustomFont(Context context, CollapsingToolbarLayout layout){
        Typeface font = Typeface.createFromAsset(context.getAssets(), "myFont.otf");
        layout.setCollapsedTitleTypeface(font);
        layout.setExpandedTitleTypeface(font);
        layout.setExpandedTitleColor(context.getResources().getColor(R.color.white));
        layout.setCollapsedTitleTextColor(context.getResources().getColor(R.color.white));
    }
}

package com.padassist.Graphics;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.padassist.Adapters.MonsterGridAwakeningRecycler;
import com.padassist.Data.Monster;
import com.padassist.R;

import java.util.ArrayList;

/**
 * Created by DragonLotus on 8/23/2016.
 */

public class TooltipAwakening {
    protected WindowManager mWindowManager;
    protected Context mContext;
    protected PopupWindow mWindow;
    private RecyclerView monsterGrid;
    private TextView awakeningDesc;
    private ImageView upArrow, downArrow;
    private RelativeLayout tooltipContent;
    private MonsterGridAwakeningRecycler monsterGridAwakeningRecycler;
    private boolean monsterSpecificAdapter = false;

    protected View mView;

    protected Drawable mBackgroundDrawable = null;
//    protected ShowListener showListener;

    public TooltipAwakening(Context context, String text, int viewResource, int awakening, boolean isLatent, ArrayList<Monster> monsterList, boolean monsterSpecificAdapter) {
        mContext = context;
        mWindow = new PopupWindow(context);

        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setContentView(layoutInflater.inflate(viewResource, null));

        awakeningDesc = (TextView) mView.findViewById(R.id.awakeningDesc);
        upArrow = (ImageView) mView.findViewById(R.id.arrow_up);
        downArrow = (ImageView) mView.findViewById(R.id.arrow_down);
        tooltipContent = (RelativeLayout) mView.findViewById(R.id.tooltipContent);
        monsterGrid = (RecyclerView) mView.findViewById(R.id.monsterGrid);

        if(monsterSpecificAdapter){
            monsterGrid.setVisibility(View.GONE);
        } else {
            monsterGridAwakeningRecycler = new MonsterGridAwakeningRecycler(context, awakening, isLatent, monsterList);
//        monsterGrid.setHasFixedSize(false);
            GridLayoutManager monsterGridLayoutManager = new GridLayoutManager(context, monsterList.size());
            monsterGrid.setLayoutManager(monsterGridLayoutManager);
            monsterGrid.setAdapter(monsterGridAwakeningRecycler);
        }

        awakeningDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
        awakeningDesc.setSelected(true);
    }

//    public TooltipAwakening(Context context) {
//        this(context, "", R.layout.tooltip_awakening);
//
//    }

    public TooltipAwakening(Context context, String text, int awakening, boolean isLatent, ArrayList<Monster> monsterList, boolean monsterSpecificAdapter) {
        this(context, text, R.layout.tooltip_awakening, awakening, isLatent, monsterList, monsterSpecificAdapter);

        setText(text);
    }

    public void show(final View anchor) {
        if (mBackgroundDrawable == null)
            mWindow.setBackgroundDrawable(new BitmapDrawable());
        else
            mWindow.setBackgroundDrawable(mBackgroundDrawable);

        mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);

        mWindow.setContentView(mView);

        int[] location = new int[2];

        anchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0]
                + anchor.getWidth(), location[1] + anchor.getHeight());

        final int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        final int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

//        mView.measure(View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(screenHeight, View.MeasureSpec.AT_MOST));
//        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        int tooltipWidth = mView.getMeasuredWidth();
        if(mView.getMeasuredWidth() > screenWidth){
            mWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        }
        int tooltipHeight = mView.getMeasuredHeight();


        boolean onTop = true;

        int yPos = anchorRect.top - tooltipHeight;
        if (anchorRect.top < screenHeight / 2) {
            yPos = anchorRect.bottom;
            onTop = false;
        }
        int whichArrow, anchorCenterX;

        whichArrow = ((onTop) ? R.id.arrow_down : R.id.arrow_up);
        anchorCenterX = anchorRect.centerX();

        View arrow = whichArrow == R.id.arrow_up ? upArrow
                : downArrow;
        View hideArrow = whichArrow == R.id.arrow_up ? downArrow
                : upArrow;


        final int arrowWidth = arrow.getMeasuredWidth();

        arrow.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) arrow.getLayoutParams();

        hideArrow.setVisibility(View.INVISIBLE);

        int xPos = 0;

        if (anchorRect.left + tooltipWidth > screenWidth) {
            xPos = (screenWidth - tooltipWidth);
        } else if (anchorRect.left - (tooltipWidth / 2) < 0) {
           xPos = anchorRect.left;
        } else {
            xPos = (anchorRect.centerX() - (tooltipWidth / 2));
        }

        if (mView.getMeasuredWidth() > screenWidth) {
            param.leftMargin = ((anchorRect.right - anchorRect.left) / 2) + location[0] - (arrowWidth / 2);
        } else {
            param.leftMargin = (anchorCenterX - xPos) - (arrowWidth / 2);
        }

//        if (onTop) {
//            awakeningDesc.setMaxHeight(anchorRect.top - anchorRect.height());
//        } else {
//            awakeningDesc.setMaxHeight(screenHeight - yPos);
//        }

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);

//        mView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.float_anim));

        mView.post(new Runnable() {
            @Override
            public void run() {
                setUpWindow(anchor, mView.getMeasuredWidth(), mView.getMeasuredHeight());
            }
        });
    }

    public void setUpWindow(View anchor, int width, int height){
        int[] location = new int[2];

        anchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0]
                + anchor.getWidth(), location[1] + anchor.getHeight());


        final int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        final int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

        if(width > screenWidth){
            mWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        }

        boolean onTop = true;

        int yPos = anchorRect.top - height;
         if (anchorRect.top < screenHeight / 2) {
            yPos = anchorRect.bottom;
            onTop = false;
        }
        int whichArrow, anchorCenterX;

        whichArrow = ((onTop) ? R.id.arrow_down : R.id.arrow_up);
        anchorCenterX = anchorRect.centerX();

        View arrow = whichArrow == R.id.arrow_up ? upArrow
                : downArrow;
        View hideArrow = whichArrow == R.id.arrow_up ? downArrow
                : upArrow;


        final int arrowWidth = arrow.getMeasuredWidth();

        arrow.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) arrow.getLayoutParams();

        hideArrow.setVisibility(View.INVISIBLE);

        int xPos = 0;

        if (anchorRect.left + width > screenWidth) {
            xPos = (screenWidth - width);
        } else if (anchorRect.left - (width / 2) < 0) {
            xPos = anchorRect.left;
        } else {
            xPos = (anchorRect.centerX() - (width / 2));
        }

        if (mView.getMeasuredWidth() > screenWidth) {
            param.leftMargin = ((anchorRect.right - anchorRect.left) / 2) + location[0] - (arrowWidth / 2);
        } else {
            param.leftMargin = (anchorCenterX - xPos) - (arrowWidth / 2);
        }

//        if (onTop) {
//            awakeningDesc.setMaxHeight(anchorRect.top - anchorRect.height());
//        } else {
//            awakeningDesc.setMaxHeight(screenHeight - yPos);
//        }
        if(mWindow.isShowing()){
            mWindow.dismiss();
        }

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    public void setContentView(View root) {
        mView = root;

        mWindow.setContentView(root);
    }

    public void setText(String text) {
        awakeningDesc.setText(text);
    }

    public void setContentView(int layoutResID) {
        LayoutInflater inflator = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setContentView(inflator.inflate(layoutResID, null));
    }

}

package com.example.zhaoyouqing.doublerecycleview.mDecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhaoyouqing.doublerecycleview.BuildConfig;
import com.example.zhaoyouqing.doublerecycleview.MainActivity;
import com.example.zhaoyouqing.doublerecycleview.R;
import com.example.zhaoyouqing.doublerecycleview.bean.SortBean;
import com.example.zhaoyouqing.doublerecycleview.interfaces.CheckListener;

import java.util.List;
import java.util.Objects;

/**
 * Created by Zhaoyouqing on 2017/7/15.
 */

public class ItemHeadDecoration extends RecyclerView.ItemDecoration {
    private List<SortBean> mDatas;
    private LayoutInflater mInflater;
    private int mTitleHeight;
    private CheckListener mCheckListener;
    private String currentTag = "0";

    public ItemHeadDecoration(Context context, List<SortBean> datas) {
        super();
        this.mDatas = datas;
        Paint paint = new Paint();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        int titleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 17, context.getResources().getDisplayMetrics());
        paint.setTextSize(titleFontSize);
        paint.setAntiAlias(true);
        mInflater = LayoutInflater.from(context);
    }

    public ItemHeadDecoration setData(List<SortBean> datas){
        mDatas = datas;
        return this;
    }

    public void setmCheckListener(CheckListener mCheckListener) {
        this.mCheckListener = mCheckListener;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int pos = ((LinearLayoutManager)(parent.getLayoutManager())).findFirstVisibleItemPosition();
        String tag = mDatas.get(pos).getTag();
        View child = parent.findViewHolderForLayoutPosition(pos).itemView;
        boolean flag = false;
        if((pos + 1) < mDatas.size()){
            String suspensionTag = mDatas.get(pos + 1).getTag();
            if(tag != null && !tag.equals(suspensionTag)){
                if(child.getHeight() + child.getTop() < mTitleHeight){
                    c.save();
                    flag = true;
                    c.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                }
            }
        }

        View topTitleView = mInflater.inflate(R.layout.item_title, parent, false);
        TextView tv_title = (TextView) topTitleView.findViewById(R.id.tv_title);
        tv_title.setText("测试数据" + tag);


        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) topTitleView.getLayoutParams();
        if(layoutParams == null){
            layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        topTitleView.setLayoutParams(layoutParams);
        Spec spec = setDrawWidthSpec(parent, layoutParams);//用于测量的MeasureSpec
        //依次调用 measure,layout,draw方法，将复杂头部显示在屏幕上。
        topTitleView.measure(spec.toDrawWidthSpec, spec.toDrawHeightSpec);
        topTitleView.layout(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getPaddingLeft() + topTitleView.getMeasuredWidth(), parent.getPaddingTop() + topTitleView.getMeasuredHeight());
        topTitleView.draw(c);//Canvas默认在视图顶部，无需平移，直接绘制

        if(flag){
           c.restore();
        }
        if(MainActivity.isCheck){
            MainActivity.isCheck = false;
            return;
        }

        if(!Objects.equals(tag, currentTag)){
            if(BuildConfig.DEBUG){
                Log.d("tag---->", String.valueOf(MainActivity.finalNumber));
            }
            currentTag = tag;
            mCheckListener.check(Integer.valueOf(currentTag), false);
        }

    }

    /**
     * 设置WidthSpec
     * @param parent
     * @param layoutParams
     * @return
     */
    private Spec setDrawWidthSpec(RecyclerView parent, RecyclerView.LayoutParams layoutParams){
        Spec spec = new Spec();
        //宽度设置
        if(layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT){
            //如果是MATCH_PARENT，则用父控件能分配的最大宽度和EXACTLY构建MeasureSpec。
            spec.toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth()
                    - parent.getPaddingLeft()
                    - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
        }else if(layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT){
            //如果是WRAP_CONTENT，则用父控件能分配的最大宽度和AT_MOST构建MeasureSpec。
            spec.toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth()
                    - parent.getPaddingRight()
                    - parent.getPaddingLeft(), View.MeasureSpec.AT_MOST);
        }else{
            //否则则是具体的宽度数值，则用这个宽度和EXACTLY构建MeasureSpec。
            spec.toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.width, View.MeasureSpec.EXACTLY);
        }
        //高度设置
        if(layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT){
            spec.toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight()
                    - parent.getPaddingTop()
                    - parent.getPaddingBottom(), View.MeasureSpec.EXACTLY);
        }else if(layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT){
            spec.toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight()
                    - parent.getPaddingBottom()
                    - parent.getPaddingTop(), View.MeasureSpec.AT_MOST);
        }else {
            spec.toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(mTitleHeight, View.MeasureSpec.EXACTLY);
        }

        return spec;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    private class Spec{
        int toDrawWidthSpec;
        int toDrawHeightSpec;
    }
}


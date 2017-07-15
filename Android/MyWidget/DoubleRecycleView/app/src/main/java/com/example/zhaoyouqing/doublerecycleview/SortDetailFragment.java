package com.example.zhaoyouqing.doublerecycleview;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhaoyouqing.doublerecycleview.adapter.ClassifyDetailAdapter;
import com.example.zhaoyouqing.doublerecycleview.bean.SortBean;
import com.example.zhaoyouqing.doublerecycleview.interfaces.CheckListener;
import com.example.zhaoyouqing.doublerecycleview.interfaces.Rvlistener;
import com.example.zhaoyouqing.doublerecycleview.mDecoration.ItemHeadDecoration;
import com.example.zhaoyouqing.doublerecycleview.presenter.BasePresenter;
import com.example.zhaoyouqing.doublerecycleview.presenter.SortDetailPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhaoyouqing on 2017/7/14.
 */

public class SortDetailFragment extends BaseFragment<SortDetailPresenter, String> implements CheckListener{
    private RecyclerView mRv;
    private ClassifyDetailAdapter mAdapter;
    private GridLayoutManager mManager;
    private List<SortBean> mDatas = new ArrayList<>();
    private ItemHeadDecoration mDecoration;
    private boolean move = false;
    private int mIndex = 0;
    private CheckListener checkListener;

    @Override
    protected void initCustomView(ViewGroup view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv_sort_detail);
    }

    @Override
    protected SortDetailPresenter initPresenter() {
        showRightPage(1);
        mManager = new GridLayoutManager(mContext, 3);
        mManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mDatas.get(position).isTitle() ? 3: 1;
            }
        });
        mRv.setLayoutManager(mManager);
        mAdapter = new ClassifyDetailAdapter(mContext, mDatas, new Rvlistener() {
            @Override
            public void onItemClick(int id, int position) {
                String content = "";
                switch (id){
                    case R.id.root:
                        content = "title";
                        break;
                    case R.id.content:
                        content = "content";
                        break;
                }
                Snackbar snackbar = Snackbar.make(mRv, "当前点击的是:" + content + ":"
                        + mDatas.get(position).getName(), Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                view.setBackgroundColor(Color.BLUE);
                TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(25);
                snackbar.show();
            }
        });
        mRv.setAdapter(mAdapter);
        mDecoration = new ItemHeadDecoration(mContext, mDatas);
        mRv.addItemDecoration(mDecoration);
        initData(mContext.getResources().getStringArray(R.array.pill));
        mDecoration.setmCheckListener(this);
        return new SortDetailPresenter();
    }

    public void setCheckedListener(CheckListener checkedListener){
        this.checkListener = checkedListener;
    }

    private void initData(final String[] data) {
        for(int i = 0; i < data.length; i++){
            SortBean titleBean = new SortBean(String.valueOf(i));
            titleBean.setTitle(true);
            titleBean.setTag(String.valueOf(i));
            mDatas.add(titleBean);
            for (int j = 0; j < 10; j++) {
                SortBean sortBean = new SortBean(String.valueOf(i + "行" + j + "个"));
                sortBean.setTag(String.valueOf(i));
                mDatas.add(sortBean);
            }
        }
        mAdapter.notifyDataSetChanged();
       // mDecoration.setData(mDatas);

    }

    @Override
    protected void getData() {

    }

    public void setData(int n){
        if(n < 0 || n >= mAdapter.getItemCount()){
            Toast.makeText(mContext, "超出范围了", Toast.LENGTH_SHORT).show();
            return;
        }
        mIndex = n;
        mRv.stopScroll();
        smoothMoveToPosition(n);
    }

    /**
     * 左侧recyclerView选中时，该recyclerView的相应滑动到顶部
     * @param n
     */
    private void smoothMoveToPosition(int n) {
        int firstItem = mManager.findFirstVisibleItemPosition();
        int lastItem = mManager.findLastVisibleItemPosition();
        if(n <= firstItem){
            mRv.smoothScrollToPosition(n);
        }else if(n <= lastItem){
            int top = mRv.getChildAt(n - firstItem).getTop();
            mRv.smoothScrollBy(0, top);
        }else{
            mRv.smoothScrollToPosition(n);
            move = true;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sort_detail;
    }

    @Override
    protected void initListener() {
        mRv.addOnScrollListener(new RecyclerViewListener());
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void refreshView(int code, String data) {

    }

    @Override
    public void check(int position, boolean isCheck) {
        checkListener.check(position, isCheck);
    }

    private class RecyclerViewListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(move && newState == RecyclerView.SCROLL_STATE_IDLE){
                move = false;
                int n = mIndex - mManager.findFirstVisibleItemPosition();
                if(BuildConfig.DEBUG){
                    Log.d("n---->", String.valueOf(n));
                }
                if(0 <= n && n < mRv.getChildCount()){
                    int top = mRv.getChildAt(n).getTop();
                    Log.d("top--->", String.valueOf(top));
                    mRv.smoothScrollBy(0, top);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }
}

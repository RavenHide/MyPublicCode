package com.example.zhaoyouqing.doublerecycleview;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.zhaoyouqing.doublerecycleview.adapter.SortAdapter;
import com.example.zhaoyouqing.doublerecycleview.interfaces.CheckListener;
import com.example.zhaoyouqing.doublerecycleview.interfaces.Rvlistener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CheckListener{
    private RecyclerView rv_sort;
    private Context mContext = this;
    private SortDetailFragment mSortDetailFragment;
    private SortAdapter mSortAdapter;

    public static boolean isCheck;
    public static int finalNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

    }

    private void initData() {
        String[] classify = getResources().getStringArray(R.array.pill);
        List<String> list = Arrays.asList(classify);
        mSortAdapter = new SortAdapter(mContext, list, new Rvlistener() {
            @Override
            public void onItemClick(int id, int position) {
                if(mSortDetailFragment != null){
                    setChecked(position, true);
                }
            }
        });
        rv_sort.setAdapter(mSortAdapter);
        creatFragment();
    }

    /**
     * 创建右侧fragment
     */
    private void creatFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mSortDetailFragment = new SortDetailFragment();
        transaction.add(R.id.lin_fragment, mSortDetailFragment);
        mSortDetailFragment.setCheckedListener(this);
        transaction.commit();
    }

    /**
     * 设置左边选中背景和右侧相应的位置滚动到顶部
     * @param position
     * @param isCheck
     */
    private void setChecked(int position, boolean isCheck){
        finalNumber = position;
        this.isCheck = isCheck;
        if(BuildConfig.DEBUG){
            Log.d("boolean---->", String.valueOf(this.isCheck));
        }
        mSortAdapter.setCheckedPosition(position);
        if(this.isCheck){
            mSortDetailFragment.setData(position * 10 + position);
        }
    }

    private void initView() {
        rv_sort = (RecyclerView) findViewById(R.id.rv_sort);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        rv_sort.setLayoutManager(linearLayoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        rv_sort.addItemDecoration(decoration);

    }

    @Override
    public void check(int position, boolean isCheck) {
        setChecked(position, isCheck);
    }
}

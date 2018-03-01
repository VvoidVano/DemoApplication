package com.example.abc.demoapplication;

import android.content.Context;
import android.os.Build;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewormhole.pulltorefresh.PullToRefreshBase;
import com.ewormhole.pulltorefresh.PullToRefreshScrollView;
import com.example.abc.demoapplication.adapter.NewsAdapter;
import com.example.abc.demoapplication.http.Api;
import com.example.abc.demoapplication.http.RetrofitService;
import com.example.abc.demoapplication.model.BaseBean;
import com.example.abc.demoapplication.model.NewsBean;
import com.example.abc.demoapplication.utils.LogUtils;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    @BindView(R.id.ptrScrollView_product)
    PullToRefreshScrollView pullToRefresh;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    private Context mContext;
    private List<NewsBean> newsBeanList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        getData();
        init();
    }



    private void init() {
        findViewById(R.id.tv_no_data).setOnClickListener(this);

        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getData();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(mContext,getString(R.string.waiting) + i,Toast.LENGTH_LONG).show();
            }
        });
    }


    private void getData() {
        Api api = RetrofitService.getRetrofit().create(Api.class);
        final Call<BaseBean<List<NewsBean>>> newsList = api.getNewsList();
        newsList.enqueue(new Callback<BaseBean<List<NewsBean>>>() {
            @Override
            public void onResponse(Call<BaseBean<List<NewsBean>>> call, Response<BaseBean<List<NewsBean>>> response) {
                pullToRefresh.onRefreshComplete();
                if (response.isSuccessful()) {
                    tvNoData.setVisibility(View.GONE);
                    //update the title
                    getSupportActionBar().setTitle(response.body().getTitle());
                    //init data and show with adapter
                    newsBeanList = response.body().getRows();
                    showData();
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, R.string.request_failed, Toast.LENGTH_LONG).show();
                    LogUtils.logE(TAG, "response code = " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseBean<List<NewsBean>>> call, Throwable t) {
                tvNoData.setVisibility(View.VISIBLE);
                tvNoData.setText(getString(R.string.net_error));
                pullToRefresh.onRefreshComplete();
                Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_LONG).show();
                LogUtils.logE(TAG, t.toString());

            }
        });

    }

    //filter data before show them
    private void showData(){
        List<NewsBean> showNewsList = new ArrayList<>();
        for (NewsBean bean:newsBeanList) {
            if(!TextUtils.isEmpty(bean.getTitle())){
                showNewsList.add(bean);
            }
        }
        listView.setAdapter(new NewsAdapter(mContext.getApplicationContext(), showNewsList));

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_no_data:
                //when internet down, click image to request again
//                if(tvNoData.getText().toString().equals(getString(R.string.net_error)))
                getData();
                break;
        }
    }
}

package com.example.abc.demoapplication.http;

import com.example.abc.demoapplication.model.BaseBean;
import com.example.abc.demoapplication.model.NewsBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by yangyj on 2018/2/12.
 */
public interface Api {

    @GET("s/2iodh4vg0eortkl/facts.json")
    Call<BaseBean<List<NewsBean>>> getNewsList();

}

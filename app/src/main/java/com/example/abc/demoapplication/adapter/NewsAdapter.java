package com.example.abc.demoapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.abc.demoapplication.R;
import com.example.abc.demoapplication.model.NewsBean;


import java.util.List;

/**
 * Created by yangyj on 2018/2/28
 */

public class NewsAdapter extends BaseAdapter {
    private Context mContext;
    private List<NewsBean> mData;

    public NewsAdapter(Context context, List<NewsBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        NewsViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_news, null);
            viewHolder = new NewsViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvDescrition = (TextView) convertView.findViewById(R.id.tv_desc);
            viewHolder.ivPic = (ImageView) convertView.findViewById(R.id.iv_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NewsViewHolder) convertView.getTag();
        }

        NewsBean newsBean = mData.get(position);
        viewHolder.tvTitle.setText(newsBean.getTitle());
        viewHolder.tvDescrition.setText(newsBean.getDescription());

        Glide.with(mContext).load(newsBean.getImageHref()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(0).error(0).into(viewHolder.ivPic);

        viewHolder.ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"item " + position + " clicked",Toast.LENGTH_LONG).show();
            }
        });
        return convertView;
    }

    class NewsViewHolder {
        TextView tvTitle, tvDescrition;
        ImageView ivPic;
    }
}

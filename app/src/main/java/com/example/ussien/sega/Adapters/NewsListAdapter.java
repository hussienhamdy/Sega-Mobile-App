package com.example.ussien.sega.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ussien.sega.Model.News;
import com.example.ussien.sega.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Stack;

import retrofit2.http.Url;

/**
 * Created by ussien on 20/09/2017.
 */

public class NewsListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<News> news = new ArrayList<>();
    @Override
    public int getCount() {
        return news.size();
    }

    @Override
    public Object getItem(int i) {
        return news.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public NewsListAdapter(Context context, ArrayList<News> news) {
        this.context = context;
        this.news = news;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = new ViewHolder();
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.news_listview_item,viewGroup,false);
            vh.Img  = view.findViewById(R.id.news_item_img);
            vh.newsText = view.findViewById(R.id.news_text);
            view.setTag(vh);
        }
        else
        {
            vh = (ViewHolder)view.getTag();
        }

        Uri imageUri = Uri.parse(news.get(i).getImage());

        vh.Img.setImageURI(imageUri);

        String [] strings = news.get(i).getContent().split(" ");
        String temp = "";
        for( int j = 0 ; j < strings.length ; j ++ )
        {
            if(strings[j].startsWith("https://")||strings[j].startsWith("http://"))
            {
                try {
                    URL url = new URL(strings[j]);
                  temp+=url;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            else
                temp+=strings[j];
            temp+=" ";
        }
        vh.newsText.setText(temp);
        Linkify.addLinks(vh.newsText, Linkify.WEB_URLS);
        vh.newsText.setLinkTextColor(ContextCompat.getColor(context ,
                R.color.link));
        vh.newsText.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    public class ViewHolder{
        SimpleDraweeView Img;
        TextView newsText;
    }

    public void addNews(News obj)
    {
        news.add(0,obj);
        notifyDataSetChanged();
    }
}

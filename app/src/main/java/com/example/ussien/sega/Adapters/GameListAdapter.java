package com.example.ussien.sega.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ussien.sega.Model.Game;
import com.example.ussien.sega.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import static com.example.ussien.sega.R.id.parent;

/**
 * Created by ussien on 19/09/2017.
 */

public class GameListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Game> games = new ArrayList<>();

    public GameListAdapter(Context context, ArrayList<Game> games) {
        this.context = context;
        this.games = games;
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int i) {
        return games.get(i);
    }

    @Override
    public long getItemId(int i) {
        return games.get(i).getGameID();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = new ViewHolder();
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.games_gridview_item,viewGroup,false);
            vh.Img  = view.findViewById(R.id.product_img);
            vh.gameName = view.findViewById(R.id.game_name);
            vh.gameDescription = view.findViewById(R.id.game_desc);
            view.setTag(vh);
        }
        else
        {
            vh = (ViewHolder)view.getTag();
        }

        Uri imageUri = Uri.parse(games.get(i).getGamePhoto());
        vh.Img.setImageURI(imageUri);
        vh.gameName.setText(games.get(i).getGameName());
        vh.gameDescription.setText(games.get(i).getGameDescription());
        return view;
    }


    public class ViewHolder{
        SimpleDraweeView Img;
        TextView gameName;
        TextView gameDescription;
    }

}

package com.angeloparenteapp.upcomingmovies.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angeloparenteapp.upcomingmovies.MyClasses.Cast;
import com.angeloparenteapp.upcomingmovies.MyClasses.Utils;
import com.angeloparenteapp.upcomingmovies.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private ArrayList<Cast> listOfElements;
    private Context mContext;

    private String url = "https://image.tmdb.org/t/p/w500";

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterImage;
        public TextView posterTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.image_element);
            posterTitle = itemView.findViewById(R.id.title_element);

            Utils.setTextViewCustomFont(mContext, posterTitle);
        }
    }

    public CastAdapter(Context context, ArrayList<Cast> listOfElements) {
        this.mContext = context;
        this.listOfElements = listOfElements;
    }

    @Override
    public CastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_element, parent, false);
        return new CastAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CastAdapter.ViewHolder holder, int position) {
        final Cast currentElement = listOfElements.get(position);

        Glide.with(mContext).load(url + currentElement.getActorPhoto()).into(holder.posterImage);
        holder.posterTitle.setText(currentElement.getActorName() + " AS " +currentElement.getCharacter());
    }

    @Override
    public int getItemCount() {
        return listOfElements.size();
    }
}

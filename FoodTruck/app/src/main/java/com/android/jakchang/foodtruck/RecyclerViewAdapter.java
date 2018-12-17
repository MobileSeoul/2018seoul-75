package com.android.jakchang.foodtruck;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<ListItem> mListItem;
    int position;


    public RecyclerViewAdapter(Context mContext, List<ListItem> mData){
        this.mContext = mContext;
        this.mListItem = mData;
    }

    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_frame, parent,false);
        final RecyclerViewAdapter.MyViewHolder vHolder = new RecyclerViewAdapter.MyViewHolder(v);


        vHolder.listImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = vHolder.getAdapterPosition();
              //  Toast.makeText(getContext(), "list : " + mList.get(position).getDistance(), Toast.LENGTH_LONG).show();
            }
        });

        vHolder.roadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = vHolder.getAdapterPosition();
               // Toast.makeText(getContext(), "road : " + mList.get(position).getDistance(), Toast.LENGTH_LONG).show();
            }
        });



        return vHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder holder, int position) {
        ListItem listItem = mListItem.get(position);

        holder.nameTV.setText(mListItem.get(position).getName());
        holder.kindTV.setText(mListItem.get(position).getKind());
        holder.distanceTV.setText(mListItem.get(position).getDistance());
        holder.listImage.setImageBitmap(mListItem.get(position).getImage());
        //Glide.with(mContext).load(listItem.getImage()).into(holder.listImage);

    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    public int getPosition(){
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView listImage;
        private ImageView roadImage;
        private TextView nameTV;
        private TextView kindTV;
        private TextView distanceTV;



        public MyViewHolder(View view) {
            super(view);
            listImage = (ImageView)view.findViewById(R.id.listImage);
            roadImage = (ImageView)view.findViewById(R.id.roadImage);
            nameTV = (TextView)view.findViewById(R.id.nameTV);
            kindTV = (TextView)view.findViewById(R.id.kindTV);
            distanceTV = (TextView)view.findViewById(R.id.distanceTV);

        }


    }//class MyViewHolder


}//class RecyclerViewAdapte



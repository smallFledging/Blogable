package com.example.blogable;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

   private Context mContext;
   private List<Post> mData;

    protected PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post, viewGroup,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Object timeObj = mData.get(i).getTimeStamp();
        Long now = System.currentTimeMillis();
        String timeStr = timeObj.toString();
        Long timeLong = Long.parseLong(timeStr);

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(timeLong, now, 0L, DateUtils.FORMAT_ABBREV_RELATIVE);

        myViewHolder.desc.setText(mData.get(i).getMessage());
        myViewHolder.username.setText(mData.get(i).getUsername());
        myViewHolder.timeText.setText(timeAgo);
        ////glide is used for imgs
        //Glide.with(mContext).load(mData.get(i).getMessage())


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    protected class MyViewHolder extends RecyclerView.ViewHolder {

        TextView desc;
        TextView username;
        TextView timeText;

        protected MyViewHolder(@NonNull View itemView) {
            super(itemView);

            desc = itemView.findViewById(R.id.desc);
            username = itemView.findViewById(R.id.username);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }

}

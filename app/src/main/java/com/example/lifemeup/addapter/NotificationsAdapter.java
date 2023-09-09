package com.example.lifemeup.addapter;

import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lifemeup.R;
import com.example.lifemeup.model.NotificationModel;

import java.util.Date;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationHolder> {
    List<NotificationModel> notificationModelList;
    Context context;

    public NotificationsAdapter(List<NotificationModel> notificationModelList, Context context) {
        this.notificationModelList = notificationModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view, parent, false);

        return new NotificationHolder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        // Load notification and time values
        holder.notification.setText(notificationModelList.get(position).getNotification());
        holder.time.setText(calculateTime(notificationModelList.get(position).getTime()));
        // Load and display the image the notification is about
        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(notificationModelList.get(position).getImage())
                .placeholder(R.drawable.user_60)
                .timeout(6500)
                .into(holder.image);

        // If the image is null set invisible the image
        if(notificationModelList.get(position).getImage() != null){
            holder.image.setVisibility(View.VISIBLE);
        }
        else{
            holder.image.setVisibility(View.GONE);
        }
    }

    // Calculate the time has past from the moment of the event
    @RequiresApi(api = Build.VERSION_CODES.O)
    String calculateTime(Date date){

        long dateInMillis = date.toInstant().toEpochMilli();
        return DateUtils.getRelativeTimeSpanString(dateInMillis, System.currentTimeMillis(), 60000, DateUtils.FORMAT_ABBREV_TIME).toString();

    }
    // Return the amount of notifications
    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }
    // View holder for the notifications
    static class NotificationHolder  extends RecyclerView.ViewHolder{
        TextView notification, time;
        ImageView image;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);

        }

    }
}

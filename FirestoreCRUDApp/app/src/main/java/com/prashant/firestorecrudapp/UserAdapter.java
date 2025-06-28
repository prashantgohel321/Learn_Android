package com.prashant.firestorecrudapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnItemClickListener listener;

    // Interface for click listener
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    // Method to update the data in the adapter
    public void setUsers(List<User> newUsers) {
        this.userList = newUsers;
        notifyDataSetChanged(); // Notifies the RecyclerView that the data set has changed
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userNameTextView.setText("Name: " + user.getName());
        holder.userEmailTextView.setText("Email: " + user.getEmail());
        holder.userIdTextView.setText("ID: " + user.getId());

        // Set click listener for each item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ViewHolder class
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView userEmailTextView;
        TextView userIdTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userEmailTextView = itemView.findViewById(R.id.userEmailTextView);
            userIdTextView = itemView.findViewById(R.id.userIdTextView);
        }
    }
}


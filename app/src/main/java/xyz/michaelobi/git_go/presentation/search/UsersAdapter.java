package xyz.michaelobi.git_go.presentation.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.michaelobi.git_go.R;
import xyz.michaelobi.git_go.data.remote.model.User;

/**
 * Created by Michael Obi on 10/14/16.
 */

class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private final Context context;
    private List<User> items;

    UsersAdapter(List<User> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User item = items.get(position);

        holder.textViewBio.setText(item.getBio());
        if (item.getName() != null) {
            holder.textViewName.setText(item.getLogin() + " - " + item.getName());
        } else {
            holder.textViewName.setText(item.getLogin());
        }
        Picasso.with(context).load(item.getAvatarUrl()).into(holder.imageViewAvatar);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    void setItems(List<User> githubUserList) {
        this.items = githubUserList;
        notifyDataSetChanged();
    }
}


class UserViewHolder extends RecyclerView.ViewHolder {
    final TextView textViewBio;
    final TextView textViewName;
    final ImageView imageViewAvatar;

    UserViewHolder(View v) {
        super(v);
        imageViewAvatar = (ImageView) v.findViewById(R.id.imageview_userprofilepic);
        textViewName = (TextView) v.findViewById(R.id.textview_username);
        textViewBio = (TextView) v.findViewById(R.id.textview_user_profile_info);
    }
}
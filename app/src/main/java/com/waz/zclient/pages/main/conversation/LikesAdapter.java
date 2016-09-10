/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.pages.main.conversation;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.waz.api.User;
import com.waz.zclient.R;
import com.waz.zclient.pages.main.pickuser.views.viewholders.UserViewHolder;

public class LikesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private User[] likes;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.startui_user, parent, false);
        return new UserViewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User liker = likes[position];
        ((UserViewHolder) holder).bind(liker, false);
    }

    @Override
    public int getItemCount() {
        if (likes == null) {
            return 0;
        }
        return likes.length;
    }

    public void setLikes(User[] likes) {
        this.likes = likes;
        notifyDataSetChanged();
    }
}

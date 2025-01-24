package com.wid.rvtracklibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TitleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<String> titles = new ArrayList<>();

    public void setTitles(final List<String> titles) {
        this.titles.addAll(titles);
        notifyItemRangeInserted(this.titles.isEmpty() ? 0 : this.titles.size() - 1, titles.size());
    }

    public void removeTitles(final int count) {

        if(count >= titles.size() - 1) return;

        for(int i = 0; i < count; i++) {
            titles.remove(i);
        }
        notifyItemRangeRemoved(0, count);
    }

    public TitleAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TitleHolder(LayoutInflater.from(context).inflate(R.layout.adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TitleHolder titleHolder = (TitleHolder) holder;
        final String title = titles.get(position);
        titleHolder.tvTitle.setText(title);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    private class TitleHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}

package com.lxtx.mydraglayout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lxtx.mydraglayout.R;
import com.lxtx.mydraglayout.model.Model;
import com.lxtx.mydraglayout.adapter.MyAdapter.MyHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author codejoyfun
 * @since 2020/3/11
 */
public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    private ArrayList<Model> data = new ArrayList<Model>();

    public MyAdapter(ArrayList<Model> data) {
        this.data.addAll(data);
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.tvTitle.setText(data.get(position).title);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.tvTitle.setText("增量更新");
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}

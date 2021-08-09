package com.example.dsi_android_ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dsi_android_ui.R;
import com.example.dsi_android_ui.models.ProductInTask;

import java.util.List;

public abstract class TaskProductDetailsAdapter extends RecyclerView.Adapter<TaskProductDetailsAdapter.ViewHolder>
        implements RecyclerviewOnClickListener {

    List<ProductInTask> listitems = null;
    private Context context = null;

    // RecyclerView recyclerView;
    public TaskProductDetailsAdapter(Context context, List<ProductInTask> listdata) {
        this.listitems = listdata;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_category_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProductInTask item = listitems.get(position);
        holder.title.setText(item.getProductName());
        holder.countText.setText("" + item.getTotal());
        holder.skuText.setText("Sku: " + item.getSku());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerviewClick(item);

            }
        });
    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView countText;
        public TextView skuText;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.dep_name);
            this.skuText = (TextView) itemView.findViewById(R.id.item_sku);
            this.countText = (TextView) itemView.findViewById(R.id.dep_count);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.content_layout);
        }
    }
}
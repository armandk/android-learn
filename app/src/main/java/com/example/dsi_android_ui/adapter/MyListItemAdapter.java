package com.example.dsi_android_ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dsi_android_ui.R;
import com.example.dsi_android_ui.search_department.ProductOverview;
import java.util.List;

public abstract class MyListItemAdapter extends RecyclerView.Adapter<MyListItemAdapter.ViewHolder> implements RecyclerviewOnClickListener{

    List<ProductOverview> listitems = null;
    private Context context=null;
    public MyListItemAdapter(Context context, List<ProductOverview> listdata) {
        this.listitems =listdata;
        this.context=context;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_category_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProductOverview item = listitems.get(position);
        holder.title.setText(item.getProductName());
        holder.countText.setText(""+item.getCount());
        holder.gtinTextValue.setText(context.getString(R.string.product_by_location_Gtin,item.getGtin()));
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

    public void setData(List<ProductOverview> productList) {
        this.listitems.clear();
        this.listitems.addAll(productList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView countText;
        public TextView gtinTextValue;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.dep_name);
            this.gtinTextValue = (TextView) itemView.findViewById(R.id.item_sku);
            this.countText = (TextView) itemView.findViewById(R.id.dep_count);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.content_layout);
        }
    }
}
package com.example.dsi_android_ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dsi_android_ui.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> implements RecyclerviewOnClickListener{

    List<ItemObject> listitems = new ArrayList<ItemObject>();
    private Context context=null;
    // RecyclerView recyclerView;
    public MyListAdapter(Context context, HashMap<String, Integer> listdata) {
        List<String> keys = new ArrayList<>(listdata.keySet());
        this.context=context;
        Collections.sort(keys);
        if(keys.remove("All Products")){
            keys.add(0,"All Products");
        }
        for (String key : keys) {
            //listPickerFormCell.addView(getTextView(key,result.get(key)));
            ItemObject itemObject=new ItemObject();
            itemObject.setItemname(key);
            itemObject.setItemCount(listdata.get(key));
            listitems.add(itemObject );
        }

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ItemObject item = listitems.get(position);
        holder.title.setText(item.getItemname());
        holder.countText.setText(""+item.getItemCount());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(),"click on item: "+item.getItemname(),Toast.LENGTH_LONG).show();

                recyclerviewClick(item);
                /*FragmentTransaction transaction = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
                //TODO item serche searchView.getQuery().toString().trim())
                transaction.replace(R.id.container, DepartmentCategoryFragment.newInstance(holder.title.getText().toString(), ""));
                transaction.addToBackStack(null);
                transaction.commit();*/
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
        public LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.dep_name);
            this.countText = (TextView) itemView.findViewById(R.id.dep_count);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.content_layout);
        }
    }
    public class  ItemObject
    {
        public String getItemname() {
            return itemname;
        }

        public void setItemname(String itemname) {
            this.itemname = itemname;
        }

        public int getItemCount() {
            return itemCount;
        }

        public void setItemCount(int itemCount) {
            this.itemCount = itemCount;
        }

        private String itemname="";
        private int itemCount=0;

    }
}
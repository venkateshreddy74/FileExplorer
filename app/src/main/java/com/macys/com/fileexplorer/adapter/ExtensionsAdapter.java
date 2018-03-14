package com.macys.com.fileexplorer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macys.com.fileexplorer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class ExtensionsAdapter extends RecyclerView.Adapter<ExtensionsViewHolder> {
    private HashMap<String, Integer> extensionMap;
    private List<String> extensionNames;
    private List<Integer> extensionCount;
    private Context context;

    public ExtensionsAdapter(Context context, HashMap<String, Integer> extensionMap) {

        this.extensionMap = extensionMap;
        this.context = context;
        extensionCount = new ArrayList<>();
        extensionNames = new ArrayList<>();
        setUpListofExtensionNamesAndCounts();
    }


    private void setUpListofExtensionNamesAndCounts() {
        Set set = extensionMap.entrySet();
        Iterator iterator2 = set.iterator();
        while (iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator2.next();
            extensionNames.add((String) me2.getKey());
            extensionCount.add((Integer) me2.getValue());
        }
    }

    @Override
    public ExtensionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater extensioninflater = LayoutInflater.from(parent.getContext());
        View extensionview = extensioninflater.inflate(R.layout.extensions_list_item_layout, parent, false);
        return new ExtensionsViewHolder(context, extensionview);
    }

    @Override
    public void onBindViewHolder(ExtensionsViewHolder holder, int position) {

        String extensionname = extensionNames.get(position);
        Integer extcount = extensionCount.get(position);
        holder.extensionNameView.setText(extensionname + " COUNT:  " + extcount);

    }

    @Override
    public int getItemCount() {
        return extensionMap.size();
    }
}

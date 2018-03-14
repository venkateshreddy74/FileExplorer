package com.macys.com.fileexplorer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.macys.com.fileexplorer.R;


public class ExtensionsViewHolder extends RecyclerView.ViewHolder {

    TextView extensionNameView;
    Context context;

    public ExtensionsViewHolder(Context context, View itemView) {
        super(itemView);
        extensionNameView = itemView.findViewById(R.id.extension_name_text_view);
        this.context = context;
    }
}

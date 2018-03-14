package com.macys.com.fileexplorer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.macys.com.fileexplorer.R;


public class FilesViewHolder extends RecyclerView.ViewHolder {

    TextView fileNameView;
    Context context;

    public FilesViewHolder(Context context, View itemView) {
        super(itemView);
        fileNameView = itemView.findViewById(R.id.file_name_text_view);
        this.context = context;
    }
}

package com.macys.com.fileexplorer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macys.com.fileexplorer.R;

import java.io.File;
import java.util.List;


public final class FilesAdapter extends RecyclerView.Adapter<FilesViewHolder> {
    List<File> fileList;
    Context context;

    public FilesAdapter(Context context, List<File> fileList) {
        this.fileList = fileList;
        this.context = context;
    }

    @Override
    public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater filesinflater = LayoutInflater.from(parent.getContext());
        View fileview = filesinflater.inflate(R.layout.files_list_item_layout, parent, false);
        return new FilesViewHolder(context, fileview);
    }

    @Override
    public void onBindViewHolder(FilesViewHolder holder, int position) {
        String filename = fileList.get(position).getName();
        long size = fileList.get(position).length();
        holder.fileNameView.setText(filename + "   " + size + " bytes");
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}


package com.macys.com.fileexplorer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.macys.com.fileexplorer.adapter.ExtensionsAdapter;
import com.macys.com.fileexplorer.adapter.FilesAdapter;
import com.macys.com.fileexplorer.utils.FileExplorerConstants;

public class FilesActivity extends AppCompatActivity {

    private TextView mFilesHeaderText;
    private TextView mAverageFileSizeText;
    private TextView mExtensionHeadeText;

    private RecyclerView mFilesRecyclerView;
    private RecyclerView mExtensionsRecyclerView;

    private FileScanResult mFileScanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(myToolbar);
        mFileScanResult = (FileScanResult) getIntent().getSerializableExtra(FileExplorerConstants.FILE_SCAN_RESULT);
        bindViews();
    }

    private void bindViews() {
        mFilesHeaderText = findViewById(R.id.files_header_text);
        mAverageFileSizeText = findViewById(R.id.average_file_size);
        mExtensionHeadeText = findViewById(R.id.extensions_header_text);
        mFilesRecyclerView = findViewById(R.id.files_recyclerView);
        mExtensionsRecyclerView = findViewById(R.id.extensions_recyclerView);

        mAverageFileSizeText.setText(getResources().getString(R.string.average_file_size) + "  " + mFileScanResult.getAverageFileSize() + " " + getResources().getString(R.string.bytes));

        mFilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFilesRecyclerView.setAdapter(new FilesAdapter(this, mFileScanResult.getLargeFiles()));

        mExtensionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mExtensionsRecyclerView.setAdapter(new ExtensionsAdapter(this, mFileScanResult.getFrequentFileExtensions()));

        //Add decorator for the divider line for the recycler view
        mFilesRecyclerView.addItemDecoration(new
                DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mExtensionsRecyclerView.addItemDecoration(new
                DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


    }


}

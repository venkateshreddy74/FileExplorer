package com.macys.com.fileexplorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.macys.com.fileexplorer.service.ScanFilesLoader;
import com.macys.com.fileexplorer.utils.FileExplorerConstants;


public class MainActivity extends AppCompatActivity implements ShareActionProvider.OnShareTargetSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mStartButton;
    private Button mStopButton;
    private Button mViewResults;
    private ProgressBar mProgressbar;
    private TextView mScanText;
    private FileScanResult mFileScanResult;
    private ShareActionProvider mShareActionProvider;
    private MenuItem shareItem;


    private LoaderManager.LoaderCallbacks<FileScanResult>
            mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<FileScanResult>() {
                @Override
                public Loader<FileScanResult> onCreateLoader(int id, Bundle args) {
                    return new ScanFilesLoader(MainActivity.this);
                }

                @Override
                public void onLoadFinished(Loader<FileScanResult> loader, FileScanResult data) {
                    if (data.isScanComplete()) {
                        //dismiss progress dialog
                        if (data.getErrorMessage() != null) {
                            mProgressbar.setVisibility(View.INVISIBLE);
                            mScanText.setVisibility(View.INVISIBLE);
                            mFileScanResult = data;
                            Toast.makeText(getBaseContext(), data.errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            mFileScanResult = data;
                            mProgressbar.setVisibility(View.INVISIBLE);
                            mScanText.setVisibility(View.INVISIBLE);
                            mViewResults.setEnabled(true);
                            shareItem.setVisible(true);
                            setSharedIntent();
                        }
                    } else {
                        //show progress dialog
                        mProgressbar.setVisibility(View.VISIBLE);
                        mScanText.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onLoaderReset(Loader<FileScanResult> loader) {

                }
            };

    private void setSharedIntent() {
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("text/plain");
        if (mFileScanResult != null) {
            myShareIntent.putExtra(Intent.EXTRA_TEXT, mFileScanResult.getAverageFileSize());
        }
        mShareActionProvider.setShareIntent(myShareIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(myToolbar);
        isStoragePermissionGranted();
        bindViews();
        setupListeners();
        if (savedInstanceState != null && savedInstanceState.getBoolean(FileExplorerConstants.IS_LOADING, false)) {
            startOrAttachLoader();
        }

    }


    private void startOrAttachLoader() {
        setUpLoader();
        mProgressbar.setVisibility(View.VISIBLE);
        mScanText.setVisibility(View.VISIBLE);
        mStopButton.setEnabled(true);
        mStartButton.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        shareItem = menu.findItem(R.id.action_share);
        shareItem.setVisible(false);
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        return true;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    private void bindViews() {
        mStartButton = findViewById(R.id.start_scan_button);
        mStopButton = findViewById(R.id.stop_scan_button);
        mProgressbar = findViewById(R.id.progressbar);
        mViewResults = findViewById(R.id.view_results);
        mScanText = findViewById(R.id.scanning_text);
    }

    private void setupListeners() {
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrAttachLoader();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLoader();
                mProgressbar.setVisibility(View.INVISIBLE);
                mScanText.setVisibility(View.INVISIBLE);
                mStopButton.setEnabled(false);
                mStartButton.setEnabled(true);
            }
        });


        mViewResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileScanResult != null && mFileScanResult.isScanComplete()) {
                    Intent intent = new Intent(MainActivity.this, FilesActivity.class);
                    intent.putExtra(FileExplorerConstants.FILE_SCAN_RESULT, mFileScanResult);
                    getBaseContext().startActivity(intent);
                }
            }
        });

    }

    private void setUpLoader() {
        Bundle bundle = new Bundle();
        getSupportLoaderManager().initLoader(FileExplorerConstants.LOADER_ID, bundle, mLoaderCallbacks);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isLoadingInProgress()) {
            outState.putBoolean(FileExplorerConstants.IS_LOADING, true);
        }


    }

    /**
     * Ensures that Activity is performing background operation
     *
     * @return
     */
    private boolean isLoadingInProgress() {
        return mProgressbar.getVisibility() == View.VISIBLE && mStopButton.isEnabled() && !mStartButton.isEnabled();
    }

    private void stopLoader() {
        getSupportLoaderManager().destroyLoader(FileExplorerConstants.LOADER_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopLoader();
    }

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        return false;
    }
}

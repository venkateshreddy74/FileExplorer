package com.macys.com.fileexplorer.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.AsyncTaskLoader;

import com.macys.com.fileexplorer.FileScanResult;
import com.macys.com.fileexplorer.MainActivity;
import com.macys.com.fileexplorer.R;
import com.macys.com.fileexplorer.utils.FileExplorerConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;


public class ScanFilesLoader extends AsyncTaskLoader<FileScanResult> {
    FileScanResult mFileScanResult;
    NotificationManagerCompat mnotificationManagerCompat;

    public ScanFilesLoader(Context context) {
        super(context);
    }

    private static HashMap sortHashMapByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mFileScanResult != null) {
            deliverResult(mFileScanResult);
        } else {
            forceLoad();
        }
    }

    @Override
    public FileScanResult loadInBackground() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {

                //setup notification
                setUpNotification();
                List<File> files = new ArrayList<File>();
                files.addAll(FileUtils.listFiles(Environment.getExternalStorageDirectory(), null, true));
                mFileScanResult = new FileScanResult();
                mFileScanResult.setLargeFiles(getTopTenLargeFiles(files));
                mFileScanResult.setAverageFileSize(getAverageFileSize(files));
                mFileScanResult.setFrequentFileExtensions(getTopFiveFileExtensionsWithFrequencies(files));
                mFileScanResult.setScanComplete(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //TODO no SD card throw an Error ?
        }

        return mFileScanResult;
    }

    private void setUpNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = getContext().getResources().getString(R.string.channel_name);
            String description = getContext().getResources().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(getContext().getResources().getString(R.string.channel_id), name, importance);
            mChannel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), getContext().getResources().getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getContext().getResources().getString(R.string.notification_title))
                .setContentText(getContext().getResources().getString(R.string.notification_content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setChannelId(getContext().getResources().getString(R.string.channel_id))
                .setAutoCancel(false);


        mnotificationManagerCompat = NotificationManagerCompat.from(getContext());
        mnotificationManagerCompat.notify(FileExplorerConstants.SCAN_PROGRESS_NOTIFICATION_ID, mBuilder.build());
    }

    private List<File> getTopTenLargeFiles(List<File> fileList) {
        List<File> largeFiles = new ArrayList<>();
        //sort the files in descending order of size
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return (int) (o2.length() - o1.length());
            }
        });
        //Add the last 10 files from the
        for (int i = 0; i < 10; i++) {
            largeFiles.add(fileList.get(i));
        }

        return largeFiles;
    }

    private String getAverageFileSize(List<File> fileList) {
        long sumofallfilesizes = 0;

        for (int i = 0; i < fileList.size(); i++) {
            sumofallfilesizes = sumofallfilesizes + fileList.get(i).length();
        }

        long averagefilesize = (sumofallfilesizes / fileList.size());
        return String.valueOf(averagefilesize);
    }

    private HashMap<String, Integer> getTopFiveFileExtensionsWithFrequencies(List<File> fileList) {
        HashMap<String, Integer> extensionswithfreq = new HashMap<>();
        for (int i = 0; i < fileList.size(); i++) {
            String extension = FilenameUtils.getExtension(fileList.get(i).getName());

            if (extensionswithfreq.containsKey(extension) && extensionswithfreq.get(extension) != null) {
                Integer count = extensionswithfreq.get(extension);
                extensionswithfreq.put(extension, count + 1);
            } else {
                extensionswithfreq.put(extension, 1);
            }
        }
        HashMap<String, Integer> sortedfileextensionswithfreq = sortHashMapByValues(extensionswithfreq);
        return sortedfileextensionswithfreq;
    }

    @Override
    public void deliverResult(FileScanResult data) {
        if (mnotificationManagerCompat != null) {
            mnotificationManagerCompat.cancel(FileExplorerConstants.SCAN_PROGRESS_NOTIFICATION_ID);
        }
        super.deliverResult(data);
    }


}

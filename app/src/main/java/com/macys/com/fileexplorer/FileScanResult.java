package com.macys.com.fileexplorer;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


public class FileScanResult implements Serializable {
    List<File> largeFiles;  // list of large files on SD card
    String averageFileSize;
    HashMap<String, Integer> frequentFileExtensions; //map of file extensions to frequencies
    boolean scanComplete = false;

    public boolean isScanComplete() {
        return scanComplete;
    }

    public void setScanComplete(boolean scanComplete) {
        this.scanComplete = scanComplete;
    }

    public List<File> getLargeFiles() {
        return largeFiles;
    }

    public void setLargeFiles(List<File> largeFiles) {
        this.largeFiles = largeFiles;
    }

    public String getAverageFileSize() {
        return averageFileSize;
    }

    public void setAverageFileSize(String averageFileSize) {
        this.averageFileSize = averageFileSize;
    }

    public HashMap<String, Integer> getFrequentFileExtensions() {
        return frequentFileExtensions;
    }

    public void setFrequentFileExtensions(HashMap<String, Integer> frequentFileExtensions) {
        this.frequentFileExtensions = frequentFileExtensions;
    }
}

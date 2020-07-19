package aie.amg.theshow.activity.utils;

import aie.amg.theshow.models.DownloadFile;

public interface OnDownloadFileClick {
    void onDownloadFileClick(DownloadFile file, int position);
    void onRemoveButtonClick(DownloadFile file);
}

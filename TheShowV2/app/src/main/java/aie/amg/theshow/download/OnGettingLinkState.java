package aie.amg.theshow.download;

public interface OnGettingLinkState {

    void onLinkFinish(String link);

    void onFailedGettingLink(int message);
}

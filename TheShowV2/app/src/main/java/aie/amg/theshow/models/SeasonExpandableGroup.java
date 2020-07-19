package aie.amg.theshow.models;

import aie.amg.theshow.views.expandable.ExpandableGroup;

public class SeasonExpandableGroup extends ExpandableGroup<Series.Episode> {
    private Series.Season season;

    public SeasonExpandableGroup(String title, Series.Season season) {
        super(title, season.getEpisodes());
        this.season = season;
    }

    public Series.Season getSeason() {
        return season;
    }

    public void setSeason(Series.Season season) {
        this.season = season;
    }
}

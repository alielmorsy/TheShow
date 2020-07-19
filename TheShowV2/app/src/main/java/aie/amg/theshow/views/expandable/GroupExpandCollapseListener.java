package aie.amg.theshow.views.expandable;

public interface GroupExpandCollapseListener {
    void onGroupExpanded(ExpandableGroup group);

    /**
     * Called when a group is collapsed
     * @param group the {@link ExpandableGroup} being collapsed
     */
    void onGroupCollapsed(ExpandableGroup group);
}


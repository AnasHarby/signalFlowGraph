package sfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoopGroupContainer {
    private List<LoopGroup> groupList = null;
    private double gain = 0;
    private int degree = 0;

    public LoopGroupContainer(final int degree) {
        this.groupList = new ArrayList<>();
        this.degree = degree;
    }

    public void addLoopGroups(final LoopGroup... loopGroups) {
        this.groupList.addAll(Arrays.asList(loopGroups));
        for (LoopGroup group : loopGroups)
            gain += group.getGain();
    }

    public int size() {
        return this.groupList.size();
    }

    public boolean empty() {
        return size() == 0;
    }

    public List<LoopGroup> getGroupList() {
        return groupList;
    }

    public double getGain() {
        return this.gain;
    }

    public int getDegree() {
        return degree;
    }

    @Override
    public Object clone() {
        LoopGroupContainer clone = new LoopGroupContainer(this.degree);
        for (LoopGroup loopGroup : this.groupList)
            clone.addLoopGroups((LoopGroup) loopGroup.clone());
        return clone;
    }
}
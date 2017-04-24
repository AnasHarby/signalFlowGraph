package sfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoopGroup {
    private List<Path> loopList = null;
    private double gain = 1;

    public LoopGroup() {
        this.loopList = new ArrayList<>();
    }

    public double getGain() {
        return this.gain;
    }

    public List<Path> getLoopList() {
        return this.loopList;
    }

    public void addLoops(final Path... loops) {
        this.loopList.addAll(Arrays.asList(loops));
        for (Path loop : loops)
            gain *= loop.getGain();
    }

    public void removeLoops(final Path... loops) {
        this.loopList.removeAll(Arrays.asList(loops));
        for (Path loop : loops)
            gain /= loop.getGain();
    }

    public boolean touches(final Path path) {
        for (Path loopInGroup : this.loopList)
            if (path.touches(loopInGroup))
                return true;
        return false;
    }

    @Override
    public Object clone() {
        LoopGroup clone = new LoopGroup();
        clone.addLoops(this.loopList.toArray(new Path[this.loopList.size()]));
        return clone;
    }
}
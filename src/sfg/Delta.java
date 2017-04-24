package sfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Delta {
    private List<LoopGroupContainer> containerList = null;
    private double gain;

    public Delta() {
        containerList = new ArrayList<>();
        this.gain = 1;
    }

    public void clear() {
        this.containerList.clear();
        this.gain = 1;
    }

    public void addContainers(final LoopGroupContainer... containers) {
        this.containerList.addAll(Arrays.asList(containers));
        for (LoopGroupContainer container : containers) {
            this.gain += container.getDegree() % 2 == 0 ? container.getGain()
                    : -1 * container.getGain();
        }
    }

    public List<LoopGroupContainer> getContainerList() {
        return this.containerList;
    }

    public double getGain() {
        return this.gain;
    }

    @Override
    public Object clone() {
        Delta clone = new Delta();
        for (LoopGroupContainer container : this.containerList)
            clone.addContainers((LoopGroupContainer) container.clone());
        return clone;
    }
}
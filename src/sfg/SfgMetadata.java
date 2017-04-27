package sfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A metadata compilation for the SFG for a signal flow inside of it,
 * contains the overall gain result, a list of forward paths copies and a
 * list of loops copies.
 * It's safe to change any of the values of the metadata without affecting
 * the actual SFG.
 */
public class SfgMetadata {
    private double result = 0;
    private List<Path> loops = null;
    private Delta delta = null;
    private Map<Path, Delta> forwardPathsDeltas = null;

    public SfgMetadata(final double result, final List<Path> loops, final Delta delta
            , final Map<Path, Delta> forwardPathsDeltas) {
        this.result = result;
        this.loops = clonePaths(loops);
        this.delta = (Delta) delta.clone();
        this.forwardPathsDeltas = cloneDeltas(forwardPathsDeltas);
    }

    public double getResult() {
        return this.result;
    }

    public List<Path> getForwardPaths() {
        return new ArrayList<>(this.forwardPathsDeltas.keySet());
    }

    public List<Path> getLoops() {
        return this.loops;
    }

    public Delta getDelta() {
        return delta;
    }

    public Map<Path, Delta> getForwardPathsDeltas() {
        return forwardPathsDeltas;
    }

    private List<Path> clonePaths(final List<Path> pathList) {
        List<Path> clone = new ArrayList<>();
        for (Path path : pathList)
            clone.add((Path) path.clone());
        return clone;
    }

    private Map<Path, Delta> cloneDeltas(final Map<Path, Delta> deltaMap) {
        Map<Path, Delta> clone = new HashMap<>();
        for (Map.Entry<Path, Delta> entry : deltaMap.entrySet())
            clone.put((Path) entry.getKey().clone(),
                    (Delta) entry.getValue().clone());
        return clone;
    }
}
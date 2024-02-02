import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexNode extends Node{

    // m nodes
    protected ArrayList<Node> children; // m+1 children

    public IndexNode(double key, Node child0, Node child1) {
        isLeafNode = false;
        keys = new ArrayList<Double>();
        keys.add(key);
        children = new ArrayList<Node>();
        children.add(child0);
        children.add(child1);
    }

    public IndexNode(List<Double> newKeys, List<Node> newChildren) {
        isLeafNode = false;
        keys = new ArrayList<Double>(newKeys);
        children = new ArrayList<Node>(newChildren);

    }
    public void insertSorted(Map.Entry<Double, Node> e, int index) {
        double key = e.getKey();
        Node child = e.getValue();
        if (index >= keys.size()) {
            keys.add(key);
            children.add(child);
        } else {
            keys.add(index, key);
            children.add(index+1, child);
        }
    }
}

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class LeafNode extends Node{
    protected ArrayList<Map<Double, Map<String,String>>> values;
    protected LeafNode nextLeaf;

    public LeafNode(double firstKey, Map<Double, Map<String,String>> firstValue) {
        isLeafNode = true;
        keys = new ArrayList<Double>();
        values = new ArrayList<Map<Double, Map<String,String>>>();
        keys.add(firstKey);
        values.add(firstValue);

    }

    public LeafNode(List<Double> newKeys, List<Map<Double, Map<String,String>>> newValues) {
        isLeafNode = true;
        keys = new ArrayList<Double>(newKeys);
        values = new ArrayList<Map<Double, Map<String,String>>>(newValues);

    }
    public void insertSorted(double key, Map<Double, Map<String,String>> value) {
        if (key < keys.get(0)) {
            keys.add(0, key);
            values.add(0, value);
        } else if (key > keys.get(keys.size() - 1)) {
            keys.add(key);
            values.add(value);
        } else {
            ListIterator<Double> iterator = keys.listIterator();
            while (iterator.hasNext()) {
                if (iterator.next() > key) {
                    int position = iterator.previousIndex();
                    keys.add(position, key);
                    values.add(position, value);
                    break;
                }
            }

        }
    }
}

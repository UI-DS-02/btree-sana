import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

public class BPlusTree {

    public Node root;
    public static final int D = 2;
    //================== actual search ==================
    public Map<Double, Map<String,String>> search(double key) {
        // look for leaf node that should contain key
        LeafNode leaf = findLeafNodeWithKey(root, key);

        // look for value within leaf
        for (int i = 0; i < leaf.keys.size(); i++){
            if (leaf.keys.get(i) == key){
                return leaf.values.get(i);
            }
        }
        return null;
    }

    public void insert(double key, Map<Double, Map<String,String>> value) {
        // initial insert to tree
        if (root == null){
            root = new LeafNode(key, value);
        }

        Map.Entry<Double, Node> overflowed = insertHelper(root, key, value);
        if (overflowed != null){
            // overflow emerged to root level
            root = new IndexNode(overflowed.getKey(), root, overflowed.getValue());
        }

    }
    private Map.Entry<Double, Node> insertHelper(Node node, double key, Map<Double, Map<String,String>> value){
        Map.Entry<Double,Node> overflow = null;
        if (node.isLeafNode){
            LeafNode leaf = (LeafNode) node;
            leaf.insertSorted(key, value);
            if (leaf.isOverflowed()){
                Map.Entry<Double, Node> rightSplit = splitLeafNode(leaf);
                return rightSplit;
            }
            return null;
        }
        else {
            IndexNode idxNode = (IndexNode) node;
            if (key < node.keys.get(0))
                overflow = insertHelper(idxNode.children.get(0), key, value);
            else if (key >= node.keys.get(idxNode.keys.size() - 1))
                overflow = insertHelper(idxNode.children.get(idxNode.children.size() - 1), key, value);
            else {
                // insert at one of the middle child
                for (int i = 0; i < idxNode.children.size(); i++){
                    if (idxNode.keys.get(i) > key){
                        overflow = insertHelper(idxNode.children.get(i), key, value);
                        break;
                    }
                }
            }
        }
        if (overflow != null){
            IndexNode idxNode = (IndexNode)node;

            // figure out where to put insert the overflowed index
            double splittingKey = overflow.getKey();
            int indexAtParent = idxNode.keys.size();
            if (splittingKey < idxNode.keys.get(0)){
                indexAtParent = 0;
            } else if (splittingKey > idxNode.keys.get(idxNode.keys.size() -1)){
                indexAtParent = idxNode.children.size();
            } else {
                for (int i = 0; i < idxNode.keys.size(); i++){
                    if (i < idxNode.keys.get(i)){
                        indexAtParent = i;
                    }
                }
            }

            idxNode.insertSorted(overflow, indexAtParent);
            if (idxNode.isOverflowed()){
                Map.Entry<Double, Node> rightSplit = splitIndexNode(idxNode);
                return rightSplit;
            }
            return null;
        }
        return overflow;

    }
    private void manageSiblingPtrs(LeafNode leftLeaf, LeafNode rightLeaf) {
        if (leftLeaf.nextLeaf != null){
            rightLeaf.nextLeaf = leftLeaf.nextLeaf;
        }
        leftLeaf.nextLeaf = rightLeaf;
    }
    public Map.Entry<Double, Node> splitLeafNode(LeafNode leaf) {
        int RIGHT_BUCKET_SIZE = D+1;

        ArrayList<Double> rightKeys = new ArrayList<Double>(RIGHT_BUCKET_SIZE);
        ArrayList<Map<Double, Map<String,String>>> rightValues = new ArrayList<Map<Double, Map<String,String>>>(RIGHT_BUCKET_SIZE);

        rightKeys.addAll(leaf.keys.subList(D, leaf.keys.size()));
        rightValues.addAll(leaf.values.subList(D, leaf.values.size()));

        // delete the right side from the left
        leaf.keys.subList(D, leaf.keys.size()).clear();
        leaf.values.subList(D, leaf.values.size()).clear();

        LeafNode rightLeaf = new LeafNode(rightKeys, rightValues);

        // manage the new siblinghood
        manageSiblingPtrs(leaf, rightLeaf);

        return new AbstractMap.SimpleEntry<Double, Node>(rightLeaf.keys.get(0), rightLeaf);

    }
    public Map.Entry<Double, Node> splitIndexNode(IndexNode index) {
        int BUCKET_SIZE = D;
        ArrayList<Double> rightKeys = new ArrayList<Double>(BUCKET_SIZE);
        ArrayList<Node> rightChildren = new ArrayList<Node>(BUCKET_SIZE + 1);

        rightKeys.addAll(index.keys.subList(D+1, index.keys.size()));
        rightChildren.addAll(index.children.subList(D+1, index.children.size()));

        // push up the new index
        IndexNode rightNode = new IndexNode(rightKeys, rightChildren);
        AbstractMap.SimpleEntry<Double, Node> splitted = new AbstractMap.SimpleEntry<Double, Node>(index.keys.get(D), rightNode);

        // delete the right side from the left
        index.keys.subList(D, index.keys.size()).clear();
        index.children.subList(D+1, index.children.size()).clear();

        return splitted;
    }
    public void delete(double key) {
        int index = (int) deleteHelper(null, root, key);
        if (index != -1){
            root.keys.remove(index);
            if (root.keys.size() == 0){
                root = ((IndexNode) root).children.get(0);
            }
        }

        if (root.keys.size() == 0){
            root = null;
        }
    }
    private double deleteHelper(IndexNode parent, Node node, double key) {
        double indexToDelete = -1;

        // find index of node in parent
        double indexInParent = -1;
        if (parent != null){
            for (indexInParent = 0; indexInParent < parent.children.size(); indexInParent++){
                if (parent.children.get((int) indexInParent) == node){
                    break;
                }
            }
        }

        if (node.isLeafNode){
            LeafNode leafNode = (LeafNode) node;
            for (int i = 0; i < leafNode.keys.size(); i++){
                if (leafNode.keys.get(i) == key){
                    // delete key from leafNode's keys
                    leafNode.keys.remove(i);
                    // delete the associated value
                    leafNode.values.remove(i);
                    break;
                }
            }


            // check for underflow
            if (leafNode.isUnderflowed() && leafNode != root){
                // find index leafnode resides in parent
                if (indexInParent - 1 >= 0){
                    // node has left child
                    LeafNode left = (LeafNode) parent.children.get((int) (indexInParent -1));
                    return handleLeafNodeUnderflow(left, leafNode, parent);
                } else {
                    // node does not have left child
                    LeafNode right = (LeafNode) parent.children.get((int) (indexInParent + 1));
                    return handleLeafNodeUnderflow(leafNode, right, parent);
                }
            } else {
                // may need to update parents / ancestors if deleted splitting key
                if (leafNode.keys.size() > 0)
                    updateIndexNodeKeyWithKey(root, key, leafNode.keys.get(0));
                return -1; // delete did not cause underflow
            }

        }

        else {
            // Node is an IndexNode
            IndexNode idxNode = (IndexNode) node;
            if (key < idxNode.keys.get(0)){
                // go down first child
                indexToDelete = deleteHelper(idxNode, idxNode.children.get(0), key);
            }
            else if (key >= idxNode.keys.get(idxNode.keys.size() - 1)){
                // go down last child
                indexToDelete = deleteHelper(idxNode, idxNode.children.get(idxNode.children.size() - 1), key);
            }
            else {
                // go down the middle children
                for (int i = 0; i < idxNode.keys.size(); i++){
                    if (idxNode.keys.get(i) > key){
                        indexToDelete = deleteHelper(idxNode, idxNode.children.get(i), key);
                    }
                }
            }
        }

        // see if there is an index to delete remaining
        if (indexToDelete != -1){
            if (node == root ){
                return indexToDelete;
            }
            node.keys.remove(indexToDelete);

            // if removal caused underflow
            if (node.isUnderflowed()){
                // determine if node has left sibling
                IndexNode left = (IndexNode)node;
                IndexNode right = (IndexNode)node;

                // check to see if indexNode has sibling
                if (indexInParent - 1 >= 0){
                    left = (IndexNode) parent.children.get((int) (indexInParent - 1));
                } else {
                    right = (IndexNode) parent.children.get((int) (indexInParent + 1));
                }
                return handleIndexNodeUnderflow(left, right, parent);
            }
        }

        return -1;
    }
    public int handleLeafNodeUnderflow(LeafNode left, LeafNode right,
                                       IndexNode parent) {

        // merge
        if (left.keys.size() + right.keys.size() < 2*D){
            left.keys.addAll(right.keys);
            left.values.addAll(right.values);
            left.nextLeaf = right.nextLeaf;

            // delete the other node
            int indexInParent = parent.children.indexOf(right);
            parent.children.remove(indexInParent);
            return indexInParent -1;
        }

        // re-distribute
        int childsIndexInParent;
        if (left.isUnderflowed()){
            childsIndexInParent = parent.children.indexOf(right);
            // get the minimum key value of right
            left.insertSorted(right.keys.remove(0), right.values.remove(0));
        } else {
            childsIndexInParent = parent.children.indexOf(right);
            // get maximum key value of left
            right.insertSorted(left.keys.remove(left.keys.size()-1), left.values.remove(left.values.size()-1));
            parent.keys.set(childsIndexInParent - 1, parent.children.get(childsIndexInParent).keys.get(0));
        }
        parent.keys.set(childsIndexInParent - 1, parent.children.get(childsIndexInParent).keys.get(0));

        // update the parent's index key


        return -1;

    }
    public int handleIndexNodeUnderflow(IndexNode leftIndex, IndexNode rightIndex, IndexNode parent) {
        double separatingKey;
        int index;

        // find separating key value from parent
        for (index = 0; index < parent.keys.size(); index++){
            if (parent.children.get(index) == leftIndex && parent.children.get(index+1) == rightIndex){
                break;
            }
        }

        separatingKey = parent.keys.get(index);

        // Action : merge
        if (leftIndex.keys.size() + rightIndex.keys.size() < 2*D){
            // move separating key down
            leftIndex.keys.add(separatingKey);
            leftIndex.keys.addAll(rightIndex.keys);

            leftIndex.children.addAll(rightIndex.children);

            // delete the right side
            parent.children.remove(parent.children.indexOf(rightIndex));
            return index;

        }

        // Action: Distribute
        if (leftIndex.isUnderflowed()){
            // move separating key down to leftIndex
            leftIndex.keys.add(separatingKey);
            // move leftmost key from right up
            parent.keys.set(index, rightIndex.keys.remove(0));
            // leftmost child of right is now left's
            leftIndex.children.add(rightIndex.children.remove(0));
        }
        else if (rightIndex.isUnderflowed()) {
            // move separating key down to rightIndex
            rightIndex.keys.add(0, separatingKey);
            // the last child of left index sibling is now right index's
            Node lastChild = leftIndex.children.remove(leftIndex.children.size() - 1);
            rightIndex.children.add(0, lastChild);
            // move rightmost key from leftIndex up
            parent.keys.set(parent.keys.size()-1, leftIndex.keys.remove(leftIndex.keys.size() - 1));
        }

        return -1;
    }
    private LeafNode findLeafNodeWithKey(Node theNode, double key){
        if (theNode == null)
            return null;

        if (theNode.isLeafNode){
            // Found the LeafNode
            return (LeafNode) theNode;
        }
        else {
            // The node is an index node
            IndexNode indexNode = (IndexNode) theNode;

            if (key < theNode.keys.get(0)){
                return findLeafNodeWithKey(indexNode.children.get(0), key);
            }
            else if (key >= theNode.keys.get(theNode.keys.size() - 1)) {
                return findLeafNodeWithKey(indexNode.children.get(indexNode.children.size() - 1), key);
            }
            else {
                ListIterator<Double> iterator = indexNode.keys.listIterator();
                while (iterator.hasNext()){
                    if (iterator.next() > key){
                        return findLeafNodeWithKey(indexNode.children.get(iterator.previousIndex()), key);
                    }
                }
            }
        }
        return null;
    }
    private void updateIndexNodeKeyWithKey(Node theNode, double searchKey, double newKey){
        if (theNode == null)
            return;

        if (theNode.isLeafNode)
            return;

        IndexNode idxNode = (IndexNode) theNode;
        for (int i = 0; i < theNode.keys.size(); i++){
            // not here, don't need to keep going

            if (idxNode.keys.get(i) > searchKey){
                break;
            }

            if (idxNode.keys.get(i) == searchKey){
                idxNode.keys.set(i, newKey);
                return;
            }
        }

        // not found, perhaps in another child
        if (searchKey < idxNode.keys.get(0)){
            updateIndexNodeKeyWithKey(idxNode.children.get(0), searchKey, newKey);
        } else if (searchKey > idxNode.keys.get(idxNode.keys.size() - 1)){
            updateIndexNodeKeyWithKey(idxNode.children.get(idxNode.children.size() - 1), searchKey, newKey);
        } else {
            for (int i = 0; i < theNode.keys.size(); i++){
                if (idxNode.keys.get(i) > searchKey){
                    updateIndexNodeKeyWithKey(idxNode.children.get(i), searchKey, newKey);
                }
            }

        }
    }
}

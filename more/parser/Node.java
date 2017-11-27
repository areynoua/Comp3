package parser;
/**
* Node for parse trees
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Node {

    private GrammarSymbol data;
    private Integer id;
    private Node parent;
    private List<Node> children;

    public Node(GrammarSymbol data, Integer id) {
        this.data = data;
        this.id = id;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    private void setParent(Node parent) {
        this.parent = parent;
    }

    public void addChild(Node child) {
        this.children.add(child);
        Collections.sort(this.children, new NodeComparator());
        child.setParent(this);
    }

    public Node getParent() {
        return this.parent;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public boolean isLeaf() {
        return (this.children.size() == 0);
    }

    public Integer getId() {
        return this.id;
    }

    public GrammarSymbol getSymbol() {
        return this.data;
    }

    public class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node node1, Node node2) {
            return node2.getId() - node1.getId();
        }
    }
}
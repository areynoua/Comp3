package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
* Node to compose parse trees
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/
public class Node {

    /** Grammar symbol represented by the node */
    private GrammarSymbol data;
    /** Node identifier */
    private Integer id;
    /** Parent node */
    private Node parent;
    /** Node children */
    private List<Node> children;

    /**
     * Creates a tree node.
     *
     * @param data  Grammar symbol represented by the node
     * @param id    Node identifier
     */
    public Node(GrammarSymbol data, Integer id) {
        this.data = data;
        this.id = id;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    /**
     * Set the given node as parent. This method is private and
     * automatically called by the node after calling addChild
     *
     * @param parent  Parent node
     * @see addChild
     */
    private void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Adds child to the current list of children. After adding the new child,
     * the list is automatically sorted by node identifier.
     *
     * @param child  New child to be added
     */
    public void addChild(Node child) {
        this.children.add(child);
        Collections.sort(this.children, new NodeComparator());
        child.setParent(this);
    }

    /** Get parent node */
    public Node getParent() {
        return this.parent;
    }

    /** Get list of children */
    public List<Node> getChildren() {
        return this.children;
    }

    /** Returns true if the node has no child */
    public boolean isLeaf() {
        return (this.children.size() == 0);
    }

    /** Get node identifier */
    public Integer getId() {
        return this.id;
    }

    /** Get the grammar symbol represented by the node */
    public GrammarSymbol getSymbol() {
        return this.data;
    }

    /**
     * Comparator used to sort nodes by identifiers.
     * A node is greater than another node if it has a greater identifier.
     */
    public class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node node1, Node node2) {
            return node1.getId() - node2.getId();
        }
    }
}

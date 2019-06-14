package io.github.clearwsd.type;

public class AnchoredDepNode extends DefaultDepNode {

    int start;
    int end;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public AnchoredDepNode(int index) {
        super(index);
    }

    public AnchoredDepNode(DepNode node) {
        super(node.index());
        if (node instanceof DefaultDepNode) {
            DefaultDepNode orinode = (DefaultDepNode) node;
            this.nlpToken = orinode.nlpToken;
            this.head = orinode.head;
            this.children = orinode.children;
        }
    }
}

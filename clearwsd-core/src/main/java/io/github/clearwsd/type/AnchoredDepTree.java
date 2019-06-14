package io.github.clearwsd.type;

import java.util.List;

public class AnchoredDepTree extends DefaultDepTree implements DepTree {

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

    public AnchoredDepTree(DepTree tree) {
        super(tree.index(), tree.tokens(), tree.root());
    }

    public AnchoredDepTree(int index, List<DepNode> tokens, DepNode root, int start, int end) {
        super(index, tokens, root);
        this.start = start;
        this.end = end;
    }

    public AnchoredDepTree(int index, List<DepNode> tokens, DepNode root) {
        super(index, tokens, root);
    }

}

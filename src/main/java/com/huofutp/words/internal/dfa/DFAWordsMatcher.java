package com.huofutp.words.internal.dfa;

import com.huofutp.words.WordsMatcher;
import com.huofutp.common.function.Func;

import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:likelovec@gmail.com">韦朕</a>
 * @date 2022/3/11 12:18
 */
public interface DFAWordsMatcher extends WordsMatcher {

    /**
     * dfa节点
     *
     * @author <a href="mailto:likelovec@gmail.com">韦朕</a>
     * @date 2022/03/10 15:25:31
     * @apiNote <pre>
     *     {@code
     *     public static void main(String[] args) {
     * 		DfaNode node = new DfaNode('中');
     *
     * 		DfaNode g = new DfaNode('国');
     * 		g.addChild(new DfaNode('人'));
     *
     * 		DfaNode n = new DfaNode('男');
     * 		n.addChild(new DfaNode('人'));
     * 		g.addChild(n);
     *
     * 		node.addChild(g);
     * 		node.addChild(new DfaNode('间'));
     *
     * 		node.print(node);
     *        }
     *
     *     }
     * </pre>
     */
    class DfaNode {

        private final int type;
        private final Map<Character, DfaNode> childes = Func.mapOf();
        private final char _char;
        // private DfaNode parent;
        private boolean word;
        private String source;

        public DfaNode(final char _char) {
            this(_char, DfaNodeType.normal);
        }

        public DfaNode(final char _char, final int type) {
            if (type == DfaNodeType.en) {
                this._char = WordsMatcher.toLowerCase(_char);
            } else {
                this._char = _char;
            }
            this.type = type;
        }

        public int type() {
            return this.type;
        }

        public String source() {
            return this.source;
        }

        public boolean isWord() {
            return this.word;
        }

        public boolean isLeaf() {
            return this.childes.isEmpty();
        }

        public char getChar() {
            return this._char;
        }

        public void addChild(final DfaNode child) {
            this.childes.put(child.getChar(), child);
            //child.setParent(this);
        }

        public Map<Character, DfaNode> getChildes() {
            return this.childes;
        }

        public void fillChildren(DfaNode root, final String word, final int type) {
            final int wordLength = word.length();

            for (int i = 1; i < wordLength; i++) {
                final char nextChar = word.charAt(i);
                DfaNode nextNode = null;

                if (!root.isLeaf()) {
                    nextNode = root.getChildes().get(nextChar);
                }

                if (nextNode == null) {
                    nextNode = new DfaNode(nextChar, type);
                }

                root.addChild(nextNode);
                root = nextNode;

                if (i == wordLength - 1) {
                    root.word(word);
                }
            }
        }

        public void print() {
            System.out.print(this.getChar());
            if (this.getChildes() != null) {
                final Set<Character> keys = this.getChildes().keySet();
                for (final Character _char : keys) {
                    this.print(this.getChildes().get(_char));
                }
            }
        }

        public void word(final String word) {
            this.source = word;
            this.word = true;
        }

        private void print(final DfaNode node) {
            System.out.print(node.getChar());
            if (node.getChildes() != null) {
                final Set<Character> keys = node.getChildes().keySet();
                for (final Character _char : keys) {
                    this.print(node.getChildes().get(_char));
                }
            }
        }

        interface DfaNodeType {

            int normal = 0;
            int zh = 1;
            int en = 2;
        }
    }
}
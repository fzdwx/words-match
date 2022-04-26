package org.atomicode.fzdwx.words.internal.dfa;


import io.github.fzdwx.lambada.Tuple;
import io.github.fzdwx.lambada.fun.State;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.atomicode.fzdwx.words.WordsMatcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 精确词语匹配器
 *
 * @author <a href="mailto:likelovec@gmail.com">韦朕</a>
 * @apiNote DFA匹配器，精确匹配
 * @date 2022/3/10 15:22
 */
@NoArgsConstructor
public class AccurateWordsMatcher implements DFAWordsMatcher {

    private Map<Character, DfaNode> nodes;

    private AccurateWordsMatcher(final Collection<String> words) {
        this.nodes = new HashMap<>();
        words.forEach(this::put);
    }

    public static AccurateWordsMatcher create(final Collection<String> words) {
        return new AccurateWordsMatcher(words);
    }

    @Override
    public boolean process(final boolean partMatch, String content, final Handler handle) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(content)) {
            return false;
        }

        content = org.apache.commons.lang3.StringUtils.trim(content);
        final int contentSize = content.length();
        if (contentSize < 2) { // 单字符不支持
            return false;
        }

        for (int index = 0; index < contentSize; index++) {
            final char firstChar = content.charAt(index);

            DfaNode node = this.nodes.get(firstChar);
            if (node == null || node.isLeaf()) {
                continue;
            }

            // 源文本中匹配敏感词位置
            int charCount = 1;

            for (int i = index + 1; i < contentSize; i++) {
                final char wordChar = content.charAt(i);

                node = node.getChildes().get(wordChar);
                if (node != null) {
                    charCount++;
                } else {
                    break;
                }

                if (partMatch && node.isWord()) {
                    if (handle.apply(Tuple.of(node.source(), StringUtils.substring(content, index, index + charCount)))) {
                        return true;
                    }
                    break;
                } else if (node.isWord()) {
                    if (handle.apply(Tuple.of(node.source(), StringUtils.substring(content, index, index + charCount)))) {
                        return true;
                    }
                }

                if (node.isLeaf()) {
                    break;
                }
            }

            if (partMatch) {
                index += charCount;
            }
        }

        return false;
    }

    @Override
    public State<Void> put(String word) {
        final State<String> state = WordsMatcher.isValidWord(word);
        if (state.isFailure()) {
            return state.newFail();
        }

        word = state.get();

        final char firstChar = word.charAt(0);
        DfaNode firstNode = this.nodes.get(firstChar);
        if (firstNode == null) {
            firstNode = new DfaNode(firstChar);
            this.nodes.put(firstChar, firstNode);
        }

        firstNode.fillChildren(firstNode, word, DfaNode.DfaNodeType.normal);

        return state.newSuccess();
    }

    @Override
    public AccurateWordsMatcher refresh(final Collection<String> words) {
        this.nodes.clear();

        if (words != null) {
            words.forEach(this::put);
        }

        return this;
    }

    @Override
    public boolean hasWords() {
        return this.nodes != null && this.nodes.size() > 0;
    }
}
package io.github.fzdwx.words.internal.dfa;


import io.github.fzdwx.lambada.Tuple;
import io.github.fzdwx.lambada.fun.State;
import io.github.fzdwx.words.WordsMatcher;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 模糊词语匹配器
 *
 * @author <a href="mailto:likelovec@gmail.com">韦朕</a>
 * @date 2022/3/10 15:22
 * @apiNote DFA 模糊词语匹配器<pre>
 *     1.每一个违禁词只能是纯中文或纯英文
 *     3.违禁词英文只能为小写(fuzz内部会转换为小写)
 *     2. case:
 *      hello
 *          helloWorld -> hello
 *          he*l l.o   -> hello
 * </pre>
 */
public class FuzzWordsMatcher implements DFAWordsMatcher {

    private final Map<Character, DfaNode> zhNodes;
    private final Map<Character, DfaNode> enNodes;

    private FuzzWordsMatcher(final Collection<String> words) {
        this.zhNodes = new HashMap<>();
        this.enNodes = new HashMap<>();
        words.forEach(this::put);
    }

    private FuzzWordsMatcher(final String word) {
        this.zhNodes = new HashMap<>();
        this.enNodes = new HashMap<>();
        this.put(word);
    }

    public static WordsMatcher create(final Collection<String> words) {
        return new FuzzWordsMatcher(words);
    }

    public static WordsMatcher create(final String word) {
        return new FuzzWordsMatcher(word);
    }

    @Override
    public boolean process(final boolean partMatch, String content, final Handler handle) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }

        content = StringUtils.trim(content);
        final int contentSize = content.length();
        if (contentSize < 2) { // 单字符不支持
            return false;
        }

        for (int index = 0; index < contentSize; index++) {
            final char firstChar = content.charAt(index);

            DfaNode node = this.getNode(firstChar);
            if (node == null || node.isLeaf()) {
                continue;
            }

            // 源文本中匹配敏感词位置
            int charCount = 1;

            for (int i = index + 1; i < contentSize; i++) {
                char wordChar = content.charAt(i);

                if ((node.type() == DfaNode.DfaNodeType.zh) || (WordsMatcher.isChinese(node.getChar()))) {
                    if (!WordsMatcher.isChinese(wordChar)) { // 只匹配中文
                        charCount++;
                        continue;
                    } // todo 简繁体？
                } else if ((node.type() == DfaNode.DfaNodeType.en) || (WordsMatcher.isLetter(node.getChar()))) {
                    if (!WordsMatcher.isLetter(wordChar)) { // 只匹配英文 其他字符不匹配
                        charCount++;
                        continue;
                    } else // 英文全部小写
                        wordChar = WordsMatcher.toLowerCase(wordChar);
                }

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
        final State<String> state = WordsMatcher.isValidFuzzWord(word);
        if (state.isFailure()) {
            return state.newFail();
        }

        word = state.get();

        char firstChar = word.charAt(0);

        if (WordsMatcher.isChinese(firstChar)) {
            DfaNode firstNode = this.zhNodes.get(firstChar);
            if (firstNode == null) {
                firstNode = new DfaNode(firstChar, DfaNode.DfaNodeType.zh);
                this.zhNodes.put(firstChar, firstNode);
            }
            firstNode.fillChildren(firstNode, word, DfaNode.DfaNodeType.zh);
        } else if (WordsMatcher.isLetter(firstChar)) {
            firstChar = WordsMatcher.toLowerCase(firstChar);
            DfaNode firstNode = this.enNodes.get(firstChar);
            if (firstNode == null) {
                firstNode = new DfaNode(firstChar, DfaNode.DfaNodeType.en);
                this.enNodes.put(firstChar, firstNode);
            }
            firstNode.fillChildren(firstNode, word, DfaNode.DfaNodeType.en);
        } else return state.newFail(new IllegalArgumentException("not support char " + firstChar)); // 不支持的字符 只支持中文和英文

        return state.newSuccess();
    }

    @Override
    public WordsMatcher refresh(final Collection<String> words) {
        this.zhNodes.clear();
        this.enNodes.clear();

        if (words != null) {
            words.forEach(this::put);
        }

        return this;
    }

    @Override
    public boolean hasWords() {
        return (this.zhNodes != null && this.zhNodes.size() > 0) || (this.enNodes != null && this.enNodes.size() > 0);
    }

    private DfaNode getNode(final char firstChar) {
        if (WordsMatcher.isLetter(firstChar)) {
            // 英文统一转小写
            final char c = WordsMatcher.toLowerCase(firstChar);
            return this.enNodes.get(c);
        } else if (WordsMatcher.isChinese(firstChar)) {
            // todo 简繁体？
            return this.zhNodes.get(firstChar);
        }
        return null;
    }
}
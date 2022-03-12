package com.fzdwx.words.internal.dfa;


import com.fzdwx.words.WordsMatcher;
import com.fzdwx.lambada.Tuple;
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

                if ((node.type() == DfaNode.DfaNodeType.zh) || (this.isChinese(node.getChar()))) {
                    if (!this.isChinese(wordChar)) { // 只匹配中文
                        charCount++;
                        continue;
                    } // todo 简繁体？
                } else if ((node.type() == DfaNode.DfaNodeType.en) || (this.isLetter(node.getChar()))) {
                    if (!this.isLetter(wordChar)) { // 只匹配英文 其他字符不匹配
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
    public boolean put(String word) {
        if (StringUtils.isEmpty(word)) {
            return false;
        }

        word = StringUtils.trim(word);
        if (word.length() < 2) { // 单字符不支持
            return false;
        }

        char firstChar = word.charAt(0);

        if (this.isChinese(firstChar)) {
            DfaNode firstNode = this.zhNodes.get(firstChar);
            if (firstNode == null) {
                firstNode = new DfaNode(firstChar, DfaNode.DfaNodeType.zh);
                this.zhNodes.put(firstChar, firstNode);
            }
            firstNode.fillChildren(firstNode, word, DfaNode.DfaNodeType.zh);
        } else if (this.isLetter(firstChar)) {
            firstChar = WordsMatcher.toLowerCase(firstChar);
            DfaNode firstNode = this.enNodes.get(firstChar);
            if (firstNode == null) {
                firstNode = new DfaNode(firstChar, DfaNode.DfaNodeType.en);
                this.enNodes.put(firstChar, firstNode);
            }
            firstNode.fillChildren(firstNode, word, DfaNode.DfaNodeType.en);
        } else return false; // 不支持的字符 只支持中文和英文

        return true;
    }

    @Override
    public WordsMatcher refresh(final Collection<String> words) {
        this.zhNodes.clear();
        this.enNodes.clear();
        words.forEach(this::put);

        return this;
    }

    private DfaNode getNode(final char firstChar) {
        if (this.isLetter(firstChar)) {
            // 英文统一转小写
            final char c = WordsMatcher.toLowerCase(firstChar);
            return this.enNodes.get(c);
        } else if (this.isChinese(firstChar)) {
            // todo 简繁体？
            return this.zhNodes.get(firstChar);
        }
        return null;
    }
}
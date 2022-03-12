package com.huofutp.words.internal.dfa;


import cn.hutool.core.util.StrUtil;
import com.huofutp.common.function.Func;
import com.huofutp.common.function.core.Kv;
import com.huofutp.words.WordsMatcher;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 精确词语匹配器
 *
 * @author <a href="mailto:likelovec@gmail.com">韦朕</a>
 * @apiNote DFA匹配器，精确匹配
 * @date 2022/3/10 15:22
 */
public class AccurateWordsMatcher implements DFAWordsMatcher {

    private final Map<Character, DfaNode> nodes;

    private AccurateWordsMatcher(final Collection<String> words) {
        this.nodes = Func.mapOf();
        words.forEach(this::put);
    }

    private AccurateWordsMatcher(final String words) {
        this.nodes = Func.mapOf();
        this.put(words);
    }

    public static WordsMatcher create(final Collection<String> words) {
        return new AccurateWordsMatcher(words);
    }

    public static WordsMatcher create(final String word) {
        return new AccurateWordsMatcher(word);
    }

    @Override
    public boolean process(final boolean partMatch, String content, final Handler handle) {
        if (StrUtil.isEmpty(content)) {
            return false;
        }

        content = StrUtil.trim(content);
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
                    if (handle.apply(Kv.create(node.source(), StringUtils.substring(content, index, index + charCount)))) {
                        return true;
                    }
                    break;
                } else if (node.isWord()) {
                    if (handle.apply(Kv.create(node.source(), StringUtils.substring(content, index, index + charCount)))) {
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
        if (StrUtil.isEmpty(word)) {
            return false;
        }

        word = StrUtil.trim(word);
        if (word.length() < 2) { // 单字符不支持
            return false;
        }

        final char firstChar = word.charAt(0);
        DfaNode firstNode = this.nodes.get(firstChar);
        if (firstNode == null) {
            firstNode = new DfaNode(firstChar);
            this.nodes.put(firstChar, firstNode);
        }

        firstNode.fillChildren(firstNode, word, DfaNode.DfaNodeType.normal);

        return true;
    }

    @Override
    public void refresh(final Collection<String> words) {
        this.nodes.clear();
        words.forEach(this::put);
    }
}
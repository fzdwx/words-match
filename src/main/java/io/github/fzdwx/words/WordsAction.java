package io.github.fzdwx.words;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * words action.
 *
 * @author <a href="mailto:likelovec@gmail.com">fzdwx</a>
 * @date 2022/3/10 16:55
 */
public class WordsAction {

    private final static String HTML_HIGHLIGHT = "<font color='red'>%s</font>";

    /**
     * 源内容
     */
    private final String content;

    /**
     * 映射器
     */
    private final Function<WordsMatcher.Handler, Boolean> mapper;

    /**
     * 命中的单词
     */
    private Map<String, String> hintWords;
    private Collection<String> matchHintWords;
    private Collection<String> sensitiveWords;

    WordsAction(final String content, final Function<WordsMatcher.Handler, Boolean> mapper) {
        this.content = content;
        this.mapper = mapper;
    }

    /**
     * 匹配
     *
     * @return boolean 是否包含敏感词
     */
    public boolean match() {
        if (this.hintWords == null)
            return this.mapper.apply(s -> true);
        else {
            return this.hintWords.size() > 0;
        }
    }

    /**
     * 返回匹配到的敏感词语
     *
     * @return {@link Map<String,String> } 返回匹配的敏感词语集合
     */
    public Map<String, String> findAll() {
        this.init();

        return this.hintWords;
    }

    /**
     * 把敏感词替换为指定字符
     *
     * @param replaceChar 替换字符
     * @return {@link String } 转换后的字符串
     */
    public String replace(final char replaceChar) {
        this.init();
        String content = this.content;
        final String replaceCharStr = String.valueOf(replaceChar);

        for (final String word : this.matchHintWords) {
            content = StringUtils.replace(content, word, Strings.repeat(replaceCharStr, word.length()));
        }

        return content;
    }


    /**
     * map
     *
     * @param mapper 映射器
     * @return {@link Set<T> }
     */
    public <T> Stream<T> map(final Function<String, T> mapper) {
        this.init();
        return this.matchHintWords.stream().map(mapper);
    }

    /**
     * 根据匹配的词高亮显示
     *
     * @param hintWord 命中的单词
     * @return string
     */
    public String highlightOne(String hintWord) {
        return this.highlightOne(hintWord, HTML_HIGHLIGHT);
    }

    /**
     * html高亮敏感词
     *
     * @return {@link String }
     */
    public String highlightOne(String hintWord, String template) {
        this.init();
        String content = this.content;
        return StringUtils.replace(content, hintWord, String.format(template, hintWord));
    }

    /**
     * html高亮敏感词
     *
     * @return {@link String }
     */
    public String highlight() {
        return this.highlight(HTML_HIGHLIGHT);
    }

    /**
     * html高亮敏感词
     *
     * @param template 高亮模板{@link #HTML_HIGHLIGHT }
     * @return {@link String }
     */
    public String highlight(final String template) {
        this.init();
        String content = this.content;

        for (final String word : this.matchHintWords) {
            content = content.replaceAll(word, String.format(template, word));
        }

        return content;
    }

    /**
     * 原始内容
     *
     * @return {@link String }
     */
    public String rawContent() {
        return this.content;
    }

    private void init() {
        if (this.hintWords == null) {
            this.hintWords = new LinkedHashMap<>(); // 保证是有序的

            this.mapper.apply(kv -> {
                this.hintWords.put(kv.v1, kv.v2);
                return false;
            });

            this.matchHintWords = this.hintWords.values();
            this.sensitiveWords = this.hintWords.keySet();
        }
    }

}
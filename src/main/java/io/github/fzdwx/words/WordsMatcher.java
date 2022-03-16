package io.github.fzdwx.words;


import cn.hutool.core.util.CharUtil;
import io.github.fzdwx.lambada.internal.Tuple2;
import io.github.fzdwx.words.internal.dfa.AccurateWordsMatcher;
import io.github.fzdwx.words.internal.dfa.FuzzWordsMatcher;
import io.github.fzdwx.words.internal.dfa.MixWordsMatcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/**
 * @author <a href="mailto:likelovec@gmail.com">韦朕</a>
 * @apiNote <pre>
 *     1.单字符不支持
 *     2.部分匹配是匹配到敏感词后立即退出当前匹配；而完整匹配会把所有词都匹配出来，也就是把敏感词中的小词也匹配到。
 *     3.英文敏感词都转换成小写
 * </pre>
 * @date 2022/3/10 15:43
 */
public interface WordsMatcher {

    static WordsMatcher accurate(final Collection<String> words) {
        return AccurateWordsMatcher.create(words);
    }

    static WordsMatcher accurate(String... words) {
        return AccurateWordsMatcher.create(Arrays.asList(words));
    }

    static WordsMatcher fuzz(String... words) {
        return FuzzWordsMatcher.create(Arrays.asList(words));
    }

    static WordsMatcher fuzz(final Collection<String> words) {
        return FuzzWordsMatcher.create(words);
    }

    static WordsMatcher mixed(final Collection<String> words) {
        return MixWordsMatcher.create(words);
    }

    static WordsMatcher mixed(final String... words) {
        return MixWordsMatcher.create(Arrays.asList(words));
    }

    /**
     * 判断一段文字包含敏感词语
     *
     * @param partMatch 部分匹配（粒度）
     * @param content   内容
     * @param handle    处理
     * @return boolean 是否包含敏感词
     * @throws RuntimeException 运行时异常
     */
    boolean process(boolean partMatch, String content, Handler handle);

    /**
     * 添加敏感词
     *
     * @param word 敏感词
     * @return boolean 是否添加成功
     */
    boolean put(String word);

    /**
     * 刷新
     *
     * @param words 新敏感词组
     */
    WordsMatcher refresh(Collection<String> words);

    /**
     * 是否存在违禁词
     *
     * @return boolean
     */
    boolean hasWords();

    /**
     * 细粒度
     *
     * @param content 需要匹配的内容
     * @return {@link WordsAction }
     */
    default WordsAction action(final String content) {
        return new WordsAction(content, (h) -> this.process(false, content, h));
    }

    /**
     * 粗粒度
     *
     * @param content 需要匹配的内容
     * @return {@link WordsAction }
     */
    default WordsAction actionFast(final String content) {
        return new WordsAction(content, (h) -> this.process(true, content, h));
    }

    /**
     * 是中文
     *
     * @param c c
     * @return boolean
     */
    default boolean isChinese(final char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    /**
     * 判断是否为字母（包括大写字母和小写字母）<br>
     * 字母包括A~Z和a~z
     *
     * <pre>
     *   CharUtil.isLetter('a')  = true
     *   CharUtil.isLetter('A')  = true
     *   CharUtil.isLetter('3')  = false
     *   CharUtil.isLetter('-')  = false
     *   CharUtil.isLetter('\n') = false
     *   CharUtil.isLetter('&copy;') = false
     * </pre>
     *
     * @param ch 被检查的字符
     * @return true表示为字母（包括大写字母和小写字母）字母包括A~Z和a~z
     */
    default boolean isLetter(final char ch) {
        return isLetterUpper(ch) || isLetterLower(ch);
    }

    default boolean hasChAndEn(final String word) {
        if (word == null) return false;

        boolean havaCh = false;
        boolean havaEn = false;
        for (final char c : word.toCharArray()) {
            if (this.isLetterOrNumber(c)) {
                havaEn = true;
            }

            if (this.isChinese(c)) {
                havaCh = true;
            }
        }

        return havaCh && havaEn;
    }

    default boolean isLetterOrNumber(char c) {
        return CharUtil.isLetterOrNumber(c);
    }

    static boolean isLetterUpper(final char ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    static boolean isLetterLower(final char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    static char toLowerCase(final char ch) {
        return Character.toLowerCase(ch);
    }

    /**
     * wordsMatcher 匹配后的回调
     *
     * @apiNote <pre>
     *     ky: 实际敏感词=对应的被匹配到的
     *     boolean: 是否继续匹配
     * </pre>
     */
    interface Handler extends Function<Tuple2<String, String>, Boolean> {

    }
}
package com.huofutp.words;

import com.huofutp.common.function.Func;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:likelovec@gmail.com">韦朕</a>
 * @date 2022/3/10 16:34
 */
class WordsMatcherTest {

    private static Collection<String> words = Func.setOf(
            "中国人",
            "中国男人",
            "中国人民",
            "人民",
            "中间",
            "女人",
            "一举",
            "一举成名",
            "一举成名走四方",
            "成名",
            "走四方",
            "zzz",
            "hello",
            "Qwe",
            "你好aa",
            "qweFJAKf"
    );
    final String content = "中1国1人,民,一zzz举,he*l l.oQWE你好aa  qWefJAkf";
    final WordsMatcher fuzz = WordsMatcher.fuzz(words);
    final WordsMatcher accurate = WordsMatcher.accurate(words);
    final WordsMatcher mixed = WordsMatcher.mixed(words);

    @Test
    void testAccurate() {
        final WordsAction action = this.accurate.action(this.content);
        final Map<String, String> all = action.findAll();
        this.assertAccurate(all);
    }

    @Test
    void testMixed() {
        final WordsAction action = this.mixed.action(this.content);

        final Map<String, String> all = action.findAll();
        System.out.println(all);
        this.assertAccurate(all);
        this.assertFuzz(all);
    }

    @Test
    void testHasChAndEn() {
        Assertions.assertTrue(this.fuzz.hasChAndEn("qwe你好"));
        Assertions.assertFalse(this.fuzz.hasChAndEn("qwe"));
        Assertions.assertFalse(this.fuzz.hasChAndEn("你好"));
    }

    @Test
    void testFuzz() {
        final WordsAction action = this.fuzz.action(this.content);
        final Map<String, String> all = action.findAll();
        this.assertFuzz(all);
    }

    private void assertAccurate(final Map<String, String> all) {
        Assertions.assertTrue(all.containsKey("你好aa"));
        Assertions.assertTrue(all.containsKey("zzz"));
    }

    private void assertFuzz(final Map<String, String> all) {
        Assertions.assertTrue(all.containsKey("一举"));
        Assertions.assertTrue(all.containsKey("中国人"));
        Assertions.assertTrue(all.containsKey("zzz"));
        Assertions.assertTrue(all.containsKey("hello"));
        Assertions.assertTrue(all.containsKey("中国人民"));
        Assertions.assertTrue(all.containsKey("人民"));
        Assertions.assertTrue(all.containsKey("Qwe"));
        Assertions.assertTrue(all.containsKey("qweFJAKf"));
    }
}
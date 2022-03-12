package com.huofutp.words;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * @author <a href="mailto:likelovec@gmail.com">韦朕</a>
 * @date 2022/3/10 16:34
 */
class WordsMatcherTest {

    private static Collection<String> words = new HashSet<String>() {
        {
            add("中国人");
            add("中国男人");
            add("中国人民");
            add("人民");
            add("中间");
            add("女人");
            add("一举");
            add("一举成名");
            add("一举成名走四方");
            add("成名");
            add("走四方");
            add("zzz");
            add("hello");
            add("Qwe");
            add("你好aa");
            add("qweFJAKf");
            add("hhhasd");
        }
    };
    final String content = "中1国1人,民,一zzz举,he*l l.oQWE你好aa  qWefJAkf,h1...h1h1a1S1D";
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
        Assertions.assertTrue(all.containsKey("hhhasd"));
    }
}
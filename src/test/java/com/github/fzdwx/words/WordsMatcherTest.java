package com.github.fzdwx.words;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
    void testActionFast() {
        final WordsAction action = fuzz.actionFast(content);
        Assertions.assertTrue(action.match());
    }


    @Test
    void testFuzzFindAll() {
        final WordsAction action = WordsMatcher.fuzz("fuck").action("fuc12312k");

        Assertions.assertTrue(action.findAll().containsKey("fuck"));
    }

    @Test
    void testPut() {
        final WordsAction a2 = fuzz.action("我我我我草你的");
        Assertions.assertFalse(a2.match());
        Assertions.assertTrue(fuzz.put("我草你的"));
        Assertions.assertTrue(a2.match());
        Assertions.assertFalse(fuzz.put(null));

        Assertions.assertTrue(accurate.put("我草你的"));
        Assertions.assertFalse(accurate.put(null));

        Assertions.assertTrue(mixed.put("我草你的"));
        Assertions.assertFalse(mixed.put(null));
    }

    @Test
    void testReplace() {
        final WordsMatcher fuzz = WordsMatcher.fuzz(
                "中国人"
        );
        String content = "中国人民";
        final WordsAction action = fuzz.action(content);
        Assertions.assertEquals(action.replace('草'), "草草草民");
        Assertions.assertEquals(action.rawContent(), content);
    }

    @Test
    void testHasWord() {
        Assertions.assertTrue(this.mixed.hasWords());
        Assertions.assertTrue(this.fuzz.hasWords());
        Assertions.assertTrue(this.accurate.hasWords());

        Assertions.assertFalse(this.mixed.refresh(null).hasWords());
        Assertions.assertFalse(this.fuzz.refresh(null).hasWords());
        Assertions.assertFalse(this.accurate.refresh(null).hasWords());
    }

    @Test
    @DisplayName("测试替换方法")
    void testReplace2() {
        //  stringUtil replace 不能替换带*的字符
        //  word: hello
        //  matchWord: he*l l.o
        //  content: 中1国1人,民,一zzz举,he*l l.oQWE你好aa  qWefJAkf,h1...h1h1a1S1D
        final WordsAction action = WordsMatcher.fuzz("hello").action(this.content);

        final String replace = action.replace('草');

        Assertions.assertTrue(replace.contains("草草草草草草草草"));
    }

    @Test
    void testRefresh() {
        mixed.refresh(words);
        final WordsAction action = mixed.action(content);
        final Map<String, String> all = action.findAll();
        this.assertAccurate(all, action);
        this.assertFuzz(all, action);
    }

    @Test
    void testAccurate() {
        final WordsAction action = this.accurate.action(this.content);
        final Map<String, String> all = action.findAll();
        this.assertAccurate(all, action);
    }

    @Test
    void testMixed() {
        final WordsAction action = this.mixed.action(this.content);

        final Map<String, String> all = action.findAll();
        this.assertAccurate(all, action);
        this.assertFuzz(all, action);
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
        this.assertFuzz(all, action);
    }

    private void assertAccurate(final Map<String, String> all, WordsAction action) {
        Assertions.assertTrue(all.containsKey("你好aa"));
        Assertions.assertTrue(all.containsKey("zzz"));
        Assertions.assertTrue(action.match());
    }

    private void assertFuzz(final Map<String, String> all, WordsAction action) {
        Assertions.assertTrue(action.match());
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
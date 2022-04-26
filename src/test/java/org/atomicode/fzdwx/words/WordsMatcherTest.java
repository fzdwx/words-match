package org.atomicode.fzdwx.words;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
    void testNumber() {
        String word = "123好";
        String content = "123，，，，好";
        final WordsMatcher fuzz = WordsMatcher.fuzz(word);
        Assertions.assertEquals(fuzz.action(content).findAll().size(), 0);
    }

    @Test
    void testNumber2() {
        String word = "好123";
        String content = "好,123";
        System.out.println(WordsMatcher.isValidFuzzWord(word).isSuccess());
    }

    @Test
    void test244() {
        final String word = "我草 你好";
        System.out.println(WordsMatcher.isValidFuzzWord(word).isSuccess());
        final WordsMatcher fuzz = WordsMatcher.accurate(word);
        System.out.println(fuzz.action("我草 你好a").findAll());
    }

    @Test
    void test33333333() {
        Assertions.assertTrue(WordsMatcher.hasChAndEn("s单"));
        Assertions.assertTrue(WordsMatcher.hasChAndEn("s 单"));
    }

    @Test
    void test() {
        Assertions.assertFalse(WordsMatcher.isValidFuzzWord("s单").isSuccess());
    }

    @Test
    void test_1111() {
        final String word = "你好";
        final String content = "你好啊";

        Assertions.assertTrue(WordsMatcher.accurate(word).action(content).findAll().containsKey(word));
        Assertions.assertTrue(WordsMatcher.fuzz(word).action(content).findAll().containsKey(word));
    }

    @Test
    void test_222222() {
        final String word = "你好吗";
        final String content = "你好好吗";

        Assertions.assertFalse(WordsMatcher.accurate(word).action(content).findAll().containsKey(word));
        Assertions.assertFalse(WordsMatcher.fuzz(word).action(content).findAll().containsKey(word));
    }

    @Test
    void test_33333() {
        final String word = "你好吗";
        final String content = "你好，吗";

        Assertions.assertFalse(WordsMatcher.accurate(word).action(content).findAll().containsKey(word));
        Assertions.assertTrue(WordsMatcher.fuzz(word).action(content).findAll().containsKey(word));
    }

    @Test
    void test_44444() {
        final String word = "helloworld";
        final String content = "hello     world";

        final WordsMatcher accurate = WordsMatcher.accurate(word);
        final Map<String, String> all = accurate.action(content).findAll();
        Assertions.assertFalse(all.containsKey(word));
        final WordsMatcher fuzz = WordsMatcher.fuzz(word);
        final Map<String, String> all1 = fuzz.action(content).findAll();
        Assertions.assertTrue(all1.containsKey(word));
    }

    @Test
    void test_1() {
        final WordsAction action = WordsMatcher.mixed("红包", "啊啊").action("恭喜发财，红包拿来啊，什么.啊啊？？？");
        final Map<String, String> map = action.findAll();
        Assertions.assertTrue(map.containsKey("红包"));
        Assertions.assertTrue(map.containsKey("啊啊"));
    }

    @Test
    void test_2() {
        final WordsAction action = WordsMatcher.mixed("啊啊啊123").action("啊啊啊，123呀，红包拿来啊————啊啊啊123！");
        final Map<String, String> map = action.findAll();
        Assertions.assertTrue(map.containsKey("啊啊啊123"));
    }

    @Test
    void test_3() {
        final WordsAction action = WordsMatcher.mixed("什么 什么").action("啊啊啊，123呀，红包拿来啊————啊啊啊123！什么 什么啊");
        final Map<String, String> map = action.findAll();
        Assertions.assertTrue(map.containsKey("什么 什么"));
    }

    @Test
    void test_4() {
        final WordsMatcher mixed = WordsMatcher.mixed("66");
        final WordsAction action = mixed.action("恭喜发财，红包拿来！！！老铁们，aa666啊！！！快一点儿啊，等得花儿都谢了！！！3组（呵呵）呵呵呵呵呵呵！！！");
        final Map<String, String> map = action.findAll();
        Assertions.assertTrue(map.containsKey("66"));
    }

    @Test
    void test_5() {
        final WordsMatcher mixed = WordsMatcher.mixed("66啊");
        final WordsAction action = mixed.action("恭喜发财，红包拿来！！！老铁们，aa666啊！！！快一点儿啊，等得花儿都谢了！！！3组（呵呵）呵呵呵呵呵呵！！！");
        final Map<String, String> map = action.findAll();
        Assertions.assertTrue(map.containsKey("66啊"));
    }

    @Test
    void test_6() {
        final WordsMatcher mixed = WordsMatcher.mixed("aa66bb");
        final WordsAction action = mixed.action("恭喜发财，红包拿来！！！老铁们，aa66bb啊！！！快一点儿啊，等得花儿都谢了！！！3组（呵呵）呵呵呵呵呵呵！！！");
        final Map<String, String> map = action.findAll();
        Assertions.assertTrue(map.containsKey("aa66bb"));
    }

    @Test
    void test_7() {
        final WordsMatcher mixed = WordsMatcher.mixed("66bb");
        final WordsAction action = mixed.action("恭喜发财，红包拿来！！！老铁们，aa66bb啊！！！快一点儿啊，等得花儿都谢了！！！3组（呵呵）呵呵呵呵呵呵！！！");
        final Map<String, String> map = action.findAll();
        Assertions.assertTrue(map.containsKey("66bb"));
    }

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
        Assertions.assertTrue(fuzz.put("我草你的").isSuccess());
        Assertions.assertFalse(fuzz.put("我").isSuccess());
        Assertions.assertTrue(a2.match());
        Assertions.assertFalse(fuzz.put(null).isSuccess());

        Assertions.assertTrue(accurate.put("我草你的").isSuccess());
        Assertions.assertFalse(accurate.put("我").isSuccess());
        Assertions.assertFalse(accurate.put(null).isSuccess());

        Assertions.assertTrue(mixed.put("我草你的").isSuccess());
        Assertions.assertFalse(mixed.put("我").isSuccess());
        Assertions.assertFalse(mixed.put(null).isSuccess());
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
    void test_3333333333() {
        final List<String> strings = FileUtil.readLines("1.txt", CharsetUtil.UTF_8);
        final WordsMatcher mixed = WordsMatcher.mixed(strings);
        final WordsAction action = mixed.action("回归测试，，，1、大爷 2、尼达噎吗 3、傻帽 4、你老味吖 5、脑子进水了 6、我怎么听不懂你说的话，哦！听听，我家的猪在跟我说话了 7、哪儿凉快哪儿待着去 8、太阳从西边出来 9、母猪都会上树 10、驴见驴踢，猪见猪踩 11、左脸欠抽，右脸欠踹 12、姥姥不疼，舅舅不爱 13、你从小缺钙，长大缺爱 14、I don’t want to see your face 15、我不愿再见到你 16、You shouldn’t have done that 17、你真不应该那样做 18、Don’t talk to me like that 19、别那样和我说话 20、Who do you think you are 21、你以为你是谁 22、TMD 23、your mother fucking bull shit 24、一派胡言 25、Don’t give me your shoot 26、别跟我胡扯 27、Who says 28、谁说的 29、Fuck off 30、滚蛋 31、Drop dead 32、去死吧 33、You are out of your mind 34、你脑子有病 35、You have a lot of nerve 36、脸皮真厚 37、You stupid jerk! 38、你这蠢猪! 39、Shut up 40、闭嘴 41、对不起，敬个礼。放个P，送给你！！！ 42、出门踩到翔 43、loW（不区分大小写） 44、小人，真小人 45、猪狗不如 46、无语 47、蠢猪 48、呵呵 49、nishixiaogou 50、真狗 51、老子 52、滚粗 53、出门被车撞死， 54、喝水被.呛死， 55、吃饭被1噎死， 56、给你个大嘴巴子（后面没有了，没有了，没有了...）游泳被水撑死，回家楼塌被石头压死，下雨出门被雷劈死，玩电脑漏电被点死，去看大象被踩死，去买狗被狗咬死，去买猫被猫抓死，去买兔子被拉的一身屎");
        final Map<String, String> all = action.findAll();
        final Collection<String> disjunction = CollUtil.disjunction(strings, all.keySet());
        System.out.println(disjunction);
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
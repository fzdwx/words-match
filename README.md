# words match

字符串匹配,使用DFA算法实现.目前提供了两种匹配方式:

1.精确匹配 [AccurateWordsMatcher](https://github.com/fzdwx/words-match/blob/f0b47ae55372566dc89995d7df5b8a5853d4774d/src/main/java/com/huofutp/words/internal/dfa/AccurateWordsMatcher.java)

- content中的内容与word完全匹配

```text
word: hello
    content: hello world -> true
    content: HEllo world -> false
word： 你好hello
    content: 你好hello world -> true
    content: 你好HEllo world -> false 
```

2.模糊匹配 [FuzzyWordsMatcher](https://github.com/fzdwx/words-match/blob/f0b47ae55372566dc89995d7df5b8a5853d4774d/src/main/java/com/huofutp/words/internal/dfa/FuzzWordsMatcher.java)

- word只能为中文或英文
- content不区分英文大小写

```text
word: hhhasd
    content: h1...h1h1a1S1D -> true
word: qweFJAKf
    content: qWefJAkf -> true
```

3.[MixWordsMatcher](https://github.com/fzdwx/words-match/blob/f0b47ae55372566dc89995d7df5b8a5853d4774d/src/main/java/com/huofutp/words/internal/dfa/MixWordsMatcher.java)

- 精确和模糊结合

## usage

```java
  private static Collection<String> words=new HashSet<String>(){
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

final String content="中1国1人,民,一zzz举,he*l l.oQWE你好aa  qWefJAkf,h1...h1h1a1S1D";

final WordsMatcher accurate=WordsMatcher.accurate(words);
final WordsAction action=this.accurate.action(this.content);
final Map<String, String> all=action.findAll();
```

## todo

- [ ] 简体繁体
- [ ] 半角圆角
- [ ] 若一个word在content中匹配到了多次,如何保存?
    - 现在是覆盖,保留最后一个
- [ ] replace 方法不能替换*
- [ ] more
# words match

字符串匹配,使用DFA算法实现,word不支持有空格.

| -       |     |      |      |                 |
|---------|-----|------|------|-----------------|
| word    | 你好  | 你好吗  | 你好吗  | helloworld      |
| content | 你好啊 | 你好好吗 | 你好，吗 | hello     world |
| 模糊匹配    | √   | ×    | √    | √               |
| 精确匹配    | √   | ×    | ×    | ×               |

## Rule

- 中英混合word: 精确匹配
- word最小长度: 2

## usage

```xml

<dependency>
    <groupId>io.github.fzdwx</groupId>
    <artifactId>words-match</artifactId>
    <version>0.09</version>
</dependency>
```

code

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

[more](https://github.com/fzdwx/words-match/blob/159c5dfe0a8c58b8db9e3bb69e3e24c7312a5b1e/src/test/java/io/github/fzdwx/words/WordsMatcherTest.java)

## todo

- [ ] 简体繁体
- [ ] 半角圆角
- [ ] 若一个word在content中匹配到了多次,如何保存?
    - 现在是覆盖,保留最后一个
- [x] replace 方法不能替换*
- [ ] more
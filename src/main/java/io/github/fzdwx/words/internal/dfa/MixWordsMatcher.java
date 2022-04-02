package io.github.fzdwx.words.internal.dfa;

import io.github.fzdwx.lambada.Tuple;
import io.github.fzdwx.lambada.internal.Tuple2;
import io.github.fzdwx.words.WordsMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * mixed words matcher
 *
 * @author <a href="mailto:likelovec@gmail.com">韦朕</a>
 * @apiNote 混合了精确和模糊匹配的词语匹配器
 * @date 2022/3/11 11:31
 */
public class MixWordsMatcher implements DFAWordsMatcher {

    private WordsMatcher accurate;
    private WordsMatcher fuzz;

    public MixWordsMatcher(final Tuple2<List<String>, List<String>> divert) {
        this(divert.v1, divert.v2);
    }

    private MixWordsMatcher(final Collection<String> words) {
        this(divert(words));
    }

    public MixWordsMatcher(final Collection<String> accurateCollection, final Collection<String> fuzzCollection) {
        if (accurateCollection!= null && accurateCollection.size() > 0) {
            this.accurate = WordsMatcher.accurate(accurateCollection);
        } else {
            this.accurate = null;
        }

        if (fuzzCollection!= null && fuzzCollection.size() > 0) {
            this.fuzz = WordsMatcher.fuzz(fuzzCollection);
        } else {
            this.fuzz = null;
        }
    }

    public static MixWordsMatcher create(final Collection<String> words) {
        return new MixWordsMatcher(words);
    }

    public static MixWordsMatcher create(final Collection<String> accurateCollection, final Collection<String> fuzzCollection) {
        return new MixWordsMatcher(accurateCollection, fuzzCollection);
    }

    @Override
    public boolean process(final boolean partMatch, final String content, final WordsMatcher.Handler handle) {
        boolean processor = false;
        if (this.accurate != null) {
            processor = this.accurate.process(partMatch, content, handle);
        }

        if (this.fuzz != null) {
            processor = this.fuzz.process(partMatch, content, handle);
        }
        return processor;
    }

    @Override
    public boolean put(final String word) {
        if (WordsMatcher.hasChAndEn(word)) {
            if (this.accurate == null) {
                this.accurate = WordsMatcher.accurate(word);
            }
            return this.accurate.put(word);
        }

        if (this.fuzz == null) {
            this.fuzz = WordsMatcher.fuzz(word);
        }
        return this.fuzz.put(word);
    }

    @Override
    public MixWordsMatcher refresh(final Collection<String> words) {
        final Tuple2<List<String>, List<String>> tuple = divert(words);
        return refresh(tuple.v1, tuple.v2);
    }

    public MixWordsMatcher refresh(final Collection<String> accurateCollection, final Collection<String> fuzzCollection) {
        if (this.accurate == null) {
            this.accurate = WordsMatcher.accurate(accurateCollection);
        } else {
            this.accurate = accurate.refresh(accurateCollection);
        }

        if (this.fuzz == null) {
            this.fuzz = WordsMatcher.fuzz(fuzzCollection);
        } else {
            this.fuzz = fuzz.refresh(fuzzCollection);
        }

        return this;
    }

    @Override
    public boolean hasWords() {
        return (this.accurate.hasWords() || this.fuzz.hasWords());
    }

    private static Tuple2<List<String>, List<String>> divert(final Collection<String> words) {
        if (words == null) {
            return new Tuple2<>(new ArrayList<>(), new ArrayList<>());
        }

        final ArrayList<String> ac = new ArrayList<>();
        final ArrayList<String> fuzz = new ArrayList<>();
        for (final String word : words) {

            int chCount = 0;
            int enCount = 0;
            final char[] chars = word.toCharArray();
            for (final char c : chars) {
                if (WordsMatcher.isChinese(c)) {
                    chCount++;
                }

                if (WordsMatcher.isLetter(c)) {
                    enCount++;
                }
            }

            if (chars.length == chCount || chars.length == enCount) {
                fuzz.add(word);
                continue;
            }

            ac.add(word);
        }
        return Tuple.of(ac, fuzz);
    }
}
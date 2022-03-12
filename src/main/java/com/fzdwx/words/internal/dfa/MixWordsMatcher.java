package com.fzdwx.words.internal.dfa;

import com.fzdwx.words.WordsMatcher;
import com.fzdwx.words.lambada.internal.Tuple2;
import com.fzdwx.words.lambada.Seq;

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

    private MixWordsMatcher(final Collection<String> words) {
        final Tuple2<List<String>, List<String>> tuple = this.divert(words);

        final List<String> v1 = tuple.v1;
        if (v1.size() > 0) {
            this.accurate = WordsMatcher.accurate(v1);
        } else {
            this.accurate = null;
        }

        final List<String> v2 = tuple.v2;
        if (v2.size() > 0) {
            this.fuzz = WordsMatcher.fuzz(v2);
        } else {
            this.fuzz = null;
        }
    }

    public static WordsMatcher create(final Collection<String> words) {
        return new MixWordsMatcher(words);
    }

    @Override
    public boolean process(final boolean partMatch, final String content, final Handler handle) {
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
        if (this.hasChAndEn(word)) {
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
    public void refresh(final Collection<String> words) {
        final Tuple2<List<String>, List<String>> tuple = this.divert(words);

        final List<String> v1 = tuple.v1;
        if (v1.size() > 0) {
            this.accurate = WordsMatcher.accurate(v1);
        } else {
            this.accurate = null;
        }

        final List<String> v2 = tuple.v2;
        if (v2.size() > 0) {
            this.fuzz = WordsMatcher.fuzz(v2);
        } else {
            this.fuzz = null;
        }
    }

    private Tuple2<List<String>, List<String>> divert(final Collection<String> words) {
        return Seq.of(words).collect(this::hasChAndEn);
    }
}
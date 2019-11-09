package ec.tec.ami.data.dao.filter;

import java.util.regex.Pattern;

import ec.tec.ami.model.Post;

public class WordFilter implements Filter{

    private String word;

    public WordFilter(String word) {
        this.word = word.toLowerCase();
        this.word = ".*"+Pattern.quote(this.word)+".*";
    }

    @Override
    public boolean accept(Post post) {
        return post.getDescription().toLowerCase().matches(word);
    }
}

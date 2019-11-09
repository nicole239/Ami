package ec.tec.ami.data.dao.filter;

import ec.tec.ami.model.Post;

public class WordFilter implements Filter{

    private String word;

    public WordFilter(String word) {
        this.word = word;
    }

    @Override
    public boolean accept(Post post) {
        return post.getDescription().contains(word);
    }
}

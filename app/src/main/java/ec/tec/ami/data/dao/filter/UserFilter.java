package ec.tec.ami.data.dao.filter;

import ec.tec.ami.model.Post;

public class UserFilter implements Filter{

    String userEmail;

    public UserFilter(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public boolean accept(Post post) {
        return userEmail.equals(post.getUser());
    }
}

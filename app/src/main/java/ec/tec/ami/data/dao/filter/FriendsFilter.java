package ec.tec.ami.data.dao.filter;

import java.util.List;

import ec.tec.ami.model.Post;
import ec.tec.ami.model.User;

public class FriendsFilter implements Filter {

    List<User> friends;

    public FriendsFilter(List<User> friends) {
        this.friends = friends;
    }


    @Override
    public boolean accept(Post post) {
        for(User u:friends)
            if(u.getEmail().equals(post.getUser()))
                return true;
        return false;
    }
}

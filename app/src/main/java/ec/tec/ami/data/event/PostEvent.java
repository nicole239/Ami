package ec.tec.ami.data.event;

import java.util.List;

import ec.tec.ami.model.Post;
import ec.tec.ami.model.User;

public class PostEvent {
    public void onFailure(Exception e){};
    public void onSuccess(List<Post> users){};
    public void onSuccess(Post user){};
}

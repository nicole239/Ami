package ec.tec.ami.data.dao.filter;

import ec.tec.ami.model.Post;

public interface Filter {

    boolean accept(Post post);
}

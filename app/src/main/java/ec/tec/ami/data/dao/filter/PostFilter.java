package ec.tec.ami.data.dao.filter;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.model.Post;

public class PostFilter implements Filter {

    private List<Filter> filters;
    private FilterType type;

    public PostFilter(FilterType type) {
        this.filters = new ArrayList<>();
        this.type = type;
    }

    @Override
    public boolean accept(Post post) {
        boolean accept = type == FilterType.AND;

        for(Filter filter : filters){
            switch (type){
                case OR:
                    accept = accept || filter.accept(post);
                    if(accept)
                        return true;
                case AND:
                    accept = accept && filter.accept(post);
                    if(!accept)
                        return false;
            }
        }
        return accept;
    }

    public void addFilter(Filter filter){
        filters.add(filter);
    }

    public enum FilterType{
        OR, AND;
    }

}

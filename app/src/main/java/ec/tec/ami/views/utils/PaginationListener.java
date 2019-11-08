package ec.tec.ami.views.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationListener extends RecyclerView.OnScrollListener {

    public static final int PAGE_START = 1;
    public static final int PAGE_SIZE = 2;
    private LinearLayoutManager layoutManager;

    public PaginationListener(@NonNull LinearLayoutManager layoutManager){
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosititon = layoutManager.findFirstVisibleItemPosition();

        if(!isLoading() && !isLastPage()){
            if((visibleItemCount + firstVisibleItemPosititon) >= totalItemCount && firstVisibleItemPosititon >=0 && totalItemCount >= PAGE_START){
                loadMoreItems();
            }
        }
    }

    protected abstract void loadMoreItems();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}

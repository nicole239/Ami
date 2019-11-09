package ec.tec.ami.data.dao;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ec.tec.ami.data.dao.filter.Filter;
import ec.tec.ami.model.Post;

public class PostCursor {

    private int fetchSize = 10;
    private List<String> friends;
    private int lastItem;
    private long lastDate = -1l;
    private int count;
    private int itemsFetched;
    private String userEmail;
    private String laskKey;
    private PostEvent event;
    private FirebaseDatabase database;
    private boolean empty = false;
    private Filter filter;
    Object lock = new Object();

    public PostCursor(List<String> friends, String userEmail, int count) {
        this.friends = friends;
        this.userEmail = userEmail;
        this.count = count;
        this.fetchSize = count;
        init();
    }

    private void init(){
        database = FirebaseDatabase.getInstance();
    }

    public boolean isValid(Post post){
//        if(post.getUser().equals(userEmail)){
//            return true;
//        }
//        for(String friend : friends){
//            if(post.getUser().equals(friend)){
//                return true;
//            }
//        }
        return filter == null || filter.accept(post);
//        return true;
    }

    public void setEvent(PostEvent event) {
        this.event = event;
    }

    public void next(){
        new PostFetcher().execute();
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    private class PostFetcher extends AsyncTask <Void,Void,List<Post>>{
        @Override
        protected List<Post> doInBackground(Void... voids) {


            final LinkedList<Post> posts = new LinkedList<>();
            DatabaseReference reference = database.getReference().child("posts");
            synchronized (lock){
                while (itemsFetched < count){
                    final LinkedList<Post> tmp = new LinkedList<>();
                    final boolean[] first = {true};
                    final String key = laskKey;
                    Query query = null;
                    if(lastDate == -1l) {
                        query = reference.orderByChild("date/time").limitToLast(fetchSize);
                    }else{
                        query = reference.orderByChild("date/time").endAt((double)lastDate).limitToLast(fetchSize - itemsFetched + 1);
                    }
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            synchronized (lock){
                                if(dataSnapshot.getChildrenCount()>0){
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        if(!snapshot.getKey().equals(key)){
                                            Post post = snapshot.getValue(Post.class);
                                            if(first[0]) {
                                                lastDate = post.getDate().getTime();
                                                laskKey = snapshot.getKey();
                                                first[0] = false;
                                            }
                                            if(isValid(post)){
                                                post.setId(snapshot.getKey());
                                                tmp.addFirst(post);
                                                itemsFetched++;
                                            }
                                        }else{
                                            if(dataSnapshot.getChildrenCount()==1){
                                                empty = true;
                                                itemsFetched = count + 1;
                                            }
                                        }

                                    }
                                    posts.addAll(tmp);
                                    lock.notify();
                                }else{
                                    empty = true;
                                    itemsFetched = count + 1;
                                    lock.notify();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            itemsFetched = count + 1;
                            lock.notify();
                        }
                    });
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Log.e("err-wait",e.getMessage());
                    }
                }
            }

            return posts;
        }

        @Override
        protected void onPostExecute(List<Post> posts) {
            itemsFetched = 0;
            if(empty && posts.size() == 0){
                event.onEmptyData();
                empty = false;
            }else
                event.onDataFetched(posts);
        }
    }

    public interface PostEvent{
        void onDataFetched(List<Post> posts);
        void onFailure(Exception e);
        void onEmptyData();
    }

}

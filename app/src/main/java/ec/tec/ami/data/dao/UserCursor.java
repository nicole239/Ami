package ec.tec.ami.data.dao;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ec.tec.ami.data.dao.filter.Filter;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.User;

public class UserCursor {

    private int fetchSize = 10;

    private String lastEmail = "";
    private int count;
    private int itemsFetched;
    private String userEmail;
    private String lastKey;
    private UserCursoEvent event;
    private FirebaseDatabase database;
    private boolean empty = false;
    private Filter filter;
    private String regx;

    Object lock = new Object();

    public UserCursor(int count, String regx) {
        this.count = count;
        this.fetchSize = count;
        this.regx = regx.toLowerCase();
        this.regx = ".*"+ Pattern.quote(this.regx)+".*";
        init();
    }

    private void init(){
        database = FirebaseDatabase.getInstance();
    }

    public boolean isValid(User user){
        return user.getName().toLowerCase().matches(regx) || user.getLastNameA().toLowerCase().matches(regx) || user.getLastNameB().toLowerCase().matches(regx);
    }

    public void setEvent(UserCursoEvent event) {
        this.event = event;
    }

    public void next(){
        new UserFetcher().execute();
    }

    private class UserFetcher extends AsyncTask <Void,Void,List<User>>{
        @Override
        protected List<User> doInBackground(Void... voids) {


            final List<User> posts = new ArrayList<>();
            DatabaseReference reference = database.getReference().child("users");
            synchronized (lock){
                while (itemsFetched < count){
                    final boolean[] first = {true};
                    final String key = lastKey;
                    Query query = null;
                    if(lastEmail.isEmpty()) {
                        query = reference.orderByChild("lastNameA").limitToLast(fetchSize);
                    }else{
                        query = reference.orderByChild("lastNameA").endAt(lastEmail).limitToLast(fetchSize - itemsFetched + 1);
                    }
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            synchronized (lock){
                                if(dataSnapshot.getChildrenCount()>0){
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        if(!snapshot.getKey().equals(key)){
                                            User user = snapshot.getValue(User.class);
                                            if(first[0]) {
                                                lastEmail = user.getLastNameA();
                                                lastKey = snapshot.getKey();
                                                first[0] = false;
                                            }
                                            if(isValid(user)){
                                                posts.add(user);
                                                itemsFetched++;
                                            }
                                        }else{
                                            if(dataSnapshot.getChildrenCount()==1){
                                                empty = true;
                                                itemsFetched = count + 1;
                                            }
                                        }

                                    }
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
        protected void onPostExecute(List<User> users) {
            itemsFetched = 0;
            if(empty && users.size() == 0){
                event.onEmptyData();
                empty = false;
            }else
                event.onDataFetched(users);
        }
    }

    public interface UserCursoEvent {
        void onDataFetched(List<User> users);
        void onFailure(Exception e);
        void onEmptyData();
    }

}

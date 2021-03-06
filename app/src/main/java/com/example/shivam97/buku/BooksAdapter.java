package com.example.shivam97.buku;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.shivam97.buku.Buku.mDatabase;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    private Context ctx;
    private ProgressBar progressBar;
    private ArrayList<String> titles;
    private ArrayList<String> keys;
    private ArrayList<String> authors;
     private ArrayList<String> ratings;
    private ArrayList<String> url;

    public void setData(ArrayList<String> titles,ArrayList<String> authors,ArrayList<String> ratings){
        this.titles = titles;
        this.authors = authors;
        this.ratings = ratings;

        this.notifyDataSetChanged();
    }
    public void addData(String title,String author,String rating){
        titles.add(title); authors.add(author);
        //ratings.add(rating);
        url=new ArrayList<>();
        this.notifyDataSetChanged();

    }

    void addKey(String key){
        initiateFirebase(key);
    }

    BooksAdapter(Context context,ProgressBar progressBar){
        ctx= context;this.progressBar=progressBar;
        titles =new ArrayList<>();
        authors=new ArrayList<>();
        url=new ArrayList<>();
        keys=new ArrayList<>();
        ratings=new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.book_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final int i=holder.getAdapterPosition();
        Picasso.get().load(url.get(i)).into(holder.image);
        holder.title.setText(titles.get(i));
        holder.author.setText(authors.get(i));
        holder.rating.setText(ratings.get(i));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1=new Intent(ctx,BookDeatails.class);
                i1.putExtra("title",titles.get(i));
                i1.putExtra("author",authors.get(i));
                i1.putExtra("index",keys.get(i));
                i1.putExtra("url",url.get(i));
                i1.putExtra("rating",ratings.get(i));
                ctx.startActivity(i1);

            }
        });

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title,author,rating;
        ViewHolder(View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.book_img);
            title=itemView.findViewById(R.id.book_name);
            author=itemView.findViewById(R.id.book_author);
            rating=itemView.findViewById(R.id.book_rating);

        }

    }

    private void initiateFirebase(final String key) {

        final String[] s = new String[4];

        mDatabase.child("bookTitle").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                s[0] =dataSnapshot.getValue().toString();
                mDatabase.child("imageUrlL").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        s[1]=dataSnapshot.getValue().toString();
                        mDatabase.child("bookAuthor").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                s[2]= dataSnapshot.getValue().toString();
                               mDatabase.child("rating").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       s[3]=dataSnapshot.getValue().toString();
                                       titles.add(s[0]);
                                       url.add(s[1]);
                                       authors.add(s[2]);
                                       ratings.add(s[3]);
                                       keys.add(key);
                                       notifyDataSetChanged();
                                       progressBar.setVisibility(View.GONE);
                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                   }
                               });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

}




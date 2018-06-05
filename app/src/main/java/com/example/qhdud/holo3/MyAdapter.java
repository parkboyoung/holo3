package com.example.qhdud.holo3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.RealmResults;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private RealmResults<Member> mDataset; //MainActivity에 item class를 정의해 놓았음

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 사용될 항목들 선언
        public TextView mName;
        public TextView mAge;
        public TextView mEmail;
        public ImageView mPhoto;

        public ViewHolder(View v) {
            super(v);

            mName = (TextView) v.findViewById(R.id.info_text);
            mAge = (TextView) v.findViewById(R.id.info_age);
            mEmail = (TextView) v.findViewById(R.id.info_email);
            mPhoto = (ImageView)v.findViewById(R.id.ivPreview);
        }
    }

    // 생성자 - 넘어 오는 데이터파입에 유의해야 한다.
    public MyAdapter(RealmResults<Member> myDataset) {
        mDataset = myDataset;
    }


    //뷰홀더
    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mName.setText(mDataset.get(position).getName());
        holder.mAge.setText(String.valueOf(mDataset.get(position).getAge())+"세"); //int를 가져온다는점 유의
        holder.mEmail.setText(mDataset.get(position).getEmail());
//1
//        Intent intent = ((Activity)context).getIntent();
//        byte[] image =intent.getByteArrayExtra("image");
//        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
//        holder.mPhoto.setImageBitmap(bitmap);
   //2
        // holder.mPhoto.setImageURI((mDataset.get(position).getProfilePhoto()));
        //setImageBitmap(mDataset.get(position).getPhoto());
       // Glide.with(intent).load(mDataset.get(position).getProfilePhoto()).centerCrop().into(holder.mPhoto);


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}

package com.example.qhdud.holo3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;

public class JoinActivity extends AppCompatActivity {

    TextView tv_name, tv_age, tv_email, tv_password;
    ImageView ivPreview;
    Button bt_joinOK, bt_joinCancel, bt_Select;
    Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_password = (TextView) findViewById(R.id.tv_password);
        bt_joinOK = (Button) findViewById(R.id.bt_join_ok);
        bt_joinCancel = (Button) findViewById(R.id.bt_join_cancel);
        bt_Select = (Button) findViewById(R.id.bt_photo);
        ivPreview = (ImageView)findViewById(R.id.ivPreview);
        //암호화
        //Password를 암호화 해서 입력해야 하는데 이건 다음에 알아 보자.

        //Realm 인스턴스를 얻는다. 액티비티 마다 다시 정의해 줘야 하나???
        realm = Realm.getDefaultInstance();

        //회원가입 확인
        bt_joinOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_name.getText().toString().equals("")|| tv_email.getText().toString().equals("")||tv_password.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "필수입력 항목이 비어 있음", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    insertDatabase();

                    //돌려보내자.
                    Intent intent = new Intent();
                    //2차추가
                    Bitmap sendBitmap = BitmapFactory.decodeResource(getResources(), R.id.ivPreview);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("image",byteArray);
                    startActivity(intent);

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
        //이미지 선택
        bt_Select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //사진 갤러리 호출
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });




        //취소
        bt_joinCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });


    }
    //입력
    private void insertDatabase() {
        //방법2 : 자동으로 begin/commit를 관리하는 방법, 에러가 발생시 자동 캔슬된다.
        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                //increment index
                //DB에서 id최대값을 가져와서 1을 더해서 입력할 id값으로 넣는다.
                Number num = realm.where(Member.class).max("id");
                int nextID;
                if (num == null) {
                    nextID = 1;
                } else {
                    nextID = num.intValue() + 1;
                }
                Member member = realm.createObject(Member.class, nextID);//primarykey 지정한 경우
                member.setName(tv_name.getText().toString());
                Integer i = Integer.parseInt("" + tv_age.getText());
                member.setAge(i);
                member.setEmail(tv_email.getText().toString());
                member.setPassword(tv_password.getText().toString());
            }
        });
    }
    //모든 realm 인스턴스들을 닫아 준다. 중요
    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
        //
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {
            if(resultCode == Activity.RESULT_OK){
                //data 에서 uri를 가져온다.
                //미리보기로 출력하기
                //---------------

                String str = data.getDataString();
                //Uri uri= (data.getData());
                Glide.with(this) //context 부분을 어떻게 처리하는지 유의하자.
                        .load(str) //실시간 데이터로 교체할것.
                        .centerCrop()
                        .placeholder(R.color.cardview_dark_background)
                        .into(ivPreview);
            }
        }
    }
    ///------------

}

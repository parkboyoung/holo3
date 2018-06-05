package com.example.qhdud.holo3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    Realm realm;
    Button btDelete, btJoin, btLogin;
    TextView tvNotice;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RealmQuery<Member> query;
    private RealmResults<Member> results;
    private boolean isLogin = false;
    private boolean isAdmin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            //Realm 초기화
            Realm.init(this);
            //configuration 설정(중간에 설정을 다르게 하는게 들어갈 수 있다)
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(realmConfiguration);

            btDelete = (Button)findViewById(R.id.bt_delete);
            btJoin = (Button) findViewById(R.id.bt_join);
            btLogin = (Button) findViewById(R.id.bt_login);
            tvNotice = (TextView) findViewById(R.id.tv_notice);

            btDelete.setOnClickListener(this);
            btJoin.setOnClickListener(this);
            btLogin.setOnClickListener(this);

            //Realm 인스턴스를 얻는다.
            realm = Realm.getDefaultInstance();
            //쿼리
            query = realm.where(Member.class);
            results = query.findAll();
            results = results.sort("id", Sort.DESCENDING); //내림차순으로 변경

            //리스너추가 - 지켜 보고 있다.
            results.addChangeListener(new RealmChangeListener<RealmResults<Member>>() {
                @Override
                public void onChange(RealmResults<Member> element) {
                    //회원목록을 갱신한다. 다른 방법은 없나?
                    //Toast.makeText(MainActivity.this, "목록을 갱신합니다.", Toast.LENGTH_SHORT).show();
                    mAdapter = new MyAdapter(results);
                    mRecyclerView.setAdapter(mAdapter);
                }
            });

            //어답터 설정
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);//옵션
            //Linear layout manager 사용
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            //어답터 세팅
            mAdapter = new MyAdapter(results);
            mRecyclerView.setAdapter(mAdapter);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.bt_delete:
                    //삭제
                    realm.executeTransaction(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm) {
                            Member mMember = realm.where(Member.class).findFirst(); //첫번재 데이터를 찾아서 지운다.
                            if (mMember != null){
                                //삭제
                                mMember.deleteFromRealm();
                            } else {
                                return;
                            }
                        }
                    });
                    break;
                case R.id.bt_join:
                    //추가하기 - 새액티비티에서 처리하도록 수정예정
                    Intent joinIntent = new Intent(this, JoinActivity.class);
                    startActivityForResult(joinIntent, 0);

                    break;
                case R.id.bt_login:
                    //로그인 - 새 액티비티 열어서 처리
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    startActivityForResult(loginIntent, 1);
                    break;
            }
        }

        //인텐트 수행후 결과처리부
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            //회원가입 결과 처리부
            case 0:
                if (resultCode == RESULT_OK){
                    Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show();
                }
                break;
            //로그인 결과 처리부
            case 1:
                if(resultCode == RESULT_OK) {
                    //Intent mintent = getIntent();
                    String mName = data.getStringExtra("Name");
                    String mEmail = data.getStringExtra("Email");
                    tvNotice.setText(mName+"("+mEmail+")으로 로그인 했습니다.");
                    isLogin = true;
                } else {
                    Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    //모든 realm 인스턴스들을 닫아 준다. 메모리관리 중요
    @Override
    public void onDestroy(){
        super.onDestroy();
        //정리하기
        realm.removeAllChangeListeners();
        realm.close();
    }
}

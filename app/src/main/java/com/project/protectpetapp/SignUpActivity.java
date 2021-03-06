package com.project.protectpetapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    private EditText sign_name, sign_email, sign_pw, sign_pwck;
    private Button signup;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);



        //파이어베이스 접근 설정
        firebaseAuth = FirebaseAuth.getInstance();

        //EditText
        sign_name = (EditText) findViewById(R.id.signup_name);
        sign_email = (EditText) findViewById(R.id.signup_email);
        sign_pw = (EditText) findViewById(R.id.signup_pw);
        sign_pwck = (EditText) findViewById(R.id.signup_pwck);

        //Button
        back = (ImageView) findViewById(R.id.back);
        signup = (Button) findViewById(R.id.signup);

        //회원가입
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //입력 정보 가져오기
                final String email = sign_email.getText().toString();
                final String password = sign_pw.getText().toString();
                String pwcheck = sign_pwck.getText().toString();

                if (password.equals(pwcheck)) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setMessage("가입중입니다...");
                    mDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                            //가입 성공시
                            if (task.isSuccessful()) {
                                mDialog.dismiss();

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                String name = sign_name.getText().toString().trim();
                                String pw = sign_pw.getText().toString().trim();


                                //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                                HashMap<Object,String> hashMap = new HashMap<>();

                                hashMap.put("uid",uid);
                                hashMap.put("email",email);
                                hashMap.put("name",name);
                                hashMap.put("password", pw);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users").child(uid).child("Userinfo");
                                reference.setValue(hashMap);


                                //가입이 이루어져을시 가입 화면을 빠져나감.
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(SignUpActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                return;  //해당 메소드 진행을 멈추고 빠져나감.

                            }

                        }
                    });

                    //비밀번호 오류시
                }else{
                    Toast.makeText(SignUpActivity.this, "비밀번호가 틀렸습니다.\n 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;


                }
            }
        });




        //뒤로가기 눌렀을 때 이벤트
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}



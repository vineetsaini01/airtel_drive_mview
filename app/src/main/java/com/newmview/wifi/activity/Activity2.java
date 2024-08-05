//package com.mcpsinc.mview.activity;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import com.mcpsinc.mview.R;
//import com.mcpsinc.mview.fragment.Fragment1;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//public class Activity2 extends AppCompatActivity {
//    Button firstFragmentBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_2);
//        firstFragmentBtn = findViewById(R.id.fragment1btn);
//        firstFragmentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceFragment(new Fragment1());
//            }
//        });
//    }
//
//    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frameLayout, fragment);
//        fragmentTransaction.commit();
//
//
//    }
//}

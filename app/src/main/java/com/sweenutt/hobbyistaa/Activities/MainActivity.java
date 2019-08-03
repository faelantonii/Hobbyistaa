package com.sweenutt.hobbyistaa.Activities;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sweenutt.hobbyistaa.Fragments.AkunFragment;
import com.sweenutt.hobbyistaa.Fragments.HomeFragment;
import com.sweenutt.hobbyistaa.Fragments.PostFragment;
import com.sweenutt.hobbyistaa.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportActionBar().setTitle("Halaman Utama");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frm, new HomeFragment()).commit();
                    mTextMessage.setText("");
                    return true;
                case R.id.navigation_post:
                    getSupportActionBar().setTitle("Post");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frm, new PostFragment()).commit();
                    mTextMessage.setText("");
                    return true;
                case R.id.navigation_akun:
                    getSupportActionBar().setTitle("Akun");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frm, new AkunFragment()).commit();
                    mTextMessage.setText("");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setTitle("Halaman Utama");
        getSupportFragmentManager().beginTransaction().replace(R.id.frm, new HomeFragment()).commit();
        mTextMessage.setText("");
    }

}

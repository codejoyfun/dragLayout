package com.lxtx.mydraglayout;

import android.os.Bundle;

import com.lxtx.recyclerView.Model;
import com.lxtx.recyclerView.MyAdapter;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Model> data = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            data.add(new Model("测试标题" + i));
        }
        final MyAdapter adapter = new MyAdapter(data);
        rv.setAdapter(adapter);
//        findViewById(R.id.tv1).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"t1被点击了",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}

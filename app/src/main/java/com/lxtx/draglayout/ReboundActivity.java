package com.lxtx.draglayout;

import android.os.Bundle;

import com.lxtx.draglayout.adapter.MyAdapter;
import com.lxtx.draglayout.model.Model;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReboundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebound);

        RecyclerView rv1 = findViewById(R.id.rv1);
        rv1.setLayoutManager(new GridLayoutManager(this, 4));
        ArrayList<Model> data1 = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            data1.add(new Model("测试标题" + i));
        }
        final MyAdapter adapter1 = new MyAdapter(data1);
        rv1.setAdapter(adapter1);
    }
}

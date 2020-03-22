package com.lxtx.mydraglayout;

import android.os.Bundle;

import com.lxtx.mydraglayout.adapter.MyAdapter;
import com.lxtx.mydraglayout.model.Model;
import com.lxtx.mydraglayout.view.DragLayout;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rv1 = findViewById(R.id.rv1);
        rv1.setLayoutManager(new GridLayoutManager(this, 4));
        ArrayList<Model> data1 = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            data1.add(new Model("测试标题" + i));
        }
        final MyAdapter adapter1 = new MyAdapter(data1);
        rv1.setAdapter(adapter1);

        RecyclerView rv2 = findViewById(R.id.rv2);
        rv2.setLayoutManager(new SpeedLayoutManager(this).setSpeedRatio(1f));
        ArrayList<Model> data2 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data2.add(new Model("内容" + i));
        }
        final MyAdapter adapter2 = new MyAdapter(data2);
        rv2.setAdapter(adapter2);

        DragLayout dragLayout = findViewById(R.id.drag_layout);
        dragLayout.openBottom(false);
    }
}

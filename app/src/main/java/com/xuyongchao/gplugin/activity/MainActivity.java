package com.xuyongchao.gplugin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.xuyongchao.gplugin.MainActivity_ViewBinding;
import com.xuyongchao.gplugin.R;
import com.xuyongchao.gplugin.an.BindView;
import com.xuyongchao.gplugin.utils.BindUtils;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    public TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindUtils.bind(this);
        tv.setText("第二个activity");

    }
}
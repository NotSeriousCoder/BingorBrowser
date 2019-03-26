package com.bingor.bingorborwser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bingor.browserlib.BrowserConstant;
import com.bingor.browserlib.view.activity.BrowserActivity;

public class MainActivity extends AppCompatActivity {
    private EditText etUrl;
    private Button btGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUrl = findViewById(R.id.et_url);
        btGo = findViewById(R.id.bt_go);

        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(MainActivity.this, BrowserActivity.class)
                                .putExtra(BrowserConstant.KEY_URL, etUrl.getText().toString())
                );
            }
        });
    }
}

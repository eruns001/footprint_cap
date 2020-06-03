package com.example.capston

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_main_to_infor.setOnClickListener{
            val intent_toinfor = Intent(this, Activity_infor::class.java)
            startActivity(intent_toinfor)
        }

        btn_main_to_map.setOnClickListener {
            val intent_tomap = Intent(this, Activity_map::class.java)
            intent_tomap.putExtra("write_1", false)
            startActivity(intent_tomap)
        }
    }
}

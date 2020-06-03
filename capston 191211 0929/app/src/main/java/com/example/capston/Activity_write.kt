package com.example.capston

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_write.*
import org.jetbrains.anko.image

var bitmap : Bitmap ?= null

var takecam : Boolean = false

class Activity_write : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        btn_write_cancel.setOnClickListener {
            val itn = Intent(this, Activity_map::class.java)
            startActivity(itn)
            finish()
        }

        btn_write_done.setOnClickListener {
            val intent_write = Intent(this, Activity_map::class.java)
            val ttl = title_write.text.toString()
            val cont = edittxt_write.text.toString()

            intent_write.putExtra("write_1", true)
            if(takecam)
            {
                intent_write.putExtra("write_2", bitmap)
                intent_write.putExtra("write_3", ttl)
                intent_write.putExtra("write_4", cont)
                startActivity(intent_write)
                finish()
            }
            else
            {
                val bitmapdraw = resources.getDrawable(R.drawable.beachflag) as BitmapDrawable
                val bm = bitmapdraw.bitmap
                bitmap = Bitmap.createScaledBitmap(bm, 200, 200, false)
                intent_write.putExtra("write_2", bitmap)
                intent_write.putExtra("write_3", ttl)
                intent_write.putExtra("write_4", cont)
                startActivity(intent_write)
                finish()
            }
        }

    }

    fun showCamerBtn(view: View?) {
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
            bitmap = data?.getParcelableExtra("data") as Bitmap
            image_write.setImageBitmap(bitmap)
            takecam = true

            //val bitmap = data!!.getParcelableArrayExtra("data") as Bitmap
            //imageView2.setImageBitmap(bitmap)
        }
    }
}

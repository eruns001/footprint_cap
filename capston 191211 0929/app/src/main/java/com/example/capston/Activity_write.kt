package com.example.capston

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_write.*
import java.io.ByteArrayOutputStream

var bitmap : Bitmap ?= null
var takecam : Boolean = false
var FilePathUri : Uri? = null

class Activity_write : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val storage : StorageReference = FirebaseStorage.getInstance().getReference()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        //val bundle = intent.getParcelableExtra<Bundle>("position")
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        btn_write_cancel.setOnClickListener {
            val itn = Intent(this, Activity_map::class.java)
            startActivity(itn)
            finish()
        }

        btn_write_done.setOnClickListener {
            val intent_write = Intent(this, Activity_map::class.java)
            val spotname = title_write.text.toString()
            val cont = edittxt_write.text.toString()
            val uri = "https://console.firebase.google.com/project/capston-map-empty-98424/storage/capston-map-empty-98424.appspot.com/files?hl=ko"

            database.reference.child(spotname).child("Comments").setValue(cont)
            database.reference.child(spotname).child("Position").child("latitude").setValue(latitude)
            database.reference.child(spotname).child("Position").child("longitude").setValue(longitude)
            database.reference.child(spotname).child("Image URI").setValue(uri)
            storage.child(spotname).child("picture.jpg")

            intent_write.putExtra("write_1", true)
            if(takecam)
            {
                intent_write.putExtra("write_2", bitmap)
                intent_write.putExtra("write_3", spotname)
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
                intent_write.putExtra("write_3", spotname)
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

    private fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val datas = bytes.toByteArray()
        var uploadTask = storage.putBytes(datas)
        val path = MediaStore.Images.Media.insertImage(
            context.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if (data != null) {
                FilePathUri = data.data
            }
            bitmap = data?.getParcelableExtra("data") as Bitmap
            FilePathUri = getImageUri(applicationContext, bitmap!!)
            image_write.setImageBitmap(bitmap)
            takecam = true

            //val bitmap = data!!.getParcelableArrayExtra("data") as Bitmap
            //imageView2.setImageBitmap(bitmap)
        }
    }
}
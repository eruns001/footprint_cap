package com.example.capston

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton


var hr :Int = 0
var latLng = LatLng(35.076168,129.089591) //해양대 좌표
var get = false


class Activity_map : AppCompatActivity(), OnMapReadyCallback {
    //private var mMap: GoogleMap? = null
    private lateinit var mMap: GoogleMap

    //private var mFusedLocationClient: FusedLocationProviderClient? = null
    //다음세줄 원래 윗줄

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    // 위치값 얻어오기 객체
    lateinit var locationRequest: LocationRequest // 위치 요청
    lateinit var locationCallback: MyLocationCallBack // 내부 클래스, 위치 변경 후 지도에 표시.

    val REQUEST_ACCESS_FINE_LOCATION = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 어플이 사용되는 동안 화면 끄지 않기.
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // 세로모드 고정.
        setContentView(R.layout.activity_map)




        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationInit()

/*
        var bit :Bitmap? = null
        var itnt = Intent(this, Activity_write::class.java)
        bit = itnt.getParcelableExtra("write") as Bitmap

        val sydney = LatLng(-34.0, 151.0)
        if(bit != null)
        {
            mMap.addMarker(MarkerOptions().position(sydney).title("끝나간다").icon(BitmapDescriptorFactory.fromBitmap(bit)))
            bit = null
        }
*/
        //

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION // 위치에 대한 권한 요청
            )
            != PackageManager.PERMISSION_GRANTED
// 사용자 권한 체크로
// 외부 저장소 읽기가 허용되지 않았다면
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) { // 허용되지 않았다면 다시 확인.

                alert(
                    "사진 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다.",

                    "권한이 필요한 이유"
                ) {
                    yesButton {
                        // 권한 허용
                        ActivityCompat.requestPermissions(
                            this@Activity_map,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_ACCESS_FINE_LOCATION
                        )
                    }
                    noButton {
                        // 권한 비허용
                    }
                }.show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
        } else {
            addLocationListener()
        }


        //
        fltbtn.setOnClickListener {
            val intent_towrite = Intent(this, Activity_write::class.java)
            //intent_towrite.putExtra("latLng", latLng)

            val args = Bundle()
            args.putParcelable("position", latLng);
            //args에 위치넣기
            intent_towrite.putExtra("position", args);

            startActivity(intent_towrite)
            finish()
        }

    }

    //오픈소스 시작

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
// 권한이 승인 됐다면
                    addLocationListener()
                } else {
// 권한이 거부 됐다면
                    toast("권한 거부 됨")
                }
                return
            }
        }
    }

    //오픈소스 끝


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val kmou = LatLng(35.076168,129.089591) //해양대 좌표
        //35.076168,129.089591

        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        // 지도의 표시를 하고 제목을 추가.

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kmou, 17f))//어플 시작 화면 좌표
        // 마커 위치로 지도 이동.
    }

    // 오픈소스 시작

    override fun onResume() { // 잠깐 쉴 때.
        super.onResume()
        addLocationListener()
    }

    override fun onPause() {
        super.onPause()
    }

    fun removeLocationLister(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        // 어플이 종료되면 지도 요청 해제.
    }

    @SuppressLint("MissingPermission")
    // 위험 권한 사용시 요청 코드가 호출되어야 하는데,
    // 없어서 발생됨. 요청 코드는 따로 처리 했음.
    fun addLocationListener() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        //위치 권한을 요청해야 함.
        // 액티비티가 잠깐 쉴 때,
        // 자신의 위치를 확인하고, 갱신된 정보를 요청
    }

    inner class MyLocationCallBack : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            val location = p0?.lastLocation
            // 위도 경도를 지도 서버에 전달하면,
            // 위치에 대한 지도 결과를 받아와서 저장.

            location?.run {
                latLng = LatLng(latitude, longitude) // 위도 경도 좌표 전달.
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                // 지도에 애니메이션 효과로 카메라 이동.
                // 좌표 위치로 이동하면서 배율은 17 (0~19까지 범위가 존재.)

                Log.d("MapsActivity", "위도: $latitude, 경도 : $longitude")

//              markerOptions.position(latLng) // 마커를 latLng으로 설정
//
//              mMap.addMarker(markerOptions) // googleMap에 marker를 표시.

                //mMap.addMarker(MarkerOptions().position(latLng).title("끝나간다"))

                //val img =intent?.getParcelableExtra("write") as Bitmap
                get = intent.getBooleanExtra("write_1", false) //계속 마크를 찍어대서 조금 수정중
                if(get)
                {
                    val img =intent?.getParcelableExtra("write_2") as Bitmap
                    val ttl =intent.getStringExtra("write_3")
                    val contnt =intent.getStringExtra("write_4")

                    mMap.addMarker(MarkerOptions().position(latLng).title(ttl).snippet(contnt).icon(BitmapDescriptorFactory.fromBitmap(img)))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    intent.removeExtra("write_1")
                }
            }
        }
    }
    // 오픈소스 끝

    fun locationInit() {
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        // 현재 사용자 위치를 저장.
        locationCallback = MyLocationCallBack() // 내부 클래스 조작용 객체 생성
        locationRequest = LocationRequest() // 위치 요청.

        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        // 위치 요청의 우선순위 = 높은 정확도 우선.



        locationRequest.interval = 1 // 내 위치 지도 전달 간격
        locationRequest.fastestInterval = 5 // 지도 갱신 간격.

    }

    fun onLastLocationButtonClicked(view: View) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }

}



package com.thymex.admin


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main22.*
import java.util.*


@Suppress("DEPRECATION")
class MainActivity22 : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    private lateinit var mapFragment1: SupportMapFragment
    private var gotuidofemployee=""
    private var gmap1: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main22)

         gotuidofemployee= intent.getStringExtra("employeehistoryuid").toString()




        mapFragment1 = supportFragmentManager.findFragmentById(R.id.fragment1) as SupportMapFragment
        mapFragment1.getMapAsync(this)

        ref.setOnClickListener(this)


        askforlocations()





       datefield.setOnClickListener {
           datepickershow()
       }
        selectdate.setOnClickListener {

            datepickershow()
        }

















    }

    private fun datepickershow() {
        val c=Calendar.getInstance()
        val year=c.get(Calendar.YEAR)
        val month=c.get(Calendar.MONTH)
        val day=c.get(Calendar.DAY_OF_MONTH)
        val dpd=DatePickerDialog(this,DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->

            val mmMonth=mMonth+1
            datefield.setText(""+mDay+"/"+mmMonth+"/"+mYear)

            var day=""
            var month=""
            if(mDay.toString().length==1){
                day="0${mDay}"
            }else{
                day=mDay.toString()
            }

            if(mmMonth.toString().length==1){
                month="0${mmMonth}"
            }else{
                month=mmMonth.toString()
            }

            val selectdate="$mYear$month$day"
            showhistorybydate(gotuidofemployee,selectdate)


        },year,month,day)

        dpd.show()
    }

    private fun showhistorybydate(gotuidofemployee: String, selectdate: String) {


var selectdatesss=selectdate




        if(gmap1!=null){
            gmap1!!.clear()
        }
        var ss=0

        var standardlatlng:LatLng?=null
        FirebaseDatabase.getInstance().reference.child("historyofevery").child(gotuidofemployee!!).orderByChild("tid").startAt(selectdatesss).endAt("$selectdatesss\uf8ff")
            .addListenerForSingleValueEvent(
            object : ValueEventListener {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (i in snapshot.children) {

                        val latiiiiii = i.child("historylatitudes").value.toString().toDouble()
                        val longiiiii = i.child("historylongitudes").value.toString().toDouble()

                        val placename = i.child("placename").value.toString()

                        val date = i.child("date").value.toString()
                        val tid = i.child("tid").value.toString()

                        val userslocation = LatLng(latiiiiii, longiiiii)



                        if (standardlatlng == null) {

                            ss = ss + 1
                            val circleDrawable1: Drawable =
                                getResources().getDrawable(R.drawable.em1);
                            val markerIcon1: BitmapDescriptor? =
                                getMarkerIconFromDrawable(
                                    circleDrawable1)
                            val marker = gmap1!!.addMarker(
                                MarkerOptions().position(userslocation)
                                    .title("history: Start : $placename")
                                    .icon(markerIcon1)
                                    .snippet("$date")).showInfoWindow()

                            gmap1!!.animateCamera(CameraUpdateFactory.newLatLngZoom(userslocation,
                                21.0f))
                            standardlatlng = userslocation
                        } else {

                            val results = FloatArray(1)
                            Location.distanceBetween(standardlatlng!!.latitude,
                                standardlatlng!!.longitude,
                                userslocation!!.latitude,
                                userslocation!!.longitude,
                                results)
                            if (results[0] >= 30.00) {
                                ss = ss + 1
                                val circleDrawable2: Drawable =
                                    getResources().getDrawable(R.drawable.em1);
                                val markerIcon2: BitmapDescriptor? =
                                    getMarkerIconFromDrawable(
                                        circleDrawable2)
                                val marker = gmap1!!.addMarker(
                                    MarkerOptions().position(userslocation)
                                        .title("history: $ss : $placename")
                                        .icon(markerIcon2)
                                        .snippet("$date"))



                                val line = PolylineOptions().add(standardlatlng, userslocation)
                                    .width(8F).color(Color.RED).geodesic(true)




                                gmap1!!.addPolyline(line)



                                standardlatlng = userslocation
                            } else {
                                FirebaseDatabase.getInstance().reference.child("historyofevery")
                                    .child(
                                        gotuidofemployee).child(tid).removeValue()
                            }


                        }


                    }


                    if(ss==0){
                        Toast.makeText(this@MainActivity22,"No histories found for this Employee on this Date",Toast.LENGTH_LONG).show()
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })




















    }


    private fun showhistory(gotuidofemployee: String?) {
        if(gmap1!=null){
            gmap1!!.clear()
        }
        var ss=0

        var standardlatlng:LatLng?=null
       FirebaseDatabase.getInstance().reference.child("historyofevery").child(gotuidofemployee!!).addListenerForSingleValueEvent(
           object : ValueEventListener {
               @SuppressLint("UseCompatLoadingForDrawables")
               override fun onDataChange(snapshot: DataSnapshot) {

                   for (i in snapshot.children) {

                       val latiiiiii = i.child("historylatitudes").value.toString().toDouble()
                       val longiiiii = i.child("historylongitudes").value.toString().toDouble()

                       val placename = i.child("placename").value.toString()

                       val date = i.child("date").value.toString()
                       val tid = i.child("tid").value.toString()

                       val userslocation = LatLng(latiiiiii, longiiiii)



                       if (standardlatlng == null) {

                           ss = ss + 1
                           val circleDrawable1: Drawable =
                               getResources().getDrawable(R.drawable.em1);
                           val markerIcon1: BitmapDescriptor? =
                               getMarkerIconFromDrawable(
                                   circleDrawable1)
                           val marker = gmap1!!.addMarker(
                               MarkerOptions().position(userslocation)
                                   .title("history: Start : $placename")
                                   .icon(markerIcon1)
                                   .snippet("$date")).showInfoWindow()

                           gmap1!!.animateCamera(CameraUpdateFactory.newLatLngZoom(userslocation,
                               21.0f))
                           standardlatlng = userslocation
                       } else {

                           val results = FloatArray(1)
                           Location.distanceBetween(standardlatlng!!.latitude,
                               standardlatlng!!.longitude,
                               userslocation!!.latitude,
                               userslocation!!.longitude,
                               results)
                           if (results[0] >= 30.00) {
                               ss = ss + 1
                               val circleDrawable2: Drawable =
                                   getResources().getDrawable(R.drawable.em1);
                               val markerIcon2: BitmapDescriptor? =
                                   getMarkerIconFromDrawable(
                                       circleDrawable2)
                               val marker = gmap1!!.addMarker(
                                   MarkerOptions().position(userslocation)
                                       .title("history: $ss : $placename")
                                       .icon(markerIcon2)
                                       .snippet("$date"))



                               val line = PolylineOptions().add(standardlatlng, userslocation)
                                   .width(8F).color(Color.RED).geodesic(true)




                               gmap1!!.addPolyline(line)



                               standardlatlng = userslocation
                           } else {
                               FirebaseDatabase.getInstance().reference.child("historyofevery")
                                   .child(
                                       gotuidofemployee).child(tid).removeValue()
                           }


                       }


                   }


                   if(ss==0){
                       Toast.makeText(this@MainActivity22,"No histories found for this Employee",Toast.LENGTH_LONG).show()
                   }


               }

               override fun onCancelled(error: DatabaseError) {

               }

           })
















    }

    override fun onMapReady(map1: GoogleMap?) {
        gmap1=map1

        if(gmap1!=null){
            showhistory(gotuidofemployee)
        }

    }

    override fun onClick(p: View?) {
   when(p!!.id){
       R.id.ref -> {
           datefield.setText("All History")
           showhistory(gotuidofemployee)
       }
   }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            gmap1!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            gmap1!!.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            gmap1!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            gmap1!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        R.id.logout -> {

            FirebaseAuth.getInstance().signOut()
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finishAffinity()
            true

        }

        else -> super.onOptionsItemSelected(item)
    }







    private  fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor? {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }




    override fun onResume() {
        super.onResume()
        askforlocations()
    }

    override fun onStart() {
        super.onStart()
        askforlocations()
    }



    private fun askforlocations(){
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager


        @SuppressLint("MissingPermission")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            if( lm.isLocationEnabled &&  lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                try {

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0L,
                        0.0f,
                        object : LocationListener {

                            override fun onLocationChanged(location: Location) {
                                try {
                                    val locations =
                                        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)


                                } catch (e: Exception) {

                                }


                            }

                        })


                }catch (e: Exception){

                }




            }


        }
    }
}
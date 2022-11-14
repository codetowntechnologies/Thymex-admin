package com.thymex.admin


import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener
import com.thymex.admin.Notifications.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.anotherphoneinfo.view.*
import kotlinx.android.synthetic.main.informations.view.*
import kotlinx.android.synthetic.main.my_address.*
import kotlinx.android.synthetic.main.my_address.view.*
import kotlinx.android.synthetic.main.thisdeviceinfo.*
import kotlinx.android.synthetic.main.thisdeviceinfo.view.*
import mumayank.com.airlocationlibrary.AirLocation
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


@Suppress("DEPRECATION")
open class MainActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener,
    GoogleMap.OnMarkerClickListener {
    private var lat2: Double? = 0.toDouble()
    private var lng2: Double = 0.toDouble()
    private lateinit var list: ArrayList<JSONObject>
    private lateinit var markerall: ArrayList<Marker>
    private lateinit var userslist: ArrayList<users>
    private lateinit var userslist1: ArrayList<users>
    private var lat1: Double = 0.toDouble()
    private var lng1: Double = 0.toDouble()
    private var trackmarker1: Marker? = null
    private lateinit var ll: LatLng
    private var myaddressnow = ""


    private lateinit var recyclerView5: RecyclerView
    private var userlist22: List<users22>? = null

    private var home_marker: Marker? = null


    private var gmap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var airloc: AirLocation

    private var mcity: String = ""
    private var mstate: String = ""
    private var mcountry: String = ""
    private var mpostalCode: String = ""

    private var apiService: APIService? = null

    private var firebaseUser: FirebaseUser? = null
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        toolbar = findViewById(R.id.appbar)
        setSupportActionBar(toolbar)
        apiService =
            Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fab.setOnClickListener(this)
        userslist = ArrayList<users>()
        userslist1 = ArrayList<users>()
        markerall = ArrayList<Marker>()
        faball.setOnClickListener(this)


        list = ArrayList<JSONObject>()
        askforlocations()
        val spaceNavigationView: SpaceNavigationView = findViewById(R.id.bottom_navigation_view)
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState)
        spaceNavigationView.addSpaceItem(SpaceItem("History-Employee", R.drawable.history))
        spaceNavigationView.addSpaceItem(SpaceItem("instruction", R.drawable.ins))
        spaceNavigationView.isSelected = false


        firebaseUser = FirebaseAuth.getInstance().currentUser

        mydataonfirebase()








        try {
            val refreshedToken = FirebaseInstanceId.getInstance().getToken().toString()
            updateToken(refreshedToken)
        } catch (e: java.lang.Exception) {

        }
        spaceNavigationView.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
                try {
                    askforlocations()
                } catch (e: Exception) {

                }
                if (gmap != null) {


                    airloc =
                        AirLocation(this@MainActivity, true, true, object : AirLocation.Callbacks {
                            @SuppressLint("SetTextI18n")
                            override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Failed to get the current position",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                            @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
                            override fun onSuccess(location: Location) {
                                try {


                                    if (home_marker != null) {
                                        home_marker!!.remove()
                                    }
                                    ll = LatLng(lat1, lng1)
                                    val circleDrawable: Drawable =
                                        getResources().getDrawable(R.drawable.smartphone);
                                    val markerIcon: BitmapDescriptor? = getMarkerIconFromDrawable(
                                        circleDrawable
                                    )
                                    myaddress()
                                    home_marker = gmap!!.addMarker(
                                        MarkerOptions().position(ll)
                                            .title("This Device : $myaddressnow").icon(markerIcon)
                                    )
                                    home_marker!!.showInfoWindow()
                                    gmap!!.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            ll,
                                            14.0f
                                        )
                                    )

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Failed to get the device's position",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                                spaceNavigationView.isSelected = false

                            }


                        })

                }


            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(itemIndex: Int, itemName: String) {
                when (itemIndex) {
                    0 -> {

                        showallemployees()


                    }
                    1 -> {

                        val bottomsheet: BottomSheetDialog = BottomSheetDialog(this@MainActivity)
                        val view: View = layoutInflater.inflate(R.layout.anotherphoneinfo, null)

                        bottomsheet.setContentView(view)
                        bottomsheet.show()

                    }

                }
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemReselected(itemIndex: Int, itemName: String) {
                when (itemIndex) {
                    0 -> {


                        showallemployees()


                    }
                    1 -> {


                        val bottomsheet: BottomSheetDialog = BottomSheetDialog(this@MainActivity)
                        val view: View = layoutInflater.inflate(R.layout.anotherphoneinfo, null)

                        bottomsheet.setContentView(view)
                        bottomsheet.show()

                    }

                }
            }
        })

        getSupportActionBar()!!.title = ""


    }

    private fun showallemployees() {

        val bottomsheet = BottomSheetDialog(this)

        val v: View = layoutInflater.inflate(R.layout.thisdeviceinfo, null)



        recyclerView5 = v.findViewById(R.id.rv)
        recyclerView5!!.setHasFixedSize(true)
        recyclerView5!!.layoutManager = LinearLayoutManager(this@MainActivity)

        userlist22 = java.util.ArrayList<users22>()


        collectallusersvalue03()










        bottomsheet.setContentView(v)
        bottomsheet.show()


    }

    private fun collectallusersvalue03() {


        val refOfUsers = FirebaseDatabase.getInstance().reference.child("users")

        refOfUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                (userlist22 as java.util.ArrayList<users22>).clear()
                for (i in snapshot.children) {
                    val usersidd = i.child("uid").value.toString()
                    if (usersidd != FirebaseAuth.getInstance().currentUser!!.uid) {
                        val userdata = i.getValue(users22::class.java)
                        (userlist22 as java.util.ArrayList<users22>).add(userdata!!)
                    }
                }
                val historyadapter = showhistoryAdapter(
                    this@MainActivity,
                    userlist22 as java.util.ArrayList<users22>
                )

                recyclerView5!!.adapter = historyadapter


            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "No one is found! Please try after some time ",
                    Toast.LENGTH_LONG
                ).show()
            }


        })


    }

    private fun mydataonfirebase() {
        val userHashmap = HashMap<String, Any>()

        userHashmap["uid"] = firebaseUser!!.uid

        userHashmap["share"] = "OFF"
        FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
            .updateChildren(userHashmap)


    }

    private fun updateToken(tk: String?) {
        try {
            val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
            val token1 = Token(tk)
            ref.child(firebaseUser!!.uid).setValue(token1)
        } catch (e: java.lang.Exception) {

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
            gmap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            gmap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            gmap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            gmap!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        R.id.logout -> {

            FirebaseDatabase.getInstance().goOnline();
            FirebaseAuth.getInstance().signOut()
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                (applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager)
                    .clearApplicationUserData() // note: it has a return value!
            } else {
                // use old hacky way, which can be removed
                // once minSdkVersion goes above 19 in a few years.
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(Intent(this, LoginActivity::class.java))

            finishAffinity()
            exitProcess(-1)

        }

        else -> super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airloc.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        airloc.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onMapReady(map: GoogleMap?) {
        gmap = map



        gmap!!.setOnMarkerClickListener(this)
        if (gmap != null) {
            airloc = AirLocation(this, true, true, object : AirLocation.Callbacks {
                @SuppressLint("SetTextI18n")
                override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to get the current position",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
                override fun onSuccess(location: Location) {
                    try {
                        lat1 = location.latitude
                        lng1 = location.longitude

                        ll = LatLng(lat1, lng1)

                        val circleDrawable: Drawable =
                            getResources().getDrawable(R.drawable.smartphone);
                        val markerIcon: BitmapDescriptor? =
                            getMarkerIconFromDrawable(circleDrawable)
                        myaddress()

                        home_marker?.setIcon(null)
                        home_marker = gmap!!.addMarker(
                            MarkerOptions().position(ll)
                                .title("This Device : $myaddressnow").icon(markerIcon)
                        )
                        home_marker!!.showInfoWindow()

                        gmap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0f))

                    } catch (e: Exception) {
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to get the current position",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }


            })

        }

    }


    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor? {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun mydetails() {
        try {
            val bottom = BottomSheetDialog(this)
            bottom.setTitle("MY Information")
            val inflater = LayoutInflater.from(this)
            var view1: View = inflater.inflate(R.layout.my_address, null)
            view1.city_value.setText(mcity)
            view1.myaddress.setText("My Location : " + myaddressnow)
            view1.state_value.setText(mstate)
            view1.country_value.setText(mcountry)
            view1.postal_value.setText(mpostalCode)
            bottom.setContentView(view1)

            bottom.show()
        } catch (e: Exception) {
            Toast.makeText(this, "Can't access...try after some time", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    override fun onClick(v: View?) {


        when (v!!.id) {


            R.id.fab -> {
                try {
                    askforlocations()
                } catch (e: Exception) {

                }
                if (gmap != null) {
                    airloc = AirLocation(this, true, true, object : AirLocation.Callbacks {
                        @SuppressLint("SetTextI18n")
                        override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                            Toast.makeText(
                                this@MainActivity,
                                "Failed to get the current position",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
                        override fun onSuccess(location: Location) {
                            try {

                                ll = LatLng(lat1, lng1)
                                myaddress()


                                if (home_marker != null) {
                                    home_marker!!.remove()
                                }

                                val circleDrawable: Drawable =
                                    getResources().getDrawable(R.drawable.smartphone);
                                val markerIcon: BitmapDescriptor? = getMarkerIconFromDrawable(
                                    circleDrawable
                                )

                                home_marker = gmap!!.addMarker(
                                    MarkerOptions().position(ll)
                                        .title("This Device : $myaddressnow").icon(markerIcon)
                                )
                                home_marker!!.showInfoWindow()
                                gmap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 14.5f))
                                mydetails()

                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Can not get Details",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    })


                }


            }

            R.id.faball -> {
                try {
                    askforlocations()
                } catch (e: Exception) {

                }
                val fid = firebaseUser!!.uid
                if (gmap != null) {
                    gmap!!.clear()
                }
                val circleDrawable: Drawable =
                    getResources().getDrawable(R.drawable.smartphone);
                val markerIcon: BitmapDescriptor? = getMarkerIconFromDrawable(
                    circleDrawable
                )
                ll = LatLng(lat1, lng1)
                home_marker = gmap!!.addMarker(
                    MarkerOptions().position(ll)
                        .title("My Position : $myaddressnow").icon(markerIcon)
                )
                userslist.clear()
                markerall.clear()
                FirebaseDatabase.getInstance().reference.child("users")
                    .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (i in snapshot.children) {
                                    val allusers: users? = i.getValue(users::class.java)


                                    val latitude1 = allusers!!.getlatitudes()
                                    val longitudes1 = allusers!!.getlongitudes()
                                    val share = allusers!!.getshare()

                                    val uidf = allusers.getuid()
                                    if (uidf != fid && share != "OFF") {
                                        val llll =
                                            LatLng(latitude1!!.toDouble(), longitudes1!!.toDouble())
                                        val phonenum = allusers.getphone()
                                        val name = allusers.getname()

                                        val circleDrawable1: Drawable =
                                            getResources().getDrawable(R.drawable.autro);
                                        val markerIcon1: BitmapDescriptor? =
                                            getMarkerIconFromDrawable(
                                                circleDrawable1
                                            )
                                        val marker = gmap!!.addMarker(
                                            MarkerOptions().position(llll).title(name)
                                                .icon(markerIcon1)
                                                .snippet("Employee :" + phonenum)

                                        )
                                        markerall.add(marker)


                                        userslist.add(allusers!!)


                                    }
                                }

                                for (i in 0..(markerall.size - 1)) {
                                    markerall[i].showInfoWindow()
                                }


                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })


            }


        }
    }


    // My location
    private fun myaddress() {
        val geocoder: Geocoder
        val addresses: List<Address>

        geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            lat1,
            lng1,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        try {
            val address: String =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            myaddressnow = address
            mcity = addresses[0].getLocality().toString()
            mstate = addresses[0].getAdminArea().toString()
            mcountry = addresses[0].getCountryName().toString()
            mpostalCode = addresses[0].getPostalCode().toString()
        } catch (e: Exception) {

        }

    }


//end


    //Address for draw routes
    private fun findaddress(lat: Double, lng: Double): String {
        val geocoder: Geocoder
        val addresses: List<Address>
        try {
            geocoder = Geocoder(this, Locale.getDefault())

            addresses = geocoder.getFromLocation(
                lat,
                lng,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            val address: String =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            return address
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "Can not find Details", Toast.LENGTH_LONG).show()
        }

        return "None"
    }

    //End


    override fun onMarkerClick(markername: Marker?): Boolean {
        if (home_marker!!.equals(markername)) {
            mydetails()
        } else if (userslist.isEmpty()) {
            Toast.makeText(this, "No Employee on the map", Toast.LENGTH_SHORT).show()

            return false
        } else {
            try {
                for (i in 0..(userslist.size - 1)) {
                    var phonenumber = ""
                    var share = ""

                    if (markerall[i].equals(markername)) {
                        val xx = i
                        val bottomsheet = BottomSheetDialog(this)
                        bottomsheet.setTitle("Employee Information")
                        val inflater = LayoutInflater.from(this)
                        val view: View = inflater.inflate(R.layout.informations, null)
                        phonenumber = userslist[xx].getphone().toString()
                        val latttt = userslist[xx].getlatitudes()!!.toDouble()
                        val longggg = userslist[xx].getlongitudes()!!.toDouble()

                        val name = userslist[xx].getname().toString()
                        share = userslist[xx].getshare().toString()
                        if (share == "OFF") {
                            view.availableop.setText("Employee: $name is not on Work")
                        } else {
                            view.availableop.setText("Employee: $name is on Working")
                        }
                        view.phnty.setText("$phonenumber")
                        val pp = findaddress(latttt, longggg)
                        view.placename11.setText(pp)
                        view.calluser.setOnClickListener {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:$phonenumber")
                            startActivity(intent)
                        }
                        bottomsheet.setContentView(view)
                        bottomsheet.show()
                    }


                }


            } catch (e: Exception) {

                Toast.makeText(this, "Can't get details...try again/Refresh", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return true
    }


    private fun askforlocations() {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager


        @SuppressLint("MissingPermission")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            if (lm.isLocationEnabled && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                try {

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0L,
                        0.0f,
                        object : LocationListener {

                            override fun onLocationChanged(location: Location) {
                                try {
                                    val locations =
                                        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                    if (locations != null) {
                                        lng1 = locations!!.longitude
                                        lat1 = locations.latitude
                                    }


                                } catch (e: Exception) {

                                }


                            }

                        })


                } catch (e: Exception) {

                }


            }


        }
    }

    override fun onResume() {
        super.onResume()
        askforlocations()
    }

    override fun onStart() {
        super.onStart()
        askforlocations()
    }


}







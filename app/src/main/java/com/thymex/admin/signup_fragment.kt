package com.thymex.admin

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context.ACTIVITY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_signup_fragment.*
import kotlinx.android.synthetic.main.fragment_signup_fragment.view.*


class signup_fragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var email_re: String
    private lateinit var passwordss: String

    private lateinit var names: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        val view = inflater.inflate(R.layout.fragment_signup_fragment, container!!, false)
        mAuth = FirebaseAuth.getInstance()
        email_re = view.email_register.text.toString().trim()
        passwordss = view.password_register.text.toString().trim()
        names = view.name.text.toString().trim()




        view.register_btn.setOnClickListener {

            registeruser()

        }



        return view
    }


    private fun registeruser() {
        mAuth = FirebaseAuth.getInstance()
        email_re = email_register.text.toString().trim()
        passwordss = password_register.text.toString().trim()
        names = name.text.toString().trim()

        if (email_re.isEmpty()) {
            email_register.setError("Phone cannot be Empty, it Must be filled")

        }
        if (passwordss.isEmpty()) {
            password_register.setError("Password cannot be Empty,it Must be filled")

        }
        if (names.isEmpty()) {
            name.setError("Name cannot be Empty,it Must be filled")
        }


        if (email_re.isEmpty() || passwordss.isEmpty() || names.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Can Not Register ,Please fillup all the fields",
                Toast.LENGTH_LONG
            )
                .show()
            return
        } else {

            mAuth.createUserWithEmailAndPassword(email_re + "@office.com", passwordss)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUserID = mAuth.currentUser!!.uid
                        val refUsers = FirebaseDatabase.getInstance().reference.child("users")
                            .child(firebaseUserID)
                        val userHashmap = HashMap<String, Any>()

                        userHashmap["uid"] = firebaseUserID
                        userHashmap["phone"] = email_re
                        userHashmap["savedloc"] = "None"

                        userHashmap["share"] = "OFF"
                        userHashmap["name"] = names

                        userHashmap["latitudes"] = "No"
                        userHashmap["longitudes"] = "No"
                        refUsers.updateChildren(userHashmap).addOnCompleteListener { tasks ->
                            if (tasks.isSuccessful) {
                                FirebaseAuth.getInstance().signOut()
                                Toast.makeText(
                                    requireContext(),
                                    "$names Employee Registration Successfully Done",
                                    Toast.LENGTH_LONG
                                ).show()

                                val builder = AlertDialog.Builder(requireContext())
                                builder.setIcon(R.drawable.logo_map1)
                                builder.setTitle("Employee Registration Process")
                                builder.setMessage("$names Employee Registration Successfully Done. Please click ' OK ' Button here . admin App will be closed. After that if you want to login to admin app or Register for another employee, Please Re open the admin app Again. Employee Registration System Process it is. Please Co-operate with us")
                                builder.setPositiveButton(
                                    "OK",
                                    DialogInterface.OnClickListener { dialogInterface, i ->
                                        signOut()

                                    })

                                val alert = builder.create()
                                alert.show()

                                alert.setCancelable(false);
                                alert.setCanceledOnTouchOutside(false);


                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Something Wrong! Please try again after some time or contact with us",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@addOnCompleteListener
                            }
                        }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Employee Registration:" + task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnCompleteListener
                    }
                }
        }
    }


    private fun signOut() {
        FirebaseDatabase.getInstance().goOnline();
        FirebaseAuth.getInstance().signOut()
        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            (requireActivity().getSystemService(ACTIVITY_SERVICE) as ActivityManager)
                .clearApplicationUserData() // note: it has a return value!
        } else {
            // use old hacky way, which can be removed
            // once minSdkVersion goes above 19 in a few years.
        }

        requireActivity().intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        requireActivity().intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))

        requireActivity().finish()


    }


}



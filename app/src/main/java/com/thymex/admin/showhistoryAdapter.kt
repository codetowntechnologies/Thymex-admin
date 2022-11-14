package com.thymex.admin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class showhistoryAdapter(var mcontext: Context, var usersList22: List<users22>):

    RecyclerView.Adapter<showhistoryAdapter.ViewHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): showhistoryAdapter.ViewHolder {
        val v: View =
            LayoutInflater.from(mcontext).inflate(R.layout.showemployee, parent, false)


        return showhistoryAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: showhistoryAdapter.ViewHolder, position: Int) {
        val user: users22 = usersList22[position]
         val useruid=user.getuid()
        val name=user.getname()
        holder.nameofemployee.setText(user.getname())
        holder.phonenumber.setText(user.getphone())
        val share=user.getshare()
        if(share=="ON"){
            holder.statusimg.setImageResource(R.drawable.buttonround113)
            holder.statusimg.setBackgroundResource(R.drawable.buttonround113)
        }else{
            holder.statusimg.setImageResource(R.drawable.buttonround112)
            holder.statusimg.setBackgroundResource(R.drawable.buttonround112)
        }


        holder.historyshow.setOnClickListener {

          val i=Intent(mcontext,MainActivity22::class.java).putExtra("employeehistoryuid",useruid)
            mcontext.startActivity(i)



        }

        holder.deletehistory.setOnClickListener {
            val builder=AlertDialog.Builder(mcontext)
            builder.setMessage("Are you sure to detete all histories of employee $name?")
            builder.setNegativeButton("NO",DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            builder.setPositiveButton("YES",DialogInterface.OnClickListener { dialogInterface, i ->
                FirebaseDatabase.getInstance().reference.child("historyofevery").child(useruid!!).removeValue().addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(mcontext,"All histories of $name deleted",Toast.LENGTH_LONG).show()

                        dialogInterface.dismiss()

                    }else{
                        Toast.makeText(mcontext,"Can't Delete, Check your internet connection",Toast.LENGTH_LONG).show()

                    }
                }






            })

            val alert11=builder.create()
            alert11.show()
        }





    }

    override fun getItemCount(): Int {
        return usersList22.size
    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var nameofemployee: TextView = v.findViewById(R.id.nameofemployee)
        var phonenumber: TextView = v.findViewById(R.id.phonenumber)
        var historyshow:TextView=v.findViewById(R.id.historyshow)

        var statusimg:ImageView=v.findViewById(R.id.statusofhim)

        var deletehistory:TextView=v.findViewById(R.id.deletehistory)
    }















}
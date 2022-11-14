package com.thymex.admin

class users22 {
    private var phone: String = ""
    private var share: String = ""

    private var name: String = ""

    private var uid:String=""

    constructor()
    protected constructor(phone: String, share: String,uid:String, name:String) {
        this.phone = phone
        this.share = share
        this.uid=uid
        this.name=name
    }
    fun getuid():String?{
        return uid
    }
    fun setuid(uid:String){
        this.uid=uid
    }





    fun getshare():String?{
        return share
    }
    fun setshare(share:String){
        this.share=share
    }


    fun getphone():String?{
        return phone
    }
    fun setphone(phone:String){
        this.phone=phone
    }













    fun getname():String?{
        return name
    }
    fun setname(name:String){
        this.name=name
    }


}
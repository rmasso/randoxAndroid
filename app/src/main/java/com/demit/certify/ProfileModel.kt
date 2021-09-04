package com.demit.certify

class ProfileModel {
    var selected: Boolean = false
    var fname: String = ""
    var sname: String = ""
    var dob: String = ""
    var email: String = ""
    var pnumber: String = ""
    var country: Int = 0
    var phone: String = ""
    var address: String = ""
    var zipcode: String = ""
    var gender: Int = 0



    constructor(
        selected: Boolean,
        fname: String,
        sname: String,
        dob: String,
        email: String,
        pnumber: String,
        country: Int,
        phone: String,
        address: String,
        zipcode: String,
        gender: Int
    ) {
        this.selected = selected
        this.fname = fname
        this.sname = sname
        this.dob = dob
        this.email = email
        this.pnumber = pnumber
        this.country = country
        this.phone = phone
        this.address = address
        this.zipcode = zipcode
        this.gender = gender
    }

    constructor()
}

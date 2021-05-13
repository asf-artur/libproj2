package com.example.libproj2.retrofit.contracts

import com.example.libproj2.models.User
import com.example.libproj2.models.UserCategory
import com.example.libproj2.utils.toCalendar
import java.util.*

data class UserPojo(
    val id: Int,
    val name: String,
    val userCategory: String,
    val barcode: String?,
    val rfid: String?,
    val canBorrowBooks: Boolean,
    val registrationDate: Date,
    val lastVisitDate: Date?,
    val imagePath: String?,
) {
}

fun UserPojo.toUser() : User{
    return User(
        this.id,
        this.name,
        UserCategory.valueOf(this.userCategory),
        this.barcode,
        this.rfid,
        this.canBorrowBooks,
        this.registrationDate.toCalendar()!!,
        this.lastVisitDate.toCalendar(),
        this.imagePath,
    )
}
package com.example.libproj2.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
open class User(
    @PrimaryKey val id: Int,
    val name: String,
    val userCategory: UserCategory,
    val barcode: String?,
    val rfid: String?,
    val canBorrowBooks: Boolean,
    val registrationDate: Calendar,
    val lastVisitDate: Calendar?,
    val imagePath: String?,
) {
}

class EmptyUser() : User(-1, "", UserCategory.Librarian, null, null, true, Calendar.getInstance(), null, null)
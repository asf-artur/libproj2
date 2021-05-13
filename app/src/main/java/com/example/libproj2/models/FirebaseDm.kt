package com.example.libproj2.models

class FirebaseDataDm(
    val Data: FirebaseDm
)

class FirebaseDm(
        val notificationType: FirebaseNotificationType,
        val initiatorUserId: Int,
        val bookCopyId: Int,
        val userLibrarianId: Int?,
) {
}

enum class FirebaseNotificationType{
    Important,
    // Книгу берут в процессе
    TryBookBorrow,
    // Книга взята
    BookIsBorrowed,
    // Книга поступила
    BookArrived,
    BookReturnLastDay,
    BookReturnOneDayLef,
    BookReturnPeriodExpired,
    TryBookBorrowFailure,
    BookReturned,
    // Пока не будет
    News,
}
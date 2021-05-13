package com.example.libproj2.services

import android.view.View
import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.BookInfo
import com.example.libproj2.models.BookNotification
import com.example.libproj2.models.User
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.repositories.BookInfoRepository
import com.example.libproj2.repositories.BooksHistoryRepository
import com.example.libproj2.repositories.BooksOnHandRepository
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable

class BookService() {
    private val bookCopiesRepository = BookCopiesRepository.get()
    private val bookInfoRepository = BookInfoRepository.get()
    private val booksHistoryRepository = BooksHistoryRepository.get()
    private val booksOnHandRepository = BooksOnHandRepository.get()
    private val bookNetWebAppService = BookNetWebAppService.get()

    fun tryBorrowBook(bookCopy: BookCopy, user: User){
        bookNetWebAppService.borrow_try(bookCopy.id, user.id)
    }

    fun borrowBookLibrarian(bookCopy: BookCopy, userReader: User, userLibrarian : User, view : View){
        val observer = object : CompletableObserver {
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onComplete() {
//                TODO("Not yet implemented")
//                Snackbar.make(view, "Подтверждено библиотекарем", Snackbar.LENGTH_SHORT)
//                        .show()
                bookCopiesRepository.tryRefresh(bookCopy, bookNetWebAppService)
                booksOnHandRepository.load()
            }

            override fun onError(e: Throwable?) {
//                TODO("Not yet implemented")
            }
        }
        val req = bookNetWebAppService.completeBorrowBookRx(bookCopy.id, userReader.id, userLibrarian.id)
        req.subscribe(observer)
    }

    fun returnBook(bookCopy: BookCopy, userLibrarianId: Int) : Completable{
        return bookNetWebAppService.returnBookRx(bookCopy.id, bookCopy.userId!!, userLibrarianId)
    }

    fun searchBookOnHand(barcode: String): BookCopy?{
        return booksOnHandRepository.books.firstOrNull {
            c -> c.barcode == barcode
        }
    }

    fun searchBookInfo(barcode: String): BookInfo?{
        return bookInfoRepository.books.firstOrNull {
            c -> c.barcode == barcode
        }
    }
}
package com.example.libproj2.services

import com.example.libproj2.fortesting.UserLogin
import com.example.libproj2.models.*
import java.util.*

private const val USER_COUNT = 2
class ExampleBuilder {
    fun buildUserLogins(): MutableList<UserLogin> {
        val result = (1..USER_COUNT).map {
            return@map UserLogin(it.toString(), it.toString())
        }.toMutableList()

        val myUserLogin = UserLogin("0", "")

        result.add(myUserLogin)

        return result
    }

    fun buildUsers(): List<User> {
        val result = (1..USER_COUNT).map {
            val userCategory = if(it == 1) UserCategory.Librarian else UserCategory.Reader
            val userName = if(it == 1) "Работник библиотеки" else "Читатель библиотеки"
            return@map User(
                it,
                userName,
                userCategory,
                null,
                null,
                true,
                Calendar.getInstance(),
                Calendar.getInstance(),
                    null
            )
        }.toMutableList()

        result.add(buildMe(UserCategory.Admin))

        return result
    }

    fun buildMe(userCategory: UserCategory): User{
        val date = Calendar.getInstance()
            .apply {
                set(2020, 4, 29)
            }
        return User(
                0,
                "Асфандияров Артур",
                userCategory,
                "988443355",
                null,
                true,
                date,
                date,
                null
            )
    }

    fun buildMyBook1() : BookCopy {
        val rfidId = "[-14, 110, 84, 32]"
        val issueDate = Calendar.getInstance()
            .apply {
                set(2018, 3, 1)
            }
        val industryIdentifier = IndustryIdentifier(IndustryIdentifierType.ISBN_13, "4606224157696")
        val bookInfo = BookInfo(
                0,
                "Тетрадь",
                "",
                listOf("Магазин \"Офисмаг\""),
                issueDate,
                48,
                listOf(),
                "ru",
                "",
                listOf(industryIdentifier),
                "officemag.jpg",
                null
        )
        return BookCopy(
                0,
                bookInfo,
                BookStatus.InStock,
                "4606224157696",
                rfidId,
                null,
                null
        )
    }

    fun buildBooks() : List<BookCopy> {
        val result = (1..14).map {
            val barcode = "1000$it"
            val bookInfo = BookInfo(
                    1,
                    "Книга $it",
                    "",
                    listOf("Автор $it"),
                    Calendar.getInstance(),
                    0,
                    listOf(),
                    "",
                    "",
                    listOf(),
                    null,
                    null,
            )
            return@map BookCopy(
                    it,
                    bookInfo,
                    BookStatus.InStock,
                    barcode,
                    null,
                    null,
                    null
            )
        }

        return result
    }

    companion object{
        private var singleton: ExampleBuilder? = null
        fun init(){
            if(singleton == null){
                singleton = ExampleBuilder()
            }
        }

        fun get(): ExampleBuilder {
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}
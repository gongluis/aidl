// BookController.aidl
package com.luis.server;
import com.luis.server.Book;
// Declare any non-default types here with import statements

interface BookController {

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    List<Book> getBookList();

    void addBookInOut(inout Book book);

    void addBookIn(in Book book);

    void addBookOut(out Book book);

}
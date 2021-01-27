package com.luis.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * author : luis
 * e-mail : luis.gong@cardinfolink.com
 * date   : 2021/1/27  10:44
 * desc   :
 */
public class AIDLService extends Service {

    private ArrayList<Book> mBookList;

    public AIDLService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mBookList = new ArrayList<>();
        initData();
    }

    private void initData() {
        Book book1 = new Book("活着");
        Book book2 = new Book("或者");
        Book book3 = new Book("曾国藩的正面与侧面");
        Book book4 = new Book("平凡的世界");
        Book book5 = new Book("明朝那些事儿");
        Book book6 = new Book("中国误会了袁世凯");
        mBookList.add(book1);
        mBookList.add(book2);
        mBookList.add(book3);
        mBookList.add(book4);
        mBookList.add(book5);
        mBookList.add(book6);
    }

    private final BookController.Stub mStub = new BookController.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBookInOut(Book book) throws RemoteException {
            if(book!= null){
                book.setName("服务器改了新书的名字 InOut");
                mBookList.add(book);
            }else{
                Log.e("TAG", "接收到了一个空对象 InOut");
            }
        }

        @Override
        public void addBookIn(Book book) throws RemoteException {
            if (book != null) {
                Log.e("TAG", "客户端传来的书的名字：" + book.getName());
                book.setName("服务器改了新书的名字 In");
                mBookList.add(book);
            } else {
                Log.e("TAG", "接收到了一个空对象 In");
            }
        }

        @Override
        public void addBookOut(Book book) throws RemoteException {
            if (book != null) {
                Log.e("TAG", "客户端传来的书的名字：" + book.getName());
                book.setName("服务器改了新书的名字 Out");
                mBookList.add(book);
            } else {
                Log.e("TAG", "接收到了一个空对象 Out");
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }
}

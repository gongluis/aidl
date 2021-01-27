package com.luis.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.luis.server.Book;
import com.luis.server.BookController;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BookController mBookController;
    private boolean connected;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBookController = BookController.Stub.asInterface(iBinder);
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            connected = false;
        }
    };
    private List<Book> mBookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_getBookList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mBookList = mBookController.getBookList();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                log();
            }
        });


        findViewById(R.id.btn_addBook_inOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connected) {
                    Book book = new Book("客户端新建得书");
                    try {
                        mBookController.addBookInOut(book);
                        Log.e(TAG, "向服务器以InOut方式添加了一本新书");
                        Log.e(TAG, "新书名：" + book.getName());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.btn_addBook_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    Book book = new Book("这是一本新书 In");
                    try {
                        mBookController.addBookIn(book);
                        Log.e(TAG, "向服务器以In方式添加了一本新书");
                        Log.e(TAG, "新书名：" + book.getName());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.btn_addBook_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    Book book = new Book("这是一本新书 Out");
                    try {
                        mBookController.addBookOut(book);
                        Log.e(TAG, "向服务器以Out方式添加了一本新书");
                        Log.e(TAG, "新书名：" + book.getName());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Intent intent = new Intent();
        intent.setPackage("com.luis.server");
        intent.setAction("com.luis.server.action");
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private void log() {
        for (Book book : mBookList) {
            Log.e(TAG, book.toString());
        }

    }


}
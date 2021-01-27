#### 概述
> 这门语言得目的是为了实现进程间通信  

android interface definition language,  
安卓接口定义语言，用于服务器和客户端通信接口语言，某种意义上说AIDL是一种模板，因在使用过程中，实际起作用得并不是aidl文件，而是据此生成得一个interface实例代码，AIDL是为了我们重复写代码而出现得一个模板。  
在一个进程中通过获取另一个进程得数据和调用另一个进程暴露出来得方法进行进程间通信。

#### 语法 
1. 以.aidl为后缀
2. 支持得数据类型  
    * 8种基本数据类型（byte char int short long double float）
    * String charSequence
    * 实现parceble数据
    * list 承载得数据必须是支持得数据类型
    * map 承载得数据必须是支持得数据类型
3. aidl文件分两类，一个是声明parceble接口得数据类型，一共其他aidl文件使用那些非默认支持得数据类型，另一类是用来定义接口方法，声明要暴漏哪些接口给客户端用，定向tag就是用来标记这些参数。
4. 定向TAG，表示跨进程中数据得流向。in表示数据只能由客户端流向服务端，out表示数据只能由服务端流向客户端，inout可以双向流动。此外如果aidl接口得参数类型是基本数据类型，String ,charsequence或者其它Aidl文件定义的方法接口，这些参数的定向tag默认是且只能是In,所以除了这些类型外，其它参数都要明确哪种定向tag，  
    * in  默认方式；客户端将参数传给服务端使用，如同子弹，打出去就不管了~ 
    * out 客户端将一个参数传给服务端，服务端将其作为容器，丢弃其中所有属性值后，再填充内容，然后还给客户端继续处理；如同一个盘子，服务端装满食物后，由客户端使用~ 
    * inout 客户端将参数传给服务器，服务端可以使用参数的值，同时对这个参数进行修改，客户端会得到修改后的参数，如果是集合数组等，可修改其内部的子对象；如同客户端传给服务端一本书，服务端可以查看书中内容，也可以做一些笔记，然后还给客户端。
```
定向tag例子  

1. 使用in方式时，参数值单向传输，客户端将对象传给服务端后，依然使用自己的对象值，不受服务端的影响。 
2. 使用out方式传递数组类型时，客户端传递给服务端的只有数组的长度，客户端得到的是服务端赋值后的新数组。 
3. 使用inout方式传递数组类型时，客户端会将完整的数组传给服务端，客户端得到的是服务端修改后的数组。 
4. 使用out方式传递Parcelable对象时，客户端传给服务端的是一个属性值全部为空的对象，得到的是服务端重新赋值后的对象。 
5. 使用inout方式传递Parcelable对象时，客户端会将完整的对象传递给服务端，得到的是服务端修改后的对象。

```
5. 明确导包，在AIDL文件中需要明确标明引用到的数据类型所在的包名，即使两个文件处在同个包名下

#### 服务端编码  
功能：客户端通过绑定服务端的service方式调用服务端的方法，获取服务端的书籍列表并向其中添加书籍，实现应用间数据共享。
1.新建一个工程com.luis.server
2. 新建一个Book.aidl  

3. 新建Book类,实现parceble  
```
public class Book implements Parcelable{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Book(String name) {
        this.name = name;
    }

    public Book() {
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public String toString() {
        return "book name：" + name;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
    }

    public void readFromParcel(Parcel dest) {
        name = dest.readString();
    }

    protected Book(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}

```
4. 修改Book.aidl文件  
```
// Book.aidl
package com.luis.server;

// Declare any non-default types here with import statements

parcelable Book;
```
5. 定义暴露给客户端的接口方法BookController.aidl
```
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
```  

6. clean_rebuild下项目，生成一个真实交互用的interface  
![aidlcontrol.jpg](https://i.loli.net/2021/01/27/RjeDP6swrUBHmgd.jpg)


7. 定义一个Service供远程客户端调用  
```
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
```  
onBind方法返回的就是mStub对象，实现当中定义的两个方法。
8. 注册该service  
```
        <service android:name=".AIDLService"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="com.luis.server.action"/>
                <category android:name="android.intent.category.DEFAULT"/>
            </intent-filter>

        </service>
```

客户端代码编写  
9. 创建一个新的工程com.luis.client
10. 将server端的aidl文件和Book类copy过来，aidl整个文件都复制到java同级别，不需要改动任何代码。Book类，需要创建和服务端相同的包名来放置Book类。
![aidlbook.jpg](https://i.loli.net/2021/01/27/bJ6GRhBwc9oD2Yp.jpg)


11. 修改布局添加两个按钮  
```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    tools:context=".MainActivity">

    <Button
        android:id="@+id/btn_getBookList"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="获取书籍列表" />

    <Button
        android:id="@+id/btn_addBook_inOut"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="InOut 添加书籍" />

    <Button
        android:id="@+id/btn_addBook_in"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="In 添加书籍in" />

    <Button
        android:id="@+id/btn_addBook_out"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Out 添加书籍out" />

</LinearLayout>
```  

```
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
```
按钮分别用来获取和添加服务端的数据，添加书籍的时候服务端还改变了对象的Name属性，

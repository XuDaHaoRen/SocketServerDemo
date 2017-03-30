package activity.xbl.com.socketserverdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * 服务端和客户端 要先启动服务端，再去去骑电动客户端
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button start_btn;
    private ConnectThread connectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_btn = (Button) findViewById(R.id.start_btn);
        start_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn:
                new ServerSocketThread().start();
                break;
        }

    }

    //数据连接发送信息线程的实例化
    private void connected(Socket socket) {
        if (connectThread != null) {
            connectThread.cancle();
            connectThread = null;
        }
        connectThread = new ConnectThread(socket);
        connectThread.start();
        Log.e("TAG", "connect success");
    }

    //监听服务端的连接
    class ServerSocketThread extends Thread {
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket();
                //绑定客户端(因为是在同一个机子上访问，所以IP地址一样，直接是127.0.0.1)
                serverSocket.bind(new InetSocketAddress("10.232.9.241", 1234));//服务端的IP地址
                //等待客户端链接
                while (true) {
                    Log.e("TAG", "server_wait");
                    Socket socket = serverSocket.accept();
                    connected(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //写数据和读取数据的线程,无论是客户端还是服务端数据的读取还有写入的方法都是一样的
    class ConnectThread extends Thread {
        Socket socket;
        InputStream in = null;
        OutputStream out = null;

        public ConnectThread(Socket socket) {
            this.socket = socket;
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes ;
            //等待另一端发送的数据
            try {
                while ((bytes = in.read(buffer)) != -1) {
                    Log.e("TAG", "server_read:" + new String(buffer, 0, bytes));
                    String data = "server_read_success";
                    write(data.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //向另一端写数据的操作
        public void write(byte[] buffer) {
            try {
                out.write(buffer);
                Log.e("TAG", "server_write  " + new String(buffer));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //关闭的方法
        public void cancle() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

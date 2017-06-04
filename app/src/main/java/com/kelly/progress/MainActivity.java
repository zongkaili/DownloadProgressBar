package com.kelly.progress;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kelly.download.DownLoadManager;
import com.kelly.download.DownLoadService;
import com.kelly.download.TaskInfo;
import com.kelly.progress.adapter.ListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.btn_add_to_download)
            Button mBtnAddDownload;
    @Bind(R.id.btn_switch_user)
            Button mBtnSwitchUser;
    @Bind(R.id.listView)
            ListView mListView;

    private DownLoadManager manager;
    private ListAdapter adapter;

    private EditText nameText;
    private EditText urlText;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    manager = DownLoadService.getDownLoadManager();
                    manager.changeUser("luffy");//设置用户ID，客户端切换用户时可以显示相应用户的下载任务
                    manager.setSupportBreakpoint(true);//断点续传需要服务器的支持，设置该项时要先确保服务器支持断点续传功能
                    adapter = new ListAdapter(MainActivity.this,manager);
                    mListView.setAdapter(adapter);
                    mBtnSwitchUser.setText("用户 : " + manager.getUserID());
                    break;
                case 2:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //下载管理器需要启动一个Service,在刚启动应用的时候需要等Service启动起来后才能获取下载管理器，所以稍微延时获取下载管理器
        handler.sendEmptyMessageDelayed(1, 50);

        setListener();

    }

    private void setListener() {
        mBtnAddDownload.setOnClickListener(this);
        mBtnSwitchUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_to_download:
                addDownloadTask();
                break;
            case R.id.btn_switch_user:
                showSwitchuserDialog();
                break;
            default:
                break;
        }
    }

    private void addDownloadTask() {
        View showview = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
        nameText = (EditText) showview.findViewById(R.id.file_name);
        urlText = (EditText) showview.findViewById(R.id.file_url);
        nameText.setText("手机qq");
        urlText.setText("http://sqdd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk");
        new AlertDialog.Builder(MainActivity.this)
                .setView(showview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("".equals(nameText.getText().toString()) || "".equals(urlText.getText().toString())) {
                    Toast.makeText(MainActivity.this, "请输入文件名和下载路径", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < manager.getAllTask().size(); i++){
                        if(nameText.getText().toString().equals(manager.getAllTask().get(i).getFileName())){
                            Toast.makeText(MainActivity.this, "该任务正在下载中...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    TaskInfo info = new TaskInfo();
                    info.setFileName(nameText.getText().toString());
                    //服务器一般会有个区分不同文件的唯一ID，用以处理文件重名的情况
                    info.setTaskID(nameText.getText().toString());
                    info.setOnDownloading(true);
                    //将任务添加到下载队列，下载器会自动开始下载
                    manager.addTask(nameText.getText().toString(), urlText.getText().toString(), nameText.getText().toString());
                    adapter.addItem(info);
                }
            }
        }).setNegativeButton("取消", null).show();
    }

    private void showSwitchuserDialog() {
        new AlertDialog.Builder(MainActivity.this).setTitle("切换用户")
                .setPositiveButton("zhuiji7", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        manager.changeUser("zhuiji7");
                        mBtnSwitchUser.setText("用户: zhuiji7");
                        adapter.setListdata(manager.getAllTask());

                    }
                }).setNegativeButton("luffy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manager.changeUser("luffy");
                mBtnSwitchUser.setText("用户 : luffy");
                adapter.setListdata(manager.getAllTask());
            }
        }).show();
    }

    /**
     * 模拟下载
     */
    private void downLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(200);
                        Message message = handler.obtainMessage();
                        message.arg1 = i + 1;
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

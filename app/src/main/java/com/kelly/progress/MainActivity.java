package com.kelly.progress;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kelly.progressbar.FlikerProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FlikerProgressBar mFlikerProgressBar;
    private Button mDownloadBtn;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mFlikerProgressBar.setProgress(msg.arg1);
            if(msg.arg1 == 100){
                mFlikerProgressBar.finishLoad();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlikerProgressBar = (FlikerProgressBar) findViewById(R.id.flikerbar);
        mDownloadBtn = (Button) findViewById(R.id.download);

        mFlikerProgressBar.setOnClickListener(this);
        mDownloadBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.flikerbar:
                if(!mFlikerProgressBar.isFinish()){
                    mFlikerProgressBar.toggle();
                }
                break;
            case R.id.download:
                downLoad();
                break;
            default:
                break;
        }
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

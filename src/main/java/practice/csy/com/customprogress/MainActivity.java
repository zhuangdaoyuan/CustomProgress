package practice.csy.com.customprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import practice.csy.com.customprogress.constant.Constant;
import practice.csy.com.customprogress.view.CustomHorizontalProgresWithNum;
import practice.csy.com.customprogress.view.CustomHorizontalProgresWithNumber;
import practice.csy.com.customprogress.view.RoundlProgresWithNum;
import practice.csy.com.customprogress.view.TextProgressBar;

public class MainActivity extends AppCompatActivity {
    private CustomHorizontalProgresWithNum horizontalProgress2, horizontalProgress3;//水平带进度
    private CustomHorizontalProgresWithNumber horizontalProgress4, horizontalProgress5;
    private RoundlProgresWithNum mRoundlProgresWithNum33, mRoundlProgresWithNum44;//自定义圆形进度条 带数字进度
    private TextProgressBar textProgressBar;

    private Timer timer3;
    private Timer timer33, timer44;
    private Button pause;
    private boolean pauseFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pause = (Button) findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseFlag = !pauseFlag;
            }
        });

        horizontalProgress2 = (CustomHorizontalProgresWithNum) findViewById(R.id.horizontalProgress2);
        horizontalProgress3 = (CustomHorizontalProgresWithNum) findViewById(R.id.horizontalProgress3);
        horizontalProgress4 = (CustomHorizontalProgresWithNumber) findViewById(R.id.horizontalProgress4);
        horizontalProgress5 = (CustomHorizontalProgresWithNumber) findViewById(R.id.horizontalProgress5);

        mRoundlProgresWithNum33 = (RoundlProgresWithNum) findViewById(R.id.mRoundlProgressWithNum33);
        mRoundlProgresWithNum44 = (RoundlProgresWithNum) findViewById(R.id.mRoundlProgressWithNum44);

        mRoundlProgresWithNum33.setProgress(0);
        mRoundlProgresWithNum33.setMax(100);
        mRoundlProgresWithNum44.setProgress(0);
        mRoundlProgresWithNum44.setMax(100);

        timer3 = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //实时更新进度
                if (horizontalProgress3.getProgress() >= 100) {
                    //指定时间取消
                    timer3.cancel();
                }
                if (pauseFlag) {
                    horizontalProgress2.setProgress(horizontalProgress2.getProgress() + 1);
                    horizontalProgress3.setProgress(horizontalProgress3.getProgress() + 1);
                    horizontalProgress4.setProgress(horizontalProgress4.getProgress() + 1);
                    horizontalProgress5.setProgress(horizontalProgress5.getProgress() + 1);
                }
            }
        };
        timer3.schedule(timerTask, 2000, 50);

        timer33 = new Timer();
        timer33.schedule(new TimerTask() {
            @Override
            public void run() {
                //实时更新进度
                if (mRoundlProgresWithNum33.getProgress() >= mRoundlProgresWithNum33.getMax()) {//指定时间取消
                    timer33.cancel();
                }
                mRoundlProgresWithNum33.setProgress(mRoundlProgresWithNum33.getProgress() + 1);
                mRoundlProgresWithNum44.setProgress(mRoundlProgresWithNum44.getProgress() + 1);
            }
        }, 20, 20);


        textProgressBar = (TextProgressBar) findViewById(R.id.textProgress);
        textProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.textProgress) {
                    switch (textProgressBar.getStateType()) {
                        case Constant.Default:
                            textProgressBar.setStateType(Constant.Downloading);
                            timer44 = new Timer();
                            timer44.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (textProgressBar.getProgress() >= textProgressBar.getMax()) {
                                        timer44.cancel();
                                    }
                                    if (textProgressBar.getStateType() == Constant.Downloading)
                                        textProgressBar.setProgress(textProgressBar.getProgress() + 1);
                                }
                            }, 20, 20);
                            break;
                        case Constant.Downloading:
                            textProgressBar.setStateType(Constant.Pause);
                            break;
                        case Constant.Pause:
                            textProgressBar.setStateType(Constant.Downloading);
                            break;
                    }
                }
            }
        });
        textProgressBar.setStateType(Constant.Default);
    }

}

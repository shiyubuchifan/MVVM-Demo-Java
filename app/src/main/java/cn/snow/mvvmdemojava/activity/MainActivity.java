package cn.snow.mvvmdemojava.activity;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.snow.mvvmdemojava.R;
import cn.snow.mvvmdemojava.api.GetRequestInterface;
import cn.snow.mvvmdemojava.bean.TestBean;
import cn.snow.mvvmdemojava.study.SingleTon;
import cn.snow.mvvmdemojava.view.MyView;
import cn.snow.network.NetWorkApi;
import cn.snow.network.observer.BaseObserver;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.internal.ws.RealWebSocket;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyView view = findViewById(R.id.myview);
//        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myviewanim);
//        view.startAnimation(animation);

//        单个动画
//        ObjectAnimator animator = ObjectAnimator
//                .ofInt(view, "backgroundColor", 0XffFF0000, 0Xff0000FF)
//                .setDuration(2000);
//        animator.setRepeatMode(ValueAnimator.RESTART);
//        animator.setRepeatCount(10);
//        animator.start();
//        动画集合
//        AnimatorSet animatorSet = new AnimatorSet();
//        ValueAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", 200f);
//        ValueAnimator animator = ObjectAnimator
//                .ofInt(view, "backgroundColor", 0XffFF0000, 0Xff0000FF);
//        animatorSet.playTogether(translationX,animator);
//        animatorSet.setDuration(1000).start();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.setDuration(2000);//动画持续时间
        valueAnimator.setRepeatCount(3);//重复次数
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);//重复方式，
        valueAnimator.setStartDelay(2000);//开始前延迟时间
        valueAnimator.setEvaluator(new IntEvaluator());//估值器
        valueAnimator.setInterpolator(new LinearInterpolator());//差值器
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取当前动画属性值，即1~100
                Integer animatedValue = (Integer) animation.getAnimatedValue();
                //获取动画的百分比
                float animatedFraction = animation.getAnimatedFraction();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                int width = layoutParams.width;
                int height = layoutParams.height;
                layoutParams.height = height + 1;
                layoutParams.width = width + 1;
                Log.e(TAG, "animatedValue: " + animatedValue + " animatedFraction : " + animatedFraction + " width : " + layoutParams.width + " height : " + layoutParams.height);
                view.requestLayout();
            }
        });
        valueAnimator.start();
        Socket socket = null;
        try {
            socket = new Socket("",433);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.connect(new InetSocketAddress(443));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(443);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            serverSocket.accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//

        SingleTon.SingleTonEnum.INSTANCE.doSomething();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: .QAq");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

//        NetWorkApi.getService(GetRequestInterface.class).getUsername().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseObserver<TestBean>() {
//            @Override
//            public void onSuccess(TestBean testBean) {
//                Log.e(TAG, "onSuccess: " + testBean.name);
//            }
//
//            @Override
//            public void onFail(Throwable e) {
//                Log.e(TAG, "onFail: " + e.getMessage());
//            }
//        });

        NetWorkApi.getService(GetRequestInterface.class).getUsername().compose(NetWorkApi.oppsObservableAndHandleError(new BaseObserver<TestBean>() {
            @Override
            public void onSuccess(TestBean testBean) {
                Log.e(TAG, "onSuccess: " + testBean.name);
            }

            @Override
            public void onFail(Throwable e) {
                Log.e(TAG, "onFail: " + e.getMessage());
            }
        }));

        WeakReference<Activity> mAct = new WeakReference<>(this);


        MyTaskAsync myTaskAsync = new MyTaskAsync(mAct);
        myTaskAsync.execute();

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                //doSomething
            }
        });

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //UI Update
            }
        });

        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }

        new Thread(new Runnable() {
            @SuppressLint("HandlerLeak")
            @Override
            public void run() {
                Looper.prepare();//子线程loop需要调用这个来创建loop对象
                new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                    }
                };
                Looper.loop();//开始循环
            }
        }).start();

    }


    //废弃 java 使用ExecutorService 替代
    static class MyTaskAsync extends AsyncTask<Void, Void, Void> {

        WeakReference<Activity> mAct = null;

        public MyTaskAsync(WeakReference<Activity> mAct) {
            super();
            this.mAct = mAct;
        }

        @Override
        protected Void doInBackground(Void... a) {

            try {
                Thread.sleep(1000 * 20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mAct.get() == null || mAct.get().isFinishing() || mAct.get().isDestroyed()) {
                System.out.println("ACT已经结束了 不能更新UI");
                return;
            }

        }

        @Override
        protected void onCancelled(Void unused) {
            super.onCancelled(unused);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
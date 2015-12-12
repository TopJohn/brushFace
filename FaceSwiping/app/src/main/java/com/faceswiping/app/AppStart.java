package com.faceswiping.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.faceswiping.app.activity.MainActivity;
import com.faceswiping.app.interf.BaseViewInterface;
import com.faceswiping.app.util.TDevice;
import com.faceswiping.app.util.UIHelper;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.kymjs.kjframe.utils.PreferenceHelper;

/**
 * 应用启动界面
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年12月22日 上午11:51:56
 */
public class AppStart extends Activity implements BaseViewInterface {

    private ImageView icon1;
    private ImageView icon2;

    private AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            String response = new String(responseBody);

            try {

                Result<String> result = new Gson().fromJson(response, new TypeToken<Result<String>>() {
                }.getType());

                if (result.getStatus().equals("ok")) {

                    AppContext.getInstance().updateToken(result.getData());
                   // AppContext.showToastShort("更新Token成功～！");
                }

            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseBody, e);
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }


    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止第三方跳转时出现双实例
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            aty.finish();
        }
        //SystemTool.gc(this); //针对性能好的手机使用，加快应用相应速度

        initView();
        initData();


    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果当前版本号比预存的版本号大则覆盖版本号，清空缓存
        int cacheVersion = PreferenceHelper.readInt(this, "first_install",
                "first_install", -1);
        int currentVersion = TDevice.getVersionCode();
        if (cacheVersion < currentVersion) {
            PreferenceHelper.write(this, "first_install", "first_install",
                    currentVersion);
            //清空图片缓存
           // cleanImageCache();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //    private void cleanImageCache() {
//        final File folder = FileUtils.getSaveFolder("OSChina/imagecache");
//        KJAsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                for (File file : folder.listFiles()) {
//                    file.delete();
//                }
//            }
//        });
//    }

    /**
     * 跳转到...
     */
    private void redirectTo() {
        // Intent uploadLog = new Intent(this, LogUploadService.class);
        // startService(uploadLog);
        if (AppContext.isFristStart()) {
            //引导界面
            //UIHelper.showAddGuestActivity(this);
            UIHelper.showSplashActivity(this);
            finish();

            //UIHelper.showMainActivity(this);
            //finish();

        } else {
            //之后启动

            if (AppContext.getInstance().isLogin()) {

                //FaceSwipingApi.updateToken(handler);

                UIHelper.showMainActivity(this);
                finish();
            } else {
                //跳到登录界面
                UIHelper.showLoginActivity(this);
                finish();
//                UIHelper.showMainActivity(this);
//                finish();

            }
        }
    }


    @Override
    public void initView() {


        final View view = View.inflate(this, R.layout.app_start, null);

        setContentView(view);

        icon1 = (ImageView) findViewById(R.id.icon_1);
        icon2 = (ImageView) findViewById(R.id.icon_2);

        //第一个Icon消失
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.alpha_out);
        final AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.slid_alpha_in);
        icon1.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                icon1.setVisibility(View.INVISIBLE);
                icon2.setVisibility(View.VISIBLE);
                icon2.startAnimation(animationSet);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2500);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    @Override
    public void initData() {

    }

}

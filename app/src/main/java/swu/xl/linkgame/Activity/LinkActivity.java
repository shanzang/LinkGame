package swu.xl.linkgame.Activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.gyf.immersionbar.ImmersionBar;

import org.litepal.LitePal;

import java.util.List;

import swu.xl.circleprogress.CircleProgress;
import swu.xl.linkgame.Constant.Constant;
import swu.xl.linkgame.Constant.Enum.PropMode;
import swu.xl.linkgame.Fragment.PauseFragment;
import swu.xl.linkgame.LinkGame.Constant.LinkConstant;
import swu.xl.linkgame.LinkGame.Manager.LinkManager;
import swu.xl.linkgame.LinkGame.Model.LinkInfo;
import swu.xl.linkgame.LinkGame.SelfView.AnimalView;
import swu.xl.linkgame.LinkGame.SelfView.XLRelativeLayout;
import swu.xl.linkgame.LinkGame.Utils.AnimalSearchUtil;
import swu.xl.linkgame.LinkGame.Utils.LinkUtil;
import swu.xl.linkgame.Model.XLLevel;
import swu.xl.linkgame.Model.XLUser;
import swu.xl.linkgame.Music.SoundPlayUtil;
import swu.xl.linkgame.R;
import swu.xl.linkgame.Util.PxUtil;
import swu.xl.linkgame.Util.ScreenUtil;
import swu.xl.numberitem.NumberOfItem;
import tyrantgit.explosionfield.ExplosionField;

public class LinkActivity extends BaseActivity implements View.OnClickListener,LinkManager.LinkGame {
    //屏幕宽度,高度
    int screenWidth;
    int screenHeight;

    //信息布局的bottom
    int message_bottom;

    //当前关卡模型数据
    XLLevel level;

    //用户
    XLUser user;

    //道具信息等布局
    RelativeLayout message_show_layout;

    //道具布局
    RelativeLayout props_layout;

    //时间信息等布局
    CircleProgress time_circle_progress;
    TextView time_s;
    //AnimalView的容器
    XLRelativeLayout link_layout;

    //存储点的信息集合
    LinkInfo linkInfo;

    //游戏管理者
    LinkManager manager;

    //显示关卡的文本
    TextView level_text;
    //暂停
    ImageView pause;

    //根布局
    RelativeLayout root_link;

    //粉碎视图
    private ExplosionField explosionField;

    //@Xml(layouts = "activity_link")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_link);

        //X2C.setContentView(this,R.layout.activity_link);

        //沉浸式状态栏
        ImmersionBar.with(this).barAlpha(1.0f).init();

        //加载数据
        initData();

        //加载视图
        initView();

        //创建分碎视图的类
        explosionField = ExplosionField.attach2Window(this);
    }

    /**
     * 加载数据
     */
    private void initData() {
        //获取数据
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        level = bundle.getParcelable("level");

        Log.d(Constant.TAG,"--------");
        Log.d(Constant.TAG, String.valueOf(level));

        //查询用户数据
        List<XLUser> users = LitePal.findAll(XLUser.class);
        user = users.get(0);

    }

    /**
     * 加载视图
     */
    private void initView() {
        screenWidth = ScreenUtil.getScreenWidth(getApplicationContext());
        screenHeight = ScreenUtil.getScreenHeight(getApplicationContext());
        Log.d(Constant.TAG,"屏幕宽度："+PxUtil.pxToDp(screenWidth,this)+" "+"屏幕高度："+PxUtil.pxToDp(screenWidth,this));

        linkInfo = new LinkInfo();

        manager = LinkManager.getLinkManager();

        message_show_layout = findViewById(R.id.message_show);
        message_show_layout.setPadding(0,ScreenUtil.getStateBarHeight(this)+ PxUtil.dpToPx(5,this),0,0);

        //CircleProgress相关设置
        time_circle_progress = findViewById(R.id.time_show);
        time_s = findViewById(R.id.time_s);
        //设置当前进度

        time_circle_progress.setProgress(LinkConstant.TIME);
        time_s.setText(LinkConstant.TIME + "");

        //设置位置
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                PxUtil.dpToPx(120, this),
                PxUtil.dpToPx(120, this)
        );
        layoutParams.setMargins(
                PxUtil.dpToPx(-50, this),
                ScreenUtil.getStateBarHeight(this) - PxUtil.dpToPx(20,this),
                0,0);
        //设置角度
        int angle1 = (int) Math.toDegrees(Math.atan(Math.sqrt(44) / 10));
        int angle2 = (int) Math.toDegrees(Math.atan(Math.sqrt(95) / 10));
        time_circle_progress.setStartAngle(270+angle1);
        time_circle_progress.setEndAngle(540-angle2);

        //设置进度颜色以及当前进度值以及总的进度值
        time_circle_progress.setProgress(90);
        time_circle_progress.setTotal_progress(90);
        time_circle_progress.getProgress_paint().setColor(Color.parseColor("#c2c2c2"));

        //设置游戏主题内容布局

        time_circle_progress.setLayoutParams(layoutParams);

        time_circle_progress.post(new Runnable() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {
                message_bottom = time_circle_progress.getBottom();


                link_layout = findViewById(R.id.link_layout);
                ViewGroup.LayoutParams params_link_layout = link_layout.getLayoutParams();
                params_link_layout.height = screenHeight-message_bottom;
                link_layout.setLayoutParams(params_link_layout);

                //监听触摸事件
                link_layout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, final MotionEvent event) {
                        //获取触摸点相对于布局的坐标
                        int x = (int) event.getX();
                        int y = (int) event.getY();

                        //触摸事件
                        if (event.getAction() == MotionEvent.ACTION_DOWN){
                            for (final AnimalView animal : manager.getAnimals()) {
                                //获取AnimalView实例的rect
                                RectF rectF = new RectF(
                                        animal.getLeft(),
                                        animal.getTop(),
                                        animal.getRight(),
                                        animal.getBottom());

                                //判断是否包含
                                if (rectF.contains(x,y) && animal.getVisibility() == View.VISIBLE){
                                    //获取上一次触摸的AnimalView
                                    final AnimalView lastAnimal = manager.getLastAnimal();

                                    //如果触摸的是石头直接结束
                                    if (animal.getFlag() == -1){
                                        //播放无法点击音效
                                        SoundPlayUtil.getInstance(getBaseContext()).play(5);

                                        break;
                                    }

                                    //如果不是第一次触摸 且 触摸的不是同一个点
                                    if (lastAnimal != null && lastAnimal != animal){

                                        Log.d(Constant.TAG,lastAnimal+" "+animal);

                                        //如果两者的图片相同，且两者可以连接
                                        if(animal.getFlag() == lastAnimal.getFlag() &&
                                                AnimalSearchUtil.canMatchTwoAnimalWithTwoBreak(
                                                        manager.getBoard(),
                                                        lastAnimal.getPoint(),
                                                        animal.getPoint(),
                                                        linkInfo
                                                )){
                                            //播放无法点击音效
                                            SoundPlayUtil.getInstance(getBaseContext()).play(3);

                                            //当前点改变背景和动画
                                            animal.changeAnimalBackground(LinkConstant.ANIMAL_SELECT_BG);

                                            //画线
                                            link_layout.setLinkInfo(linkInfo);

                                            //设置所有的宝可梦不可以点击
                                            link_layout.setEnabled(false);

                                            //延迟操作
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    //播放消除音效
                                                    SoundPlayUtil.getInstance(getBaseContext()).play(4);

                                                    //修改模板
                                                    manager.getBoard()[lastAnimal.getPoint().x][lastAnimal.getPoint().y] = 0;
                                                    manager.getBoard()[animal.getPoint().x][animal.getPoint().y] = 0;

                                                    //粉碎
                                                    //explosionField.explode(lastAnimal);
                                                    //explosionField.explode(animal);

                                                    //隐藏
                                                    lastAnimal.setVisibility(View.INVISIBLE);
                                                    lastAnimal.clearAnimation();
                                                    animal.setVisibility(View.INVISIBLE);
                                                    animal.clearAnimation();

                                                    //上一个点置空
                                                    manager.setLastAnimal(null);

                                                    //去线
                                                    link_layout.setLinkInfo(null);


                                                    //设置所有的宝可梦可以点击
                                                    link_layout.setEnabled(true);
                                                }
                                            },500);
                                        }else {
                                            //点击的两个图片不可以相连接

                                            //播放点击音效
                                            SoundPlayUtil.getInstance(getBaseContext()).play(3);

                                            //上一个点恢复原样
                                            lastAnimal.changeAnimalBackground(LinkConstant.ANIMAL_BG);
                                            if (lastAnimal.getAnimation() != null){
                                                //清楚所有动画
                                                lastAnimal.clearAnimation();
                                            }

                                            //设置当前点的背景颜色和动画
                                            animal.changeAnimalBackground(LinkConstant.ANIMAL_SELECT_BG);

                                            //将当前点作为选中点
                                            manager.setLastAnimal(animal);
                                        }
                                    }else if (lastAnimal == null){
                                        //播放点击音效
                                        SoundPlayUtil.getInstance(getBaseContext()).play(3);

                                        //第一次触摸 当前点改变背景和动画
                                        animal.changeAnimalBackground(LinkConstant.ANIMAL_SELECT_BG);

                                        //将当前点作为选中点
                                        manager.setLastAnimal(animal);
                                    }

                                    break;
                                }
                            }
                        }

                        return true;
                    }
                });

                //开始游戏
                manager.startGame(getApplicationContext(),
                        link_layout,
                        screenWidth,
                        screenHeight-message_bottom-ScreenUtil.getNavigationBarHeight(getApplicationContext()),
                        level.getL_id(),
                        level.getL_mode()
                );


                //设置监听者
                manager.setListener(LinkActivity.this);
            }
        });

        level_text = findViewById(R.id.link_level_text);
        level_text.setText(String.valueOf(level.getL_id()));

        pause = findViewById(R.id.link_pause);
        pause.setOnClickListener(this);

        root_link = findViewById(R.id.root_link);
    }



    //点击事件
    @Override
    public void onClick(View v) {
        //播放点击音效
        SoundPlayUtil.getInstance(getBaseContext()).play(3);

        switch (v.getId()){

            case R.id.link_pause:
                Log.d(Constant.TAG,"暂停");

                //暂停游戏
                //1.定时器暂停
                manager.pauseGame();

                //2.添加一个fragment
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                final PauseFragment pause = new PauseFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("level",level);
                pause.setArguments(bundle);
                transaction.replace(R.id.root_link,pause,"pause");
                transaction.commit();

                break;
        }
    }

    //时间发生改变的时间
    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeChanged(float time) {

        //如果时间小于0
        if (time <= 0.0){
            manager.pauseGame();
            manager.endGame(this,level,time);
        }else {
            //保留小数后一位
            //time_circle_progress.setProgress(time);

            time_circle_progress.setProgress(time);
            time_s.setText((int)time +"");

        }

        //如果board全部清除了
        if (LinkUtil.getBoardState()){
            //结束游戏
            manager.pauseGame();
            level.setL_time((int) (LinkConstant.TIME -time));
            level.setL_new(LinkUtil.getStarByTime((int) time));
            manager.endGame(this,level,time);

            //关卡结算
            level.update(level.getId());

            //下一关判断
            XLLevel next_level = LitePal.find(XLLevel.class, level.getId() + 1);
            if (next_level.getL_new() == '0'){
                next_level.setL_new('4');
                next_level.update(level.getId()+1);
            }

            user.update(1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //暂停游戏
        manager.pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //开启游戏
        if (manager.isPause()){
            manager.pauseGame();
        }
    }
}

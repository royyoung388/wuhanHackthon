package world.ypc.hackthon;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnTouch;
import world.ypc.hackthon.view.CircleImage;

public class MonsterActivity extends BaseActivity {

    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;
    @BindViews({R.id.piano1, R.id.piano2, R.id.piano3, R.id.piano4, R.id.piano5, R.id.piano6, R.id.piano7})
    List<ImageView> pianos;
    @BindViews({R.id.monster1, R.id.monster2, R.id.monster3, R.id.monster4, R.id.monster5, R.id.monster6, R.id.monster7})
    List<ImageView> monsters;
    @BindViews({R.id.bg_color, R.id.bg_color1, R.id.bg_color2, R.id.bg_color3, R.id.bg_color4, R.id.bg_color5, R.id.bg_color6, R.id.bg_color7,})
    List<CircleImage> circleImages;

    int curr;

    String network = "http://192.168.2.4:8000";
    @BindView(R.id.end_light)
    ImageView endLight;
    @BindView(R.id.end_person)
    ImageView endPerson;

    private int count = 0;
    private final static int all = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, MonsterActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setView() {
        return R.layout.activity_monster;
    }

    private void init() {
        soundPool = new SoundPool.Builder().setMaxStreams(2).build();

        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(1, soundPool.load(this, R.raw.piano1, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.piano2, 1));
        soundPoolMap.put(3, soundPool.load(this, R.raw.piano3, 1));
        soundPoolMap.put(4, soundPool.load(this, R.raw.piano4, 1));
        soundPoolMap.put(5, soundPool.load(this, R.raw.piano5, 1));
        soundPoolMap.put(6, soundPool.load(this, R.raw.piano6, 1));
        soundPoolMap.put(7, soundPool.load(this, R.raw.piano7, 1));
        soundPoolMap.put(8, soundPool.load(this, R.raw.percent30, 1));
        soundPoolMap.put(9, soundPool.load(this, R.raw.percent50, 1));
        soundPoolMap.put(10, soundPool.load(this, R.raw.percent100, 1));
    }

    @OnTouch({R.id.piano1, R.id.piano2, R.id.piano3, R.id.piano4, R.id.piano5, R.id.piano6, R.id.piano7})
    public boolean onPianoTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                http(pianos.indexOf(view) + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            click();

            playSound(pianos.indexOf(view) + 1);
            changeBg(pianos.indexOf(view));
        }
        if (event.getAction() == MotionEvent.ACTION_UP)
            changeBg(pianos.indexOf(view));

        return true;
    }

    //点击怪物
    @OnTouch({R.id.monster1, R.id.monster2, R.id.monster3, R.id.monster4, R.id.monster5, R.id.monster6, R.id.monster7})
    public boolean onMonsterTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                http(monsters.indexOf(view) + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            click();

            changeBg(monsters.indexOf(view));

            playSound(monsters.indexOf(view) + 1);

            scale(view);

            for (ImageView m : monsters) {
                shake(m, 0, 3, -3, 3, -3, 3, -3, 3);
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            changeBg(monsters.indexOf(view));

            disappear(view);

            for (ImageView m : monsters) {
                if (m != view) {
                    shake(m, 3, -3, 3, -3, 3, -3, 0);
                }
            }
        }
        return true;
    }

    //播放音乐
    public void playSound(int sound) {
        System.out.println("play sound: " + sound);

        AudioManager mgr = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        //参数：1、Map中取值   2、当前音量     3、最大音量  4、优先级   5、重播次数   6、播放速度
        soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);
    }

    //变化背景
    private void changeBg(int index) {

        System.out.println("change bg " + index);

        CircleImage image = circleImages.get(index + 1);
        AnimatorSet animatorSet = new AnimatorSet();
        if (curr == 0) {
            ObjectAnimator a1 = ObjectAnimator.ofFloat(image, "scaleX", 1, 120);
            ObjectAnimator a2 = ObjectAnimator.ofFloat(image, "scaleY", 1, 120);
            animatorSet.play(a1).with(a2);
            animatorSet.setDuration(600);
            animatorSet.start();
            curr = index + 1;
        } else {
            ObjectAnimator a1 = ObjectAnimator.ofFloat(circleImages.get(curr), "scaleX", 120, 1);
            ObjectAnimator a2 = ObjectAnimator.ofFloat(circleImages.get(curr), "scaleY", 120, 1);
            animatorSet.play(a1).with(a2);
            animatorSet.setDuration(600);
            animatorSet.start();
            curr = 0;
        }
    }

    //放大动画
    private void scale(View monster) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator a1 = ObjectAnimator.ofFloat(monster, "scaleX", 1f, 1.2f);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(monster, "scaleY", 1f, 1.2f);

        View piano = pianos.get(monsters.indexOf(monster));
        ObjectAnimator a3 = ObjectAnimator.ofFloat(piano, "scaleX", 1f, 1.1f);
        ObjectAnimator a4 = ObjectAnimator.ofFloat(piano, "scaleY", 1f, 1.1f);

        animatorSet.setDuration(320);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.play(a1).with(a2).with(a3).with(a4);
        animatorSet.start();
    }

    //摆动
    private void shake(View monster, float... radius) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(monster, "rotation", radius);
        animator.setInterpolator(new DecelerateInterpolator(0.6f));
        animator.setDuration(1100);
        animator.start();
    }

    //消失动画
    private void disappear(View monster) {
        ObjectAnimator a1 = ObjectAnimator.ofFloat(monster, "rotation", 0, 380);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(monster, "alpha", 1f, 0f);
        a2.setDuration(700);
        ObjectAnimator a3 = ObjectAnimator.ofFloat(monster, "scaleX", 1f, 0.4f);
        ObjectAnimator a4 = ObjectAnimator.ofFloat(monster, "scaleY", 1f, 0.4f);

        View piano = pianos.get(monsters.indexOf(monster));
        ObjectAnimator a5 = ObjectAnimator.ofFloat(piano, "scaleX", 1f, 0.98f);
        ObjectAnimator a6 = ObjectAnimator.ofFloat(piano, "scaleY", 1f, 0.98f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.setDuration(320);
        animatorSet.play(a1).with(a2).with(a3).with(a4).with(a5).with(a6);
        animatorSet.start();

        monster.setVisibility(View.GONE);
    }

    private void click() {
        count++;

        System.out.println("click " + count);

        if (count == (int) (0.3 * all)) {
            playSound(8);
        } else if (count == (int) (0.5 * all)) {
            playSound(9);
        } else if (count == all) {
            playSound(10);
            gameEnd();
        }

    }

    private void gameEnd() {
        endLight.setVisibility(View.VISIBLE);
        endPerson.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator a1 = ObjectAnimator.ofFloat(endLight, "scaleX", 0f, 1f);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(endLight, "scaleY", 0f, 1f);
        ObjectAnimator a3 = ObjectAnimator.ofFloat(endLight, "alpha", 0f, 1f);

        ObjectAnimator a4 = ObjectAnimator.ofFloat(endPerson, "scaleX", 0f, 1f);
        ObjectAnimator a5 = ObjectAnimator.ofFloat(endPerson, "scaleY", 0f, 1);
        ObjectAnimator a6 = ObjectAnimator.ofFloat(endPerson, "alpha", 0f, 1f);

        ObjectAnimator a7 = ObjectAnimator.ofFloat(endLight, "rotation", 0, 2, -2, 2, -2, 2, -2, 0);
        a7.setInterpolator(new DecelerateInterpolator());

        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(a1).with(a2).with(a3);
        animatorSet.start();
    }

    public void http(int index) {
        System.out.println("http: " + index);

        new Thread(() -> {
            try {
                URL url = new URL(network + "?key=" + index);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置连接超时为5秒
                conn.setConnectTimeout(3000);
                // 设置请求类型为Get类型
                conn.setRequestMethod("GET");
                // 判断请求Url是否成功
                if (conn.getResponseCode() != 200) {
                    System.out.println("请求url失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}

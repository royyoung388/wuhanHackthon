package world.ypc.hackthon;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.monster)
    TextView monster;
    @BindView(R.id.drum)
    TextView drum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setView() {
        return R.layout.activity_main;
    }

    @OnClick({R.id.monster, R.id.drum})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.monster:
                MonsterActivity.start(this);
                break;
            case R.id.drum:
                Toast.makeText(this, "点我也不理你", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

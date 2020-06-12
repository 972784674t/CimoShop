package com.example.cimoshop.ui.shopcat;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.cimoshop.R;
import com.example.cimoshop.adapter.ShopCarAdapter;
import com.example.cimoshop.db.UserDAO;
import com.example.cimoshop.entity.Pixabay;
import com.example.cimoshop.entity.UserShopCar;
import com.example.cimoshop.utils.SharedPrefsTools;
import com.example.cimoshop.utils.UITools;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uk.co.senab.photoview.log.LoggerDefault;


/**
 * @author 谭海山
 */
public class ShopCatFragment extends Fragment {

    private static final String TAG = "ShopCar";

    private MaterialToolbar toolbar;
    private RecyclerView shopCarRecyclerView;
    private ShopCarAdapter shopCarAdapter;
    private CheckBox selectAllShopCarItem;

    /**
     * 数据源
     */
    private static ArrayList<UserShopCar> SHOP_CAR_ITEM_LIST = null;

    /**
     * 当前用户名
     */
    private static String USER_NAME = null;

    /**
     * 关联 checkbox 和 item
     */
//    private static HashMap<Integer, Boolean> IS_ITEM_CHECKED_HASMAP;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shopcat, container, false);
        initViewAndDataSource(root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initShopCarRecycleView();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: getShopCarList()");
        getShopCarList();
        Log.d(TAG, "onResume: DATASOUCE -> "+SHOP_CAR_ITEM_LIST.size());
        shopCarAdapter.setDiffNewData(SHOP_CAR_ITEM_LIST);
    }

    /**
     * 初始化视图控件和数据源
     * @param root
     */
    private void initViewAndDataSource(View root) {

        toolbar = root.findViewById(R.id.shopCatToobar);
        //状态栏文字透明
        UITools.makeStatusBarTransparent(getActivity());

        //修复标题栏与状态栏重叠
        UITools.fitTitleBar(getActivity(),toolbar);
        getShopCarList();
        toolbar.setTitle("购物车：( "+SHOP_CAR_ITEM_LIST.size()+" )");
        shopCarRecyclerView = root.findViewById(R.id.shopCarRecyclerView);

        //全选操作
        selectAllShopCarItem = root.findViewById(R.id.selectAllShopCarItem);
        selectAllShopCarItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    shopCarAdapter.selectAllItem();
                } else {
                    shopCarAdapter.unSelectAllItem();
                }
            }
        });

    }

    /**
     * 获取购物车数据
     */
    private void getShopCarList() {
        USER_NAME = SharedPrefsTools.getInstance(getActivity().getApplication()).getUserInfo().getLogin();
        SHOP_CAR_ITEM_LIST = UserDAO.getInstance(getContext()).getShopCarList(USER_NAME);
    }

    /**
     * 初始化购物车RecycleView以及拖拽效果和滑动删除效果事件处理
     */
    private void initShopCarRecycleView() {
        shopCarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shopCarAdapter = new ShopCarAdapter();
        shopCarAdapter.setDiffCallback(new MyDiffCallback());
        shopCarAdapter.setEmptyView(initEmptyView());
        shopCarAdapter.setDiffNewData(SHOP_CAR_ITEM_LIST);

        //允许拖拽并设置拖拽监听
        shopCarAdapter.getDraggableModule().setDragEnabled(true);
        shopCarAdapter.getDraggableModule().setOnItemDragListener(initOnItemDragListener());

        //允许侧滑
        shopCarAdapter.getDraggableModule().setSwipeEnabled(true);

        //开启动画
        shopCarAdapter.setAnimationEnable(true);
        //动画只执行一次
        shopCarAdapter.setAnimationFirstOnly(false);
        //item从左边进入
        shopCarAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInLeft);

        //只允许从左侧滑动删除
        shopCarAdapter.getDraggableModule().getItemTouchHelperCallback().setSwipeMoveFlags(ItemTouchHelper.START);
        shopCarAdapter.getDraggableModule().setOnItemSwipeListener(initOnItemSwipeListener());
        shopCarRecyclerView.setAdapter(shopCarAdapter);

        shopCarAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
            }
        });

        //ItemChild点击事件
        shopCarAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                CheckBox checkBox = view.findViewById(R.id.shopCarItemcheckBox);
                if (checkBox.isChecked()){
                    shopCarAdapter.selectItem(position);
                } else {
                    shopCarAdapter.unSelectIem(position);
                    //selectAllShopCarItem.setChecked(false);
                }
            }
        });

    }

    /**
     * 侧滑事件监听处理
     * @return OnItemSwipeListener
     */
    private OnItemSwipeListener initOnItemSwipeListener() {
        return new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "侧滑开始: " + pos);
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "侧滑结束: " + pos);
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "删除视图: " + pos);
                TextView price = viewHolder.itemView.findViewById(R.id.shopcaritemprice);
                TextView size = viewHolder.itemView.findViewById(R.id.shopcaritemsize);
                String imageSize = size.getText().toString();
                String imagePrice = price.getText().toString();
                if( UserDAO.getInstance(getContext()).delImageFromShopCar(imageSize,imagePrice) ){
                    Toast.makeText(getContext(),"删除图片id："+pos+" 成功",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"删除图片id："+pos+" 失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
                Log.d(TAG, "侧滑中->   X:" + dX +"   Y:"+ dY);
                canvas.drawColor(ContextCompat.getColor(getContext(), R.color.warning));
            }
        };
    }

    /**
     * 拖拽事件监听处理
     * @return OnItemDragListener
     */
    private OnItemDragListener initOnItemDragListener() {

        return new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "开始拖拽");
                final BaseViewHolder holder = ((BaseViewHolder) viewHolder);

                // 开始拖砖时，item背景色变化，使得自然拖拽更自然
                int startColor = Color.WHITE;
                int endColor = Color.rgb(245, 245, 245);
                //如果当前版本大于5.0则可以执行动画
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
                    v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            holder.itemView.setBackgroundColor((int)animation.getAnimatedValue());
                        }
                    });
                    //动画持续时间
                    v.setDuration(300);
                    v.start();
                }
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "结束拖拽");
                final BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                // 结束拖砖时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                int startColor = Color.rgb(245, 245, 245);
                int endColor = Color.WHITE;
                //如果当前版本大于5.0则可以执行动画
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
                    v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            holder.itemView.setBackgroundColor((int)animation.getAnimatedValue());
                        }
                    });
                    //动画持续时间
                    v.setDuration(300);
                    v.start();
                }
            }
        };
    }

    /**
     * 初始化空列表视图
     * @return 空列表视图 view
     */
    private View initEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.emptyview, null);
        TextView emptyTextView = emptyView.findViewById(R.id.emptytextView);
        emptyTextView.setText("Oops！购物车空空如野");
        return emptyView;
    }

    /**
     * 指定DiffUtil类，判断item是否相同
     */
    static class MyDiffCallback extends DiffUtil.ItemCallback<UserShopCar>{

        @Override
        public boolean areItemsTheSame(@NonNull UserShopCar oldItem, @NonNull UserShopCar newItem) {
            return oldItem.getShopCarItemUrl() == newItem.getShopCarItemUrl();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull UserShopCar oldItem, @NonNull UserShopCar newItem) {
            return oldItem.equals(newItem) ;
        }

    }

}

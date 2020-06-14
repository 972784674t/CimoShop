package com.example.cimoshop.ui.shopcat;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.cimoshop.R;
import com.example.cimoshop.adapter.ShopCarAdapter;
import com.example.cimoshop.alipay.AlipayOfSandbox;
import com.example.cimoshop.db.UserDAO;
import com.example.cimoshop.entity.User;
import com.example.cimoshop.entity.UserShopCar;
import com.example.cimoshop.ui.home.HomeFragment;
import com.example.cimoshop.utils.SharedPrefsTools;
import com.example.cimoshop.utils.UITools;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 谭海山
 */
public class ShopCatFragment extends Fragment {

    private static final String TAG = "ShopCar";

    private static final int RESULT_CODE_ALIPAY = 10086;

    private MaterialToolbar toolbar;
    private RecyclerView shopCarRecyclerView;
    private ShopCarAdapter shopCarAdapter;
    private CheckBox selectAllShopCarItemCheakBox;
    private TextView selectedImageNumber;
    private TextView tatolPrice;
    private MaterialButton SettlementButton;
    private BottomNavigationView bottomNavigationView;

    /**
     * 数据源
     */
    private static ArrayList<UserShopCar> SHOP_CAR_ITEM_LIST = null;

    /**
     * 当前用户名
     */
    private static String USER_NAME = null;

    /**
     * 如果 token 不为空,则用户已经登录
     */
    private String isToken;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_shopcat, container, false);
        isToken = SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github");
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
        isToken = SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github");
        if ( !isToken.equals("null") ){
            getShopCarList();
            shopCarAdapter.setDiffNewData(SHOP_CAR_ITEM_LIST);
            toolbar.setTitle("购物车：( "+SHOP_CAR_ITEM_LIST.size()+" )");
        } else {
            SHOP_CAR_ITEM_LIST.clear();
            ShopCarAdapter.SHOPPING_BAG.clear();
            toolbar.setTitle("购物车");
            shopCarAdapter.setDiffNewData(SHOP_CAR_ITEM_LIST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_CODE_ALIPAY:
                String payResult = data.getExtras().getString("payResult");
                String resultStatus = data.getExtras().getString("resultStatus");
                Log.d(TAG, "onActivityResult: \npayResult -> "+payResult+"\nresultStatus -> "+resultStatus);

                //将购物袋中选中的商品加入临时购物袋
                HashMap<Integer,UserShopCar> temporaryShoppingBags = new HashMap<>();
                for (int i = 0; i < shopCarAdapter.getShoppingBag().size(); i++){
                    if ( shopCarAdapter.getShoppingBag().get(i).isCheck() ){
                        temporaryShoppingBags.put(i,shopCarAdapter.getShoppingBag().get(i));
                    }
                }
                Log.d(TAG, "购物袋中选中的图片 -> "+temporaryShoppingBags.size()+"\n"+temporaryShoppingBags.toString());

                //更新数据库
                if ( UserDAO.getInstance(getContext()).addImageToUserWareHouses(temporaryShoppingBags) ){
                    Toast.makeText(getContext(),"您都买的商品已经加入到您的仓库中了哦",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"Oop,系统出了点问题，请联系管理员",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 初始化视图控件和数据源
     * @param root
     */
    private void initViewAndDataSource(View root) {

        toolbar = root.findViewById(R.id.shopCatToobar);
        shopCarRecyclerView = root.findViewById(R.id.shopCarRecyclerView);
        selectAllShopCarItemCheakBox = root.findViewById(R.id.selectAllShopCarItem);
        selectedImageNumber = root.findViewById(R.id.selectedImageNumber);
        tatolPrice = root.findViewById(R.id.totalPrice);
        SettlementButton = root.findViewById(R.id.SettlementButton);

        //状态栏文字透明
        UITools.makeStatusBarTransparent(getActivity());

        //修复标题栏与状态栏重叠
        UITools.fitTitleBar(getActivity(),toolbar);

        getShopCarList();

        if ( SHOP_CAR_ITEM_LIST.size() != 0 ){
            toolbar.setTitle("购物车：( "+SHOP_CAR_ITEM_LIST.size()+" )");
        } else {
            toolbar.setTitle("购物车");
        }

        //全选操作
        selectAllShopCarItemCheakBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( selectAllShopCarItemCheakBox.isChecked() ){
                    shopCarAdapter.selectAllItem();
                } else {
                    shopCarAdapter.unSelectAllItem();
                }
            }
        });

        //结算操作
        SettlementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isToken.equals("null") ){
                    Toast.makeText(getContext(),"请先登录哦",Toast.LENGTH_SHORT).show();
                    return;
                }
                String tp = tatolPrice.getText().toString();
                String imageNumber = selectedImageNumber.getText().toString();
                if( tp.equals("0") ){
                    Toast.makeText(getContext(),"请先选择要结算的图片哦",Toast.LENGTH_SHORT).show();
                } else {
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("请确认结算信息")
                            .setMessage("当前选择："+imageNumber+" 张图片\n"+"总价："+tp+" 元")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(),AlipayOfSandbox.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("totalPrice",tp);
                                    bundle.putString("imageNumber",imageNumber);
                                    intent.putExtras(bundle);
                                    startActivityForResult(intent,RESULT_CODE_ALIPAY);
                                }
                            })
                            .show();
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

        //初始化适配器并设置数据源
        shopCarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shopCarAdapter = new ShopCarAdapter();
        shopCarAdapter.setDiffCallback(new MyDiffCallback());

        //设置空视图
        if ( !isToken.equals("null") ){
            shopCarAdapter.setEmptyView(initEmptyView("Oops！购物车空空如野"));
        } else {
            shopCarAdapter.setEmptyView(initEmptyView("请先登录哦"));
        }

        //设置没有登录时的 toolbar
        if ( SHOP_CAR_ITEM_LIST.size() != 0 ){
            toolbar.setTitle("购物车：( "+SHOP_CAR_ITEM_LIST.size()+" )");
        } else {
            toolbar.setTitle("购物车");
        }

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

        //设置拖拽与滑动，并且只允许从左侧滑动删除
        shopCarAdapter.getDraggableModule().getItemTouchHelperCallback().setSwipeMoveFlags(ItemTouchHelper.START);
        shopCarAdapter.getDraggableModule().setOnItemSwipeListener(initOnItemSwipeListener());

        shopCarRecyclerView.setAdapter(shopCarAdapter);

        shopCarAdapter.connectionAdapterAndSettlementUI(selectAllShopCarItemCheakBox,selectedImageNumber,tatolPrice);

        //设置数据源
        shopCarAdapter.setDiffNewData(SHOP_CAR_ITEM_LIST);

        //ItemChild点击事件
        shopCarAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                CheckBox checkBox = view.findViewById(R.id.shopCarItemcheckBox);
                if (checkBox.isChecked()){
                    shopCarAdapter.selectItem(position);
                } else {
                    shopCarAdapter.unSelectIem(position);
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
                    updataBottomNavgationView();
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
     * 更新底部导航栏 UI
     */
    private void updataBottomNavgationView() {
        int t = SHOP_CAR_ITEM_LIST.size()-1;
        toolbar.setTitle("购物车：( "+t+" )");
        bottomNavigationView = (BottomNavigationView) getParentFragment().getView().findViewById(R.id.bv);
        BadgeDrawable shopCat = bottomNavigationView.getOrCreateBadge(R.id.navigation_shopCat);
        shopCat.setNumber(t);
        shopCat.setVisible(false);
    }

    /**
     * 初始化空列表视图
     * @return 空列表视图 view
     */
    private View initEmptyView(String tip) {
        View emptyView = getLayoutInflater().inflate(R.layout.emptyview, null);
        TextView emptyTextView = emptyView.findViewById(R.id.emptytextView);
        emptyTextView.setText(tip);
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

package com.example.cimoshop.ui.personalcenter.mywarehouse;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.cimoshop.R;
import com.example.cimoshop.adapter.MyWareHouseAdapter;
import com.example.cimoshop.db.UserDAO;
import com.example.cimoshop.utils.SharedPrefsTools;

import java.util.ArrayList;

/**
 * @author 谭海山
 */
public class MyWareHouse extends Fragment {

    private static final String TAG = "MyWareHouse";

    /**
     * 数据源
     */
    private ArrayList<String> wareHouseItemList;

    /**
     * 当前用户
     */
    private static String USER_NAME = null;

    /**
     * 如果 token 不为空,则用户已经登录
     */
    private String isToken;
    private RecyclerView myWareHouseRecycleView;
    private MyWareHouseAdapter myWareHouseAdapter;

    public static MyWareHouse newInstance() {
        return new MyWareHouse();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_my_ware_house, container, false);
        myWareHouseRecycleView = root.findViewById(R.id.myWareHouseRecycleView);
        isToken = SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github");
        Log.d(TAG, "onCreateView: " + isToken);
        if (!"null".equals(isToken)) {
            USER_NAME = SharedPrefsTools.getInstance(getActivity().getApplication()).getUserInfo().getLogin();
            wareHouseItemList = UserDAO.getInstance(getContext()).getUserWareHousesList(UserDAO.getInstance(getContext()).findUserByUserName(USER_NAME).getUserId());
        } else {
            wareHouseItemList = null;
        }
        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMyWareHouseRecycleView();
    }

    /**
     * 初始化本页面的 RecyclerView
     */
    private void initMyWareHouseRecycleView() {
        myWareHouseAdapter = new MyWareHouseAdapter();
        myWareHouseRecycleView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        myWareHouseRecycleView.setAdapter(myWareHouseAdapter);
        myWareHouseAdapter.setDiffCallback(new MyDiffCallback());
        myWareHouseAdapter.setEmptyView(initEmptyView());
        myWareHouseAdapter.setDiffNewData(wareHouseItemList);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!"null".equals(isToken)) {
            USER_NAME = SharedPrefsTools.getInstance(getActivity().getApplication()).getUserInfo().getLogin();
            wareHouseItemList = UserDAO.getInstance(getContext()).getUserWareHousesList(UserDAO.getInstance(getContext()).findUserByUserName(USER_NAME).getUserId());
        }
        myWareHouseAdapter.setDiffNewData(wareHouseItemList);
    }

    /**
     * 初始化空列表视图
     *
     * @return 空列表视图 view
     */
    private View initEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.view_empty, null);
        TextView emptyTextView = emptyView.findViewById(R.id.emptytextView);
        if ("null".equals(isToken)) {
            emptyTextView.setText("您还没有登录哦");
        } else {
            emptyTextView.setText("您还没有购买任何图片哦");
        }
        return emptyView;
    }

    /**
     * 指定DiffUtil类，判断item是否相同
     */
    static class MyDiffCallback extends DiffUtil.ItemCallback<String> {


        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    }

}

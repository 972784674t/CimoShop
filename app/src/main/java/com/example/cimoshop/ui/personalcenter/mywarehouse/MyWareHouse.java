package com.example.cimoshop.ui.personalcenter.mywarehouse;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cimoshop.R;
import com.example.cimoshop.adapter.MyWareHouseAdapter;
import com.example.cimoshop.utils.SharedPrefsTools;

/**
 * @author 谭海山
 */
public class MyWareHouse extends Fragment {

    /**
     * 如果 token 不为空,则用户已经登录
     */
    private String isToken;
    private RecyclerView myWareHouseRecycleView;

    public static MyWareHouse newInstance() {
        return new MyWareHouse();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        isToken = SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github");
        View root = inflater.inflate(R.layout.my_ware_house_fragment, container, false);
        myWareHouseRecycleView = root.findViewById(R.id.myWareHouseRecycleView);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyWareHouseAdapter myWareHouseAdapter = new MyWareHouseAdapter(null);
        myWareHouseRecycleView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        myWareHouseRecycleView.setAdapter(myWareHouseAdapter);
        myWareHouseAdapter.setEmptyView(initEmptyView());
    }

    /**
     * 初始化空列表视图
     * @return 空列表视图 view
     */
    private View initEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.emptyview, null);
        TextView emptyTextView = emptyView.findViewById(R.id.emptytextView);
        if ("null".equals(isToken)) {
            emptyTextView.setText("您还没有登录哦");
        } else {
            emptyTextView.setText("您还没有购买任何图片哦");
        }
        return emptyView;
    }

}

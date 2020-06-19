package com.example.cimoshop.ui.personalcenter.myworks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cimoshop.R;
import com.example.cimoshop.adapter.MyWorksAdapter;
import com.example.cimoshop.utils.SharedPrefsTools;

/**
 * @author 谭海山
 */
public class MyWorks extends Fragment {

    private static final String TAG = "MyWorks";

    /**
     * 如果 token 不为空,则用户已经登录
     */
    private RecyclerView myWorksRecyclerView;

    private String isToken;

    public static MyWorks newInstance() {
        return new MyWorks();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_my_works, container, false);
        myWorksRecyclerView = root.findViewById(R.id.myWorksRecyclerView);
        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isToken = SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github");
        MyWorksAdapter myWorksAdapter = new MyWorksAdapter();
        myWorksRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        myWorksRecyclerView.setAdapter(myWorksAdapter);
        myWorksAdapter.setEmptyView(initEmptyView());
    }

    /**
     * 初始化空列表视图
     * @return 空列表视图 view
     */
    private View initEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.view_empty, null);
        TextView emptyTextView = emptyView.findViewById(R.id.emptytextView);
        Log.d(TAG, "initEmptyView: "+isToken);
        if ("null".equals(isToken)) {
            emptyTextView.setText("您还没有登录哦");
        } else {
            emptyTextView.setText("您还没有上传任何图片哦");
        }
        return emptyView;
    }

}

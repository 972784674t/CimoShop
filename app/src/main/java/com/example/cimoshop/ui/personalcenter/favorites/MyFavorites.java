package com.example.cimoshop.ui.personalcenter.favorites;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cimoshop.R;
import com.example.cimoshop.adapter.FavoritesImageAdapter;
import com.example.cimoshop.db.UserDAO;
import com.example.cimoshop.entity.Pixabay;
import com.example.cimoshop.utils.SharedPrefsTools;

import java.util.ArrayList;

/**
 * @author 谭海山
 */
public class MyFavorites extends Fragment {

    private static final String TAG = "cimoshopMyFavorites";

    /**
     * 数据源
     */
    private ArrayList<String> favoriteImgList;

    /**
     * 当前用户
     */
    private static String USER_NAME = null;

    /**
     * 如果 token 不为空,则用户已经登录
     */
    private String isToken;

    private RecyclerView recyclerView;
    private FavoritesImageAdapter favoritesImageAdapter;

    public static MyFavorites newInstance() {
        return new MyFavorites();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.my_favorites_fragment, container, false);
        recyclerView = root.findViewById(R.id.favoriteimgrecycleview);
        isToken = SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github");
        Log.d(TAG, "onCreateView: "+isToken);
        if (!"null".equals(isToken)) {
            USER_NAME =  SharedPrefsTools.getInstance(getActivity().getApplication()).getUserInfo().getLogin();
            favoriteImgList = UserDAO.getInstance(getContext()).getUserFavoriteImageList(UserDAO.getInstance(getContext()).findUserByUserName(USER_NAME).getUserId());
        } else {
            favoriteImgList = null;
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated");
        Log.d(TAG,"我的收藏list："+favoriteImgList);
        if (!isToken.equals("null")){
            initFavoriteRecyclerView(favoriteImgList);
        } else {
            initFavoriteRecyclerView(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!"null".equals(isToken)) {
            USER_NAME =  SharedPrefsTools.getInstance(getActivity().getApplication()).getUserInfo().getLogin();
            favoriteImgList = UserDAO.getInstance(getContext()).getUserFavoriteImageList(UserDAO.getInstance(getContext()).findUserByUserName(USER_NAME).getUserId());
        }
        favoritesImageAdapter.setDiffNewData(favoriteImgList);
    }

    /**
     * 初始化本页面的RecyclerView
     * @param favoriteImgList 数据源
     */
    private void initFavoriteRecyclerView(ArrayList<String> favoriteImgList) {
        Log.d(TAG, "initFavoriteRecyclerView: ");
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        favoritesImageAdapter = new FavoritesImageAdapter();
        favoritesImageAdapter.setEmptyView(initEmptyView());
        favoritesImageAdapter.setDiffCallback(new MyDiffCallback());
        favoritesImageAdapter.setDiffNewData(favoriteImgList);
        recyclerView.setAdapter(favoritesImageAdapter);
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
            emptyTextView.setText("您还没有收藏任何图片哦");
        }
        return emptyView;
    }

    /**
     * 指定DiffUtil类，判断item是否相同
     */
    static class MyDiffCallback extends DiffUtil.ItemCallback<String>{


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

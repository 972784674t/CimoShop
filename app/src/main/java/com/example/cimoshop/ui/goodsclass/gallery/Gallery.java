package com.example.cimoshop.ui.goodsclass.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.example.cimoshop.R;
import com.example.cimoshop.adapter.GalleryAdapter_BRVAH;
import com.example.cimoshop.entity.Pixabay;
import com.example.cimoshop.mytools.myTools;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class Gallery extends Fragment {

    private static final String TAG = "Gallery Fragment";

    private GalleryViewModel mViewModel;
    private RecyclerView recyclerViewGallery;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialToolbar toolbar;
    private AppBarLayout appBarLayout;
    private GalleryAdapter_BRVAH galleryAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.gallery_fragment, container, false);
        swipeRefreshLayout = root.findViewById(R.id.swipeGallery);
        recyclerViewGallery = root.findViewById(R.id.recyclerview_gallery);
        toolbar = root.findViewById(R.id.GalleryToolbar);
        appBarLayout = root.findViewById(R.id.appBarLayout);
        Log.d(TAG,"onCreateView");
        return root;
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG,"onActivityCreated");

        //监听appBarLayout是否在顶部，如果在，则swipeRefreshLayout可用
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        //状态栏文字透明
        myTools.makeStatusBarTransparent(getActivity());


        //修复标题栏与状态栏重叠
        myTools.fitTitleBar(getActivity(),toolbar);
        myTools.setMIUI(getActivity(),true);


        //viewModel初始化
        mViewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(GalleryViewModel.class);

        //recyclerView Adapter初始化 使用 BRVAH 框架
        //final GalleryAdapter galleryAdapter = new GalleryAdapter(new DIFFCALLBACK());
        galleryAdapter = new GalleryAdapter_BRVAH();
        galleryAdapter.setDiffCallback(new DIFFCALLBACK());
        galleryAdapter.setAnimationEnable(true);
        galleryAdapter.setAnimationFirstOnly(false);

        //交错布局
        recyclerViewGallery.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerViewGallery.setAdapter(galleryAdapter);

        //_hitsBean观察者，如果_hitsBean发生变化则更新recycleView
        mViewModel._hitsBean.observe(getViewLifecycleOwner(), new Observer<List<Pixabay.HitsBean>>() {
            @Override
            public void onChanged(List<Pixabay.HitsBean> hitsBeans) {
                //galleryAdapter.submitList(hitsBeans);
                galleryAdapter.setDiffNewData(hitsBeans);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //如果_hitsBean为空，则获取数据
        if(mViewModel._hitsBean.getValue() == null ){
            mViewModel.fetchData();
        }

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.fetchData();
            }
        });

        //Item点击事件
        galleryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {

                //将图片信息通过Parcelable传输到detail页面
                Bundle bundle = new Bundle();
                bundle.putParcelable("CHECKED_PHOTO_ID", (Parcelable) adapter.getItem(position));
                Intent intent = new Intent(getContext(), GalleryDetail.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        //加载更多事件处理
        BaseLoadMoreModule loadMore = galleryAdapter.getLoadMoreModule();
        loadMore.setAutoLoadMore(true);
        loadMore.setEnableLoadMoreIfNotFullPage(false);

    }

    /**
     * 指定DiffUtil类，实现动态图库效果
     */
    static class DIFFCALLBACK extends DiffUtil.ItemCallback<Pixabay.HitsBean>{

        @Override
        public boolean areItemsTheSame(@NonNull Pixabay.HitsBean oldItem, @NonNull Pixabay.HitsBean newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Pixabay.HitsBean oldItem, @NonNull Pixabay.HitsBean newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }

}

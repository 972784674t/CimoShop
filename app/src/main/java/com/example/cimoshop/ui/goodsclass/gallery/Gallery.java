package com.example.cimoshop.ui.goodsclass.gallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.example.cimoshop.R;
import com.example.cimoshop.adapter.GalleryAdapter_BRVAH;
import com.example.cimoshop.api.VolleySingleton;
import com.example.cimoshop.entity.Pixabay;
import com.example.cimoshop.utils.UITools;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.List;

/**
 * 图库展示类
 * @author 谭海山
 */
public class Gallery extends Fragment {

    private static final String TAG = "cimoGallery";

    private GalleryViewModel mViewModel;
    private RecyclerView recyclerViewGallery;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialToolbar toolbar;
    private AppBarLayout appBarLayout;
    private GalleryAdapter_BRVAH galleryAdapter;
    private FloatingActionButton toTheTopBtn;

    //加载更多事件处理类
    private BaseLoadMoreModule loadMore;

    //暂存当前页数
    private int currentPage = 1;

    //暂存总页数
    private int totalPage = 1;

    //单次请求的图片数
    private int perPage = 50;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View root = inflater.inflate(R.layout.gallery_fragment, container, false);
        initView(root);
        return root;
    }

    private void initView(View root){
        swipeRefreshLayout = root.findViewById(R.id.swipeGallery);
        recyclerViewGallery = root.findViewById(R.id.recyclerview_gallery);
        toolbar = root.findViewById(R.id.GalleryToolbar);
        appBarLayout = root.findViewById(R.id.appBarLayout);
        toTheTopBtn = root.findViewById(R.id.ToTheTopBtn);
        toTheTopBtn.setVisibility(View.GONE);

        //点击返回顶部按钮时滑动到顶部
        toTheTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewGallery.scrollToPosition(0);
            }
        });

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

        //状态栏文字透明
        UITools.makeStatusBarTransparent(getActivity());

        //修复标题栏与状态栏重叠
        UITools.fitTitleBar(getActivity(),toolbar);
        UITools.setMIUI(getActivity(),true);

        //viewModel初始化
        mViewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(GalleryViewModel.class);

        //recyclerView Adapter初始化 使用 BRVAH 框架
        galleryAdapter = new GalleryAdapter_BRVAH();
        galleryAdapter.setDiffCallback(new MyDiffCallback());
        //开启滑动动画
        galleryAdapter.setAnimationEnable(true);
        //动画只执行一次
        galleryAdapter.setAnimationFirstOnly(false);

        //加载更多对象，来自于BRVAH
        loadMore = galleryAdapter.getLoadMoreModule();

        //交错布局，2列，纵向
        recyclerViewGallery.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerViewGallery.setAdapter(galleryAdapter);

        //定制ScrollListener
        recyclerViewGallery.addOnScrollListener(new MyRecyclerViewScrollListener());

        //_hitsBean观察者，如果_hitsBean发生变化则更新recycleView
        mViewModel.hitsBean.observe(getViewLifecycleOwner(), new Observer<List<Pixabay.HitsBean>>() {
            @Override
            public void onChanged(List<Pixabay.HitsBean> hitsBeans) {
                galleryAdapter.setDiffNewData(hitsBeans);
                swipeRefreshLayout.setRefreshing(false);
                //数据发生变化时，重新获取总页数
                totalPage = mViewModel.getTotalPage();
            }
        });

        //如果_hitsBean为空，则获取数据
        if( mViewModel.hitsBean.getValue() == null ){
            mViewModel.resetQuery();
            //重新获取页数
            currentPage = mViewModel.getCurrentPage() + 1;
        }

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.resetQuery();
                toTheTopBtn.setVisibility(View.GONE);
                //重新获取页数
                currentPage = mViewModel.getCurrentPage() + 1;
                loadMore.loadMoreComplete();
            }
        });

        //Item点击事件,进入详细页面
        galleryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //将图片信息通过Parcelable传输到detail页面
                Bundle bundle = new Bundle();
                bundle.putParcelable("CHECKED_PHOTO_ID", (Parcelable) adapter.getItem(position));
                Intent intent = new Intent(getContext(), GalleryDetail.class);
                intent.putExtras(bundle);
                //共享元素过度效果
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, "detail_img");
                ActivityCompat.startActivity(getContext(), intent, compat.toBundle());
            }
        });

        //当数据不满一页不自动加载更多
        loadMore.setEnableLoadMoreIfNotFullPage(false);
        //当recycleView滑动到底部时执行此监听
        loadMore.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreGallery();
            }
        });

    }

    /**
     * 加载更多数据
     */
    private void loadMoreGallery(){
        String key = mViewModel.getKey();
        Log.d(TAG,"加载更多 -> currentPage："+ currentPage+"\ttotalPage："+totalPage +"\tkey："+key);
        if( currentPage > totalPage ){
            loadMore.loadMoreEnd();
            return;
        }
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "https://pixabay.com/api/?key=16322793-d4bcfe56af2f14816d6549dee&lang=zh&lang=en&q="+key+"&per_page="+perPage+"&page="+ currentPage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Pixabay.HitsBean> list = gson.fromJson(response, Pixabay.class).getHits();
                        totalPage = (gson.fromJson(response, Pixabay.class).getTotalHits()) / perPage + 1;
                        //追加数据，用addAll()
                        mViewModel.hitsBean.getValue().addAll(list);
                        //加载更多设置：完成
                        loadMore.loadMoreComplete();
                        //当前页+1
                        currentPage++;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,"loadMore: "+error.getClass());
                        VolleySingleton.errorMessage(error,getContext());
                        //加载跟多设置：失败
                        loadMore.loadMoreFail();
                    }
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    /**
     * 指定DiffUtil类，判断item是否相同
     */
    static class MyDiffCallback extends DiffUtil.ItemCallback<Pixabay.HitsBean>{

        @Override
        public boolean areItemsTheSame(@NonNull Pixabay.HitsBean oldItem, @NonNull Pixabay.HitsBean newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Pixabay.HitsBean oldItem, @NonNull Pixabay.HitsBean newItem) {
            return oldItem.equals(newItem) ;
        }
    }

    /**
     * 重写 OnScrollListener类
     * 当页面在第一屏和滑动时，不显示返回顶部按钮
     */
    private class MyRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            //定制返回顶部按钮出现动画
            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setInterpolator(new OvershootInterpolator());
            mShowAction.setDuration(500);

            //定制返回顶部按钮消失动画
            TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f);
            mHiddenAction.setDuration(500);

            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            int[] firstVisibleItemPosition = manager.findFirstVisibleItemPositions(null);
            // 当不滚动时
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // 判断是否滚动超过一屏
                if (firstVisibleItemPosition[0] == 0) {
//                    toTheTopBtn.startAnimation(mHiddenAction);
                    toTheTopBtn.setVisibility(View.INVISIBLE);
                } else {
                    toTheTopBtn.startAnimation(mShowAction);
                    toTheTopBtn.setVisibility(View.VISIBLE);
                }

            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING ) {//拖动中
                toTheTopBtn.startAnimation(mHiddenAction);
                toTheTopBtn.setVisibility(View.GONE);
            }
        }
    }

}

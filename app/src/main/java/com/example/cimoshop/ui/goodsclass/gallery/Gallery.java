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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.example.cimoshop.R;
import com.example.cimoshop.adapter.GalleryAdapter;
import com.example.cimoshop.api.VolleySingleton;
import com.example.cimoshop.entity.Pixabay;
import com.example.cimoshop.utils.SharedPrefsTools;
import com.example.cimoshop.utils.UITools;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.List;

/**
 * 图库展示
 * @author 谭海山
 */
public class Gallery extends Fragment {

    private static final String TAG = "cimoGallery";

    /**
     * 数据源
     */
    private GalleryViewModel mViewModel;

    private RecyclerView recyclerViewGallery;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialToolbar toolbar;
    private SearchView searchView;
    private AppBarLayout appBarLayout;
    private GalleryAdapter galleryAdapter;
    private FloatingActionButton toTheTopBtn;

    /**
     * 如果 token 不为空,则用户已经登录
     */
    private String isToken;

    //加载更多事件处理类
    private BaseLoadMoreModule loadMore;

    //暂存当前页数
    private int currentPage = 1;

    //暂存总页数
    private int totalPage = 1;

    //单次请求的图片数
    private int perPage = 50;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        initView(root);
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

        //viewModel初始化
        mViewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(GalleryViewModel.class);

        //recyclerView Adapter初始化 使用 BRVAH 框架
        galleryAdapter = new GalleryAdapter();
        galleryAdapter.setDiffCallback(new MyDiffCallback());

        //开启滑动动画
        galleryAdapter.setAnimationEnable(true);

        //动画只执行一次
        galleryAdapter.setAnimationFirstOnly(false);

        //设置空视图
        galleryAdapter.setEmptyView(initEmptyView("正在初始化资源哦...\n也可以下拉刷新试试"));

        //加载更多对象，来自于BRVAH
        loadMore = galleryAdapter.getLoadMoreModule();

        //交错布局，2列，纵向
        recyclerViewGallery.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerViewGallery.setAdapter(galleryAdapter);

        //定制ScrollListener
        recyclerViewGallery.addOnScrollListener(new MyRecyclerViewScrollListener());

        //数据源：hitsBean观察者，如果hitsBean发生变化则更新recycleView
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

        /**
         * 当recycleView滑动到底部时执行此监听
         */
        loadMore.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreGallery();
            }
        });

    }

    /**
     * 控件初始化
     * @param root
     */
    private void initView(View root){

        isToken = SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github");
        swipeRefreshLayout = root.findViewById(R.id.swipeGallery);
        recyclerViewGallery = root.findViewById(R.id.recyclerview_gallery);
        toolbar = root.findViewById(R.id.GalleryToolbar);
        searchView = root.findViewById(R.id.search);
        appBarLayout = root.findViewById(R.id.appBarLayout);
        toTheTopBtn = root.findViewById(R.id.ToTheTopBtn);
        toTheTopBtn.setVisibility(View.GONE);

        //状态栏文字透明
        UITools.makeStatusBarTransparent(getActivity());

        //修复标题栏与状态栏重叠
        UITools.fitTitleBar(getActivity(),toolbar);
        UITools.setMIUI(getActivity(),true);

        /**
         * 搜索操作
         */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchGallery(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        /**
         * 点击返回顶部按钮时滑动到顶部
         */
        toTheTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewGallery.scrollToPosition(0);
            }
        });

        /**
         * 监听appBarLayout是否在顶部，如果在，则swipeRefreshLayout可用
         */
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

    }

    /**
     * 初始化空列表视图
     * @param tip 空视图提示信息
     * @return 空列表视图 view
     */
    private View initEmptyView(String tip) {
        View emptyView = getLayoutInflater().inflate(R.layout.view_empty, null);
        TextView emptyTextView = emptyView.findViewById(R.id.emptytextView);
        emptyTextView.setText(tip);
        return emptyView;
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
     * 搜索操作
     */
    private void searchGallery(String query){

        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                .content("正在搜索"+query+"...")
                .theme(Theme.LIGHT)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        String key = query;
        Log.d(TAG,"搜索结果： -> currentPage："+ currentPage+"\ttotalPage："+totalPage +"\tkey："+key);
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
                        materialDialog.dismiss();
                        Gson gson = new Gson();
                        List<Pixabay.HitsBean> list = gson.fromJson(response, Pixabay.class).getHits();
                        totalPage = (gson.fromJson(response, Pixabay.class).getTotalHits()) / perPage + 1;
                        //新数据，postValue()
                        mViewModel.hitsBean.postValue(list);
                        //加载更多设置：完成
                        loadMore.loadMoreComplete();
                        //当前页+1
                        currentPage++;
                        int searchResultNumber = gson.fromJson(response, Pixabay.class).getTotalHits();
                        Toast.makeText(getContext(),"为您搜索到 "+searchResultNumber+"张图片",Toast.LENGTH_SHORT).show();
                        if( 0 == searchResultNumber ){
                            galleryAdapter.setEmptyView(initEmptyView("搜索的是啥玩意..."));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        materialDialog.dismiss();
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
     * 重写 OnScrollListener类 <br/>
     * 1.为回到顶部按钮添加补间动画 <br/>
     * 2.当页面在第一屏和滑动时 <br/>
     * 3.不显示返回顶部按钮
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

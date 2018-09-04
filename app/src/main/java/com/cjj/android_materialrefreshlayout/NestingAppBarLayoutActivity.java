package com.cjj.android_materialrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

public class NestingAppBarLayoutActivity extends BaseActivity {

    private static final String TAG = "NestingAppBarLayout";

    private MaterialRefreshLayout refreshLayout;
    private AppBarLayout appBarLayout;
    private RecyclerView recyclerView;

    private StringAdapter adapter;
    private Handler handler;

    @Override



    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_layout);
        refreshLayout = findViewById(R.id.refresh_layout);
        appBarLayout = findViewById(R.id.app_bar_layout);
        recyclerView = findViewById(R.id.recycler_view);

        handler = new Handler();

        initListener();
        load(false);
    }

    private int page;

    private void load(final boolean more) {
        if (more) {
            page++;
        } else {
            page = 1;
        }
        adapter.setSize(page * 10);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (more) {
                    refreshLayout.finishRefreshLoadMore();
                } else {
                    refreshLayout.finishRefresh();
                }
            }
        }, 2000);
    }

    private void initListener() {
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        load(false);
                    }
                }, 1000);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        load(true);
                    }
                }, 1000);
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(TAG, "onOffsetChanged() called with: appBarLayout = [" + appBarLayout + "], verticalOffset = [" + verticalOffset + "]");
               //
                refreshLayout.setEnabledRefresh(verticalOffset >= 0);
            }
        });

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StringAdapter();
        recyclerView.setAdapter(adapter);
    }

    static class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder> {
        private int size = 0;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_text, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.textView.setText("position : " + String.valueOf(position + 1));
        }

        @Override
        public int getItemCount() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
            notifyDataSetChanged();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            final TextView textView;

            ViewHolder(View view) {
                super(view);
                textView = view.findViewById(R.id.text);
            }
        }
    }
}

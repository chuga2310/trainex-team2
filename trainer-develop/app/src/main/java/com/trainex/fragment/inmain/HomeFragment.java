package com.trainex.fragment.inmain;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.trainex.R;
import com.trainex.adapter.listadapter.CategoryAdapter;
import com.trainex.diaglog.CustomProgressDialog;
import com.trainex.model.Category;
import com.trainex.rest.RestClient;
import com.trainex.uis.main.MainActivity;

public class HomeFragment extends Fragment {
    private ImageView imgGroupClass;
    private FrameLayout groupClass;
    private View view;
    private ImageButton imgShowDrawer;
    private RecyclerView lvCategory;
    private ArrayList<Category> listCategories=new ArrayList<>();;
    private CategoryAdapter adapter;
    private int indexPageCategory;
    private int totalPageCategory;
    private Bundle savedState = new Bundle();
    private boolean isLoading;
    private CardView parentLayout;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        indexPageCategory = savedState.getInt("indexPageCategory",0);

        if (indexPageCategory==0){
            indexPageCategory=1;
            getData(indexPageCategory);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
//        imgGroupClass = view.findViewById(R.id.imgGroupClass);
        groupClass = view.findViewById(R.id.groupClass);
        imgShowDrawer = view.findViewById(R.id.imgShowDrawer);
        lvCategory = (RecyclerView) view.findViewById(R.id.lvCategory);
        parentLayout = (CardView)view.findViewById(R.id.parent_layout);

        bind();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        savedState.putInt("indexPageCategory",indexPageCategory);
    }

    private void bind() {
        imgShowDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showMenu();
            }
        });
        groupClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showGroupClassEvents();
            }
        });
        lvCategory.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new CategoryAdapter(getContext(), listCategories);
        lvCategory.setAdapter(adapter);
        lvCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (indexPageCategory < totalPageCategory) {
                    LinearLayoutManager layoutManager1 = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                    int visibleItemCount = layoutManager1.getChildCount();
                    int totalItemCount = layoutManager1.getItemCount();
                    int firstVisibleItemPosition = layoutManager1.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager1.findLastVisibleItemPosition();

                    // Load more if we have reach the end to the recyclerView
                    if ((visibleItemCount + lastVisibleItemPosition) >= (totalItemCount-1) && firstVisibleItemPosition >= 0 && !isLoading) {
                        indexPageCategory++;
                        getData(indexPageCategory);
                        isLoading = true;
                    }

                }
            }
        });
        int spanCount = 2; // 3 columns
        int spacing = 30; // 50px
        boolean includeEdge = false;
        lvCategory.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
    }


    private void getData(int page) {
        Call<JsonElement> callGetListCategory = RestClient.getApiInterface().getListCategory(page);
        callGetListCategory.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                if (response.isSuccessful()) {
                    int code = response.code();
                    if (code == 200) {
                        JsonObject body = response.body().getAsJsonObject();
                        if (body.get("code").getAsInt() == 200) {
                            JsonObject bigData = body.get("data").getAsJsonObject();
                            totalPageCategory = bigData.get("last_page").getAsInt();
                            JsonArray dataList = bigData.get("data").getAsJsonArray();
                            for (int i = 0; i < dataList.size(); i++) {

                                int id = dataList.get(i).getAsJsonObject().get("id").getAsInt();
                                String name = dataList.get(i).getAsJsonObject().get("name").getAsString();
                                String resImage ="";
                                if ( !dataList.get(i).getAsJsonObject().get("img").isJsonNull()){
                                    resImage= dataList.get(i).getAsJsonObject().get("img").getAsString();
                                }
                                Category category = new Category(id, name, resImage);
                                listCategories.add(category);
                                adapter.notifyItemInserted(listCategories.size());
                            }

                            isLoading =false;

                        } else {
                            Log.e("responseBodyCode", "" + code);
                        }
                    }else {
                        Log.e("code",response.code()+"");
                    }
                } else {
                    try {
                        Log.e("responseError", response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("errorException", e.toString());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(getContext(), "Load Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}

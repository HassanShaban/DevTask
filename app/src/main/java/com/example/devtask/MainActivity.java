package com.example.devtask;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devtask.BroadCust.Alarm;
import com.example.devtask.DataBase.TaskDB;
import com.example.devtask.adapters.Adapter;
import com.example.devtask.interfaces.PassDataInterface;
import com.example.devtask.models.TaskModel;
import com.example.devtask.repository.CallingAPI;
import com.example.devtask.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PassDataInterface, SwipeRefreshLayout.OnRefreshListener {


    RecyclerView recycler_view;
    LinearLayoutManager linearLayoutManager;
    Adapter adapter;
    PassDataInterface passDataInterface;
    CallingAPI callingAPI;
    ProgressBar progress;
    int page = 0;
    boolean firstTime = true;
    boolean data_loaded = false;
    SwipeRefreshLayout swipe_refresh;
    TaskDB taskDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passDataInterface = MainActivity.this;
        // create database
        taskDB = TaskDB.getInstance(getApplicationContext());
        callingAPI = new CallingAPI(passDataInterface);
        initalizing();
        callingAPI.getData("e37f6859ec422d0609ba42de7820eb4ec94af9f7" , page ,10);

    }

    private void initalizing() {

        recycler_view = findViewById(R.id.recycler_view);
        progress = findViewById(R.id.progress);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(linearLayoutManager);
        progress.setVisibility(View.VISIBLE);

    }


    ArrayList<TaskModel> data , data1 ;
    @Override
    public void passData(ArrayList<TaskModel> data, String state) {

        Alarm.setAlarm(MainActivity.this);
        this.data1 = data;
        progress.setVisibility(View.GONE);
        swipe_refresh.setRefreshing(false);
        if (state.equals("unsuccess") || state.equals("failed"))
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            // used to set adapter.
        else if (firstTime) setDataFirstTime();
            // used to notify.
        else setData();

    }

    private void setData() {


        // no data to show
        if( data1.size() == 0 ) {

            data_loaded = true;
            progress.setVisibility(View.GONE);
            deleteData();
            insertIntoDB();

        }

        // end of data
        else if( data1.size() < 10 ) {

            data.addAll(data1);
            data_loaded =true;
            adapter.notifyItemRangeInserted(adapter.getItemCount(), data.size() - 1);
            deleteData();
            insertIntoDB();

        }
        // get new data
        else {

            data.addAll(data1);
            adapter.notifyItemRangeInserted(adapter.getItemCount(), data.size() - 1);

        }


    }

    private void setDataFirstTime() {

        // set false to notify adapter in next time
        firstTime = false;
        data = data1;

        if (data.size() != 0) {

            adapter = new Adapter(data, MainActivity.this);
            recycler_view.setAdapter(adapter);

            recycler_view.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                    if (!data_loaded ) {
                        page++;
                        progress.setVisibility(View.VISIBLE);
                        callingAPI.getData("e37f6859ec422d0609ba42de7820eb4ec94af9f7" , page ,10);
                    }
                }
            });


            // end of report with data
            if (data.size() < 10){
                data_loaded = true;
                progress.setVisibility(View.GONE);
                deleteData();
                insertIntoDB();

            }

        }
        // end of report.
        else if(data.size()==0) {
            data_loaded = true;
        }
    }

    private void insertIntoDB() {

          long i =  taskDB.insertData(data);
          if(i > 0) Toast.makeText(this, "Data Inserted into DB", Toast.LENGTH_LONG).show();

    }


    private void deleteData() {

        taskDB.deleteData();

    }


    MenuItem action_search;
    SearchView searchView;
    TextView searchText;
    ImageView search_icon;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate search menu.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        action_search = menu.findItem(R.id.action_search);
        Drawable search_action_item = action_search.getIcon();


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchText = (TextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        search_icon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        search_icon.setEnabled(false);
        search_icon.setImageDrawable(null);

        search(searchView);

        return super.onCreateOptionsMenu(menu);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(data.size()!=0) adapter.getFilter().filter(newText);

                return true;
            }
        });


    }

    @Override
    public void onRefresh() {

        swipe_refresh.setRefreshing(true);
        data_loaded = false;
        firstTime = true;
        data.clear();
        data1.clear();
        page = 0;
        recycler_view.setAdapter(null);
        callingAPI.getData("e37f6859ec422d0609ba42de7820eb4ec94af9f7" , page ,10);

    }
}

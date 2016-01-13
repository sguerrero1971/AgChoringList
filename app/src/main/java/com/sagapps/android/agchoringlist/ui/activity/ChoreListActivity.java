package com.sagapps.android.agchoringlist.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.sagapps.android.agchoringlist.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChoreListActivity extends AppCompatActivity {

    private static final int LOGIN_ACTIVITY_CODE = 100;
    private static final int EMPTY_LIST = 0;
    TextView empty;
    private ChoreBaseAdapter adapter;
    private ParseUser currentUser = ParseUser.getCurrentUser();

    private ListView choreListView;
    private LinearLayout noChoresView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_list);
        init((ListView) findViewById(R.id.chore_list_view));
        empty = (Button) findViewById(R.id.emptyText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMPTY_LIST = 1;
                choresFinished("completed");
                finish();
            }
        });

    private void init(ListView listView) {
        adapter = new ChoreBaseAdapter();
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.emptyText));
        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(listView),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                adapter.remove(position);
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    currentItem = adapter.getItem(position);
                    currentPosition = position;
                    openEditView();
                }
            }
        });

    }
}

    private class ChoreBaseAdapter extends BaseAdapter {

        String[] mItems = getResources().getStringArray(R.array.tasklist);
        List<String> mChoreSet = new ArrayList<>(Arrays.asList(mItems));

        ChoreBaseAdapter(){

        }

        @Override
        public int getCount() {
            return mChoreSet.size();
        }

        @Override
        public String getItem(int position) {
            return mChoreSet.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void remove(int position) {
            String mItem = mChoreSet.get(position);
            submitTaskShare(mItem);
            mChoreSet.remove(position);
            notifyDataSetChanged();
        }

        private class ViewHolder {
            TextView choreTitle;
            ViewHolder(View view) {
                choreTitle = ((TextView) view.findViewById(R.id.txt_data));
                view.setTag(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = convertView == null
                    ? new ViewHolder(convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_chore_list, parent, false))
                    : (ViewHolder) convertView.getTag();

            viewHolder.choreTitle.setText(mChoreSet.get(position));
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chore_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

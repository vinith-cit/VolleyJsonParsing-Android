package vinith.com.zipgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by liveongo on 24/9/16.
 */
public class RoutesListAdapter extends BaseAdapter {


    List<Routes> routesList;


    Context context;

    public RoutesListAdapter(Context context, List<Routes> routesList) {
        this.context = context;
        this.routesList = routesList;
    }

    @Override
    public Object getItem(int position) {
        return routesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    class MyHolder {
        TextView rootCode;
        TextView rootName;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.list_item, null);
        MyHolder holder = new MyHolder();

        holder.rootCode = (TextView) view.findViewById(R.id.tv_root_code);
        holder.rootName = (TextView) view.findViewById(R.id.tv_root_name);

        holder.rootCode.setText(String.valueOf(routesList.get(position).getId()));
        holder.rootName.setText(routesList.get(position).getName().toString());


        return view;
    }


    @Override
    public int getCount() {
        return routesList.size();
    }

}
package hu.uniobuda.nik.visualizer.androidproject2017.Models;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.uniobuda.nik.visualizer.androidproject2017.R;

public class StatisticsAdapter extends BaseAdapter {

    private List<String> items;

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return items != null ? items.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = (TextView) view;
        if (textView == null) {
            textView = (TextView) View.inflate(
                    viewGroup.getContext(),
                    R.layout.list_item,
                    null
            );
            textView.setText(getItem(i).toString());
        }
        return textView;
    }
    public StatisticsAdapter(List<String> items) {
        this.items = items;
    }
}

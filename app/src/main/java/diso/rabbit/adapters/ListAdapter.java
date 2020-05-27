package diso.rabbit.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pabdiava on 27/03/2016.
 */
public class ListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {

    public Context context;
    private List<?> parents;
    private List<List<?>> children;
    private LayoutInflater vi;
    private Activity activity;

    private static final int GROUP_ITEM_RESOURCE = android.R.layout.simple_expandable_list_item_1;
    private static final int CHILD_ITEM_RESOURCE = android.R.layout.simple_expandable_list_item_2;

    public ListAdapter(Context context, Activity activity, List<?> parents, List<List<?>> children) {
        this.parents = parents;
        this.children = children;
        this.context = context;
        this.activity = activity;
        vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    public Object getGroup(int groupPosition) {
        return parents.get(groupPosition);
    }

    public int getGroupCount() {
        return parents.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        View v = convertView;
        Object child = getChild(groupPosition, childPosition);

        if (child != null) {
            v = vi.inflate(CHILD_ITEM_RESOURCE, null);
            TextView t = (TextView)v.findViewById(android.R.id.text1);
            t.setText(child.toString());
        }
        return v;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        View v = convertView;
        Object parentGroup = getGroup(groupPosition);
        if(parentGroup != null) {
            v = vi.inflate(GROUP_ITEM_RESOURCE, null);
            TextView t = (TextView)v.findViewById(android.R.id.text1);
            t.setText("\n" + parentGroup.toString() + "\n");
        }
        return v;
    }

    public void remove(int groupPosition, int childPosition){
        children.get(groupPosition).remove(childPosition);
        if(children.get(groupPosition).size() == 0) {
            children.remove(groupPosition);
            parents.remove(groupPosition);
        }
    }
}

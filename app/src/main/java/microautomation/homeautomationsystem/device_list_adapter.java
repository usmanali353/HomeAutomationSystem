package microautomation.homeautomationsystem;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HelloWorldSolution on 4/24/2018.
 */

public class device_list_adapter extends BaseExpandableListAdapter {
    ArrayList<String> headings_list;
    HashMap<String,ArrayList<String>> child_list;
      Context context;
    public device_list_adapter(ArrayList<String> headings_list, HashMap<String, ArrayList<String>> child_list,Context context) {
        this.headings_list = headings_list;
        this.child_list = child_list;
        this.context=context;
    }

    @Override
    public int getGroupCount() {
        return headings_list.size();
    }

    @Override

    public int getChildrenCount(int groupPosition) {
        if(child_list==null){
            return 0;
        }else {
            return child_list.get(headings_list.get(groupPosition)).size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headings_list.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {

        return child_list.get(headings_list.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_device_list_header_view_layout, parent, false);
        }
        TextView heading=(TextView) convertView.findViewById(R.id.heading);
        heading.setText(headings_list.get(groupPosition));
        return convertView;
    }
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        device_list_viewholder holder;
        if(convertView==null){
            convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_layout,parent,false);
            holder=new device_list_viewholder();
            holder.food_name=convertView.findViewById(R.id.switch_name);
            convertView.setTag(holder);
        }else{
            holder=(device_list_viewholder) convertView.getTag();
        }

         holder.food_name.setText(getChild(groupPosition,childPosition));
         convertView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i=new Intent(context,switches_list_page.class);
                 i.putExtra("device_name",getChild(groupPosition,childPosition));
                 context.startActivity(i);
             }
         });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    class device_list_viewholder{
        TextView food_name;
    }
}

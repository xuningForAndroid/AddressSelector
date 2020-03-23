package com.ning.addressselector;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AddressAdapter extends BaseAdapter {
    private  Context context;
    //列表数据
    private List<DistrictItem> districtItems;
    //被选中的条目，默认都不选中，为-1
    private int mSelect = -1;

    public AddressAdapter(List<DistrictItem> districtItems, Context context) {
        this.districtItems = districtItems;
        this.context=context;
    }

    @Override
    public int getCount() {
        return districtItems == null ? 0 : districtItems.size();
    }

    @Override
    public Object getItem(int position) {
        return districtItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_area, null);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.textView);
            holder.isSelect = (ImageView) convertView.findViewById(R.id.imageViewCheckMark);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        DistrictItem districtItem = districtItems.get(position);
        holder.tvAddress.setText(districtItem.getName());
        //被选中的字体变红，后面带选中标志
        if (mSelect == -1) {
            holder.tvAddress.setTextColor(Color.parseColor("#353535"));
            holder.isSelect.setVisibility(View.GONE);
        } else if (position == mSelect) {
            holder.tvAddress.setTextColor(Color.parseColor("#E94715"));
            holder.isSelect.setVisibility(View.VISIBLE);
        } else {
            holder.tvAddress.setTextColor(Color.parseColor("#353535"));
            holder.isSelect.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 设置选中的条目的方法
     *
     * @param selectPosition 选中的位置
     */
    public void setSelect(int selectPosition) {
        if (mSelect != selectPosition) {
            mSelect = selectPosition;
            notifyDataSetChanged();
        }
    }

    /**
     * 设置数据的方法
     *
     * @param districtItems 新数据
     */
    public void setData(List<DistrictItem> districtItems) {
        if (districtItems != null) {
            this.districtItems = districtItems;
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        ImageView isSelect;
        TextView tvAddress;
    }
}

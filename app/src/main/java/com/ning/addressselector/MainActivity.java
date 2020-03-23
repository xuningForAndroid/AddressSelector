package com.ning.addressselector;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //省份的数据集合
    private List<DistrictItem> provinces;
    //城市的数据集合
    private List<DistrictItem> cities;
    //县和区域的数据集合
    private List<DistrictItem> areas;
    //村和街道的数据集合
    private List<DistrictItem> streets;
    //被选中的省份
    private String selectProvince = "";
    //被选中的城市
    private String selectCity = "";
    //被选中的县或区域
    private String selectArea = "";
    //被选中的村或街道
    private String selectStreet = "";
    //顶部的tab
    private EnhanceTabLayout mTabLayoot;
    //地址列表的控件
    private ListView lvAddress;
    //地址弹窗
    private PopupWindow addressPop;
    //第一个tab（省份的tab）
    private TabLayout.Tab tabProvince;
    //第二个tab（城市的tab）
    private TabLayout.Tab tabCity;
    //第三个tab（县或区的tab）
    private TabLayout.Tab tabArea;
    //第四个tab（街道的tab）
    private TabLayout.Tab tabStreet;
    //被选中的tab的角标，默认进去是0
    private int tabIndex;
    //被选中的省份在列表中的位置，默认没有选中的为-1
    private int provinceIndex = -1;
    //被选中的城市在列表中的位置，默认没有选中的为-1
    private int cityIndex = -1;
    //被选中的县或区在列表中的位置，默认没有选中的为-1
    private int areaIndex = -1;
    //被选中的街道在列表中的位置，默认没有选中的为-1
    private int streetIndex = -1;
    //列表的适配器
    private AddressAdapter adapter;
    private TextView tvAddress;
    private String adcode;
    private ImageView mIvClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        initPopupWindow();
    }
    //初始化控件
    private void initview() {
        findViewById(R.id.rl_address).setOnClickListener(this);
        tvAddress = ((TextView) findViewById(R.id.tv_address));
    }
    //初始化地址选择弹窗
    private void initPopupWindow() {
        //加载弹窗布局
        final View popView = View.inflate(this, R.layout.address_choose_pw, null);
        //弹窗布局中的顶部tab
        mTabLayoot = popView.findViewById(R.id.tablayout);
        mIvClose = popView.findViewById(R.id.iv_close);
        //地址列表控件
        lvAddress = ((ListView) popView.findViewById(R.id.lv_address));
        addressPop = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 300));
        //给popupwindow设置进出动画
        addressPop.setAnimationStyle(R.style.addressAnimation);
        addressPop.setFocusable(true);
        addressPop.setBackgroundDrawable(new ColorDrawable());
        //popwindow隐藏时背景恢复
        addressPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                //弹窗消失的时候会认为地址选择完毕，选中的地址就是选中的省市区街道拼接起来的
                String address = selectProvince + selectCity + selectArea + selectStreet;
                if (!TextUtils.isEmpty(address)) {
                    tvAddress.setText(address);
                    Toast.makeText(MainActivity.this, address, Toast.LENGTH_SHORT).show();
                } else {
                    tvAddress.setText("请选择地址");
                }
            }
        });
        //初始化的时候只有一个tab，显示省份的列表
        adapter = new AddressAdapter(provinces,this);
        lvAddress.setAdapter(adapter);
        lvAddress.setOnItemClickListener(this);
        if (!TextUtils.isEmpty(selectProvince)) {
            tabProvince.setText(selectProvince);
        }
        searchDistrict(0 );
        //tab选择监听
        mTabLayoot.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabLayoot.changeTabsState(tab);
                tabIndex = tab.getPosition();
                if (tabIndex == 0) {
                    //第一个tab选中的时候，给列表设置省份的数据
                    adapter.setData(provinces);
                    //设置被选中的省份
                    adapter.setSelect(provinceIndex);
                    //列表要移动到选中的地方
                    lvAddress.setSelection(provinceIndex == -1 ? 0 : provinceIndex);
                } else if (tabIndex == 1) {
                    adapter.setData(cities);
                    adapter.setSelect(cityIndex);
                    lvAddress.setSelection(cityIndex == -1 ? 0 : cityIndex);
                } else if (tabIndex == 2) {
                    adapter.setData(areas);
                    adapter.setSelect(areaIndex);
                    lvAddress.setSelection(areaIndex == -1 ? 0 : areaIndex);
                } else {
                    adapter.setData(streets);
                    adapter.setSelect(streetIndex);
                    lvAddress.setSelection(streetIndex == -1 ? 0 : streetIndex);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressPop.dismiss();
            }
        });
    }

    private void showPopupwindow(String selectProvince, String selectCity, String selectArea, String selectStreet) {
        addressPop.showAsDropDown(findViewById(R.id.divier));
        //popwindow弹出时背景变灰
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (addressPop.isShowing()) {
            lp.alpha = 0.6f;
            getWindow().setAttributes(lp);
        }
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_address:
                showPopupwindow(selectProvince, selectCity, selectArea, selectStreet);
                break;
        }
    }

    //listview的条目点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (tabIndex == 0) {
            //当tabindex的时候，条目点击选中的是省份数据，选中之后，搜索省份对应的子行政区域
            tabProvince.setText(provinces.get(position).getName());
            provinceIndex = position;
            //设置选中的省份
            adapter.setSelect(provinceIndex);
            selectProvince = provinces.get(position).getName();
            searchDistrict(1);
            //每次点击省份之后，后面所有的子行政区域全部置空，重新点击了之后才赋值，后面同理
            selectCity = "";
            selectArea = "";
            selectStreet = "";
        } else if (tabIndex == 1) {
            tabCity.setText(cities.get(position).getName());
            cityIndex = position;
            adapter.setSelect(cityIndex);
            selectCity = cities.get(position).getName();
            searchDistrict(2);
            selectArea = "";
            selectStreet = "";
        } else if (tabIndex == 2) {
            tabArea.setText(areas.get(position).getName());
            areaIndex = position;
            adapter.setSelect(areaIndex);
            selectArea = areas.get(position).getName();
            searchDistrict(3);
            selectStreet = "";
        } else {
            tabStreet.setText(streets.get(position).getName());
            streetIndex = position;
            adapter.setSelect(streetIndex);
            selectStreet = streets.get(position).getName();
            addressPop.dismiss();
        }
    }
    private void clearData(int position) {
        if (position == 0) {
            if (provinces != null) {
                provinces.clear();
            }
            if (cities != null) {
                cities.clear();
            }
            if (areas != null) {
                areas.clear();
            }
            if (streets != null) {
                streets.clear();
            }
            mTabLayoot.removeAllTabs();
            if (tabProvince != null) {
                tabProvince.setText("请选择");
            } else {
                tabProvince = mTabLayoot.addTab("请选择");
            }
            tabProvince.select();
            provinceIndex = -1;
            cityIndex = -1;
            areaIndex = -1;
            streetIndex = -1;
            adapter.setSelect(-1);
        } else if (position == 1) {
            if (cities != null) {
                cities.clear();
            }
            if (areas != null) {
                areas.clear();
            }
            if (streets != null) {
                streets.clear();
            }
            if (tabCity != null) {
                tabCity.setText("请选择");
            } else {
                tabCity = mTabLayoot.addTab("请选择");
            }
        } else if (position == 2) {
            if (areas != null) {
                areas.clear();
            }
            if (streets != null) {
                streets.clear();
            }
            if (tabArea != null) {
                tabArea.setText("请选择");
            } else {
                tabArea = mTabLayoot.addTab("请选择");
            }
        } else {
            if (streets != null) {
                streets.clear();
            }
            if (tabStreet != null) {
                tabStreet.setText("请选择");
            } else {
                tabStreet = mTabLayoot.addTab("请选择");
            }
        }
    }

    private void searchDistrict(final int type) {
        clearData(type);
        switch (type) {
            case 0:
                provinces = new ArrayList<>();
                DistrictItem districtItem = new DistrictItem();
                districtItem.setName("云南");
                provinces.add(districtItem);

                DistrictItem districtItem2 = new DistrictItem();
                districtItem2.setName("北京");
                provinces.add(districtItem2);

                DistrictItem districtItem3 = new DistrictItem();
                districtItem3.setName("天津");
                provinces.add(districtItem3);

                DistrictItem districtItem13 = new DistrictItem();
                districtItem13.setName("云南");
                provinces.add(districtItem13);

                DistrictItem districtItem14 = new DistrictItem();
                districtItem14.setName("北京");
                provinces.add(districtItem14);

                DistrictItem districtItem15 = new DistrictItem();
                districtItem15.setName("天津");
                provinces.add(districtItem15);

                DistrictItem districtItem16 = new DistrictItem();
                districtItem16.setName("云南");
                provinces.add(districtItem16);

                DistrictItem districtItem18 = new DistrictItem();
                districtItem18.setName("北京");
                provinces.add(districtItem18);

                DistrictItem districtItem17 = new DistrictItem();
                districtItem17.setName("天津");
                provinces.add(districtItem17);

                tabIndex = 0;
                provinceIndex = -1;
                cityIndex = -1;
                areaIndex = -1;
                streetIndex = -1;
                tabProvince.select();
                mTabLayoot.changeTabsState(0);

                adapter.setData(provinces);
                adapter.setSelect(-1);
                break;
            case 1:
                cities = new ArrayList<>();
                DistrictItem districtItem4 = new DistrictItem();
                districtItem4.setName("昆明");
                cities.add(districtItem4);

                DistrictItem districtItem5 = new DistrictItem();
                districtItem5.setName("临沧");
                cities.add(districtItem5);

                DistrictItem districtItem6 = new DistrictItem();
                districtItem6.setName("西双版纳");
                cities.add(districtItem6);
                mTabLayoot.removeAllTabs();

                tabProvince = mTabLayoot.addTab(selectProvince);
                tabCity = mTabLayoot.addTab("请选择");

                tabIndex = 1;
                cityIndex = -1;
                areaIndex = -1;
                streetIndex = -1;
                tabCity.select();
                adapter.setData(cities);

                adapter.setSelect(-1);
                break;
            case 2:
                areas = new ArrayList<>();
                DistrictItem districtItem7 = new DistrictItem();
                districtItem7.setName("云县");
                areas.add(districtItem7);

                DistrictItem districtItem8 = new DistrictItem();
                districtItem8.setName("祥云");
                areas.add(districtItem8);

                DistrictItem districtItem9 = new DistrictItem();
                districtItem9.setName("宾县");
                areas.add(districtItem9);

                mTabLayoot.removeAllTabs();
                tabProvince = mTabLayoot.addTab(selectProvince);
                tabCity = mTabLayoot.addTab(selectCity);
                tabArea = mTabLayoot.addTab("请选择");
                tabArea.select();
                adapter.setData(areas);
                tabIndex = 2;
                areaIndex = -1;
                streetIndex = -1;
                adapter.setSelect(-1);
                break;
            case 3:
                streets = new ArrayList<>();
                DistrictItem districtItem10 = new DistrictItem();
                districtItem10.setName("爱华镇");
                streets.add(districtItem10);

                DistrictItem districtItem11 = new DistrictItem();
                districtItem11.setName("小街镇");
                streets.add(districtItem11);

                DistrictItem districtItem12 = new DistrictItem();
                districtItem12.setName("后沟乡");
                streets.add(districtItem12);

                mTabLayoot.removeAllTabs();
                tabProvince = mTabLayoot.addTab(selectProvince);
                tabCity = mTabLayoot.addTab(selectCity);
                tabArea = mTabLayoot.addTab(selectArea);
                tabStreet = mTabLayoot.addTab("请选择");
                adapter.setData(streets);
                tabIndex = 3;
                tabStreet.select();
                streetIndex = -1;
                adapter.setSelect(-1);
                break;
        }
        lvAddress.setSelection(0);

    }


}

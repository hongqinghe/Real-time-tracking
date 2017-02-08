package com.hongqing.real_time_tracking;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.hongqing.real_time_tracking.bean.Track;
import com.hongqing.real_time_tracking.bean.TrackDetail;
import com.hongqing.real_time_tracking.database.DataBaseAdapter;
import com.hongqing.real_time_tracking.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    public LocationClient locationClient;
    private BaiduMap myMap;
    private boolean flag = false;
    private LatLng currentlatLng;
    private double currentLat;
    private double currentLog;
    private EditText endPosition;
    private String endPlace;
    private String currentAddStr;//当前位置
    private DataBaseAdapter baseAdapter;
    private int currentId;//当前的Id
    private ArrayList<LatLng> list = new ArrayList<>();//存放当前的位置
    private boolean isThreading = false;//标记当前线程的状态
    private GeoCoder geoCoder;
    private ListView listView;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mv_map);
        myMap = mapView.getMap();
        baseAdapter = new DataBaseAdapter(this);
        //创建地位
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(bdLocationListener);
        //启动时默认加载到我的
        flag = true;
        myLocation();


    }

    //定位服务的监听者
    BDLocationListener bdLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            currentAddStr = bdLocation.getAddrStr();
            if (bdLocation != null && flag) {
                flag = false;
                //开启定位图层
                myMap.setMyLocationEnabled(true);
//            //设置自动定位
                MyLocationData myLocationData = new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude()).build();
                //设置定位数据显示
                myMap.setMyLocationData(myLocationData);
                //设置我的 位置图标
                currentLat = bdLocation.getLatitude();
                currentLog = bdLocation.getLongitude();

                currentlatLng = new LatLng(currentLat, currentLog);
                MyLocationConfiguration.LocationMode locationMOde = MyLocationConfiguration.LocationMode.FOLLOWING;
//            BitmapDescriptor bitmap= BitmapDescriptorFactory.fromResource();//使用默认图标，也可以在这里设置自定义图标
                myMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMOde, true, null));
                //设置跳转动画
                myMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(currentlatLng, 18));
            }
        }
    };

    //配置定位参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 2000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        locationClient.setLocOption(option);
        locationClient.start();//开始定位
        locationClient.requestLocation();//发起请求
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.position:
                //定位我的位置
                toast("正在定位中...");
                flag = true;//由于设置的是1000刷新请求 ，只需请求一次
                myLocation();

                break;
            case R.id.start:
                //开始追踪
                startTrack();
                break;
            case R.id.end:
                //结束追踪
                endTrack();
                break;
            case R.id.play:
                //追踪回放
                playTrack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog alertDialog;

    //回放  使用一个dialogFragment
    private void playTrack() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragment=new MyDialogFragment();
//        fragment.show(fragmentManager,"111");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.tracks_dialog, null);
        listView = (ListView) view.findViewById(R.id.listView);
        List<HashMap<String, String>> data = new ArrayList<>();

        //查询数据
        ArrayList<Track> tracks = baseAdapter.findTrack();
        HashMap<String, String> hashMap;
        Track track = null;
        int size = tracks.size();
        for (int i = 0; i < size; i++) {
            hashMap = new HashMap<>();
            track = tracks.get(i);//得到对象的第i个值
            hashMap.put("tv_id", String.valueOf(track.getId()));
            hashMap.put("start_place", track.getTrack_name() + "--" + track.getCreate_date());
            hashMap.put("current_place", track.getStrat_loc() + "到" + track.getEnd_loc());
            System.out.println(track.getStrat_loc() + "-------------");
            data.add(hashMap);//将值传到data中，这个就是加载的数据
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.list_item,
                new String[]{"tv_id", "start_place", "current_place"},
                new int[]{R.id.tv_id, R.id.tv_track, R.id.tv_trackDetails});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_id = (TextView) view.findViewById(R.id.tv_id);
                int current_id = Integer.parseInt(tv_id.getText().toString());
                System.out.println(current_id + "sbsbadbbadbiab");
                new Thread(new MyTrackDetails(current_id)).start();
                dialog.dismiss();
            }
        });
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.show();//这里是得到一个dialog  然后关闭
        
        dialog.show();

    }


    private void endTrack() {
        //得到当前的位置，并将它传到数据库中
        isThreading = false;//用于停止跟踪，相当于将线程停止
        toast("以停止跟踪");
        //解析编写
        createGeoCoder();
    }

    private void createGeoCoder() {
        geoCoder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener code = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    Toast.makeText(MainActivity.this, "没有查到周边该结果", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取地理编码结果 查找该对象
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    //没有检索到结果
                    Toast.makeText(MainActivity.this, "没有查到周边该结果", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取反向地理编码结果
//                Toast.makeText(MainActivity.this, "反向地理编码结果为" + reverseGeoCodeResult.getAddress(), Toast.LENGTH_SHORT).show();
                baseAdapter.updataTrack(reverseGeoCodeResult.getAddress(), currentId);
            }
        };
        geoCoder.setOnGetGeoCodeResultListener(code);//设置编码检索监听者
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(currentLat, currentLog)));
    }

    private void startTrack() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alertdialog_layout, null);
        builder.setView(view);
        endPosition = (EditText) view.findViewById(R.id.et_end);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endPlace = endPosition.getText().toString();
                if (TextUtils.isEmpty(endPlace)) {
                    toast("请输入终点位置");
                    return;
                }
                createTrack(endPlace);
                dialog.dismiss();
            }
        });


        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //添加一条线路
    private void createTrack(String endPlace) {
        Track track = new Track();
        track.setTrack_name(endPlace);//设置路线名称
        track.setStrat_loc(currentAddStr);
        track.setCreate_date(DateUtils.toDate(new Date()));
        currentId = baseAdapter.addTrack(track);


        //添加路线详情表
        baseAdapter.addTrackDetials(currentId, currentLat, currentLog);
        list.add(currentlatLng);
        addOverLay();//加点
        //模拟一个线程进行加载位置  ,并给一个标记让它运行
        isThreading = true;
        new Thread(new MyTrackThread()).start();
    }

    private void addOverLay() {
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        OverlayOptions overlayOptions = new MarkerOptions().icon(descriptor).position(new LatLng(currentLat, currentLog));
        myMap.addOverlay(overlayOptions);

    }


    private class MyTrackThread implements Runnable {
        @Override
        public void run() {
            while (isThreading) {
                //获取当前的位置
                randomPositon();
                baseAdapter.addTrackDetials(currentId, currentLat, currentLog);

                list.add(new LatLng(currentLat, currentLog));
                addOverLay();
                //画线
                drawLines();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
//通过两点划线  ，每次都将第一个点清掉，下次保证是两个点
    public void drawLines() {
        OverlayOptions overlayOptions = new PolylineOptions().points(list).color(0xFFFF0000);
        myMap.addOverlay(overlayOptions);
        list.remove(0);
    }
//模拟位置
    private void randomPositon() {
        currentLat = currentLat + Math.random() / 1000;
        currentLog = currentLog + Math.random() / 1000;
    }

    private void myLocation() {
        //定位位置
        initLocation();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private class MyTrackDetails implements Runnable {
        int id;

        public MyTrackDetails(int current_id) {
            this.id = current_id;

            myMap.clear();//清空当前map中的图标
        }

        @Override
        public void run() {
            //根据id查询所对应的线路
            ArrayList<TrackDetail> trackDetailArrayList = baseAdapter.findTrackDetail(id);

            //查询出当前的位置坐标
            TrackDetail trackDetail = null;
            currentLat = trackDetailArrayList.get(0).getLat();
            currentLog = trackDetailArrayList.get(0).getLng();
            list.clear();
            list.add(new LatLng(currentLat, currentLog));//添加第一个点
            addOverLay();
            int size = trackDetailArrayList.size();//这里list的大小确定的话 ，建议使用局部变量，效率更高
            for (int i = 1; i < size; i++) {
                //得到每一个数据库中的坐标
                trackDetail = trackDetailArrayList.get(i);
                currentLat = trackDetail.getLat();
                currentLog = trackDetail.getLng();

                list.add(new LatLng(currentLat, currentLog));
                System.out.println(list.size() + "000000000000000");
                addOverLay();
                drawLines();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //回放完成后通知UI
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toast("已更新完成");
                }
            });
        }
    }
}

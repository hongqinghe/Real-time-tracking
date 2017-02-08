package com.hongqing.real_time_tracking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hongqing.real_time_tracking.bean.Track;
import com.hongqing.real_time_tracking.bean.TrackDetail;

import java.util.ArrayList;

/**
 * Created by 贺红清 on 2017/2/7.
 */

public class DataBaseAdapter {

    private final MyDataBaseHelper myDataBaseHelper;

    public DataBaseAdapter(Context context) {
        myDataBaseHelper = new MyDataBaseHelper(context);
    }
   public int addTrack(Track  track){
       SQLiteDatabase database=myDataBaseHelper.getWritableDatabase();
       ContentValues values=new ContentValues();
       values.put(MyDataBaseHelper.TRACK_NAME,track.getTrack_name());
       values.put(MyDataBaseHelper.CREATE_DATE,track.getCreate_date());
       values.put(MyDataBaseHelper.START_LOC,track.getStrat_loc());
       values.put(MyDataBaseHelper.END_LOC,track.getEnd_loc());
       int  id= (int) database.insertOrThrow(MyDataBaseHelper.TABLE_TRACK,null,values);
       database.close();
       return id;
   }
   //跟新路线的最后位置
   public  void updataTrack(String endplace,int id){
       SQLiteDatabase database=myDataBaseHelper.getWritableDatabase();

       String sql="update track2 set end_loc=? where id=?";
       database.execSQL(sql,new Object[]{endplace,id});
       database.close();
   }
   //删除路线
   public void deleteTrack(int id){
       SQLiteDatabase database=myDataBaseHelper.getWritableDatabase();
       database.delete(MyDataBaseHelper.TABLE_TRACK,MyDataBaseHelper.ID+"=?",new String []{String.valueOf(id)});
       database.close();
   }
   //查询明细表  这里记住每个字段要与数据库中的一致
   public ArrayList<TrackDetail> findTrackDetail(int id){
       ArrayList<TrackDetail> list=new ArrayList<>();
       SQLiteDatabase database=myDataBaseHelper.getReadableDatabase();
       String sql="select id,lat,lng from track_detail2 where tid=? order by id desc";
      Cursor cursor=database.rawQuery(sql,new String[]{String.valueOf(id)});
       TrackDetail trackDetail=null;
       if (cursor!=null){
           while (cursor.moveToNext()) {
               trackDetail=new TrackDetail(cursor.getInt(0),cursor.getDouble(1),cursor.getDouble(2));
               list.add(trackDetail);
           }
           cursor.close();
       }
       return list;
   }
   //查询线路
   public ArrayList<Track> findTrack(){
       ArrayList<Track> list=new ArrayList<>();
       SQLiteDatabase database=myDataBaseHelper.getReadableDatabase();


       String sql2="select id ,track_name,create_date,start_loc,end_loc from track2";
       Cursor cursor = database.rawQuery(sql2, null);
       System.out.println("这里被执行了");
       Track track=null;
       if (cursor!=null){
           while (cursor.moveToNext()){
               track=new Track(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
               list.add(track);
           }
           cursor.close();
       }
       return list;
   }
   public void addTrackDetials(int currentId,double lat, double lng){
       SQLiteDatabase database=myDataBaseHelper.getWritableDatabase();
       String sql="insert into track_detail2(tid,lat,lng)values(?,?,?)";
       database.execSQL(sql,new Object[]{currentId,lat,lng});
       database.close();
   }

}

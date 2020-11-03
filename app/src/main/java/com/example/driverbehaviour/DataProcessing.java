package com.example.driverbehaviour;

import android.app.AlertDialog;
import android.database.Cursor;
import android.icu.text.Edits;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DataProcessing extends AppCompatActivity {

    DatabaseHelper1 myDb1;
    DatabaseHelper2 myDb2;
    DatabaseHelper3 myDb3;
    DataBaseHelperFinal myDbf;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            myDb1 = new DatabaseHelper1(this);
            myDb2 = new DatabaseHelper2(this);
            myDb3 = new DatabaseHelper3(this);
            myDbf = new DataBaseHelperFinal(this);
            getData();
            //    viewAll();
        }

        public void getData(){
            Cursor res1 = myDb1.getAllData();
            Cursor res2 = myDb2.getAllData();
            Cursor res3 = myDb3.getAllData();
//            List<HashMap<String,String>> List1 = new ArrayList<HashMap<String,String>>();
//            List<HashMap<String,String>> List2 = new ArrayList<HashMap<String,String>>();
//            List<HashMap<String,String>> List3 = new ArrayList<HashMap<String,String>>();
            ArrayList<ArrayList<Object>> List1 = new ArrayList<>();
            ArrayList<ArrayList<Object>> List2 = new ArrayList<>();
            ArrayList<ArrayList<Object>> List3 = new ArrayList<>();
            updateData1(res1 ,List1);
            updateData2(res2 ,List2);
            updateData3(res3 ,List3);
            //Add data to myDbf
            //TODO
            for (List<Object> list : List1){
                list.get()
            }
//            Iterator<ArrayList<Object>> iter = List1.iterator();
//            while(iter.hasNext()){
//                Iterator<Object> siter = iter.;
//                while(siter.hasNext()){
//                    Object s = siter.next();
//                    System.out.println(s);
//                }
//            }
        }

        public void updateData1(Cursor res,ArrayList list){
            if(res.getCount() == 0){
                // show message
                showMessage("Error","Nothing found");
                return;
            }else {
                while (res.moveToNext()) {
                    ArrayList<Object> hm = new ArrayList();
                    hm.add(0, res.getInt(0));
                    hm.add(1, res.getDouble(1));
                    hm.add(2, res.getFloat(2));
                    hm.add(3, res.getFloat(3));
                    hm.add(4, res.getFloat(4));
                    list.add(hm);
                }
            }
            for (int i=0; i<list.size(); i++)
            {
                myDb1.deleteData(getString(i));
            }
        }
    public void updateData2(Cursor res,List list){
        if(res.getCount() == 0){
            // show message
            showMessage("Error","Nothing found");
            return;
        }else {
            while (res.moveToNext()) {
                ArrayList<Object> hm = new ArrayList();
                hm.add(0, res.getInt(0));
                hm.add(1, res.getDouble(1));
                hm.add(2, res.getFloat(2));
                hm.add(3, res.getFloat(3));
                hm.add(4, res.getFloat(4));
                list.add(hm);
            }
        }
        for (int i=0; i<list.size(); i++)
        {
            myDb2.deleteData(getString(i));
        }
    }
    public void updateData3(Cursor res,List list){
        if(res.getCount() == 0){
            // show message
            showMessage("Error","Nothing found");
            return;
        }else {
            while (res.moveToNext()) {
                ArrayList<Object> hm = new ArrayList();
                hm.add(0, res.getInt(0));
                hm.add(1, res.getDouble(1));
                hm.add(2, res.getFloat(2));
                hm.add(3, res.getFloat(3));
                hm.add(4, res.getDouble(4));
                hm.add(5, res.getFloat(5));
                list.add(hm);
            }
        }
        for (int i=0; i<list.size(); i++)
        {
            myDb3.deleteData(getString(i));
        }
    }
        public void viewAll() {

         //   myDb = new DatabaseHelper1(this);
            //populate an List<HashMap<String,String>> from the database and then view it
            List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
            Cursor res = myDb1.getAllData();
            if(res.getCount() == 0){
                // show message
                showMessage("Error","Nothing found");
                return;
            }else{
                while(res.moveToNext()){
                    HashMap<String, String> hm = new HashMap<String,String>();
                    hm.put("txt1","ID : " + res.getString(0));
                    hm.put("txt2","Name : " + res.getString(1));
                    hm.put("txt3","Age : " + res.getString(2));
                    aList.add(hm);
                    String[] from = { "txt1","txt2","txt3" };
                    // Ids of views in listlayout
//                    int[] to = { R.id.textView1,R.id.textView2,R.id.textView3};
//                    SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList , R.layout.listlayout, from, to);
//                    listView.setAdapter(adapter);
                }
            }


        }


        public void showMessage(String title,String Message){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(Message);
            builder.show();
        }


}

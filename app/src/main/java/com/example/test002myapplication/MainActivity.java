package com.example.test002myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.AndroidException;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.DataMatrix;
import com.onbarcode.barcode.android.IBarcode;



public class MainActivity extends AppCompatActivity {

    String PathBD;
    String Login;
    String Password;
   // ArrayAdapter<String> adapter;
    GridView gridviewMain2;
    EditText et01;
    TextView tv01;
    String serial01,sp1,sp2;
    Spinner spinner,spinner2;
    SharedPreferences sPref;

    View promptsView,promptsView2; //for alertdialog view


    String[] data = {"black", "blue", "gray", "green", "orange", "red", "yellow", "white"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // адаптер  для спинера
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(7);
//login-password dialog
showDialog(1);


    }

protected Dialog onCreateDialog(int id) {
    if (id == 1) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Autorization");
        adb.setNegativeButton("cancel",DialogClickListenerLoginCancel);
        adb.setPositiveButton("ok",  DialogClickListenerLoginOK);
        adb.setCancelable(false);  //что бы не закрывался без нажатия кнопок
        //Получаем вид с файла .xml, который применим для диалогового окна:
        //LayoutInflater li = LayoutInflater.from(this);
        LayoutInflater li = getLayoutInflater();
         promptsView = li.inflate(R.layout.activity_login_paswd, null);  //View promptsView
        EditText etLogin = promptsView.findViewById(R.id.editTextLogin);
        EditText etPasswd = promptsView.findViewById(R.id.editTextPaswd);
        //Настраиваем .xml для нашего AlertDialog:
        adb.setView(promptsView);
        sPref = getPreferences(MODE_PRIVATE);
        PathBD = sPref.getString("PathBD", "");
        Login = sPref.getString("Login", "");
        etLogin.setText(Login);

        Log.d("TAVKA",Login+"  "+PathBD);
        return adb.create();
    }
    if (id == 2) {
        AlertDialog.Builder adb2 = new AlertDialog.Builder(this);
        adb2.setTitle("Config");
        adb2.setNegativeButton("cancel",DialogClickListenerLoginCancel);
        adb2.setPositiveButton("ok",  DialogClickListenerLoginOK2);
        adb2.setCancelable(false);  //что бы не закрывался без нажатия кнопок
        //Получаем вид с файла .xml, который применим для диалогового окна:
        LayoutInflater li2 = getLayoutInflater();
        promptsView2 = li2.inflate(R.layout.activity_config, null);  //View promptsView
        EditText etLogin = promptsView2.findViewById(R.id.editTextLoginConfig);
        EditText etPathBD = promptsView2.findViewById(R.id.editTextPatchConfig);
        //Настраиваем .xml для нашего AlertDialog:
        adb2.setView(promptsView2);
        sPref = getPreferences(MODE_PRIVATE);
        PathBD = sPref.getString("PathBD", "");
        etPathBD.setText(PathBD);
        Login = sPref.getString("Login", "");
        etLogin.setText(Login);


        return adb2.create();
    }


        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener DialogClickListenerLoginOK = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            EditText etLogin =  promptsView.findViewById(R.id.editTextLogin);
            Login=etLogin.getText().toString();
            EditText etPasswd = promptsView.findViewById(R.id.editTextPaswd);
            Password=String.valueOf(etPasswd.getText());
        }
    };


    DialogInterface.OnClickListener DialogClickListenerLoginCancel = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        }
    };


    DialogInterface.OnClickListener DialogClickListenerLoginOK2 = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {

            EditText etLogin =  promptsView2.findViewById(R.id.editTextLoginConfig);
            Login=etLogin.getText().toString();
            //Log.d("TAVKA",etLogin.toString());

            EditText etPathBD = promptsView2.findViewById(R.id.editTextPatchConfig);
            PathBD=String.valueOf(etPathBD.getText());

            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("Login", Login);
            ed.putString("PathBD", PathBD);
            ed.commit();
        }
    };


    public void ButtonConfig(View view) {
        showDialog(2);
    }


    public void ButtonCreate(View view){
        String message=" ";
        tv01=(TextView)findViewById(R.id.textViewMESSAGE);
        ////////////////////////
        ////////////////    work with ms sql
        ///////////////////////
        et01 = (EditText) findViewById(R.id.editTextSerial);
        serial01= et01.getText().toString();
//       serial01="1100880HTF8YY";   //for exsamle
//message=message+serial01+" ";
//tv01.setText(message);
        if(serial01.equals("")){
//message=message+" return ";
//tv01.setText(message);
            return;        };
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        }catch(Exception e){
//message=message+" net error ";
//tv01.setText(message);
        }
        String str01="";
        try{
            if (android.os.Build.VERSION.SDK_INT > 9)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            //PathBD="10.168.2.47:1433/Seavision";
            //Login="dm";
            //Password="88 названий конторы";
            Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://"+PathBD,Login,Password);
            Statement statement = connection.createStatement();
            String query =
                    //"select top 1 * from DC_DataDefinition";
                    "SELECT replace(replace(a.AI,'(',''),')','')+NCHAR(29)+'91'+B.value+NCHAR(29)+'92'+ (SELECT top 1 B.value "+
                            " FROM SN_Aggregations as A  left join SN_ItemSubID as B  on A.InternalID=B.InternalID "+
                            " where  A.SerialNumber='"+serial01+"'  and b.SubID=2) "+
                            " FROM SN_Aggregations as A  left join SN_ItemSubID as B  on A.InternalID=B.InternalID  where   A.SerialNumber='"+serial01+"'  and b.SubID=1" ;


//message=message+" "+query+" ";
//tv01.setText(message);
            ResultSet result = statement.executeQuery(query);
//message=message+"-"+str01+"-";
//tv01.setText(message);
            if(result.next()){
               str01 = "";
               str01=result.getString(1);
//message=message+"+"+str01+"+";
//tv01.setText(message);
            }
            result.close();
        }catch(Exception e){
            e.printStackTrace();
            String stackTrace = Log.getStackTraceString(e);
//message=message+" errror 2 ";
//tv01.setText(message);
        }




        ////////////////////////
        ////////////////    work with datamatrix
        ///////////////////////
        ImageView iv = (ImageView)findViewById(R.id.imageViewDataMatrix);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);

        DataMatrix barcode = new DataMatrix();

        /*
           Data Matrix Valid data char set:
                ASCII values 0 - 127 in accordance with the US national version of ISO/IEC 646
	            ASCII values 128 - 255 in accordance with ISO 8859-1. These are referred to as extended ASCII.

        */

        barcode.setData( str01
                // "\t\n" +
                //"\t"+"" +
         //       "0104602521006811211100880HTF8YY\u001D91EE07\u001D92f9DKCOSbLI8ngdYTakGrx4Aro1FinDh3IqM18p0APQA="
                );

        barcode.setDataMode(DataMatrix.M_AUTO);

        // if your selected format mode doesnot have enough space to encode your data,
        // the library will choose the right format mode for you automatically.
        barcode.setFormatMode(DataMatrix.F_36X36);

        //  Set the processTilde property to true, if you want use the tilde character "~" to specify special characters in the input data. Default is false.
        //  1-byte character: ~ddd (character value from 0 ~ 255)
        //  ASCII (with EXT): from ~000 to ~255
        //  2-byte character: ~6ddddd (character value from 0 ~ 65535)
        //  Unicode: from ~600000 to ~665535
        //  ECI: from ~7000000 to ~7999999
        barcode.setProcessTilde(true);

        //  if you want to encode GS1 compatible Data Matrix, you need set FNC1 mode to IBarcode.FNC1_ENABLE
        barcode.setFnc1Mode(IBarcode.FNC1_NONE);  //FNC1_ENABLE

        // Unit of Measure, pixel, cm, or inch
        barcode.setUom(IBarcode.UOM_PIXEL);  //UOM_PIXEL  UOM_CM
        // barcode bar module width (X) in pixel
        barcode.setX(14f);

        //barcode.setLeftMargin(10f);
        // barcode.setRightMargin(10f);
        //barcode.setTopMargin(10f);
        //barcode.setBottomMargin(10f);
        // barcode image resolution in dpi
        barcode.setResolution(72);

        // barcode bar color and background color in Android device

        AndroidColor colorName1 =AndroidColor.black;
        AndroidColor colorName2 =AndroidColor.white;
        int colorName3 =Color.WHITE;
        sp1=spinner.getSelectedItem().toString();
        sp2=spinner2.getSelectedItem().toString();
        if(sp1.equals("black")){  colorName1 = AndroidColor.black;}
        if(sp1.equals("blue")){  colorName1 = AndroidColor.blue;}
        if(sp1.equals("gray")){  colorName1 = AndroidColor.gray;}
        if(sp1.equals("green")){  colorName1 = AndroidColor.green;}
        if(sp1.equals("orange")){  colorName1 = AndroidColor.orange;}
        if(sp1.equals("red")){  colorName1 = AndroidColor.red;}
        if(sp1.equals("yellow")){  colorName1 = AndroidColor.yellow;}
        if(sp1.equals("white")){  colorName1 = AndroidColor.white;}

        if(sp2.equals("black")){  colorName2 = AndroidColor.black;  colorName3 =Color.BLACK;}
        if(sp2.equals("blue")){  colorName2 = AndroidColor.blue;    colorName3 =Color.BLUE;}
        if(sp2.equals("gray")){  colorName2 = AndroidColor.gray;    colorName3 =Color.GRAY;}
        if(sp2.equals("green")){  colorName2 = AndroidColor.green;  colorName3 =Color.GREEN;}
        if(sp2.equals("orange")){  colorName2 = AndroidColor.orange;colorName3 =Color.MAGENTA;}
        if(sp2.equals("red")){  colorName2 = AndroidColor.red;      colorName3 =Color.RED;}
        if(sp2.equals("yellow")){  colorName2 = AndroidColor.yellow;colorName3 =Color.YELLOW;}
        if(sp2.equals("white")){  colorName2 = AndroidColor.white;  colorName3 =Color.WHITE;}

        barcode.setForeColor(colorName1);
        barcode.setBackColor(colorName2);

        //разрешение экрана
        Display display1 = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display1.getRealSize(size);
        int scrWidth = size.x;
        //int scrHeight = size.y;

        /*
        specify your barcode drawing area
	    */

        RectF bounds = new RectF((scrWidth-500)/2, 100, 1, 1);
       /////////// RectF bounds = new RectF(1, 100, 1, 1);


        LinearLayout ll = (LinearLayout) findViewById(R.id.LinearLayoutImage);
        ll.setBackgroundColor(colorName3);
        Bitmap bitmap = Bitmap.createBitmap(scrWidth, 700, Bitmap.Config.ARGB_8888);//RGBA_F16
      /////////  Bitmap bitmap = Bitmap.createBitmap(550, 700, Bitmap.Config.ARGB_8888);//RGBA_F16
        bitmap.eraseColor(colorName3);
        Canvas canvas = new Canvas(bitmap);
        try {
            barcode.drawBarcode(canvas, bounds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iv.setImageBitmap(bitmap);

//message=message+" the end ";
//tv01.setText(message);



    }


}
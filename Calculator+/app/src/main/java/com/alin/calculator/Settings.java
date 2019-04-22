package com.alin.calculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Settings extends AppCompatActivity {

    String mulStyle="*",divStyle="/",isComplex="1";
    Properties properties;
    boolean checked=true;
    QMUICommonListItemView setMulStyle,setDivStyle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(new File(getCacheDir(), "config"));
            properties.load(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch(loadProperties("isComplex")==null?"1":loadProperties("isComplex")){
            case "0":checked=false;break;
            default:checked=true;
        }
        initTopbar();
        initLayout();



    }
    public void initLayout(){
        QMUIGroupListView mGroupListView=findViewById(R.id.group_list_item_contact);
        QMUICommonListItemView cusFunc = mGroupListView.createItemView("自定义函数(WIP)");
        cusFunc.setOrientation(QMUICommonListItemView.VERTICAL); //默认文字在左边

        setMulStyle = mGroupListView.createItemView("乘号样式");
        setMulStyle.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);//右侧更多按钮

        setDivStyle = mGroupListView.createItemView("除号样式");
        setDivStyle.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);//右侧更多按钮


        QMUICommonListItemView ifComplex = mGroupListView.createItemView("复数计算");
        ifComplex.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);

        ifComplex.getSwitch().setChecked(checked);
        ifComplex.getSwitch()
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(Settings.this, "下次启动时生效", Toast.LENGTH_SHORT).show();
                writeProperties("isComplex",isChecked?"1":"0");
            }
        });//默认文字在左边   右侧选择按钮

        QMUICommonListItemView aboutInfo = mGroupListView.createItemView("关于");
        aboutInfo.setOrientation(QMUICommonListItemView.VERTICAL);

       QMUICommonListItemView itemWithCustom = mGroupListView.createItemView("检查更新");
        itemWithCustom.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        QMUILoadingView loadingView = new QMUILoadingView(Settings.this);
        itemWithCustom.addAccessoryCustomView(loadingView);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    switch(text.toString())
                    {
                        case "关于":{
                            final String version= getResources().getString(R.string.version);
                            new QMUIDialog.MessageDialogBuilder(Settings.this)
                                .setTitle("关于")
                                .setMessage("Calculator+ "+version+"\nby Jialin Zhang\n使用的开源项目:\nQMUI by QMUI Team https://github.com/Tencent/QMUI_Android\n" +
                                        "AndroidEdit by qinci https://github.com/qinci/AndroidEdit\n" +
                                        "Android-pickers by addappcn https://github.com/addappcn/android-pickers\n"+
                                        "XUpdate by xuexiangjys https://github.com/xuexiangjys/XUpdate\n")
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();}
                        break;
                        case  "乘号样式":mulStyleMenu();break;
                        case  "除号样式":divStyleMenu();break;
                       // case "自定义函数(WIP)":customFunctionsShow();
                       // case "检查更新":
                        default: new QMUIDialog.MessageDialogBuilder(Settings.this)
                                .setTitle("提示")
                                .setMessage("相关功能仍未完善，将在后续版本中发布")
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            }
        };//默认文字在左边   自定义加载框按钮

        QMUIGroupListView.newSection(Settings.this)
                .setTitle("基本设置")
              //  .addItemView(normalItem, onClickListener)
                .addItemView(setMulStyle, onClickListener)
                .addItemView(setDivStyle,onClickListener)
                .addItemView(ifComplex, onClickListener)
                .addTo(mGroupListView);

        QMUIGroupListView.newSection(Settings.this)
                .setTitle("高级功能")
                .addItemView(cusFunc, onClickListener)
                .addTo(mGroupListView);

        QMUIGroupListView.newSection(Settings.this)
                .setTitle("关于本程序")
                .addItemView(aboutInfo,onClickListener)
                .addItemView(itemWithCustom, onClickListener)
                .addTo(mGroupListView);
    }
    public void initTopbar(){
        final String version= getResources().getString(R.string.version);
        QMUITopBar topbar= findViewById(R.id.Stopbar);
        topbar.setTitle("设置");
        topbar.addLeftTextButton("返回",R.id.filler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }
    public void mulStyleMenu(){
        final String[] items = new String[]{"*", "×"};
        new QMUIDialog.CheckableDialogBuilder(Settings.this)
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Settings.this, "乘号样式变更为 " + items[which]+"下次启动时生效", Toast.LENGTH_SHORT).show();
                        mulStyle= items[which];
                        writeProperties("mulStyle",mulStyle);
                        dialog.dismiss();
                    }
                })
                .show();
    }
    public void divStyleMenu(){
        final String[] items = new String[]{"/", "÷"};
        new QMUIDialog.CheckableDialogBuilder(Settings.this)
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Settings.this, "除号样式变更为 " + items[which]+"下次启动时生效", Toast.LENGTH_SHORT).show();
                        divStyle= items[which];
                        writeProperties("divStyle",divStyle);
                        dialog.dismiss();
                    }
                })
                .show();
    }
    public String loadProperties(String key) {
        return properties.getProperty(key);
    }
    public void writeProperties(String key,String value){
        try {
            FileOutputStream fos = new FileOutputStream(new File(getCacheDir(), "config"));
            properties.setProperty(key, value);
            properties.store(fos, null);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void customFunctionsShow(){
        Intent intent = new Intent();
        intent.setClass(Settings.this, CustomFuncs.class);
        Settings.this.startActivity(intent);
    }
}


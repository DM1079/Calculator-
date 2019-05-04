package com.alin.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;

public class CustomFuncs extends AppCompatActivity {
    private Properties properties;

    private ArrayList<String> cFuncName = new ArrayList<> ();
    private ArrayList<String> cFunc = new ArrayList<> ();
    private String loadProperties(String key) {
        return properties.getProperty(key);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_funcs);

        properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(new File(getCacheDir(), "customFunctions"));
            properties.load(fis);
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
for (int i=0;i<99;i++)
{
    String namebuffer=loadProperties("cFuncName")+String.valueOf(i);
    String funcbuffer=loadProperties("cFunc")+String.valueOf(i);
    if (namebuffer!=null)
        cFuncName.add(namebuffer);
    if(funcbuffer!=null)
        cFunc.add(funcbuffer);
}

        iniTopbar();
      iniLayout();
    }
    private void iniLayout(){
        QMUIGroupListView mGroupListView=findViewById(R.id.group_list_item_contact);
        ArrayList <QMUICommonListItemView> button=new ArrayList<>();
        for (int i=0;i<99 && cFunc.get(i)!=null;i++) {
            button.add(mGroupListView.createItemView(cFuncName.get(i)));
            button.get(i).setOrientation(QMUICommonListItemView.VERTICAL); //默认文字在左边
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    switch(text.toString())
                    {
                        case "":

                        default:
                    }
                }
            }
        };//默认文字在左边   自定义加载框按钮


        QMUIGroupListView.newSection(CustomFuncs.this)
                .setTitle("函数列表")
                .addItemView(button.get(0),onClickListener)
                .addTo(mGroupListView);

    }
    private void iniTopbar(){
        QMUITopBar topbar= findViewById(R.id.Ftopbar);
        topbar.setTitle("自定义函数");
        topbar.addLeftTextButton("返回",R.id.filler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topbar.addRightTextButton("保存",R.id.filler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
    public void newFunction(){
        final  QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(CustomFuncs.this);
        builder.setTitle("ID输入");
        builder.setPlaceholder("请在此输入ID");
        builder.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.addAction("确定", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                CharSequence text = builder.getEditText().getText();
                //获得出入的ID
                String ID = text.toString().trim();
                if (!ID.equals("") && ID != null) {
                    int bulbID = Integer.parseInt(ID);
                    if (bulbID >= 0 && bulbID <= 32767) {
                        dialog.dismiss();
                    } else {
                        Toast.makeText(CustomFuncs.this, "请填入合法ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CustomFuncs.this, "请填入合法ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }
}

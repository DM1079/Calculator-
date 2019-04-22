package com.alin.calculator;

/**
 * @author Alin
 * @version 见res文件
 * 主界面
 */
/**
 * 更新计划：
 * 增加设置页面
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.alin.calculator.Core.Complex;
import com.alin.calculator.Core.Expression;
import com.alin.calculator.Core.Result;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import ren.qinc.edit.PerformEdit;

public class MainActivity extends AppCompatActivity {
    private String divStyle = "/", mulStyle = "*", isComplex = "1";
    private boolean accComplex;

    private void loadProperties() {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(new File(getCacheDir(), "config"));
            properties.load(fis);
            fis.close();
            mulStyle = properties.getProperty("mulStyle", "*");
            divStyle = properties.getProperty("divStyle", "/");
            isComplex = properties.getProperty("isComplex", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Toast.makeText(MainActivity.this, "加载 " + mulStyle,
        // Toast.LENGTH_SHORT).show();
        // 调试用语句
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 沉浸式状态栏
        // QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_main);
        loadProperties();
        // 初始化标题栏
        initTopbar();
        // input.setInputType(InputType.TYPE_NULL);//阻止弹出输入法
        // 初始化按钮
        initButtons();

    }

    private void initTopbar() {

        QMUITopBar topbar = findViewById(R.id.topbar);
        topbar.setTitle("Calculator+");
        topbar.addRightTextButton("设置", R.id.filler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void insert(String s) {
        final EditText input = findViewById(R.id.inputText);
        // 创建PerformEdit，一定要传入不为空的EditText
        int index = input.getSelectionStart();
        Editable edit = input.getEditableText();// 获取EditText的文字
        if (index < 0 || index >= edit.length()) {
            edit.append(s);
        } else {
            edit.insert(index, s);// 光标所在位置插入文字
        }
    }

    private void insert(int n) {
        final EditText input = findViewById(R.id.inputText);
        if (!input.getText().toString().equals("0")){
            String s = Integer.toString(n);
            insert(s);
        }else{
            input.setText(String.valueOf(n));
            input.setSelection(1);
        }
    }

    private void insert(char c) {
        final EditText input = findViewById(R.id.inputText);
        if (!input.getText().toString().equals("0")){
            String s = Character.toString(c);
            insert(s);
        }else{
            input.setText(String.valueOf(c));
            input.setSelection(1);
        }
    }

    private void initButtons() {
        final EditText input = findViewById(R.id.inputText);
        // 创建PerformEdit，一定要传入不为空的EditText
        final PerformEdit pfe = new PerformEdit(input);
        Button bt0 = findViewById(R.id.bt0);
        Button bt1 = findViewById(R.id.bt1);
        Button bt2 = findViewById(R.id.bt2);
        Button bt3 = findViewById(R.id.bt3);
        Button bt4 = findViewById(R.id.bt4);
        Button bt5 = findViewById(R.id.bt5);
        Button bt6 = findViewById(R.id.bt6);
        Button bt7 = findViewById(R.id.bt7);
        Button bt8 = findViewById(R.id.bt8);
        Button bt9 = findViewById(R.id.bt9);
        Button btCE = findViewById(R.id.btCE);
        Button btBackspace = findViewById(R.id.btBackspace);
        Button btSqrt = findViewById(R.id.btSqrt);
        Button btDiv = findViewById(R.id.btDiv);
        Button btMul = findViewById(R.id.btMul);
        Button btSub = findViewById(R.id.btSub);
        Button btAdd = findViewById(R.id.btAdd);
        Button btSolve = findViewById(R.id.btSolve);
        Button btPow = findViewById(R.id.btPow);
        Button btBrackets = findViewById(R.id.btBrackets);
        Button btX = findViewById(R.id.btX);
        Button btI = findViewById(R.id.btI);
        Button btConstants = findViewById(R.id.btConstants);
        Button btFunctions = findViewById(R.id.btFunctions);
        Button btComma = findViewById(R.id.btComma);
        Button btUndo = findViewById(R.id.btUndo);
        Button btRedo = findViewById(R.id.btRedo);
        Button btPoint = findViewById(R.id.btPoint);
        btCE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                input.setText("");
                input.requestFocus();
            }
        });
        btMul.setText(mulStyle);
        btDiv.setText(divStyle);
        btBackspace.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (input.getText().toString().length() > 0 && input.getSelectionStart() != 0) {
                    int index = input.getSelectionStart();
                    Editable edit = input.getEditableText();// 获取EditText的文字
                    if (index < 0 || index >= edit.length()) {
                        edit.delete(index - 1, index);
                    } else {
                        edit.delete(index - 1, index);// 光标所在位置插入文字
                    }
                }
            }
        });
        bt0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(0);
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(1);
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(2);
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(3);
            }
        });
        bt4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(4);
            }
        });
        bt5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(5);
            }
        });
        bt6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(6);
            }
        });
        bt7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(7);
            }
        });
        bt8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(8);
            }
        });
        bt9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(9);
            }
        });
        btMul.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(mulStyle);
            }
        });

        btSub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert('-');
            }
        });

        btComma.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(",");
            }
        });
        btAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert('+');
            }
        });
        btDiv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(divStyle);
            }
        });
        btPow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert("^");
            }
        });
        btFunctions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FunctionsPicker();
            }
        });

        btConstants.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConstantsPicker();
            }
        });
        btUndo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pfe.undo();
            }
        });
        btRedo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pfe.redo();
            }
        });
        btBrackets.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert("()");
                input.setSelection(input.getSelectionStart() - 1);
            }
        });
        btX.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert('x');
            }
        });
        btI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert('i');
            }
        });
        btSqrt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert('√');
            }
        });
        btPoint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert(".");
            }
        });
        btSolve.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String s = input.getText().toString();
                if (!(s.equals("")||s.equals("+")||s.equals("-")||s.equals("%")||s.equals("."))) {
                    new Expression(s);
                    Result rawRst = Expression.value();
                    new Expression(rawRst.getVal().toString());
                    Result rst = Expression.value();
                    Complex rstComplex = rst.getVal();
                    accComplex = true;
                    switch (isComplex) {
                        case "1":
                            break;
                        default:
                            accComplex = false;
                    }
                    if (rst.err == 0) {
                        if (!(rstComplex.isNaN())) {
                            if (!(accComplex || rstComplex.isReal()))
                                Toast.makeText(MainActivity.this, "该表达式的结果不在实数范围内，开启复数功能后可进行运算", Toast.LENGTH_SHORT).show();
                            else {
                                input.setText(rstComplex.toString());
                                input.setSelection(rstComplex.toString().length());
                            }

                        } else
                            Toast.makeText(MainActivity.this, "运算错误，请检查您的输入，错误代码:" + rst.err, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "运算错误，请检查您的输入，错误代码:" + rst.err, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    input.setText("0");
                }
            }
        });
    }

    private void FunctionsPicker() {
        final OptionsPickerView<String> functionsPicker = new OptionsPickerView<>(this);// 函数选择器
        final ArrayList<String> listFunctions = new ArrayList<>();
        listFunctions.add("exp");
        listFunctions.add("ln");
        listFunctions.add("re");
        listFunctions.add("im");
        listFunctions.add("conj");
        listFunctions.add("sqrt");
        listFunctions.add("abs");
        listFunctions.add("norm");
        listFunctions.add("arg");
        listFunctions.add("sin");
        listFunctions.add("cos");
        listFunctions.add("tan");
        listFunctions.add("arcsin");
        listFunctions.add("arccos");
        listFunctions.add("arctan");
        listFunctions.add("gamma");
        listFunctions.add("floor");
        listFunctions.add("ceil");
        listFunctions.add("fzero");
        listFunctions.add("limit");
        listFunctions.add("sum");
        listFunctions.add("integ");
        listFunctions.add("comb");
        listFunctions.add("perm");
        listFunctions.add("diff");
        // 设置数据
        functionsPicker.setPicker(listFunctions);
        // 设置选项单位

        functionsPicker.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {
                String func = listFunctions.get(option1);
                String toastTip;
                switch (func) {
                    case "exp":
                        toastTip = "求以自然对数为底的指数，语法exp(Z)";
                        break;
                    case "ln":
                        toastTip = "求以自然对数为底的对数，语法ln(Z)";
                        break;
                    case "re":
                        toastTip = "返回实部，语法re(Z)";
                        break;
                    case "im":
                        toastTip = "返回虚部，语法im(Z)";
                        break;
                    case "sqrt":
                        toastTip = "返回算术平方根，语法sqrt(x)";
                        break;
                    case "abs":
                        toastTip = "返回绝对值，语法abs(x)";
                        break;
                    case "norm":
                        toastTip = "返回模，语法norm(Z)";
                        break;
                    case "arg":
                        toastTip = "返回幅角，语法arg(Z)";
                        break;
                    case "sin":
                    case "cos":
                    case "tan":
                    case "arcsin":
                    case "arccos":
                    case "arctan":
                        toastTip = "三角函数，语法" + func + "(x)";
                        break;
                    case "fzero":
                        toastTip = "返回函数零点，语法fzero(f(x),零点近似值)";
                        break;
                    case "limit":
                        toastTip = "返回函数f(x)趋于a的极限，语法limit(f(x),a)，可在科学常数内输入无穷";
                        break;
                    case "gamma":
                        toastTip = "返回gamma函数值，语法gamma(x)";
                        break;
                    case "floor":
                        toastTip = "返回下取整值，语法floor(Z)";
                        break;
                    case "ceil":
                        toastTip = "返回上取整值，语法ceil(Z)";
                        break;
                    case "sum":
                        toastTip = "返回f(x)由a到b的累加值，语法sum(f(x),a,b)";
                        break;
                    case "integ":
                        toastTip = "返回f(x)由a到b的积分值，语法integ(f(x),a,b)";
                        break;
                    case "comb":
                        toastTip = "返回m取n的组合数，语法comb(m,n)";
                        break;
                    case "perm":
                        toastTip = "返回m取n的排列数，语法perm(m,n)";
                        break;
                    case "diff":
                        toastTip = "返回f(x)在a处导数值，语法diff(f(x),a)";
                        break;
                    case "conj":
                        toastTip = "返回共轭复数，语法conj(x)";
                        break;
                    default:
                        toastTip = "未知错误";
                }
                insert(func + "()");
                EditText input = findViewById(R.id.inputText);
                input.setSelection(input.getSelectionStart() - 1);
                Toast.makeText(MainActivity.this, toastTip, Toast.LENGTH_SHORT).show();
            }
        });
        functionsPicker.show();
    }

    private void ConstantsPicker() {
        final OptionsPickerView<String> constantsPicker = new OptionsPickerView<>(this);// 常数选择器
        final ArrayList<String> listConstants = new ArrayList<>();
        listConstants.add("е");
        listConstants.add("π");
        listConstants.add("∞");
        listConstants.add("%");
        listConstants.add("°");
        // 设置数据
        constantsPicker.setPicker(listConstants);
        // 设置选项单位
        constantsPicker.setLabels("选择常数");
        constantsPicker.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {
                insert(listConstants.get(option1));
            }
        });
        constantsPicker.show();
    }
}

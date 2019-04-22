package com.alin.calculator.Core;
/**
 * 打算使用正则表达式重写
 * @version 0.1.2
 * 衷心感谢Iraka-C，本表达式处理模块思路借鉴了其开源的Calci-Kernel项目
 * edit by Alin in Feb,2019
 */

public class Expression {
    private static String text;// 表达式的值
    private static int[] brDepth; // 括号深度
    private static int[] nextFuncSym; // 同层下一个功能性标记 如括号、逗号等
    private static int[] commaCnt; // 括号内逗号数量
    private static int[] funcSer; // 被解释的函数
    private static int brDiff; // 左右括号的不同
    private static Cache[] interpretResult;// 存储所有解释过的算式块


    private static boolean isContained(char _char, String _String) {
        return _String.contains(String.valueOf(_char));
    }

    private static Complex ansValue = new Complex(); // ANS
    
    public Expression(String s) { // 对表达式进行初步处理
        text = s;
        int sLength = s.length();
        brDepth = new int[sLength + 1];
        nextFuncSym = new int[sLength + 1];
        commaCnt = new int[sLength + 1];
        brDiff = 0;

        int[] symbolStack = new int[sLength + 1]; // 所有左括号的位置栈
        int[] lastSymbol = new int[sLength + 1]; // 上一个符号的位置

        int top = -1;

        brDepth[0] = 0;
        for (int i = 0; i < sLength; i++) {
            nextFuncSym[i] = -1;
            commaCnt[i] = 0;
            if (i > 0) {
                brDepth[i] = brDepth[i - 1];
                if (s.charAt(i - 1) == '(' || s.charAt(i - 1) == '（')
                    brDepth[i]++;
                if (s.charAt(i) == ')' || s.charAt(i) == '）')
                    brDepth[i]--;
            }
            switch (s.charAt(i)) {
                case '（':
                case '(':// 压栈
                    top++;
                    symbolStack[top] = i;
                    lastSymbol[top] = i;
                    brDiff++;
                    break;
                case '）':
                case ')':
                    if (top >= 0) { // 弹栈
                        nextFuncSym[lastSymbol[top]] = i;
                        top--;
                        brDiff--;
                    }
                    break;
                case '，':
                case ',':
                    if (top >= 0) {
                        commaCnt[symbolStack[top]]++;
                        nextFuncSym[lastSymbol[top]] = i;
                        lastSymbol[top] = i;
                    }
                    break;

                default:
            }
        }
    }

    private static void initCache() { // 初始化赋值的缓存
        funcSer = new int[text.length()];
        interpretResult = new Cache[text.length()];
        int iLength = interpretResult.length;
        for (int i = 0; i < iLength; i++) {
            interpretResult[i] = new Cache();
            funcSer[i] = -1;
        }
    }

    // 字符变量是否是运算符
    private static boolean isOperator(char c) {
        return isContained(c, "+-*/^=√");
    }

    // 大于0的位置p的字符是否是+或-
    private static boolean isAddSubSymbol(int p) {

        if (p == 0)
            return false;

        char lastChar = text.charAt(p - 1);
        char thisChar = text.charAt(p);

        if (!(thisChar == '+' || thisChar == '-')) {
            return false;
        } else
            return !isOperator(lastChar) && lastChar != 'E';
    }

    // 大于0的位置p是否是*号
    private static boolean isOmitMult(int p) {
        if (p == 0)
            return false;

        char thisChar = text.charAt(p);
        char lastChar = text.charAt(p - 1);

        boolean isLastCharPreSymbol = isContained(lastChar, ")∞π°е%");
        boolean isLastCharNumber = ((lastChar >= '0' && lastChar <= '9') || lastChar == '.');
        boolean isLastCharFunc = (lastChar >= 'a' && lastChar <= 'z');
        boolean isThisCharNumber = (thisChar >= '0' && thisChar <= '9' || thisChar == '.');

        boolean case1 = (isLastCharNumber || isLastCharPreSymbol)
                && (thisChar >= 'a' && thisChar <= 'z' || thisChar == '(');
        // preSymbol/数字+函数或括号

        boolean case2 = (isLastCharPreSymbol || isLastCharFunc) && (isThisCharNumber);
        // preSymbol/函数+数字

        boolean case3 = (isLastCharNumber || isLastCharPreSymbol || isLastCharFunc) && isContained(thisChar, "∞π°%√");
        // 数字/preSymbol/函数+特殊符号

        boolean case4 = (isLastCharPreSymbol && isContained(thisChar, "∞π°е%"));
        // preSymbol+preSymbol

        return case1 || case2 || case3 || case4;
    }

    // 在计算中不会出现0+NaN*I，因此这个值被用来标记“未提供变量X”
   static Result value(int l, int r, Complex vX) {
    //vX代表variable X


        if (l > r) {
            return new Result(2).err("表达式语法错误");
        }

        // 检查结果是否被存储
        CachePair pair = interpretResult[l].checkCache(r);
        if (pair != null) { // 存储的结果
            Result r1, r2;
            switch (pair.symbol) { // err<=0
                case CachePair.SYMBOL_CONST:
                    return new Result(pair.cachedValue);
                case CachePair.SYMBOL_NUM:
                    return new Result(pair.cachedValue);
                case CachePair.SYMBOL_ADD:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.add(r1.getVal(), r2.getVal()));
                case CachePair.SYMBOL_SUB:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.sub(r1.getVal(), r2.getVal()));
                case CachePair.SYMBOL_POS:
                    return value(l + 1, r, vX);
                case CachePair.SYMBOL_NEG:
                    r1 = value(l + 1, r, vX);
                    return new Result(Complex.inv(r1.getVal()));
                case CachePair.SYMBOL_MUL:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.mul(r1.getVal(), r2.getVal()));
                case CachePair.SYMBOL_DIV:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.div(r1.getVal(), r2.getVal()));
                case CachePair.SYMBOL_MUL_OMIT:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos, r, vX); // 注意位置！
                    return new Result(Complex.mul(r1.getVal(), r2.getVal()));
                case CachePair.SYMBOL_POW:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.pow(r1.getVal(), r2.getVal()));
                case CachePair.SYMBOL_SQRT:
                    r1 = value(l + 1, r, vX);
                    return new Result(Complex.sqrt(r1.getVal()));
                case CachePair.SYMBOL_FUNC:
                    return funcValue(l, r, vX);
                case CachePair.SYMBOL_BRACKET:
                    return value(l + 1, r - 1, vX);
            }
        }

        // 解释表达式
        String s = text.substring(l, r + 1);

        // 变量
        if (s.equals("x") && (vX.isValid() || vX.isNaN()))
            return new Result(vX); // 变量X

        // 忽略输入的空白符号
        if (isContained(text.charAt(l)," \n\r"))
            return value(l + 1, r, vX);
        if (isContained(text.charAt(r)," \n\r"))
            return value(l, r - 1, vX);

        // =======================处理表达式========================

        { // 常数
            Complex complexConst = null;
            if (s.equals("е"))
                complexConst = Complex.E; // 常数e，采用俄文字符避免与函数表达式中的混淆
            if (s.equals("π"))
                complexConst = Complex.PI; // 常数pi
            if (s.equals("i"))
                complexConst = Complex.I; // 虚数单位
            if (s.equals("∞"))
                complexConst = Complex.Inf; // 常数无穷
            if (s.equals("inf"))
                complexConst = Complex.Inf;// 无穷的另一种表述
            if (s.equals("nan"))
                complexConst = Complex.NAN;// 非数
            if (s.equals("°"))
                complexConst = new Complex(Math.PI / 180); // 角度值
            if (s.equals("%"))
                complexConst = new Complex(0.01); // 百分数值
            if (s.equals("ans"))
                complexConst = ansValue; // 在一次计算中ans不变
            if (complexConst != null) {
                interpretResult[l].submit(r, CachePair.SYMBOL_CONST, complexConst);
                return new Result(complexConst);
            }
        }

        // 分析数字
        try {
            try {
                double v = Double.parseDouble(s);
                interpretResult[l].submit(r, CachePair.SYMBOL_NUM, new Complex(v));
                return new Result(new Complex(v));
            } catch (NumberFormatException e) {
                double v = parse(s);
                interpretResult[l].submit(r, CachePair.SYMBOL_NUM, new Complex(v));
                return new Result(new Complex(v));
            }
        } catch (Exception ignored) {

        }

        char thisChar;
        // 加减运算
        for (int i = r; i > l; i--) {
            thisChar = text.charAt(i);
            if (brDepth[i] == brDepth[l] && isAddSubSymbol(i)) {
                if (thisChar == '+') {
                    interpretResult[l].submit(r, CachePair.SYMBOL_ADD, i);
                    Result r1 = value(l, i - 1, vX);
                    if (r1.isFatalError())
                        return r1;
                    Result r2 = value(i + 1, r, vX);
                    if (r2.isFatalError())
                        return r2;
                    return new Result(Complex.add(r1.getVal(), r2.getVal()));
                } else if (thisChar == '-') {
                    interpretResult[l].submit(r, CachePair.SYMBOL_SUB, i);
                    Result r1 = value(l, i - 1, vX);
                    if (r1.isFatalError())
                        return r1;
                    Result r2 = value(i + 1, r, vX);
                    if (r2.isFatalError())
                        return r2;
                    return new Result(Complex.sub(r1.getVal(), r2.getVal()));
                }
            }
        }

        // 一元运算符 正负号
        if (text.charAt(l) == '+') {
            interpretResult[l].submit(r, CachePair.SYMBOL_POS, -1);
            return value(l + 1, r, vX);
        } else if (text.charAt(l) == '-') {
            interpretResult[l].submit(r, CachePair.SYMBOL_NEG, -1);
            Result r1 = value(l + 1, r, vX);
            if (r1.isFatalError())
                return r1;
            return new Result(Complex.inv(r1.getVal()));
        }

        // 乘除运算
        for (int i = r; i > l; i--) {
            if (brDepth[i] == brDepth[l]) {
                thisChar = text.charAt(i);
                if (thisChar == '*' || thisChar == '×') {
                    interpretResult[l].submit(r, CachePair.SYMBOL_MUL, i);
                    Result r1 = value(l, i - 1, vX);
                    if (r1.isFatalError())
                        return r1;
                    Result r2 = value(i + 1, r, vX);
                    if (r2.isFatalError())
                        return r2;
                    return new Result(Complex.mul(r1.getVal(), r2.getVal()));
                } else if (thisChar == '/' || thisChar == '÷') {
                    interpretResult[l].submit(r, CachePair.SYMBOL_DIV, i);
                    Result r1 = value(l, i - 1, vX);
                    if (r1.isFatalError())
                        return r1;
                    Result r2 = value(i + 1, r, vX);
                    if (r2.isFatalError())
                        return r2;
                    return new Result(Complex.div(r1.getVal(), r2.getVal()));
                } else if (isOmitMult(i)) { // 省略乘法符号
                    interpretResult[l].submit(r, CachePair.SYMBOL_MUL_OMIT, i);
                    Result r1 = value(l, i - 1, vX);
                    if (r1.isFatalError())
                        return r1;
                    Result r2 = value(i, r, vX);
                    if (r2.isFatalError())
                        return r2;
                    return new Result(Complex.mul(r1.getVal(), r2.getVal()));
                }
            }
        }

        // 幂运算（优先级：从右往左）
        for (int i = l; i <= r; i++)
            if (brDepth[i] == brDepth[l] && text.charAt(i) == '^') {
                interpretResult[l].submit(r, CachePair.SYMBOL_POW, i);
                Result r1 = value(l, i - 1, vX);
                if (r1.isFatalError())
                    return r1;
                Result r2 = value(i + 1, r, vX);
                if (r2.isFatalError())
                    return r2;
                return new Result(Complex.pow(r1.getVal(), r2.getVal()));
            }

        // 平方根符号
        if (text.charAt(l) == '√') {
            interpretResult[l].submit(r, CachePair.SYMBOL_SQRT, -1);
            Result r1 = value(l + 1, r, vX);
            if (r1.isFatalError())
                return r1;
            return new Result(Complex.sqrt(r1.getVal()));
        }

        // 括号
        if (text.charAt(r) != ')')
            return new Result(1).err("语法错误");
        if (text.charAt(l) == '(') {
            interpretResult[l].submit(r, CachePair.SYMBOL_BRACKET, -1);
            if (l == r - 1)
                return new Result(new Complex(0, 0));
            return value(l + 1, r - 1, vX);
        }

        // 读取函数
        interpretResult[l].submit(r, CachePair.SYMBOL_FUNC, -1);
        return funcValue(l, r, vX);
    }

    // 下面两个函数都用于读取数字
    private static double parse(String s) throws NumberFormatException {
        int dotPos;
        for (dotPos = 0; dotPos < s.length(); dotPos++) {
            if (s.charAt(dotPos) == '.')
                break;
        }

        double frac = 0;
        double digitBase = 1;
        for (int i = dotPos - 1; i >= 0; i--) {
            frac += getDigit(s.charAt(i)) * digitBase;
            digitBase *= 10;
        }
        digitBase = 1. / 10;
        for (int i = dotPos + 1; i < s.length(); i++) {
            frac += getDigit(s.charAt(i)) * digitBase;
            digitBase /= 10;
        }

        return frac;
    }

    private static int getDigit(char c) throws NumberFormatException {
        int digit;
        if (c >= '0' && c <= '9') {
            digit = c - '0';
        } else {
            throw new NumberFormatException();
        }
        return digit;
    }

    private static Result funcValue(int l, int r, Complex vX) { // 计算函数括号内的值
        String s = text.substring(l, r + 1);

        // 函数
        int listPos; // 在Function类中的位置
        int funcID; // 在Function类中的编号
        int paramNum; // 函数参数个数
        int leftBr; // 左括号位置
        int expectedParamNum; // 函数需要的参数个数

        if (funcSer[l] < 0) { // 值为负代表未开始查找
            for (int i = 0; i < Function.funcList.length; i++) {
                if (s.startsWith(Function.funcList[i].funcName + "(")) {
                    funcSer[l] = i;
                    break;
                }
            }
        }

        listPos = funcSer[l];

        // 如果未找到函数
        if (listPos < 0) {
            return new Result(1).err("未找到所输入的函数");
        }

        funcID = Function.funcList[listPos].funcID;
        leftBr = l + Function.funcList[listPos].funcName.length();
        expectedParamNum = Function.funcList[listPos].expectedParamNum;
        if (text.charAt(leftBr + 1) == ')') {
            paramNum = 0;
        } else {
            paramNum = commaCnt[leftBr] + 1;
        }

        // 参数过多
        if (paramNum > 9)
            return new Result(1).err("函数中所含参数过多");

        // 计算每个参数表达式的值
        Complex[] val = new Complex[10];
        if (paramNum > 0) {
            for (int p = leftBr, i = 0; nextFuncSym[p] >= 0; p = nextFuncSym[p], i++) {
                if (i >= expectedParamNum) {
                    int resl = p + 1;
                    int resr = nextFuncSym[p] - 1;
                    Result res = value(resl, resr, vX);
                    if (res.isFatalError())
                        return res.err("函数参数不合法");
                    val[i] = res.getVal();
                }
            }
        }
        switch (paramNum) {
            case 1:
                switch (funcID) {
                    case Function.EXP:
                        return new Result(Complex.exp(val[0]));
                    case Function.LN:
                        return new Result(Complex.ln(val[0]));
                    case Function.RE:
                        return new Result(new Complex(val[0].re));
                    case Function.IM:
                        return new Result(new Complex(val[0].im));
                    case Function.SQRT:
                        return new Result(Complex.sqrt(val[0]));
                    case Function.ABS:
                        return new Result(val[0].norm());
                    case Function.NORM:
                        return new Result(val[0].norm());
                    case Function.ARG:
                        return new Result(val[0].arg());
                    case Function.SIN:
                        return new Result(Complex.sin(val[0]));
                    case Function.COS:
                        return new Result(Complex.cos(val[0]));
                    case Function.TAN:
                        return new Result(Complex.tan(val[0]));
                    case Function.ARCSIN:
                        return new Result(Complex.arcsin(val[0]));
                    case Function.ARCCOS:
                        return new Result(Complex.arccos(val[0]));
                    case Function.ARCTAN:
                        return new Result(Complex.arctan(val[0]));
                    case Function.GAMMA:
                        return new Result(Complex.gamma(val[0]));
                    case Function.FLOOR:
                        return new Result(new Complex(Math.floor(val[0].re), Math.floor(val[0].im)));
                    case Function.CEIL:
                        return new Result(new Complex(Math.ceil(val[0].re), Math.ceil(val[0].im)));
                    case Function.CONJ:
                        return new Result(new Complex(val[0].re, -val[0].im));
                    default:
                        return new Result(1).err("参数格式不正确");
                }
            case 2:
                switch (funcID) {
                    case Function.DIFF:
                        return Calculus.diff(leftBr + 1, nextFuncSym[leftBr] - 1, val[1]);
                    case Function.LIMIT:
                        return Calculus.limit(leftBr + 1, nextFuncSym[leftBr] - 1, val[1]);
                    case Function.FZERO:
                        return Calculus.solve(leftBr + 1, nextFuncSym[leftBr] - 1, val[1]);
                    case Function.PERM:
                        return new Result(perm(val[0], val[1]));
                    case Function.COMB:
                        return new Result(comb(val[0], val[1]));
                    default:
                        return new Result(1).err("参数格式不正确");
                }
            case 3:
                switch (funcID) {
                    case Function.INTEG:
                        return Calculus.integrate(leftBr + 1, nextFuncSym[leftBr] - 1, val[1], val[2]);
                    case Function.SUM:
                        return Calculus.sum(leftBr + 1, nextFuncSym[leftBr] - 1, val[1], val[2]);
                    default:
                        return new Result(1).err("参数格式不正确");
                }
            default:
                return new Result(1).err("参数格式不正确");
        }
    }

    public static Result value() { // 入口

        if (brDiff != 0) {
            return new Result(1).err("括号不成对");
        }
        // 0+NaNi代表“未指定变量X”
        initCache();
        Result res = value(0, text.length() - 1, new Complex(0, Double.NaN));
        if (res.getVal().isValid()) {
            ansValue = res.getVal();
        }
        return res;
    }

    private static Complex permIter(Complex n_, Complex m_) { // Gamma(n+1)/Gamma(m+1)
        Complex n, m;
        Complex ans = new Complex(1);
        n = n_;
        m = m_;

        for (;;) {
            if (n.re > 1 && m.re > 1) {
                if (n.re - m.re >= 1) {
                    ans = Complex.mul(new Complex(n.re), ans);
                    n.re -= 1;
                } else if (m.re - n.re >= 1) {
                    ans = Complex.div(ans, new Complex(m.re));
                    m.re -= 1;
                } else {
                    ans = Complex.mul(new Complex(n.re / m.re), ans);
                    n.re -= 1;
                    m.re -= 1;
                }
            } else if (n.re == m.re && n.im == m.im) {
                break;
            } else {
                Complex af = Complex.div(Complex.gamma(new Complex(n.re + 1, n.im)),
                        Complex.gamma(new Complex(m.re + 1, m.im)));
                ans = Complex.mul(af, ans);
                break;
            }
            if (!ans.isFinite()) { // 出现无效值，不需继续计算
                break;
            }

        }

        return ans;
    }

    private static Complex perm(Complex n, Complex r) {
        return permIter(n, Complex.sub(n, r));
    }

    private static Complex combIter(Complex n_, Complex m_) { // Gamma(n+1)/Gamma(m+1)/Gamma(n-m+1)
        Complex n, m;
        Complex ans = new Complex(1);
        n = n_;
        m = m_;

        for (;;) {
            if (n.re > 1 && m.re > 1) {
                ans = Complex.mul(new Complex(n.re / m.re), ans);
                n.re -= 1;
                m.re -= 1;
            } else {
                Complex af = Complex.div(perm(n, m), Complex.gamma(new Complex(m.re + 1, m.im)));
                ans = Complex.mul(af, ans);
                break;
            }
            if (!ans.isFinite()) {
                break;
            }
        }
        return ans;
    }

    private static Complex comb(Complex n, Complex r) {
        return combIter(n, r);
    }

}
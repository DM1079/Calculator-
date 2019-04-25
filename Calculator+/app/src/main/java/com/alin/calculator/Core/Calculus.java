package com.alin.calculator.Core;

import java.util.ArrayList;
import java.util.List;
//============微积分相关函数算法来自网络============
/**
 * 微积分类 实现定积分、导数值、求和运算
 * @author HK-SHAO
 * 感谢HK-SHAO以及Calci-Kernel项目的开源
 */
public class Calculus {
    private static final double[] gaussNodes15 = new double[] {
            0.000000000000000000000000000000000e+00, 2.011940939974345223006283033945962e-01,
            3.941513470775633698972073709810455e-01, 5.709721726085388475372267372539106e-01,
            7.244177313601700474161860546139380e-01, 8.482065834104272162006483207742169e-01,
            9.372733924007059043077589477102095e-01, 9.879925180204854284895657185866126e-01 };

    private static final double[] gaussWeights15 = new double[] { 2.025782419255612728806201999675193e-01,
            1.984314853271115764561183264438393e-01, 1.861610000155622110268005618664228e-01,
            1.662692058169939335532008604812088e-01, 1.395706779261543144478047945110283e-01,
            1.071592204671719350118695466858693e-01, 7.036604748810812470926741645066734e-02,
            3.075324199611726835462839357720442e-02 };

    private static Complex gaussIntegrate15(int l, int r, Complex x0, Complex x2) {
        Complex lenH = new Complex(x2.re - x0.re, x2.im - x0.im);
        Complex halfH = new Complex(lenH.re / 2, lenH.im / 2);

        Complex t0 = new Complex((x0.re + x2.re) / 2, (x0.im + x2.im) / 2);
        Complex[] tp = new Complex[7];
        Complex[] tn = new Complex[7];

        for (int i = 0; i < 7; i++) {
            tp[i] = new Complex(t0.re + gaussNodes15[i + 1] * halfH.re, t0.im + gaussNodes15[i + 1] * halfH.im);
            tn[i] = new Complex(t0.re - gaussNodes15[i + 1] * halfH.re, t0.im - gaussNodes15[i + 1] * halfH.im);
        }

        t0 =  Expression.value(l, r, t0).getVal();
        Complex sum = new Complex(t0.re * gaussWeights15[0], t0.im * gaussWeights15[0]);
        for (int i = 0; i < 7; i++) {
            tp[i] =  Expression.value(l, r, tp[i]).getVal();
            tn[i] =  Expression.value(l, r, tn[i]).getVal();
            sum = Complex.add(sum, new Complex((tp[i].re + tn[i].re) * gaussWeights15[i + 1],
                    (tp[i].im + tn[i].im) * gaussWeights15[i + 1]));
        }

        sum = Complex.mul(sum, halfH);

        return sum;
    }

    private static Result solve(int l, int r, Complex x0, Complex M, int iter) {
        Complex x1 = x0;
        Result res1 = Expression.value(l, r, x1);
        if (res1.isFatalError())
            return res1;
        Complex v1 = res1.getVal();
        Complex r1 = Complex.div(v1, diff(l, r, x1).getVal());
        if (r1.isNaN()) {
            return new Result(-1);
        }
        Complex x2 = Complex.sub(x1, r1);
        Complex v2 = Expression.value(l, r, x2).getVal();
        if (r1.normDouble2() < 1E-20 && v2.normDouble2() < 1E-20) {
            return new Result(x2);
        }

        Complex r2 = Complex.div(v2, diff(l, r, x2, r1).getVal());
        if (r2.isNaN()) {
            return new Result(-1);
        }

        Complex x3;
        Result root = new Result(0);
        List<Complex> histRes = new ArrayList<>();
        double minDe = 1E200;
        int minPos = -1;
        int overErrorRangeCount = 0;

        for (int i = 0; i <= iter; i++) {


            Complex d1 = Complex.mul(Complex.sub(x2, x1), r2);
            Complex d2 = Complex.sub(r2, r1);

            x3 = Complex.mul(Complex.sub(x2, Complex.div(d1, d2)), M);

            Complex deltaX = Complex.sub(x2, x3);
            double deltaE = deltaX.normDouble2();

            histRes.add(x3);
            if (i > 0) {

                if (deltaE < minDe) {
                    minDe = deltaE;
                    minPos = i;
                    overErrorRangeCount = 0;
                } else {
                    overErrorRangeCount++;
                }

                if (!x3.isFinite() || overErrorRangeCount > 20) {
                    Complex res = histRes.get(minPos);
                    if (!(minDe > 1E-20) && !(v2.normDouble2() > 1E-18)) {
                        root = new Result(res);
                    }
                    break;
                }

            }

            v2 = Expression.value(l, r, x3).getVal();
            x1 = x2;
            x2 = x3;
            r1 = r2;
            r2 = Complex.div(v2, diff(l, r, x3).getVal());
            if (r2.isNaN()) {
                if (M.re == 1.0) {
                    root = new Result(-1);
                }
                break;
            }
        }

        return root;
    }


    static Result solve(int l, int r, Complex x0) {
        Result rp;
        for (double M = 1.0; M > 0.05; M *= 0.7) {

            rp = solve(l, r, x0, new Complex(M), (int) Math.round(1500 / Math.sqrt(M)));
            if (rp.isFatalError())
                return rp;
            if (rp.getVal().isValid() && !rp.getVal().isNaN()) {
                return rp;
            }
            if (rp.err == -1) {
                break;
            }
        }
        rp = new Result(-1);
        return rp;
    }
    private static boolean isIntegOverTolerance = false;
    private static final double infIntegrateStepRatio = Math.E * Math.E;

    private static Result adaptiveIntegrate(int l, int r, Complex x0, Complex x2, Complex lastSum, double TOL, int depth) {


        Complex x1;

        if (Double.isInfinite(x0.re)) {
            return new Result(3).setVal(new Complex(0));
        }

        if (Double.isInfinite(x2.re)) {
            double aRe = Math.abs(x0.re);
            double newRe;
            if (aRe <= 1E5) {
                double logNextPoint = aRe * 2;
                newRe = (logNextPoint < 1 ? Math.exp(logNextPoint) : infIntegrateStepRatio * aRe);
            } else
                return new Result(-1).setVal(new Complex(0));
            if (x2.re > 0) {
                x1 = new Complex(newRe, x0.im);
            } else {
                x1 = new Complex(-newRe, x0.im);
            }
        } else {
            x1 = new Complex((x0.re + x2.re) / 2, (x0.im + x2.im) / 2);
        }

        Complex sAB, sAC, sCB, sABnew, abbr;

        sAB = lastSum;
        sAC = gaussIntegrate15(l, r, x0, x1);
        sCB = gaussIntegrate15(l, r, x1, x2);
        sABnew = new Complex(0);
        if (sAC.isFinite())
            sABnew = Complex.add(sABnew, sAC);
        if (sCB.isFinite())
            sABnew = Complex.add(sABnew, sCB);
        abbr = Complex.sub(sAB, sABnew);


        if (abbr.isValid() && abbr.normDouble2() < 200 * TOL) {
            return new Result(sABnew);
        }

        if (depth >= 20) {
            Result r1 = new Result(sABnew);
            if (!isIntegOverTolerance && abbr.norm().re > 1E3 * TOL) {
                isIntegOverTolerance = true;
            }

            return r1;
        }

        sAC = adaptiveIntegrate(l, r, x0, x1, sAC, TOL / 4, depth + 1).getVal();
        sCB = adaptiveIntegrate(l, r, x1, x2, sCB, TOL / 4, depth + 1).getVal();
        sABnew = Complex.add(sAC, sCB);

        return new Result(sABnew);
    }


     static Result integrate(int l, int r, Complex x0, Complex x2) {
        if (x0.isNaN()) {
            return new Result(-1);
        }
        if (x2.isNaN()) {
            return new Result(-1);
        }
        Result check =  Expression.value(l, r, x0);
        if (check.isFatalError())
            return check;

        if (Double.isInfinite(x0.re)) {
            if (Double.isInfinite(x2.re)) {
                double iim = ((Complex.isDoubleFinite(x0.im) ? x0.im : 0) + (Complex.isDoubleFinite(x2.im) ? x2.im : 0))
                        / 2;

                Result r1 = integrate(l, r, x0, new Complex(0, iim));
                Result r2 = integrate(l, r, new Complex(0, iim), x2);
                return new Result(Complex.add(r1.getVal(), r2.getVal()));
            } else {
                Result r1 = integrate(l, r, x2, x0);
                return new Result(new Complex(-r1.getVal().re, -r1.getVal().im));
            }
        }

        double TOL = 1E-8;

        Complex sAB = gaussIntegrate15(l, r, x0, x2);
        return adaptiveIntegrate(l, r, x0, x2, sAB, TOL * TOL, 0);
    }

    // 求和
     static Result sum(int l, int r, Complex start, Complex end) {
        double ds = start.re;
        double de = end.re;
        boolean isInfiniteSummation = (Double.isInfinite(ds) || Double.isInfinite(de));

        if (de < ds) {
            return new Result(2);
        }

        Complex sum = new Complex(0);
        Complex v = new Complex(0);
        final double TOL2 = 1E-16;
        final int maxBoundCnt = 1000;
        final int maxCnt = 100000;
        int boundCnt = 0;
        int cnt = 0;

        double ratio = (end.im - start.im) / (de - ds);
        if (!Complex.isDoubleFinite(ratio)) {
            return new Result(3);
        }
        for (v.re = ds; v.re <= de; v.re += 1, cnt++) {

            v.im = (v.re - ds) * ratio + start.im;
            Result res = Expression. value(l, r, v);
            if (res.isFatalError()) {
                return res;
            }
            if (!res.getVal().isFinite()) {
                return new Result(sum);
            }

            if (isInfiniteSummation) {
                if (res.getVal().normDouble2() < TOL2) {
                    boundCnt++;
                } else {
                    boundCnt = 0;
                }
                if (boundCnt > maxBoundCnt) {
                    break;
                }
            }

            if (cnt == maxCnt) {
                new Result(-1);
            }

            sum = Complex.add(sum, res.getVal());
        }

        return new Result(sum);
    }

    // 排列组合


    private static Result diff3(int l, int r, Complex x0, Complex delta) {
        Result rn = Expression.value(l, r, Complex.sub(x0, delta));
        if (rn.isFatalError())
            return rn;
        if (!rn.getVal().isValid())
            return new Result(-1);
        Result rp = Expression.value(l, r, Complex.add(x0, delta));
        if (rp.isFatalError())
            return rp;
        Complex dv = Complex.div(Complex.sub(rp.getVal(), rn.getVal()), new Complex(delta.re * 2, delta.im * 2));
        return new Result(dv);
    }


    private static Result diff5(int l, int r, Complex x0, Complex delta) {
        Result r1 = diff3(l, r, x0, delta);
        if (r1.isFatalError())
            return r1;
        if (!r1.getVal().isValid())
            return new Result(-1);
        Result r2 = diff3(l, r, x0, new Complex(delta.re * 2, delta.im * 2));
        if (r2.isFatalError())
            return r2;

        Complex dv = Complex.div(new Complex(r1.getVal().re * 4 - r2.getVal().re, r1.getVal().im * 4 - r2.getVal().im), new Complex(3));
        return new Result(dv);
    }


    private static boolean isDiffOverTolerance = false;

    static Result diff(int l, int r, Complex x0) {

        final int sect = 8;
        final double sectAngle = Math.PI / sect;
        final double TOL = 1E-5;
        Complex[] dirDer = new Complex[sect];
        Complex dsum = new Complex(0);
        double dvar = 0;
        for (int i = 0; i < sect; i++) {
            Complex delta = new Complex(Math.cos(i * sectAngle) * TOL, Math.sin(i * sectAngle) * TOL);
            Result rv = diff5(l, r, x0, delta);
            if (rv.isFatalError())
                return rv;
            dirDer[i] = rv.getVal();
            dsum = Complex.add(dsum, rv.getVal());
        }
        dsum.re /= sect;
        dsum.im /= sect;
        for (int i = 0; i < sect; i++) {
            dvar += Complex.sub(dirDer[i], dsum).normDouble2();
        }
        Result res = new Result(dsum);
        if (!isDiffOverTolerance && dvar > TOL) {
            isDiffOverTolerance = true;
        }
        return res;
    }

     static Result diff(int l, int r, Complex x0, Complex dir) {
        if (dir.re == 0 && dir.im == 0 || !dir.isFinite())
            return new Result(1);

        final double TOL = 1E-5;

        double norm = dir.norm().re;
        Complex delta = new Complex(dir.re / norm * TOL, dir.im / norm * TOL);
        Result rv = diff5(l, r, x0, delta);
        if (rv.isFatalError())
            return rv;
        return new Result(rv.getVal());
    }


    private static final Complex par2p = new Complex((1 + Math.sqrt(2)) / 4);
    private static final Complex par2n = new Complex((1 - Math.sqrt(2)) / 4);
    private static final Complex hRatio = new Complex(1 + Math.sqrt(2));

    private static Result limitH(int l, int r, Complex x0, Complex h) {

        Complex f0;
        boolean finiteLimit = x0.isFinite();

        Complex x1, x2, x3, x4;

        if (finiteLimit) {
            x1 = Complex.add(x0, h);
            x2 = Complex.add(x0, Complex.mul(h, hRatio));
            x3 = Complex.sub(x0, h);
            x4 = Complex.sub(x0, Complex.mul(h, hRatio));
        } else {
            double normDouble2 = h.normDouble2();
            x1 = new Complex(h.re / normDouble2, h.im / normDouble2);
            x2 = new Complex(x1.re * 2, x1.im * 2);
            x3 = new Complex(x1.re * 3, x1.im * 3);
            x4 = new Complex(x1.re * 4, x1.im * 4);
        }

        Result r1 = Expression.value(l, r, x1);
        if (r1.isFatalError())
            return r1;
        Result r2 =  Expression.value(l, r, x2);
        if (r2.isFatalError())
            return r2;
        Result r3 =  Expression.value(l, r, x3);
        if (r3.isFatalError())
            return r3;
        Result r4 =  Expression.value(l, r, x4);
        if (r4.isFatalError())
            return r4;

        Complex f1 = r1.getVal();
        Complex f2 = r2.getVal();
        Complex f3 = r3.getVal();
        Complex f4 = r4.getVal();

        if (finiteLimit) {
            Complex f13 = Complex.mul(Complex.add(f1, f3), par2p);
            Complex f24 = Complex.mul(Complex.add(f2, f4), par2n);
            f0 = Complex.add(f13, f24);
        } else {
            f0 = new Complex((-f1.re + 24 * f2.re - 81 * f3.re + 64 * f4.re) / 6,
                    (-f1.im + 24 * f2.im - 81 * f3.im + 64 * f4.im) / 6);
        }

        return new Result(f0);
    }


    static Result limit(int l, int r, Complex x0) {

        if (Double.isInfinite(x0.re)) {
            return limit(l, r, x0, new Complex(x0.re > 0 ? 1 : -1));
        }
        if (Double.isInfinite(x0.im)) {
            return limit(l, r, x0, new Complex(0, x0.im > 0 ? 1 : -1));
        }

        final int sect = 8;
        final double sectAngle = Math.PI / sect;
        Complex[] limitRes = new Complex[sect];
        Complex limitSum = new Complex(0);
        double limitVar = 0;
        int validSect = 0;

        for (int i = 0; i < sect; i++) {

            List<Complex> histRes = new ArrayList<>();
            double minDe = 1E200;
            int minPos = -1;

            int cnt = 0;
            int overErrorRangeCount = 0;
            double h;
            for (h = 1E-1; h >= 1E-10; h *= 0.9, cnt++) {
                Complex delta = new Complex(Math.cos(i * sectAngle) * h, Math.sin(i * sectAngle) * h);
                Result resR = limitH(l, r, x0, delta);
                if (resR.isFatalError())
                    return resR;
                Complex res = resR.getVal();

                if (cnt > 0) {
                    double e = Complex.sub(res, histRes.get(cnt - 1)).norm().re;

                    if (e < minDe) {
                        overErrorRangeCount = 0;
                        minDe = e;
                        minPos = cnt;
                    } else {
                        overErrorRangeCount++;
                    }
                    if (overErrorRangeCount > 20) {
                        break;
                    }

                }

                histRes.add(res);
            }

            if (minDe > 1E-5) {
                new Result(-1);
            } else {
                Complex minRes = histRes.get(minPos - 1);
                limitSum = Complex.add(limitSum, minRes);
                limitRes[validSect] = minRes;
                validSect++;
            }
        }

        if (validSect == 0)
            return new Result(-1);

        limitSum.re /= validSect;
        limitSum.im /= validSect;
        for (int i = 0; i < validSect; i++) {
            limitVar += Complex.sub(limitRes[i], limitSum).normDouble2();
        }



        Result res = new Result(limitSum);
        if (!(limitVar > 1E-5)) {
            return res;
        }

        return res;
    }


    static Result limit(int l, int r, Complex x0, Complex dir) {
        if (dir.re == 0 && dir.im == 0 || !dir.isFinite())
            return new Result(1);

        List<Complex> histRes = new ArrayList<>();
        double minDe = 1E200;
        int minPos = -1;
        double norm = dir.norm().re;

        int cnt = 0;
        int overErrorRangeCount = 0;
        double h;
        for (h = 1E-1; h >= 1E-10; h *= 0.9, cnt++) {
            Complex delta = new Complex(dir.re / norm * h, dir.im / norm * h);
            Result resR = limitH(l, r, x0, delta);
            if (resR.isFatalError())
                return resR;
            Complex res = resR.getVal();

            if (cnt > 0) {
                double e = Complex.sub(res, histRes.get(cnt - 1)).norm().re;

                if (e < minDe) {
                    overErrorRangeCount = 0;
                    minDe = e;
                    minPos = cnt;
                } else {
                    overErrorRangeCount++;
                }
                if (overErrorRangeCount > 20) {
                    break;
                }

            }

            histRes.add(res);
        }

        if (minPos < 1) {
            return new Result(-1);
        }
        Complex minRes = histRes.get(minPos - 1);

        if (minDe > 1E-5) {
            return new Result(-1);
        } else {
            return new Result(minRes);
        }
    }

}


/**
 * @version 1.0.0
 *  复数以及部分复数函数的实现
 */

package com.alin.calculator.Core;

public class Complex {
	final static Complex E = new Complex(Math.E);
	final static Complex PI = new Complex(Math.PI);
	final static Complex I = new Complex(0, 1);
	final static Complex Inf = new Complex(Double.POSITIVE_INFINITY, Double.NaN);
	final static Complex NAN = new Complex(Double.NaN);

	double re;
	public double im;

	/**
	 * 0---正常 1----非数 错误代码改为在Result类中表示
	 */
	// ----------------构造方法----------------
	Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}

	Complex(double re) {
		this.re = re;
		this.im = 0;
	}
	Complex(Complex v){
		this.re=v.re;
		this.im=v.im;
	}

	Complex() {
		this.re = Double.NaN;
		this.im = Double.NaN;
	}

	void setComplex(Complex v){
		this.re=v.re;
		this.im=v.im;
	}

	Complex getComplex(){
		return this;
	}


	public boolean isReal() {
		return (this.im == 0);
	}

	boolean isPureIm() {
		return ((this.im != 0) && this.re == 0);
	}

	// ----------------基本运算----------------
	static Complex add(Complex a, Complex b) {
		// 复数相加
		return new Complex(a.re + b.re, a.im + b.im);
	}

	static Complex sub(Complex a, Complex b) {
		// 复数相减
		return new Complex(a.re - b.re, a.im - b.im);
	}

	static Complex inv(Complex a) {
		// 相反数
		return new Complex(-a.re, -a.im);
	}

	static Complex mul(Complex a, Complex b) {
		// 复数相乘
		return new Complex(a.re * b.re - a.im * b.im, a.re * b.im + a.im * b.re);
	}

	static Complex div(Complex a, Complex b) {
		// 复数除法
		double aNorm = a.norm().re;
		double bNorm = b.norm().re;
		if (aNorm > 0 && bNorm == 0)
			return Inf;
		if (Double.isInfinite(bNorm) && Complex.isDoubleFinite(aNorm))
			return new Complex(0);
		double bre = b.re / bNorm; // 避免溢出
		double bim = b.im / bNorm;
		double re = (a.re * bre + a.im * bim) / bNorm; // 避免溢出
		double im = (a.im * bre - a.re * bim) / bNorm;
		return new Complex(re, im);
	}

	double normDouble2() {
		// 返回模的平方
		if (Double.isInfinite(re) || Double.isInfinite(im))
			return Double.POSITIVE_INFINITY;
		return Math.hypot(re, im) * Math.hypot(re, im);
	}

	Complex norm() {
		// 使用Math类的hypot方法求模
		return new Complex(Math.hypot(re, im));
	}

	Complex abs() {
		// 返回实数的绝对值
		if (im == 0)
			return new Complex(Math.abs(re));
		return new Complex();
	}

	Complex arg() {
		// 求幅角
		if (im == 0 && re == 0) // 0的幅角无意义
			return NAN;
		return new Complex(Math.atan2(im, re));
	}

	public boolean isNaN() {
		return Double.isNaN(re);
	}

	boolean isValid() { // 复数无穷大
		return !(isDoubleFinite(re) && Double.isNaN(im));
	}

	static boolean isDoubleFinite(double d) {
		return (!Double.isNaN(d)) && (!Double.isInfinite(d));
	}

	boolean isFinite() {
		return Complex.isDoubleFinite(re) && Complex.isDoubleFinite(im);
	}

	static Complex pow(Complex a, Complex b) {
		if (a.re == 0 && a.im == 0) {
			if (b.re > 0)
				return new Complex(0);
			else if (b.re < 0 && b.im == 0)
				return Inf;
			else
				return NAN;
		}
		if (a.norm().re < 1 && b.re == Double.POSITIVE_INFINITY) {
			return new Complex(0);
		}
		if (a.norm().re > 1 && b.re == Double.NEGATIVE_INFINITY) {
			return new Complex(0);
		}

		return Complex.exp(Complex.mul(b, Complex.ln(a)));
	}

	private static String doubleToString(double d_) {
		if (Double.isNaN(d_)) {
			return "nan";
		}
		if (Double.isInfinite(d_)) {
			return d_ > 0 ? "inf" : "-inf";
		}
		String negativeSymbol = (d_ >= 0 ? "" : "-");
		double d = Math.abs(d_);
		double maxPreciseValue = Math.pow(10, 10);
		double minPreciseValue = Math.pow(10, -10);
		String result;
		if (d < maxPreciseValue && d > minPreciseValue) {
			result = negativeSymbol + toPositiveRawString(d);
		} else { // 实现科学计数法
			double fracPart = d;
			int digitExp = 0;
			while (fracPart >= 10) {
				digitExp++;
				fracPart /= 10;
			}
			while (fracPart < 1) {
				digitExp--;
				fracPart *= 10;
			}
			result = negativeSymbol + toPositiveRawString(fracPart);
			result += "E";
			result += digitExp;
		}
		return result;
	}

	private static String toPositiveRawString(double d) {
		int[] digits = new int[100];
		int intDigitNum = (int) Math.floor(Math.log(d) / Math.log(10)) + 1;
		if (intDigitNum < 0)
			intDigitNum = 0;

		long intPart = (long) Math.floor(d);
		double fracPart = d - intPart;

		for (int i = intDigitNum; i >= 0; i--) {
			digits[i] = (int) (intPart % 10);
			intPart /= 10;
		}

		for (int i = intDigitNum + 1; i <= 10 + 1; i++) {
			fracPart *= 10;
			digits[i] = (int) Math.floor(fracPart);
			fracPart -= digits[i];
		}

		if (digits[11] * 2 >= 10) { // carry-over
			digits[10]++;
			for (int i = 10; i > 0; i--) {
				if (digits[i] == 10) {
					digits[i] = 0;
					digits[i - 1]++;
				} else {
					break;
				}
			}
		}
		int maxNonZeroPos; // omit 0 digits after dot & at the rear of a notation
		for (maxNonZeroPos = 10; maxNonZeroPos >= 0; maxNonZeroPos--) {
			if (maxNonZeroPos <= intDigitNum || digits[maxNonZeroPos] > 0)
				break;
		}

		StringBuilder resultBuilder = new StringBuilder();
		for (int i = 0; i <= maxNonZeroPos; i++) { // construct string
			if (!(intDigitNum > 0 && i == 0 && digits[0] == 0))
				resultBuilder.append("0123456789".charAt(digits[i]));
			if (i == intDigitNum && i < maxNonZeroPos)
				resultBuilder.append('.');
		}
		StringBuilder result = new StringBuilder(resultBuilder.toString());

		for (int i = maxNonZeroPos + 1; i <= intDigitNum; i++)
			result.append('0');
		return result.toString();
	}

	public String toString() { // 需要优化
		String s = "";
		double threshold = (Result.precision < Result.maxPrecision ? Math.pow(Result.base, -Result.precision) : 0);
		if (Double.isNaN(im) && Double.isInfinite(re)) {
			s = (re > 0 ? "∞" : "-∞");
		} else if (Math.abs(re) > threshold || Double.isNaN(re)) {
			s += doubleToString(re);
			if (isDoubleFinite(im)) {
				if (Math.abs(im) > threshold) {
					s += (im > 0 ? "+" : "-");
					if (Math.abs(Math.abs(im) - 1) > threshold) {
						s += doubleToString(Math.abs(im));
					}
					s += "i";
				}
			} else { // inf 或 nan
				s += (im < 0 ? "" : "+"); // +inf/nan 化为 +
				s += doubleToString(im) + "*i";
			}
		} else {
			if (isDoubleFinite(im)) {
				if (Math.abs(im) > threshold) {
					s += (im > 0 ? "" : "-");
					if (Math.abs(Math.abs(im) - 1) > threshold) {
						s += doubleToString(Math.abs(im));
					}
					s += "i";
				} else { // Nothing
					s += "0";
				}
			} else { // Inf或Nan
				s += doubleToString(im) + "*i";
			}
		}
		return s;
	}

	// =======================复数函数分割线============================

	static Complex exp(Complex z) {
		if (z.re == Double.NEGATIVE_INFINITY)
			return new Complex(0);
		double norm = Math.exp(z.re);
		return new Complex(norm * Math.cos(z.im), norm * Math.sin(z.im));
	}

	static Complex ln(Complex c) {
		return new Complex(Math.log(c.norm().re), c.arg().re);
	}

	static Complex sqrt(Complex c) {
		double norm = c.norm().re;
		if (norm == 0)
			return new Complex(0);
		double cosArg = c.re / norm; // 不适用于0
		double sind2 = Math.sqrt((1 - cosArg) / 2);
		double cosd2 = Math.sqrt((1 + cosArg) / 2);
		if (c.im < 0)
			sind2 = -sind2;
		norm = Math.sqrt(norm);
		return new Complex(norm * cosd2, norm * sind2);
	}

	static Complex sin(Complex c) {
		double eip = Math.exp(c.im);
		double ein = Math.exp(-c.im);
		return new Complex((eip + ein) * Math.sin(c.re) / 2, (eip - ein) * Math.cos(c.re) / 2);
	}

	static Complex cos(Complex c) {
		double eip = Math.exp(c.im);
		double ein = Math.exp(-c.im);
		return new Complex((eip + ein) * Math.cos(c.re) / 2, (ein - eip) * Math.sin(c.re) / 2);
	}

	static Complex tan(Complex c) {
		double re2 = c.re * 2;
		double im2 = c.im * 2;

		double eip2 = Math.exp(im2);
		double ein2 = Math.exp(-im2);
		double sinhi2 = (eip2 - ein2) / 2;
		double coshi2 = (eip2 + ein2) / 2;

		if (Double.isInfinite(coshi2)) { // 特殊情况
			return new Complex(0, c.im > 0 ? 1 : -1);
		}

		double ratio = Math.cos(re2) + coshi2;
		double resRe = Math.sin(re2) / ratio;
		double resIm = sinhi2 / ratio;
		return new Complex(resRe, resIm);
	}

	static Complex arcsin(Complex c) {
		Complex v = Complex.add(Complex.mul(c, I), Complex.sqrt(Complex.sub(new Complex(1), Complex.mul(c, c))));
		return Complex.mul(new Complex(0, -1), Complex.ln(v));
	}

	static Complex arccos(Complex c) {
		Complex v = Complex.add(c, Complex.sqrt(Complex.sub(Complex.mul(c, c), new Complex(1))));
		return Complex.mul(new Complex(0, -1), Complex.ln(v));
	}

	static Complex arctan(Complex c) {
		if (c.re == Double.POSITIVE_INFINITY)
			return new Complex(Math.PI / 2);
		if (c.re == Double.NEGATIVE_INFINITY)
			return new Complex(Math.PI / 2);

		Complex c1 = new Complex(1 - c.im, c.re);
		Complex c2 = new Complex(1 + c.im, -c.re);
		double re_ = (c1.arg().re - c2.arg().re) / 2;
		double im_ = (Math.log(c2.norm().re) - Math.log(c1.norm().re)) / 2;
		return new Complex(re_, im_);
	}

	/**
	 * by HK-SHAO https://github.com/HK-SHAO/DarkCalculator
	 */

	private static final double[] gammaP = { // constants for Lanczos approximation
			676.5203681218851, -1259.1392167224028, 771.32342877765313, -176.61502916214059, 12.507343278686905,
			-0.13857109526572012, 9.9843695780195716E-6, 1.5056327351493116E-7 };
	private static final double[] gammaT = { // constants for Taylor approximation
			-0.57721566490153286, 0.9890559953279725, 0.9074790760808862, 0.9817280868344002, 0.9819950689031453,
			0.9931491146212761 };

	static Complex gamma(Complex c) { // Lanczos approximation + Taylor series

		if (c.re == Double.POSITIVE_INFINITY && c.im == 0)
			return Complex.Inf;
		Complex result;
		if (c.re < -310) { // guarantee result in double field
			if (c.re == Double.NEGATIVE_INFINITY) {
				if (c.im == 0)
					result = new Complex();
				else
					result = new Complex(0);

			} else if (c.re == Math.floor(c.re) && c.im == 0) {
				result = Complex.Inf;
			} else {
				result = new Complex(0);
			}
		} else if (c.re < -0.5) { // negative x complex plane
			int k = (int) Math.floor(-c.re) + 1;
			result = Complex.gamma(new Complex(c.re + k, c.im));
			for (int i = k - 1; i >= 0; i--) { // reversed order, prevent 0/0 -> NaN
				if (!result.isFinite())
					break;
				result = Complex.div(result, new Complex(c.re + i, c.im));
			}
		} else if (c.re > 142) { // big numbers
			double kd = Math.ceil(c.re - 142);
			long k = (long) kd;
			result = Complex.gamma(new Complex(c.re - kd, c.im));
			if (result.re != 0 || result.im != 0) {
				for (long i = 1; i <= k; i++) {
					if (!result.isFinite())
						break;
					result = Complex.mul(result, new Complex(c.re - i, c.im));
				}
			}
		} else if (Math.abs(c.re) < 1E-3 && Math.abs(c.im) < 1E-2) { // Taylor series, deal with value REALLY near the
			// pole 0
			result = new Complex(0);
			for (int i = gammaT.length - 1; i >= 0; i--) {
				result = Complex.mul(result, c);
				result = new Complex(result.re + gammaT[i], result.im);
			}
			result = Complex.add(result, Complex.div(new Complex(1), c));
		} else if (c.re < 0.5 && Math.abs(c.im) <= 220) { // Reflection formula(more precise), deal with value near the
			// pole 0
			Complex sZ = Complex.sin(Complex.mul(Complex.PI, c));
			Complex gZ = Complex.gamma(Complex.sub(new Complex(1), c));
			// Log.i("Gamma","sZ="+sZ+" gZ="+gZ);
			result = Complex.div(Complex.PI, Complex.mul(sZ, gZ));
		} else {
			Complex z = new Complex(c.re - 1, c.im);
			Complex x = new Complex(0.99999999999980993);

			for (int i = 0; i < gammaP.length; i++) {
				Complex dn = new Complex(z.re + i + 1, z.im);
				x = Complex.add(x, Complex.div(new Complex(gammaP[i]), dn));
			}

			Complex t = new Complex(z.re + gammaP.length - 0.5, z.im);
			result = Complex.exp(Complex.mul(new Complex(z.re + 0.5, z.im), Complex.ln(t)));
			result = Complex.mul(new Complex(Math.sqrt(2 * Math.PI)), result);
			result = Complex.mul(Complex.exp(Complex.inv(t)), result);
			result = Complex.mul(result, x);
		}
		if (Double.isInfinite(result.re) && !Complex.isDoubleFinite(result.im))
			result.im = Double.NaN;
		return result;
	}

}
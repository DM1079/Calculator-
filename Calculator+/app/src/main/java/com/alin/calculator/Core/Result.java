package com.alin.calculator.Core;

public class Result extends Complex {
	public int err;
	static int precision = 10;
	static int base = 10;
	static int maxPrecision = 15;
	private String text;

	/*
	 * Error : 0: 正常运行 1: 数学语法错误 2: 输入语法有误 3: 计算线程终止 -1: 数学错误 <=-2: 个别函数错误
	 */

	Result(Complex v) {
		super(v);
		err = 0;
	}

	public String getText() {
		return this.text;
	}

	public Result(Complex v, int _err) {
		super(v);
		err = _err;
	}

	Result(int _err) {
		super(Complex.NAN);
		this.err = _err;
	}

	Result append(String _name, String _text, int l, int r) {
		this.text = _text;
		return this;
	}

	Result setVal(Complex v) {
		super.setComplex(v);
		return this;
	}

	public Complex getVal(){
		return super.getComplex();
	}

	Result err(String _text) {
		this.text = _text;
		return this;
	}

	boolean isFatalError() {
		return err > 0;
	}
}
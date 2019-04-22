
/**
 * 函数列表
 */

package com.alin.calculator.Core;

class Function {

	static final int EXP = 10;
	static final int LN = 20;
	static final int RE = 30;
	static final int IM = 40;
	static final int SQRT = 50;
	static final int ABS = 60;
	static final int NORM = 70;
	static final int ARG = 80;
	static final int SIN = 90;
	static final int COS = 100;
	static final int TAN = 110;
	static final int ARCSIN = 120;
	static final int ARCCOS = 130;
	static final int ARCTAN = 140;
	static final int GAMMA = 150;
	static final int FLOOR = 160;
	static final int CEIL = 170;
	static final int CONJ = 180;
	static final int DIFF = 190;
	static final int LIMIT = 200;
	static final int FZERO = 210;
	static final int INTEG = 220;
	static final int SUM = 230;
	static final int PERM = 240;
	static final int COMB = 250;

	static class FunctionList {
		String funcName;
		int funcID;
		int expectedParamNum; // 可接受的参数数量

		FunctionList(String funcName, int funcID) {
			this.funcName = funcName;
			this.funcID = funcID;
			this.expectedParamNum = 0;
		}

		FunctionList(String funcName, int funcID, int exprParamNum) {
			this.funcName = funcName;
			this.funcID = funcID;
			this.expectedParamNum = exprParamNum;
		}
	}

	// 注册函数列表
	static final FunctionList[] funcList = { new FunctionList("exp", EXP), new FunctionList("ln", LN), new FunctionList("re", RE),
			new FunctionList("im", IM), new FunctionList("sqrt", SQRT), new FunctionList("abs", ABS), new FunctionList("norm", NORM),
			new FunctionList("arg", ARG), new FunctionList("sin", SIN), new FunctionList("cos", COS), new FunctionList("tan", TAN),
			new FunctionList("arcsin", ARCSIN), new FunctionList("arccos", ARCCOS), new FunctionList("arctan", ARCTAN),
			new FunctionList("gamma", GAMMA), new FunctionList("floor", FLOOR), new FunctionList("ceil", CEIL), new FunctionList("conj", CONJ),
			new FunctionList("diff", DIFF, 1), new FunctionList("limit", LIMIT, 1), new FunctionList("fzero", FZERO, 1),
			new FunctionList("integ", INTEG, 1), new FunctionList("sum", SUM, 1), new FunctionList("perm", PERM),
			new FunctionList("comb", COMB), };
}
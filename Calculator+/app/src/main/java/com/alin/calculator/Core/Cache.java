package com.alin.calculator.Core;

import java.util.ArrayList;
import java.util.List;

// 存储表达式解释结果
class CachePair {
    static final int SYMBOL_NUM = 0;
    static final int SYMBOL_ADD = 1;
    static final int SYMBOL_POS = 2;
    static final int SYMBOL_SUB = 3;
    static final int SYMBOL_NEG = 4;
    static final int SYMBOL_MUL = 5;
    static final int SYMBOL_DIV = 6;
    static final int SYMBOL_MUL_OMIT = 7;
    static final int SYMBOL_POW = 8;
    static final int SYMBOL_SQRT = 9;
    static final int SYMBOL_CONST = 10;
    static final int SYMBOL_FUNC = 11;
    static final int SYMBOL_BRACKET = 12;

    int end_pos;
    int symbol;
    int symbol_pos;

    Complex cachedValue;

    CachePair(int _end_pos, int _symbol, int _symbol_pos, Complex _cachedValue) {
        this.end_pos = _end_pos;// 结束位置
        this.symbol = _symbol;
        this.symbol_pos = _symbol_pos;// 符号位置
        this.cachedValue = _cachedValue;
    }
}

class Cache {
    List<CachePair> cacheList;

    Cache() {
        cacheList = new ArrayList<>();
    }

    void submit(int end_pos, int symbol, int symbol_pos) {
        cacheList.add(new CachePair(end_pos, symbol, symbol_pos, new Complex()));
    }

    void submit(int end_pos, int symbol, Complex cachedValue) {
        cacheList.add(new CachePair(end_pos, symbol, -1, cachedValue));
    }

    CachePair checkCache(int end_pos) {
        int ls = cacheList.size();
        for (int i = 0; i < ls; i++) {
            CachePair pair = cacheList.get(i);
            if (pair.end_pos == end_pos) {
                return pair;
            }
        }
        return null;
    }
}
package com.ap;

import com.ebay.a.HelperA;
import com.ebay.b.HelperB;
import com.ebay.a.aa.HelperAa;

public class App {

    public static void main(String[] args) {
        AppHelper helper = new AppHelper();
        helper.doHelper();

        HelperA helperA = new HelperA();
        helperA.helpA();

        HelperAa helperAa = new HelperAa();
        helperAa.helpAa();

        HelperB helperB = new HelperB();
        helperB.helpB();
    }
}

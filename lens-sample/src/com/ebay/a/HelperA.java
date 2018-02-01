package com.ebay.a;

public class HelperA {
    public void helpA() {
        try {
            Thread.sleep(20);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("From HelperA - helpA");
    }
}

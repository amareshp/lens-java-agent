package com.ebay.a.aa;

public class HelperAa {
    public void helpAa() {
        try {
            Thread.sleep(200);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("From HelperAa - helpAa");
    }
}

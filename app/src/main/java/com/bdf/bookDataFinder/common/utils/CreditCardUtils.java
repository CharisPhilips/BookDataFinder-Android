package com.bdf.bookDataFinder.common.utils;

public class CreditCardUtils {

    public static final String VISA_PREFIX = "4";
    public static final String MASTERCARD_PREFIX = "51,52,53,54,55,";
    public static final String DISCOVER_PREFIX = "6011";
    public static final String AMEX_PREFIX = "34,37,";

//    public static int getCardType(String cardno) {
//        if (cardno.substring(0, 1).equals(VISA_PREFIX))
//            return VISA;
//        else if (MASTERCARD_PREFIX.contains(cardno.substring(0, 2) + ","))
//            return MASTERCARD;
//        else if (AMEX_PREFIX.contains(cardno.substring(0, 2) + ","))
//            return AMEX;
//        else if (cardno.substring(0, 4).equals(DISCOVER_PREFIX))
//            return DISCOVER;
//
//        return NONE;
//    }
}
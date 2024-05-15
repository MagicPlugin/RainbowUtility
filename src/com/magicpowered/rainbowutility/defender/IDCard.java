package com.magicpowered.rainbowutility.defender;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class IDCard {
    private static final int[] VERIFY_CODE_WEIGHT = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    public static boolean isValid(String idCard) {
        if (idCard.length() == 18) {
            return isValid18IDCard(idCard);
        }
        return false;
    }

    public static boolean isValid18IDCard(String idCard) {
        if (idCard.length() != 18) {
            return false;
        }

        String idCard17 = idCard.substring(0, 17);
        String idCard18Code = idCard.substring(17, 18);
        char[] c;
        String checkCode;
        if (isDigital(idCard17)) {
            c = idCard17.toCharArray();
        } else {
            return false;
        }

        if (null != c) {
            int[] bit;
            bit = converCharToInt(c);
            int sum17;
            sum17 = getPowerSum(bit);

            checkCode = getCheckCodeBySum(sum17);
            if (null == checkCode) {
                return false;
            }
            if (!idCard18Code.equalsIgnoreCase(checkCode)) {
                return false;
            }
        }

        String birthCode = idCard.substring(6, 14);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            if (!sdf.format(sdf.parse(birthCode)).equals(birthCode)) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private static boolean isDigital(String str) {
        return str.matches("^[0-9]*$");
    }

    private static int[] converCharToInt(char[] c) throws NumberFormatException {
        int[] a = new int[c.length];
        int k = 0;
        for (char temp : c) {
            a[k++] = Integer.parseInt(String.valueOf(temp));
        }
        return a;
    }

    private static int getPowerSum(int[] bit) {
        int sum = 0;
        if (VERIFY_CODE_WEIGHT.length != bit.length) {
            return sum;
        }
        for (int i = 0; i < bit.length; i++) {
            for (int j = 0; j < VERIFY_CODE_WEIGHT.length; j++) {
                if (i == j) {
                    sum = sum + bit[i] * VERIFY_CODE_WEIGHT[j];
                }
            }
        }
        return sum;
    }

    private static String getCheckCodeBySum(int sum17) {
        String checkCode = null;
        switch (sum17 % 11) {
            case 10:
                checkCode = "2";
                break;
            case 9:
                checkCode = "3";
                break;
            case 8:
                checkCode = "4";
                break;
            case 7:
                checkCode = "5";
                break;
            case 6:
                checkCode = "6";
                break;
            case 5:
                checkCode = "7";
                break;
            case 4:
                checkCode = "8";
                break;
            case 3:
                checkCode = "9";
                break;
            case 2:
                checkCode = "X";
                break;
            case 1:
                checkCode = "0";
                break;
            case 0:
                checkCode = "1";
                break;
        }
        return checkCode;
    }
}



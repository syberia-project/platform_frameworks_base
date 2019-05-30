/*
**
** Copyright 2019, Descendant
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package com.android.keyguard.clocks;

import com.android.internal.util.ArrayUtils;

import java.io.*;
import java.lang.String;

public class LangGuard {

    public static boolean isAvailable (String[] langExceptions, String langVal) {
        return (ArrayUtils.contains(langExceptions, langVal) ? true : false);
    }

    public static String evaluateEx (String lang, int units, String[] TensString, String[] UnitsString, int tens, boolean hours, int num) {
        String numString = "";
        switch (lang) {
            case "it":
                if (num < 20) {
                    numString = UnitsString[num];
                    return numString;
                }
                if (units == 1) {
                    numString = TensString[tens].substring(0, TensString[tens].length() - 1)+
                                UnitsString[units].toLowerCase();
                    return numString;
                } else if (units == 3) {
                    numString = TensString[tens] + "tré";
                    return numString; 
                } else {
                    numString = TensString[tens] + UnitsString[units].toLowerCase();
                    return numString;
                }

            case "pt":
                if (num < 20) {
                    numString = UnitsString[num];
                    if (!hours && num != 0) {
                        numString = "E "+ numString;
                        return numString;
                    } else if (!hours && num == 0) {
                        numString = "Hora";
                        return numString;
                    } else if (hours && num == 1) {
                        numString = "Uma";
                    }
                    return numString;
                } else {
                    numString = TensString[tens] + " e " + UnitsString[units].toLowerCase();
                    if (!hours) {
                        numString = "E "+ numString;
                        return numString;
                    }
                }
                return numString;

            case "ru":
                if (num < 20) {
                   if (!hours && num < 10 ) {
                       if (num == 0){
                           numString = "Ровно";
                       } else {
                           numString = "Ноль " + UnitsString[num].toLowerCase();
                       }
                   } else
                    numString = UnitsString[num];
                    return numString;
                }
                numString = TensString[tens] + " " + UnitsString[units].toLowerCase();
                return numString;

            case "fr":
                if (num < 20) {
                    numString = UnitsString[num];
                    return numString;
                }
                if (units == 1) {
                    numString = TensString[tens] + "et un";
                } else {
                    numString = TensString[tens] + UnitsString[units].toLowerCase();
                }
                return numString;

            case "ja":
                if (num < 20) {
                    numString = UnitsString[num];
                    return numString;
                }
                numString = TensString[tens] + UnitsString[units];
                return numString;

            // Completely broken
            case "nl":
                if(hours && num < 10) {
                    units = num;
                    tens = 0;
                }
                if (units == 1 || units == 6) {
                    numString = UnitsString[units].substring(0,3 ) + "en" + TensString[tens].toLowerCase();
                } else if (units == 7 || units == 9) {
                    numString = UnitsString[units].substring(0,5) + "en" + TensString[tens].toLowerCase();
                } else if (units == 2 || units == 3 || units == 4 || units == 5 || units == 8) {
                    numString = UnitsString[units].substring(0,4) + "en" + TensString[tens].toLowerCase();
                } else {
                    numString = UnitsString[units] + "en" + TensString[tens].toLowerCase();
                }
                if (hours && num < 10) {
                    numString = numString.substring(0, (numString.length() - 2));
                } else if(num < 20) {
                    numString = UnitsString[num];
                }
                return numString;

            case "tr":
                if (num < 20) {
                    numString = UnitsString[num];
                    return numString;
                }
                numString = TensString[tens] + UnitsString[units];
                return numString;
        }
        return numString;
    }
}

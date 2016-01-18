package com.rameses.rulemgmt.developer;

import java.util.*;

public class RuleDevUtil {

    public static List shiftPos(lst, pos) { 
        if(pos == 0) return lst;
        int last = lst.size() - 1;
        if(pos>last) return lst;
        if(pos==1 ) {
            return lst[1..1] + lst[0..0] + (( last > 1 ) ? lst[2..last] : []);
        }
        else {
            return lst[0..(pos-2)]+ lst[pos..pos] + [lst[pos-1]] + ((pos == last)?[]:lst[ (pos+1)..last]);         
        }
    }

}


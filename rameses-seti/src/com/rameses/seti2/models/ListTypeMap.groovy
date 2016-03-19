package com.rameses.seti2.models;

import java.util.*;

public class ListTypeMap extends HashMap {
    
    def handler;
    public ListTypeMap( def h ) {
        handler = h;
    }
    
    public def get( def k ) {
        return handler(k);
    }
    
}

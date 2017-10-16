/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.util; 

import java.util.Date;

/**
 *
 * @author Elmo Nazareno
 */
public interface HolidayProvider {
    boolean exists(Date date);
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mycompany;

/**
 *
 * @author LG
 */
public class KeyValues {
    String[] values;    
    int viewIndex;

    @Override
    public String toString() {
        return values[viewIndex];
    }
    
    

    public KeyValues(String[] values, int viewIndex) {
        this.values = values;
        this.viewIndex = viewIndex;
    }
    
    

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }


    public int getViewIndex() {
        return viewIndex;
    }

    public void setViewIndex(int viewIndex) {
        this.viewIndex = viewIndex;
    }
    
    
    
}

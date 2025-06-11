package com.prashant.dynamicformhandlerdemo;

// This is a modal for our demo project
// Why i am using demo?
//      to handle multiple input fields

//  this is to store info
public class FormField {
    public String key; // which value to save
    public int viewId; // which edittext it comes from

    public FormField(String key, int viewId) {
        this.key = key;
        this.viewId = viewId;
    }
}

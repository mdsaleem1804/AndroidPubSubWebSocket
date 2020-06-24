package com.amazonaws.demo.androidpubsubwebsocket;


import android.app.Application;

public class GlobalClass extends Application {

        private String someVariable;

        public String getSomeVariable() {
            return someVariable;
        }

        public void setSomeVariable(String someVariable) {
            this.someVariable = someVariable;
        }
    }
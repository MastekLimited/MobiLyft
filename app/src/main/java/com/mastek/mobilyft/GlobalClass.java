package com.mastek.mobilyft;

import android.app.Application;

/**
 * Created by jeet11857 on 09/12/2015.
 */
public class GlobalClass extends Application {
    private String baseServiceURL;


    public String getBaseServiceURL() {

        //return "http://172.16.217.176/RDServices/RDServices.svc";
        //return "http://pwm.mastek.com/RDServices/RDServices.svc";
        return "http://pwm.mastek.com/MobiLyftServices/MobiLyftServices.svc";
    }

    public void setBaseServiceURL() {

        baseServiceURL = "http://172.16.217.176/RDServices/RDServices.svc";

    }
}

package com.tumuyan.ncnn.realsr;

import android.app.Application;

public class RealSRApplication extends Application {

   @Override
   public void onCreate() {
      super.onCreate();
      WorkJobRepository.get().initApplication(this);
   }
}

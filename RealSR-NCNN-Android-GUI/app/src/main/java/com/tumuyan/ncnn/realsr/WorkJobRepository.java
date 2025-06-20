package com.tumuyan.ncnn.realsr;

import android.app.Application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class WorkJobRepository {

    public static final String CHILD_REALSR_DIR_NAME = "realsr";
    private File copyRootDir;
    private Application application = null;
    private static WorkJobRepository _instant = null;
    public static WorkJobRepository get(){
        if(_instant == null){
            _instant = new WorkJobRepository();
        }
        return _instant;
    }
    public void initApplication(Application ctx){
        if(application == null){
            application = ctx;
            createDirFile();
        }
    }

    private void createDirFile() {
        if(copyRootDir == null){
            copyRootDir = application.getCacheDir();
            if(!copyRootDir.exists()){
                try {
                    copyRootDir.mkdirs();
                } catch (Exception e) {
                   e.printStackTrace();
                }
            }
        }
    }

    public File getSrDirFile(){
        return new File(copyRootDir,CHILD_REALSR_DIR_NAME);
    }

    private WorkJobRepository() {}




}

package com.tumuyan.ncnn.realsr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.WorkerThread;

public class AssetsCopyer {

    private static final String TAG = "AssetsCopyer";

    public interface AssetsFileCopyListener{
        void onStart(int max);
        void onError(Exception e);
        void onProgress(int progress);
        void onEnd();
    }

    @WorkerThread
    public static void releaseAssetsToCache(Context context,String srcPath, File destDir,
                                            Boolean skipExistFile,
                                            AssetsFileCopyListener listener){
        List<String> allFiles = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try{
            collectAllFiles(assetManager,srcPath, allFiles);
            int index = 0;
            int totalFiles = allFiles.size();
            if(listener != null){
                listener.onStart(totalFiles);
            }
            for (String fullPath : allFiles) {
                File outFile = new File(destDir, fullPath);
                File parent = outFile.getParentFile();
                if (!parent.exists()) parent.mkdirs();
                if(!(outFile.exists() && skipExistFile)){
                    copyAssetFile(assetManager, fullPath, outFile);
                }
                if(listener != null){
                    listener.onProgress(++index);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            if(listener != null){
                listener.onError(e);
            }
        }
        if(listener != null){
            listener.onEnd();
        }
    }


    private static void collectAllFiles(AssetManager assetManager,String path, List<String> outList) throws IOException {
        String[] files = assetManager.list(path);
        if (files == null || files.length == 0) {
            outList.add(path.substring(path.lastIndexOf("/") + 1));
        } else {
            for (String name : files) {
                String fullPath = path.isEmpty() ? name : path + "/" + name;
                if (isAssetFile(assetManager,fullPath)) {
                    outList.add(fullPath);
                } else {
                    collectAllFiles(assetManager,fullPath, outList);
                }
            }
        }
    }

    private static boolean isAssetFile(AssetManager assetManager,String path) throws IOException {
        String[] list = assetManager.list(path);
        return list == null || list.length == 0;
    }

    private static void copyAssetFile(AssetManager assetManager,String assetPath, File outFile) throws IOException {

        InputStream in = assetManager.open(assetPath);
        OutputStream out = new FileOutputStream(outFile);
        try {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }finally {
            out.flush();
            out.close();
            in.close();
        }
    }

    public static void releaseAssets(Context context, String assetsDir,
                                     String releaseDir, Boolean skipExistFile) {

//		Log.d(TAG, "context: " + context + ", " + assetsDir);
        if (TextUtils.isEmpty(releaseDir)) {
            return;
        }

        releaseDir = releaseDir.replaceFirst("/+$","");

        if (TextUtils.isEmpty(assetsDir) || assetsDir.equals("/")) {
            assetsDir = "";
        } else {
            assetsDir = assetsDir.replaceFirst("/+$","");
        }

        AssetManager assets = context.getAssets();
        try {
            String[] fileNames = assets.list(assetsDir);//只能获取到文件(夹)名,所以还得判断是文件夹还是文件
            if (fileNames.length > 0) {// is dir
                for (String name : fileNames) {
                    if (!TextUtils.isEmpty(assetsDir)) {
                        name = assetsDir + File.separator + name;//补全assets资源路径
                    }
//                    Log.i(, brian name= + name);
                    String[] childNames = assets.list(name);//判断是文件还是文件夹
                    if (!TextUtils.isEmpty(name) && childNames.length > 0) {
                        checkFolderExists(releaseDir + File.separator + name);
                        releaseAssets(context, name, releaseDir, skipExistFile);//递归, 因为资源都是带着全路径,
                        //所以不需要在递归是设置目标文件夹的路径
                    } else {
                        InputStream is = assets.open(name);
//                        FileUtil.writeFile(releaseDir + File.separator + name, is);
                        writeFile(releaseDir + File.separator + name, is, skipExistFile);
                    }
                }
            } else {// is file
                InputStream is = assets.open(assetsDir);
                // 写入文件前, 需要提前级联创建好路径, 下面有代码贴出
//                FileUtil.writeFile(releaseDir + File.separator + assetsDir, is);
                writeFile(releaseDir + File.separator + assetsDir, is, skipExistFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean writeFile(String fileName, InputStream in, boolean skipExistFile) throws IOException {
        boolean bRet = true;
        try {

            File file = new File(fileName);
            if (file.exists()) {
                if (skipExistFile) {
                    Log.d(TAG, "skip file: " + fileName);
                    return bRet;
                }else{
                    file.delete();
                }
            } else if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            OutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[4112];
            int read;
            while ((read = in.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            in.close();
            in = null;
            os.flush();
            os.close();
            os = null;
            Log.d(TAG, "copyed file: " + fileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bRet = false;
        }
        return bRet;
    }

    private static void checkFolderExists(String path) {
        File file = new File(path);
        if ((file.exists() && !file.isDirectory()) || !file.exists()) {
            file.mkdirs();
        }
    }
}

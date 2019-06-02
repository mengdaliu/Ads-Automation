import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.AdImage;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ImageUploader {


    private File[] _imageFolder;
    private AdAccount _account;
    private File _output;
    private String _folderName;

    ImageUploader(String headerPath, String folderName, AdAccount account) {
        File folder = new File(headerPath + folderName);
        _folderName = folderName;
        _imageFolder = folder.listFiles();
        _account = account;
        _output = new File(folderName +".csv");
    }

    ImageUploader(AdAccount account) {
        _account = account;
    }

    public String Upload(File Image) {
        try {
            AdImage returnInfo = _account.createAdImage().addUploadFile(Image.getName(), Image).execute();
            String hash = returnInfo.getFieldHash();
            System.out.println(returnInfo);
            return hash;
        } catch (APIException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public HashMap<String, String> batchUpload() {
        try {
            OutputStream writer = new PrintStream(_output);
            HashMap<String, String> hashCodes = new HashMap<String, String>();
            for (File image : _imageFolder) {
                System.out.println("Uploading " + image.getName());
                String hashCode = Upload(image);
                if (hashCode.equals("Error")) {
                    System.out.println("Failed to upload " + image.getName() + ".");
                    System.out.println("Please check the stack trace above.");
                } else {
                    hashCodes.put(image.getName(), hashCode);
                    ((PrintStream) writer).println(image.getName() + "," + hashCode);
                }
                try {
                    TimeUnit.SECONDS.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            PrintStream done = new PrintStream(
                    new FileOutputStream("imageCache.out", true));
            done.println(_folderName);
            return hashCodes;
        }
        catch (FileNotFoundException  e){
            e.printStackTrace();
            return null;
        }
    }

}

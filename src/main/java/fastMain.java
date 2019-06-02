import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.AdAccount;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class fastMain {
    static HashMap<String, HashMap<String, String>> hashOFHash = new HashMap<String, HashMap<String, String>>();
    static String _directoryHeader = null;
    static String _videoHeader = null;
    static String _campaignName = null;
    private static HashMap<String, String> videoIDs = new HashMap<String, String>();
    private static HashMap<String, ArrayList> inputList = new HashMap() {{
        put("Messages", new ArrayList<String[]>());
        put("Images", new ArrayList<String>());
        put("Videos", new ArrayList<String>());
        put("Bids", new ArrayList<Integer>());
        put("AppID", new ArrayList<String>());
        put("PageID", new ArrayList<String>());
        put("Countries", new ArrayList<String>());
    }};
    public static AdAccount getAccount(){
        return _account;
    }
    private static HashMap<String, String> _codes = new HashMap<String, String>();
    static final APIContext context = new APIContext(
            "EAAIj5nzPn8QBAGZB6ZCTZCF5XJZB0JFxVD7V2ItKT4ajn4qJ9L8BT1loe7CYMJ3nKZA3pM9zAwso41QPaMMpw3FJjzxgkSiYvZBjZB7rGOPrItvMFfbHnZBXf7cGphr7WgJL8TifTShVThsZBpFEajPtZB2ivty33RqTdkxTwWvnoyTAZDZD",
            "9bb16b47ba38d031af5a5c25e6e2e714"
    );
    public static AdAccount _account = new AdAccount("249768539268280", context);
    public static void main (String[] args) {
        System.out.println(_account);
        readInput(new File("SpecFiles/maxCleaner.inp"));
        int msgNum = inputList.get("Messages").size();
        int imgNum = inputList.get("Images").size();
        int bidNum = inputList.get("Bids").size();
        int vidNum = inputList.get("Videos").size();
        if (vidNum == 0) {
            vidNum = 1;
        }
        int total = msgNum * imgNum * bidNum * vidNum;
        System.out.println("So we will create " + total + " campaigns in total. Press Y to confirm and continue or N to exit.");
        Scanner con = new Scanner(System.in);
        while (true) {
            String response = con.next();
            if (response.equals("Y")) {
                break;
            }
            if (response.equals("N")) {
                System.exit(0);
            }
            else {
                System.out.println("???");
            }
        }
        readCodes2(new File("videoCache.csv"), videoIDs);
        for (Object videoName : inputList.get("Videos")) {
            String vidID = null;
            if (!videoIDs.containsKey(videoName)) {
                System.out.println("Uploading " + videoName);
                try {vidID = _account.createAdVideo().addUploadFile((String) videoName, new File(_videoHeader + videoName)).execute().getId();}
                catch (APIException e) {
                    System.out.println("Failed to upload " + videoName);
                    e.printStackTrace();
                    System.exit(2);
                }
                videoIDs.put((String) videoName, vidID);
                try {PrintStream done = new PrintStream(
                        new FileOutputStream("videoCache.csv", true));
                done.println(videoName + "," + vidID);
                } catch (FileNotFoundException e) {
                    System.out.println("Where is videoCache.csv?");
                    System.exit(2);
                }
            }
        }
        readCodes(new File("codes.csv"),_codes);
        SimpleDateFormat formatter = new SimpleDateFormat("MMdd");
        Date date = new Date();
        String d = formatter.format(date);
        int i = 0;
        try {
            Scanner dateTrack = new Scanner(new File("dateTrack.out"));
            String y = dateTrack.next();
            if (y.equals(d)) {
                Scanner note = new Scanner(new File("numTrack.out"));
                i = note.nextInt();
            } else {
                PrintStream takeNote = new PrintStream(
                        new FileOutputStream("dateTrack.out", false));
                takeNote.println(d);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(2);
        }
        int m = 0;
        int grand = 0;
        String campaignID;
        for (String[] message : ((ArrayList<String[]>) inputList.get("Messages"))) {
            m += 1;
            String title = message[0];
            String text = message[1];
            int image = 0;
            for (Object imagePath : inputList.get("Images")) {
                image += 1;
                HashMap<String, String> images = null;
                Boolean uploaded = false;
                try {
                    Scanner checkCache = new Scanner(new File("imageCache.out"));
                    while (checkCache.hasNextLine()) {
                        String toCheck = checkCache.nextLine();
                        if (toCheck.equals(imagePath)) {
                            uploaded = true;
                            if (hashOFHash.containsKey(imagePath)) {
                                images = hashOFHash.get(imagePath);
                            } else {
                                images = readHash(new File(imagePath + ".csv"), (String) imagePath);
                                System.out.println(imagePath + " was previously uploaded. Now starting to directly create ads.");
                            }
                        }
                    }
                } catch (FileNotFoundException e) {e.printStackTrace(); System.exit(1);}
                if (!uploaded) {
                    System.out.println("Uploading the batch " + imagePath);
                    ImageUploader uploader = new ImageUploader(_directoryHeader, (String) imagePath, _account);
                    images = uploader.batchUpload();
                }
                int b = 0;
                for (Object bid : inputList.get("Bids")) {
                    b += 1;
                    int v = 0;
                    if (inputList.get("Videos").size()!= 0) {
                        for (Object video : inputList.get("Videos")) {
                            v += 1;
                            i += 1;
                            CampaignCreator campaignCreator = new CampaignCreator(_account);
                            campaignID = campaignCreator.CreateCampaign("Adtiger_" + _campaignName + "_geng_" + d + "_TuringBot_" + i);
                            System.out.println("Created Campaign " + "Adtiger_" + _campaignName + "_geng_" + d + "_TuringBot_" + i);
                            try {
                                PrintStream takeNote = new PrintStream(
                                        new FileOutputStream("numTrack.out", false));
                                takeNote.println(i);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            String vidID = videoIDs.get(video);
                                for (Map.Entry<String, String> entry : images.entrySet()) {
                                    try {
                                        String filename = entry.getKey();
                                        filename = filename.replaceAll(".jpg","");
                                        filename = filename.replaceAll(".png","");
                                        String device = _codes.get(filename);
                                        String msg = text.replaceAll("MModell", filename.toUpperCase());
                                        grand += 1;
                                        concurrentAdCreator ad = new concurrentAdCreator(
                                                _account,
                                                "G" + grand + "M" + m  + "I" + image + "B" + b + "V" + v + " " + filename,
                                                Long.parseLong((String) bid),
                                                campaignID,
                                                inputList.get("Countries"),
                                                ((String[]) inputList.get("AppID").get(0))[0],
                                                ((String[]) inputList.get("AppID").get(0))[1],
                                                device,
                                                title,
                                                entry.getValue(),
                                                msg,
                                                ((String[]) inputList.get("PageID").get(0))[0],
                                                true,
                                                vidID);
                                        ad.start();
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                        }
                    } else {
                        i += 1;
                        CampaignCreator campaignCreator = new CampaignCreator(_account);
                        campaignID = campaignCreator.CreateCampaign("Adtiger_" + _campaignName + "_geng_" + d + "_TuringBot_" + i);
                        System.out.println("Created Campaign " + "Adtiger_" + _campaignName + "_geng_" + d + "_TuringBot_" + i);
                        try {
                            PrintStream takeNote = new PrintStream(
                                    new FileOutputStream("numTrack.out", false));
                            takeNote.println(i);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        for (Map.Entry<String, String> entry : images.entrySet()) {
                            try {
                                String filename = entry.getKey();
                                filename = filename.replaceAll(".jpg","");
                                filename = filename.replaceAll(".png","");
                                String device = _codes.get(filename);
                                String msg = text.replaceAll("MModell", filename.toUpperCase());
                                grand += 1;
                                concurrentAdCreator ad = new concurrentAdCreator(
                                        _account,
                                        "G"+ grand + "M" + m  + "I" + image + "B" + b + "V" + v + " " + filename,
                                        Long.parseLong((String) bid),
                                        campaignID,
                                        inputList.get("Countries"),
                                        ((String[]) inputList.get("AppID").get(0))[0],
                                        ((String[]) inputList.get("AppID").get(0))[1],
                                        device,
                                        title,
                                        entry.getValue(),
                                        msg,
                                        ((String[]) inputList.get("PageID").get(0))[0],
                                        false,
                                        null);
                                ad.start();
                                try {
                                    TimeUnit.MILLISECONDS.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public static HashMap<String, String> readHash(File codeCSV, String name) {
        HashMap<String, String> result = new HashMap<String, String>();
        try {
            Scanner CSVReader = new Scanner(codeCSV);
            while (CSVReader.hasNext()){
                String aPair = CSVReader.nextLine();
                List<String> match = Arrays.asList(aPair.split(","));
                result.put(match.get(0), match.get(1));
            }
            hashOFHash.put(name, result);
            return result;
        } catch (FileNotFoundException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static void readCodes(File file, HashMap<String, String> code){
        try{
            Scanner CSVReader = new Scanner(file);
            while (CSVReader.hasNext()) {
                String aPair = CSVReader.nextLine();
                List<String> match = Arrays.asList(aPair.split(","));
                code.put(match.get(1), match.get(0));
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File " + file + " not found.");
        }
    }

    public static void readCodes2(File file, HashMap<String, String> code){
        try{
            Scanner CSVReader = new Scanner(file);
            while (CSVReader.hasNext()) {
                String aPair = CSVReader.nextLine();
                List<String> match = Arrays.asList(aPair.split(","));
                code.put(match.get(0), match.get(1));
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File " + file + " not found.");
        }
    }

    public static void readInput(File file) {
        try {

            Scanner specReader = new Scanner(file);
            String[] messageSet;
            while (specReader.hasNext()) {
                String spec = specReader.next();
                String input;
                assert(spec.charAt(spec.length() - 1) == ':') : "Input titles should end with colons. " +
                        "Title with erroneous format: " + spec;
                spec = spec.substring(0, spec.length() - 1);
                assert (inputList.containsKey(spec)) : spec + " is not a legal user input.";
                int i = 0;
                if (spec.equals("Messages")) {
                    specReader.nextLine();
                    while (specReader.hasNextLine()){
                        input = specReader.nextLine();
                        if (input.length() == 0) {
                            break;
                        } else {
                            i += 1;
                            assert(input.substring(0,2).equals(i + ".")) : "Input lines should start with numbers." +
                                    "Erroneous line: " + input;
                            inputList.get(spec).add(new String[2]);
                            messageSet =  (String[]) inputList.get(spec).get(i - 1);
                            messageSet[0] = input.substring(2);
                            input = specReader.nextLine();
                            assert (input.length() != 0) : "No input text body found for message #" + i;
                            messageSet[1] = input;
                        }
                    }
                    System.out.println("You gave " + i + " types of messages.");
                }
                i = 0;
                if (spec.equals("Bids")) {
                    specReader.nextLine();
                    while(specReader.hasNextLine()) {
                        input = specReader.nextLine();
                        if (input.length() == 0) {
                            break;
                        } else {
                            i += 1;
                            assert(input.substring(0, 2).equals(i + ".")) :  "Input lines should start with numbers." +
                                    "Erroneous line: " + input;
                            inputList.get(spec).add(input.substring(3));
                        }
                    }
                    System.out.println("You gave " + i + " types of " + spec + ".");
                }
                i = 0;
                if (spec.equals("Images") || spec.equals("Videos")) {
                    if (spec.equals("Images")) {
                        _directoryHeader = specReader.nextLine().substring(1);
                    } else {
                        _videoHeader = specReader.nextLine().substring(1);
                    }
                    while(specReader.hasNextLine()) {
                        input = specReader.nextLine();
                        if (input.length() == 0) {
                            break;
                        } else {
                            i += 1;
                            assert(input.substring(0, 2).equals(i + ".")) :  "Input lines should start with numbers." +
                                    "Erroneous line: " + input;
                            inputList.get(spec).add(input.substring(3));
                            System.out.println("You gave " + i + " types of " + spec + ".");
                        }
                    }
                }
                i = 0;
                if (spec.equals("Countries")) {
                    input = specReader.nextLine();
                    String[] Countries = input.substring(1).split(",");
                    for (String Country : Countries) {
                        inputList.get(spec).add(Country);
                        i += 1;
                    }
                    System.out.println("You gave " + i + " Countries.");
                }
                if(spec.equals("AppID") || spec.equals("PageID")) {
                    String appID = specReader.nextLine();
                    String storeURL = specReader.nextLine();
                    String[] appInfo = {appID.substring(1), storeURL};
                    inputList.get(spec).add(appInfo);
                    System.out.println(spec + ": " + appID.substring(1));
                }
                if(spec.equals("Title")) {
                    String title = specReader.nextLine();
                    _campaignName = title.substring(1);
                    System.out.println("The App name and Country name you gave is " + _campaignName);
                    System.out.println("Hello Adtiger_geng!");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file path you entered is invalid");
            System.exit(1);
        }
    }

}

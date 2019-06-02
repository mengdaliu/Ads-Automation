import com.facebook.ads.sdk.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

public class concurrentAdCreator extends Thread{

    String _name;
    Long _bidAmount;
    String _CampaignID;
    List<String> _Countries;
    String _AppID;
    String _storeurl;
    String _DeviceType;
    String _caption;
    String _ImageHash;
    String _message;
    String _PageID;
    AdAccount _account;
    Boolean _continue = true;
    String _videoID = null;
    Boolean _hasVideo = false;

    concurrentAdCreator(
            AdAccount account,
            String name,
            Long bidAmount,
            String CampaignID,
            List<String> Countries,
            String AppID,
            String storeurl,
            String DeviceType,
            String caption,
            String ImageHash,
            String message,
            String PageID,
            Boolean hasVideo,
            String videoID
    ) {
        _bidAmount = bidAmount;
        _CampaignID = CampaignID;
        _Countries = Countries;
        _AppID = AppID;
        _storeurl = storeurl;
        _DeviceType = DeviceType;
        _caption = caption;
        _ImageHash = ImageHash;
        _message = message;
        _PageID = PageID;
        _account = account;
        _name = name;
        if (hasVideo) {
            _hasVideo = true;
            _videoID = videoID;
        }
    }

    public void run() {
        System.out.println("Starting to create " + _name);
        String creativeID;
        if (_hasVideo) {
            creativeID = createAdCreativeVideo(_name, _caption, _ImageHash, _message, _PageID, _storeurl, _videoID);
        } else {
            creativeID = createAdCreative(_name, _caption, _ImageHash, _message, _PageID, _storeurl);
        }
        if (!_continue) {return;}
        String adSetID = createAdSet(_name, _bidAmount, _CampaignID, _Countries, _AppID, _storeurl, _DeviceType);
        if (!_continue) {return;}
        createAd(_name, adSetID, creativeID);
        System.out.println("finished creating " + _name);
    }



    public String createAdSet(
            String name,
            Long bidAmount,
            String CampaignID,
            List<String> Countries,
            String AppID,
            String url,
            String DeviceType

    ) {
        try {
            String json = "{\"application_id\": " + AppID + "," + "\"object_store_url\":" + "\"" + url + "\"" + "}";
            AdSet adSet = _account.createAdSet()
                    .setName(name)
                    .setOptimizationGoal(AdSet.EnumOptimizationGoal.VALUE_APP_INSTALLS)
                    .setBidStrategy(AdSet.EnumBidStrategy.VALUE_LOWEST_COST_WITH_BID_CAP)
                    .setBidAmount(bidAmount)
                    .setDailyBudget((long) 50000)
                    .setCampaignId(CampaignID)
                    .setTargeting(
                            new Targeting()
                                    .setFieldGeoLocations(
                                            new TargetingGeoLocation()
                                                    .setFieldCountries(Countries)
                                    )
                                    .setFieldUserDevice(Arrays.asList(DeviceType))
                                    .setFieldUserOs(Arrays.asList("Android"))
                                    .setFieldPublisherPlatforms(Arrays.asList("facebook"))
                    )
                    .setPromotedObject(json)
                    .setStatus(AdSet.EnumStatus.VALUE_ACTIVE)
                    .setBillingEvent(AdSet.EnumBillingEvent.VALUE_IMPRESSIONS)
                    .execute();
            return adSet.getFieldId();
        } catch (APIException e) {
            JsonElement error = e.getRawResponseAsJsonObject().get("error");
            JsonElement code = ((JsonObject) error).get("code");
            JsonElement subcode = null;
            try {subcode = ((JsonObject) error).get("error_subcode");} catch (NullPointerException E) {}
            if (code.toString().equals("4") ||
                    code.toString().equals("17")) {
                System.out.println("Adset for ad " + _name + " is encountering rate limit problem, " +
                        "now it starts to sleep for 30 seconds.");
                try {
                    Thread.currentThread().sleep(3000000);
                    return createAdSet(name, bidAmount, CampaignID, Countries, AppID, url, DeviceType);
                } catch (InterruptedException E) {
                    E.printStackTrace();
                    _continue = false;
                    return null;
                }
            } else if (code.toString().equals("100")
                        && subcode.toString().equals("1885097")){
                System.out.println("Adset " + _name + ": Device is invalid so adset creation failed.");
                _continue = false;
                return null;
            } else if (code.toString().equals("2")) {
                System.out.println("Adset " + _name + " is encountering an unknown error. Trying again in 10 seconds");
                try {
                    Thread.sleep(1000000);
                    return createAdSet(name, bidAmount, CampaignID, Countries, AppID, url, DeviceType);
                } catch (InterruptedException E) {E.printStackTrace(); _continue = false; return null;}
            }
            else {
                e.printStackTrace();
                _continue = false;
                return null;
            }
        }
    }

    public String createAdCreative(
            String name, String caption, String ImageHash, String message, String PageID, String storeURL
    ) {
        try {
            AdCreative Creative = _account.createAdCreative()
                    .setName(name)
                    .setTitle(caption)
                    .setObjectStorySpec(
                            new AdCreativeObjectStorySpec()
                                    .setFieldLinkData(
                                            new AdCreativeLinkData()
                                                    .setFieldCallToAction(
                                                            new AdCreativeLinkDataCallToAction()
                                                                    .setFieldType(AdCreativeLinkDataCallToAction.EnumType.VALUE_INSTALL_MOBILE_APP)
                                                    )
                                                    .setFieldImageHash(ImageHash)
                                                    .setFieldUseFlexibleImageAspectRatio(true)
                                                    .setFieldMessage(message)
                                                    .setFieldDescription(caption)
                                                    .setFieldName(caption)
                                                    .setFieldLink(storeURL)
                                    )
                                    .setFieldPageId(PageID)
                    )
                    .execute();
            return Creative.getFieldId();
        } catch (APIException e) {
            JsonElement error = e.getRawResponseAsJsonObject().get("error");
            JsonElement code = ((JsonObject) error).get("code");
            if (code.toString().equals("4") ||
                    code.toString().equals("17")) {
                System.out.println("Adcreative for ad " + _name + " is encountering rate limit problem, " +
                        "now it starts to sleep for 30 seconds.");
                try {
                    Thread.currentThread().sleep(3000000);
                    return createAdCreative(name, caption, ImageHash, message, PageID, storeURL);
                } catch (InterruptedException E) {
                    E.printStackTrace();
                    _continue = false;
                    return null;
                }
            } else if (code.toString().equals("2")) {
                System.out.println("AdCreative " + _name + " is encountering an unknown error. Trying again in 10 seconds");
                try {
                    Thread.sleep(1000000);
                    return createAdCreative(name, caption, ImageHash, message, PageID, storeURL);
                } catch (InterruptedException E) {E.printStackTrace(); _continue = false; return null;}
            } else {
                e.printStackTrace();
                _continue = false;
                return null;
            }
        }
    }

    public String createAdCreativeVideo( String name, String caption, String ImageHash, String message, String PageID, String storeURL, String VideoID
    ) {
        try {
            AdCreative Creative =  _account.createAdCreative()
                    .setName(name)
                    .setTitle(caption)
                    .setObjectStorySpec(
                            new AdCreativeObjectStorySpec()
                                    .setFieldPageId(PageID)
                                    .setFieldVideoData(
                                            new AdCreativeVideoData()
                                                    .setFieldCallToAction(
                                                            new AdCreativeLinkDataCallToAction()
                                                                    .setFieldType(AdCreativeLinkDataCallToAction.EnumType.VALUE_INSTALL_MOBILE_APP)
                                                                    .setFieldValue(
                                                                            new AdCreativeLinkDataCallToActionValue()
                                                                                    .setFieldLink(storeURL)
                                                                    )
                                                    )
                                                    .setFieldImageHash(ImageHash)
                                                    .setFieldVideoId(VideoID)
                                                    .setFieldMessage(message)
                                                    .setFieldTitle(caption)
                                                    .setFieldLinkDescription(caption)
                                    )
                    ).execute();
            return Creative.getFieldId();
        } catch (APIException e) {
            e.printStackTrace();
            JsonElement error = e.getRawResponseAsJsonObject().get("error");
            JsonElement code = ((JsonObject) error).get("code");
            JsonElement subcode = null;
            try {subcode = ((JsonObject) error).get("error_subcode");} catch (NullPointerException E) {}
            if (code.toString().equals("4") ||
                    code.toString().equals("17")) {
                System.out.println("Adcreative for ad " + _name + " is encountering rate limit problem, " +
                        "now it starts to sleep for 30 seconds.");
                try {
                    Thread.currentThread().sleep(3000000);
                    return createAdCreativeVideo(name, caption, ImageHash, message, PageID, storeURL, VideoID);
                } catch (InterruptedException E) {
                    E.printStackTrace();
                    _continue = false;
                    return null;
                }
            } else if (code.toString().equals("100") &&
                    subcode.toString().equals("1885252")) {
                System.out.println("The video AdCreative " + name + " uses hasn't done processing." +
                        " Please wait patiently.");
                return createAdCreativeVideo(name, caption, ImageHash, message, PageID, storeURL, VideoID);
            } else if (code.toString().equals("2")) {
                System.out.println("AdCreative " + _name + " is encountering an unknown error. Trying again in 10 seconds");
                try {
                    Thread.sleep(1000000);
                    return createAdCreativeVideo(name, caption, ImageHash, message, PageID, storeURL, VideoID);
                } catch (InterruptedException E) {E.printStackTrace(); _continue = false; return null;}
            } else {
                e.printStackTrace();
                _continue = false;
                return null;
            }
        }
    }


    public String createAd(
            String name,
            String AdsetID,
            String CreativeID
    ) {
        try {
            Ad ad = _account.createAd()
                    .setName(name)
                    .setAdsetId(AdsetID)
                    .setCreative(new AdCreative().setFieldId(CreativeID))
                    .setStatus(Ad.EnumStatus.VALUE_ACTIVE)
                    .execute();
            return ad.getFieldId();
        } catch (APIException e) {
            JsonElement error = e.getRawResponseAsJsonObject().get("error");
            JsonElement code = ((JsonObject) error).get("code");
            if (code.toString().equals("4") ||
                    code.toString().equals("17")) {
                System.out.println("Ad " + _name + " is encountering rate limit problem, " +
                        "now it starts to sleep for 30 seconds.");
                try {
                    Thread.currentThread().sleep(3000000);
                    return createAd(name, AdsetID, CreativeID);
                } catch (InterruptedException E) {
                    E.printStackTrace();
                    _continue = false;
                    return null;
                }
            } else if (code.toString().equals("2")) {
                System.out.println("Ad " + _name + " is encountering an unknown error. Trying again in 10 seconds");
                try {
                    Thread.sleep(1000000);
                    return createAd(name, AdsetID, CreativeID);
                } catch (InterruptedException E) {E.printStackTrace(); _continue = false; return null;}
            }
            else {
                e.printStackTrace();
                _continue = false;
                return null;
            }
        }
    }
}

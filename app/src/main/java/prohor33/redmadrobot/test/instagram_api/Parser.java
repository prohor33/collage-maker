package prohor33.redmadrobot.test.instagram_api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

import prohor33.redmadrobot.test.instagram_api.Storage.*;

/**
 * Created by prohor on 25/02/15.
 */
class Parser {

    public Parser() {}

    public UserInfo parseUserSearch(String strAnswer) throws JSONException {
        JSONObject jsonObj = (JSONObject) new JSONTokener(strAnswer).nextValue();
        JSONArray jsonArray = jsonObj.getJSONArray("data");
        return parseJSONObjectToUserInfo(jsonArray.getJSONObject(0));
    }

    public void parseUserMedia(List<ImageInfo> imageInfoList, String strAnswer) throws JSONException {
        // Clear follows list and load it from scratch.
        // Clear Images and set them again.
        imageInfoList.clear();

        JSONObject jsonObj = (JSONObject) new JSONTokener(strAnswer).nextValue();
        JSONArray jsonArray = jsonObj.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonMedia = jsonArray.getJSONObject(i);
            imageInfoList.add(parseJSONObjectToImageInfo(jsonMedia));
        }
    }

    private UserInfo parseJSONObjectToUserInfo(JSONObject jsonObject) throws JSONException {
        UserInfo userInfo = new Storage.UserInfo();

        userInfo.username = jsonObject.getString("username");
        userInfo.id = jsonObject.getString("id");
//        userInfo.profile_picture = jsonObject.getString("profile_picture");
//        userInfo.full_name = jsonObject.getString("full_name");

        return userInfo;
    }

    private ImageInfo parseJSONObjectToImageInfo(JSONObject jsonObject) throws JSONException {
        ImageInfo imageInfo = new Storage.ImageInfo();

        imageInfo.likes_count = jsonObject.getJSONObject("likes").getInt("count");

        JSONObject jsonImages = jsonObject.getJSONObject("images");

        if(!jsonImages.isNull("low_resolution")) {
            JSONObject jsonLowRes = jsonImages.getJSONObject("low_resolution");
            imageInfo.low_resolution.url = jsonLowRes.getString("url");
            imageInfo.low_resolution.width = jsonLowRes.getInt("width");
            imageInfo.low_resolution.height = jsonLowRes.getInt("height");
        }

        if(!jsonImages.isNull("thumbnail")) {
            JSONObject jsonThumbnail = jsonImages.getJSONObject("thumbnail");
            imageInfo.thumbnail.url = jsonThumbnail.getString("url");
            imageInfo.thumbnail.width = jsonThumbnail.getInt("width");
            imageInfo.thumbnail.height = jsonThumbnail.getInt("height");
        }

        if(!jsonImages.isNull("standard_resolution")) {
            JSONObject jsonStndRes = jsonImages.getJSONObject("standard_resolution");
            imageInfo.standard_resolution.url = jsonStndRes.getString("url");
            imageInfo.standard_resolution.width = jsonStndRes.getInt("width");
            imageInfo.standard_resolution.height = jsonStndRes.getInt("height");
        }

        return imageInfo;
    }

}
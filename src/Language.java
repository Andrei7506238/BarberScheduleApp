import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Language {
    static JSONObject fileContentJson;
    static boolean isInit = false;

    static void loadFromFile(){
        String langFile = "src/Language.json";
        try {
            String fileContentTxt = Files.readString(Paths.get(langFile));
            fileContentJson = new JSONObject(fileContentTxt);
        } catch (Exception e){
            e.printStackTrace();
        }

        isInit = true;
    }

    static String get(String keyword){

        if(!isInit)
            loadFromFile();

        try{
            return fileContentJson.getString(keyword);
        }
        catch (Exception e){
            return keyword;
        }
    }
}
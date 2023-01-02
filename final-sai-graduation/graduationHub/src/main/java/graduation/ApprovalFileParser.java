package graduation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class ApprovalFileParser {
    public ApprovalFileParser()
    {

    }
    public HashMap<String, String> getRules() {
        String path = "../graduationHub/src/main/resources/approval.ini";
        HashMap<String, String> result = new HashMap<>();
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] rule_name = data.split("-");
                result.put(rule_name[1], rule_name[0]);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return result;
    }
}

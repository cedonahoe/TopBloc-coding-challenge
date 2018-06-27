import java.io.*;
import java.util.ArrayList;

import org.apache.http.entity.ContentType;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;


// Created by Cullen for the deadline of 6/27/18 @ 5pm CST

public class TopBloc {

    private static final int TABLE_SIZE = 4;
    private static final int COLUMN_ZERO = 0;
    private static final int COLUMN_ONE = 1;
    private static final int COLUMN_TWO = 2;
    private static final String email = "cullend18@gmail.com";


    // Function to extract all of the ints from an xlsx file given a column and TABLE_SIZE
    // This could be overloaded to work with doubles or generics could be used to make this more portable (name would
    // be changed)

    private static void extractInt(ArrayList<Integer> list, int column, Sheet data) {
        for (int i = 0; i < TABLE_SIZE; i++) {
            XSSFRow row = (XSSFRow) data.getRow(i + 1);
            XSSFCell cell = row.getCell(column);
            int val = (int) cell.getNumericCellValue();
            list.add(val);

        }
    }

    // Function to extract all the Strings from an xlsx file given a column and TABLE_SIZE

    private static void extractString(ArrayList<String> list, int column, Sheet data) {
        for (int i = 0; i < TABLE_SIZE; i++) {
            XSSFRow row = (XSSFRow) data.getRow(i + 1);
            XSSFCell cell = row.getCell(column);
            String str = cell.getStringCellValue();
            list.add(str);
        }
    }

    private static void fillNumJSON(JSONArray jarray, int[] array) {
        for (int i = 0; i < TABLE_SIZE; i++) {
            jarray.add(array[i]);
        }
    }

    private static void fillWordJSON(JSONArray jarray, String[] array) {
        for (int i = 0; i < TABLE_SIZE; i ++) {
            jarray.add(array[i]);
        }
    }

    // command line arguments could be added if reusing this program is important. They would replace the hardcoded
    // constants from the start of the class

    public static void main(String[] args) throws IOException{

        ArrayList<Integer> numSetOne1 = new ArrayList<Integer>();
        ArrayList<Integer> numSetOne2 = new ArrayList<Integer>();
        ArrayList<Integer> numSetTwo1 = new ArrayList<Integer>();
        ArrayList<Integer> numSetTwo2 = new ArrayList<Integer>();
        ArrayList<String> wordSetOne1 = new ArrayList<String>();
        ArrayList<String> wordSetOne2 = new ArrayList<String>();

        try {
            Workbook firstBook = new XSSFWorkbook(new FileInputStream("Data1.xlsx"));
            Workbook secondBook = new XSSFWorkbook(new FileInputStream("Data2.xlsx"));

            Sheet dataFirst = firstBook.getSheetAt(0);
            Sheet dataSecond = secondBook.getSheetAt(0);

            extractInt(numSetOne1, COLUMN_ZERO, dataFirst);
            extractInt(numSetOne2, COLUMN_ZERO, dataSecond);
            extractInt(numSetTwo1, COLUMN_ONE, dataFirst);
            extractInt(numSetTwo2, COLUMN_ONE, dataSecond);
            extractString(wordSetOne1, COLUMN_TWO, dataFirst);
            extractString(wordSetOne2, COLUMN_TWO, dataSecond);

        }

        // If this program would be used many times I would implement a better exception handler but in this simple case
        // I just print the call stack

        catch (IOException e) {
            e.printStackTrace();
        }

        // These loops perform the operations asked on the data from the xlsx files

        int[] multVal = new int[TABLE_SIZE];
        for (int i = 0; i < multVal.length; i++) {
            multVal[i] = numSetOne1.get(i) * numSetOne2.get(i);
        }
        int[] divVal = new int[TABLE_SIZE];
        for (int i = 0; i < divVal.length; i++) {
            divVal[i] = numSetTwo1.get(i) / numSetTwo2.get(i);
        }
        String[] strcat = new String[TABLE_SIZE];
        for (int i = 0; i <strcat.length; i++) {
            strcat[i] = wordSetOne1.get(i) + " " + wordSetOne2.get(i);
        }

        JSONArray numberSetOne = new JSONArray();
        JSONArray numberSetTwo = new JSONArray();
        JSONArray wordSetOne = new JSONArray();

        fillNumJSON(numberSetOne, multVal);
        fillNumJSON(numberSetTwo, divVal);
        fillWordJSON(wordSetOne, strcat);

        JSONObject request = new JSONObject();
        request.put("id", email);

        /// trying to use java arrays instead of JSONArrays
        request.put("numberSetOne", numberSetOne);
        request.put("numberSetTwo", numberSetTwo);
        request.put("wordSetOne", wordSetOne);

        CloseableHttpClient client = HttpClientBuilder.create().build();

        String json = new String();
        json = request.toString();
        StringEntity params = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("http://34.239.125.159:5000/challenge");
        post.addHeader("content-type", "application/json");
        post.setEntity(params);

        HttpResponse listen = client.execute(post);
        String str = listen.getStatusLine().toString();

        // prints if the statusLine doesn't have code of 200 OK

        if (str.indexOf("200") != 9) {
            System.out.println("Post request returned an error");
        }

        // given the single POST request I didn't implement error handling for different status codes. If it failed then
        // you can just run the program again

    }


}
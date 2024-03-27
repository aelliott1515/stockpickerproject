/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.stockpickerproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 *
 * @author alexelliott
 */
public class PolygonStockUtil {

    public static Map<String,Object> getMapFromURL(String url) throws UnsupportedEncodingException, IOException {
        String[] command = { "curl", "-H", "Authorization:Bearer QlGEeuVRXMkpJ4iVqZWUeSooCoPJxpGK", url };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (int result = bis.read(); result != -1; result = bis.read()) {
            buf.write((byte) result);
        }
        String curJson = buf.toString("UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = mapper.readValue(curJson, Map.class);
        return map;
    }
    
    public static boolean shouldDelayPolygonCall(Map<String, Object> map) {
        String status = (String) map.get("status");
        if (status == null || status.equalsIgnoreCase("ERROR")) {
            String error = (String) map.get("error");
            if (error == null) {
                throw new RuntimeException("Error message from Polygon should not be null.");
            }
            if(error.matches(".*maximum requests per minute.*")) {
                return true;
            } else {
                throw new RuntimeException(error);
            }
        }
        return false;
    }
    
    public static void waitOneMinute(HttpSession session) {
        try {
            System.out.println("Waiting one minute");
            double waitingTimeDouble = System.currentTimeMillis();
            session.setAttribute("waitingTimeDouble", waitingTimeDouble);
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        session.setAttribute("waitingTimeDouble", -1.0);
    }
}

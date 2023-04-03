package com.example.backend.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static java.lang.Math.abs;

@RestController
public class LogController {
    private String baseUrl = "http://10.60.38.174:31016/_search";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "log", method = RequestMethod.GET)
    public ResponseEntity<String> getLogs(
            @RequestParam(required = false) String dataset,
            @RequestParam(required = false) String model,
            @RequestParam(required = false, defaultValue = "0") Long time_start,
            @RequestParam(required = false, defaultValue = "2147483647000") Long time_end)
            throws IOException {
                String url = baseUrl;

                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String start = sdf.format(new Date(Long.parseLong(String.valueOf(time_start * 1000))));
                String end = sdf.format(new Date(Long.parseLong(String.valueOf(time_end * 1000))));

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("query", new JSONObject());
                jsonObject.put("fields", new JSONArray());
                jsonObject.getJSONObject("query").put("range", new JSONObject());
                jsonObject.getJSONObject("query").getJSONObject("range").put("@timestamp", new JSONObject());
                jsonObject.getJSONObject("query").getJSONObject("range").getJSONObject("@timestamp").put("gte", start);
                jsonObject.getJSONObject("query").getJSONObject("range").getJSONObject("@timestamp").put("lt", end);
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("field", "@timestamp");
                jsonObject.getJSONArray("fields").add(jsonObject1);

                String jsonData = jsonObject.toString();
//        logger.info(jsonData);

                try {
                    HttpClient httpClient = new HttpClient();
                    httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
            PostMethod postMethod = new PostMethod(url);
            postMethod.addRequestHeader("Content-type", "application/json; charset=utf-8");
            byte[] requestBytes = jsonData.getBytes("utf-8");
            InputStream inputStream = new ByteArrayInputStream(requestBytes, 0, requestBytes.length);

            RequestEntity requestEntity = new InputStreamRequestEntity(inputStream, requestBytes.length, "application/json; charset=utf-8");
            postMethod.setRequestEntity(requestEntity);
            int i = httpClient.executeMethod(postMethod);

            byte[] responseBody = postMethod.getResponseBody();
            String s = new String(responseBody);

            JSONObject response = JSONObject.parseObject(s);
//            System.out.println(response);

            JSONObject result = new JSONObject();
            long tot = 0;
            int num = 0;
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            result.put("logging", new JSONArray());

            JSONArray infoArray = response.getJSONObject("hits").getJSONArray("hits");
            for (int j = 0; j < infoArray.size(); j++) {
                JSONObject source = infoArray.getJSONObject(j).getJSONObject("_source");
                JSONObject tem = new JSONObject();

                String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                String date = source.getString("@timestamp");
                String message = source.getString("message");

                tem.put("time", simpleDateFormat.parse(date));
                tem.put("content", message);

                String message_hash = message + dataset + model;
                int score = abs(message_hash.hashCode()) % 100 + 1;
                tot += score;
                num++;
                arrayList.add(score);
                tem.put("score", score);

                result.getJSONArray("logging").add(tem);
            }

            Double probability;
            Double threshold;
            if (num > 0) {
                Double mean = Double.valueOf(tot) / Double.valueOf(num);
                Double sum_sqare_diff = 0.0;
                for (int sco : arrayList) {
                    sum_sqare_diff += Double.valueOf((sco - mean) * (sco - mean));
                }
                Double sta = Math.sqrt(sum_sqare_diff / num);
                threshold = mean + 1 * sta;

                Double count = 0.0;
                for (int sco : arrayList) {
                    if (sco > threshold) count++;
                }
                probability = count / num;
            } else {
                probability = 0.0;
                threshold = 0.0;
            }

            result.put("probability", probability);
            result.put("threshold", threshold);
            return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT);
        }
    }

    @RequestMapping(value = "log/node", method = RequestMethod.GET)
    public ResponseEntity<String> getLogsByNode(
            @RequestParam(required = false, defaultValue = "0") Long time_start,
            @RequestParam(required = false, defaultValue = "2147483647000") Long time_end,
            @RequestParam(required = false) String entity_id)
            throws IOException {
        String url = baseUrl;

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String start = sdf.format(new Date(Long.parseLong(String.valueOf(time_start * 1000))));
        String end = sdf.format(new Date(Long.parseLong(String.valueOf(time_end * 1000))));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", new JSONObject());
        jsonObject.put("fields", new JSONArray());
        jsonObject.getJSONObject("query").put("range", new JSONObject());
        jsonObject.getJSONObject("query").getJSONObject("range").put("@timestamp", new JSONObject());
        jsonObject.getJSONObject("query").getJSONObject("range").getJSONObject("@timestamp").put("gte", start);
        jsonObject.getJSONObject("query").getJSONObject("range").getJSONObject("@timestamp").put("lt", end);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("field", "@timestamp");
        jsonObject.getJSONArray("fields").add(jsonObject1);

        String jsonData = jsonObject.toString();
//        logger.info(jsonData);

        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
            PostMethod postMethod = new PostMethod(url);
            postMethod.addRequestHeader("Content-type", "application/json; charset=utf-8");
            byte[] requestBytes = jsonData.getBytes("utf-8");
            InputStream inputStream = new ByteArrayInputStream(requestBytes, 0, requestBytes.length);

            RequestEntity requestEntity = new InputStreamRequestEntity(inputStream, requestBytes.length, "application/json; charset=utf-8");
            postMethod.setRequestEntity(requestEntity);
            int i = httpClient.executeMethod(postMethod);

            byte[] responseBody = postMethod.getResponseBody();
            String s = new String(responseBody);

            JSONObject response = JSONObject.parseObject(s);
//            System.out.println(response);

            JSONArray result = new JSONArray();
            JSONArray infoArray = response.getJSONObject("hits").getJSONArray("hits");
            for (int j = 0; j < infoArray.size(); j++) {
                JSONObject host = infoArray.getJSONObject(j).getJSONObject("_source").getJSONObject("host");
                if (host == null && entity_id != null) continue;
                String id = host.getString("name");

                if (entity_id != null && !id.equals(entity_id)) continue;
                JSONObject source = infoArray.getJSONObject(j).getJSONObject("_source");
                JSONObject tem = new JSONObject();

                String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                String date = source.getString("@timestamp");
                String message = source.getString("message");

                tem.put("time", simpleDateFormat.parse(date));
                tem.put("content", message);

                result.add(tem);
            }

            return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT);
        }
    }
}

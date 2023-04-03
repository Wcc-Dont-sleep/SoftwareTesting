package com.example.backend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class MetricController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String baseUrl = "http://10.60.38.174:31013/api/v1/query_range?query=";

    @RequestMapping(value = "timeseries", method = RequestMethod.GET)
    public ResponseEntity<String> getMetrics(
            @RequestParam(required = false) String dataset,
            @RequestParam(required = false) String model,
            @RequestParam(required = false, defaultValue = "0") Long time_start,
            @RequestParam(required = false, defaultValue = "999999999") Long time_end)
            throws IOException
    {
        ArrayList<String>urls = new ArrayList<>();

        urls.add(baseUrl + "kube_node_info" +"&start=" + time_start.toString() + "&end=" + time_end.toString() + "&step=300s");
        urls.add(baseUrl + "kube_deployment_labels" +"&start=" + time_start.toString() + "&end=" + time_end.toString() + "&step=300s");
        urls.add(baseUrl + "kube_pod_info" +"&start=" + time_start.toString() + "&end=" + time_end.toString() + "&step=300s");
        urls.add(baseUrl + "kube_service_info" +"&start=" + time_start.toString() + "&end=" + time_end.toString() + "&step=300s");

        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);

            JSONObject result = new JSONObject();
            result.put("series", new JSONArray());
            long tot = 0;
            int num = 0;
            ArrayList<Integer> arrayList = new ArrayList<Integer>();

            for (String url : urls) {
                GetMethod getMethod = new GetMethod(url);
                getMethod.addRequestHeader("Content-Type", "application/json");
                logger.info(url);
                httpClient.executeMethod(getMethod);
                String response = getMethod.getResponseBodyAsString();
                getMethod.releaseConnection();

                JSONObject object = JSON.parseObject(response);
                JSONObject data = JSON.parseObject(object.getString("data"));
                JSONArray data_result = JSON.parseArray(data.getString("result"));
                logger.info(data_result.toString());

                for (int i = 0; i < data_result.size(); i++) {
                    JSONObject value = data_result.getJSONObject(i);
                    JSONArray arr = value.getJSONArray("values");

                    for (int j = 0; j < arr.size(); j++) {
                        JSONArray subArr = arr.getJSONArray(j);
                        JSONObject tem = new JSONObject();
                        tem.put("time", subArr.getString(0));
                        long v = (subArr.getString(1) + model + dataset).hashCode();
                        tem.put("value", Long.valueOf(subArr.getString(1)));
                        tem.put("score", v % 101);

                        result.getJSONArray("series").add(tem);

                        tot += v % 101;
                        num++;
                        arrayList.add((int) (v % 101));
                    }
                }
            }
            Double probability, threshold;
            if (num > 0) {
                Double mean = Double.valueOf(tot) / Double.valueOf(num);
                Double sum_sqare_diff = 0.0;
                for (int sco : arrayList) {
                    sum_sqare_diff += Double.valueOf((sco - mean) * (sco - mean));
                }
                Double sta = Math.sqrt(sum_sqare_diff / Double.valueOf(num));
                threshold = mean + 1 * sta;

                Double count = 0.0;
                for (int sco : arrayList) {
                    if (sco > threshold) count++;
                }
                probability = count / Double.valueOf(num);
                logger.info(mean.toString());
                logger.info(threshold.toString());
            } else {
                probability = 0.0;
            }

            result.put("probability", probability);

            return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
        }catch (Exception e) {
            logger.info(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "timeseries/node", method = RequestMethod.GET)
    public ResponseEntity<String> getMetricsByNode(
            @RequestParam(required = false, defaultValue = "0") Long time_start,
            @RequestParam(required = false, defaultValue = "999999999") Long time_end,
            @RequestParam(required = false) String entity_id)
            throws IOException
    {
        ArrayList<String>urls = new ArrayList<>();

        urls.add(baseUrl + "kube_node_info" +"&start=" + time_start.toString() + "&end=" + time_end.toString() + "&step=300s");
        urls.add(baseUrl + "kube_deployment_labels" +"&start=" + time_start.toString() + "&end=" + time_end.toString() + "&step=300s");
        urls.add(baseUrl + "kube_pod_info" +"&start=" + time_start.toString() + "&end=" + time_end.toString() + "&step=300s");
        urls.add(baseUrl + "kube_service_info" +"&start=" + time_start.toString() + "&end=" + time_end.toString() + "&step=300s");

        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);

            JSONArray result = new JSONArray();
            ArrayList<Integer> arrayList = new ArrayList<Integer>();

            for (int urlNum = 0; urlNum < urls.size(); urlNum++) {
                String url = urls.get(urlNum);
                GetMethod getMethod = new GetMethod(url);
                getMethod.addRequestHeader("Content-Type", "application/json");
                logger.info(url);
                httpClient.executeMethod(getMethod);
                String response = getMethod.getResponseBodyAsString();
                getMethod.releaseConnection();

                JSONObject object = JSON.parseObject(response);
                JSONObject data = JSON.parseObject(object.getString("data"));
                JSONArray data_result = JSON.parseArray(data.getString("result"));
                logger.info(data_result.toString());

                for (int i = 0; i < data_result.size(); i++) {
                    JSONObject value = data_result.getJSONObject(i);
                    if (entity_id != null) {
                        JSONObject t = value.getJSONObject("metric");
                        String entity;
                        switch(urlNum) {
                            case 0:
                                entity = "node";
                                break;
                            case 1:
                                entity = "deployment";
                                break;
                            case 2:
                                entity = "pod";
                                break;
                            default:
                                entity = "service";
                                break;
                        }

                        String id = t.getString(entity);
                        if (!entity_id.equals(id)) continue;
                    }

                    JSONArray arr = value.getJSONArray("values");

                    for (int j = 0; j < arr.size(); j++) {
                        JSONArray subArr = arr.getJSONArray(j);
                        JSONObject tem = new JSONObject();
                        tem.put("time", subArr.getString(0));
                        long v = Long.valueOf(subArr.getString(1));
                        tem.put("value", v);

                        result.add(tem);
                    }
                }
            }
            return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
        }catch (Exception e) {
            logger.info(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

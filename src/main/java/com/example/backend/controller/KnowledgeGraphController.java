package com.example.backend.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.backend.dto.EdgeDto;
import com.example.backend.dto.GraphDto;
import com.example.backend.dto.NodeDto;
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

import java.util.ArrayList;

@RestController
public class KnowledgeGraphController {
    private String baseUrl = "http://10.60.38.174:31013/api/v1/query_range?query=";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "graph", method = RequestMethod.GET)
    public ResponseEntity<String> getKnoeledgeGraph(
            @RequestParam(required = false, defaultValue = "0") String time_start,
            @RequestParam(required = false, defaultValue = "999999999") String time_end
    )
    {
        ArrayList<String> urls = new ArrayList<>();
        urls.add(baseUrl + "kube_node_info" +"&start=" + time_start + "&end=" + time_end + "&step=300s");
        urls.add(baseUrl + "kube_deployment_labels" +"&start=" + time_start + "&end=" + time_end + "&step=300s");
        urls.add(baseUrl + "kube_pod_info" +"&start=" + time_start + "&end=" + time_end + "&step=300s");
        urls.add(baseUrl + "kube_service_info" +"&start=" + time_start + "&end=" + time_end + "&step=300s");

        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);

            GraphDto result = new GraphDto();

            for (int i = 0; i < urls.size(); i++) {
                String url = urls.get(i);
                GetMethod getMethod = new GetMethod(url);
                getMethod.addRequestHeader("Content-Type", "application/json");
                httpClient.executeMethod(getMethod);
                String response = getMethod.getResponseBodyAsString();
                getMethod.releaseConnection();

                JSONObject object = JSON.parseObject(response);
                JSONObject data = JSON.parseObject(object.getString("data"));
                JSONArray data_result = JSON.parseArray(data.getString("result"));
                System.out.println(data_result);

                for(int j = 0; j < data_result.size(); j++) {
                    JSONObject metric = data_result.getJSONObject(j).getJSONObject("metric");
//                    System.out.println(metric);
                    NodeDto node = new NodeDto();
                    switch (i) {
                        case 0:
                            node.setLabel("node");
                            node.setId(metric.getString("node"));
                            node.setName(metric.getString("__name__"));
                            node.setProperty(metric.toString());
                            break;
                        case 1:
                            node.setLabel("deployment");
                            node.setId(metric.getString("deployment"));
                            node.setNamespace(metric.getString("namespace"));
                            node.setName(metric.getString("__name__"));
                            node.setProperty(metric.toString());
                            break;
                        case 2:
                            node.setLabel("pod");
                            node.setId(metric.getString("pod"));
                            node.setPod_node(metric.getString("node"));
                            node.setNamespace(metric.getString("namespace"));
                            node.setName(metric.getString("__name__"));
                            node.setProperty(metric.toString());
                            break;
                        case 3:
                            node.setLabel("service");
                            node.setId(metric.getString("service"));
                            node.setNamespace(metric.getString("namespace"));
                            node.setName(metric.getString("__name__"));
                            node.setProperty(metric.toString());
                            break;
                    }
                    result.getNodes().add(node);
                }
            }

            for (int i = 0; i < result.getNodes().size(); i++) {
                NodeDto node = result.getNodes().get(i);
                for (int j = i + 1; j < result.getNodes().size(); j++) {
                    NodeDto tem = result.getNodes().get(j);
                    if (node.getLabel().equals("node") && tem.getLabel().equals("pod")) {
                        if (node.getId().equals(tem.getPod_node())) {
                            EdgeDto edge = new EdgeDto();
                            edge.setFrom_id(node.getId());
                            edge.setTo_id(tem.getId());
                            edge.setValue("node-pod");
                            result.getEdges().add(edge);
                        }
                    }

                    if (node.getLabel().equals("pod") && tem.getLabel().equals("node")) {
                        if (node.getPod_node().equals(tem.getId())) {
                            EdgeDto edge = new EdgeDto();
                            edge.setFrom_id(node.getId());
                            edge.setTo_id(tem.getId());
                            edge.setValue("node-pod");
                            result.getEdges().add(edge);
                        }
                    }

                    if (node.getLabel().equals("pod") && tem.getLabel().equals("service")) {
                        if (node.getNamespace().equals(tem.getNamespace())) {
                            EdgeDto edge = new EdgeDto();
                            edge.setFrom_id(node.getId());
                            edge.setTo_id(tem.getId());
                            edge.setValue("pod-service");
                            result.getEdges().add(edge);
                        }
                    }

                    if (node.getLabel().equals("service") && tem.getLabel().equals("pod")) {
                        if (node.getNamespace().equals(tem.getNamespace())) {
                            EdgeDto edge = new EdgeDto();
                            edge.setFrom_id(node.getId());
                            edge.setTo_id(tem.getId());
                            edge.setValue("pod-service");
                            result.getEdges().add(edge);
                        }
                    }

                    if (node.getLabel().equals("service") && tem.getLabel().equals("deployment")) {
                        if (node.getNamespace().equals(tem.getNamespace())) {
                            EdgeDto edge = new EdgeDto();
                            edge.setFrom_id(node.getId());
                            edge.setTo_id(tem.getId());
                            edge.setValue("service-deployment");
                            result.getEdges().add(edge);
                        }
                    }

                    if (node.getLabel().equals("deployment") && tem.getLabel().equals("service")) {
                        if (node.getNamespace().equals(tem.getNamespace())) {
                            EdgeDto edge = new EdgeDto();
                            edge.setFrom_id(node.getId());
                            edge.setTo_id(tem.getId());
                            edge.setValue("service-deployment");
                            result.getEdges().add(edge);
                        }
                    }
                }
            }
            System.out.println(result.getNodes().size());
            for (int i = 0; i < result.getNodes().size(); i++) {
                NodeDto n = result.getNodes().get(i);
                System.out.println(n.getLabel() + "\t" + n.getId() + "\t" + n.getPod_node() + "\t" + n.getNamespace());
            }
            return new ResponseEntity<>(JSON.toJSONString(result), HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

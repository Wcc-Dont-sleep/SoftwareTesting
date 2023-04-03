package com.example.backend.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class NodeDto {
    String id;
    String label;
    String name;
    String property;
    @JsonIgnore
    String pod_node;
    @JsonIgnore
    String namespace;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProperty() { return property; }
    public void setProperty(String property) { this.property = property; }

    public String getPod_node() { return pod_node; }
    public void setPod_node(String pod_node) { this.pod_node = pod_node; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
}

package com.example.backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GraphDto {
    List<NodeDto> nodes = new ArrayList<>();
    List<EdgeDto> edges = new ArrayList<>();
}

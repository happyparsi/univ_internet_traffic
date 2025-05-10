package simulation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RouterGraphManager {
    private final Map<String, RouterNode> nodes;
    private final List<Edge> edges;

    public RouterGraphManager() {
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
    }

    public void addRouter(String id, String type, int priority) {
        nodes.put(id, new RouterNode(id, type, priority));
    }

    public void addConnection(String sourceId, String targetId, int latency, int congestion) {
        RouterNode source = nodes.get(sourceId);
        RouterNode target = nodes.get(targetId);
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source or Target router does not exist.");
        }
        edges.add(new Edge(source, target, latency, congestion));
    }

    public void removeNode(String nodeId) {
        nodes.remove(nodeId);
        edges.removeIf(edge -> edge.getSource().getId().equals(nodeId) || edge.getTarget().getId().equals(nodeId));
    }

    public void removeEdge(String sourceId, String targetId) {
        edges.removeIf(edge -> edge.getSource().getId().equals(sourceId) && edge.getTarget().getId().equals(targetId));
    }

    public Map<String, RouterNode> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges(String nodeId) {
        return edges.stream()
                .filter(edge -> edge.getSource().getId().equals(nodeId))
                .collect(Collectors.toList());
    }

    public List<Edge> getAllEdges() {
        return new ArrayList<>(edges);
    }

    public void exportToJSON(String path) {
        List<Map<String, Object>> elements = new ArrayList<>();

        for (RouterNode node : nodes.values()) {
            Map<String, Object> nodeData = new HashMap<>();
            nodeData.put("id", node.getId());
            nodeData.put("label", node.getId());
            nodeData.put("type", node.getType());
            nodeData.put("priority", node.getPriority());
            elements.add(Map.of("data", nodeData));
        }

        for (Edge edge : edges) {
            Map<String, Object> edgeData = new HashMap<>();
            edgeData.put("source", edge.getSource().getId());
            edgeData.put("target", edge.getTarget().getId());
            edgeData.put("latency", edge.getLatency());
            edgeData.put("congestion", edge.getCongestion());
            elements.add(Map.of("data", edgeData));
        }

        Map<String, List<Map<String, Object>>> graphData = Map.of("elements", elements);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(graphData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
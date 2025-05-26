package simulation;

public class RouterNode {
    private final String id;
    private final String type;
    private final int priority;
    private final String ipAddress;

    public RouterNode(String id, String type, int priority) {
        this.id = id;
        this.type = type;
        this.priority = priority;
        this.ipAddress = assignRouterIp(id);
    }

    private String assignRouterIp(String id) {
        if (id.equals("Internet")) {
            return "10.0.0.254"; // Assign a specific IP for Internet gateway
        }
        if (id.startsWith("R")) {
            try {
                int baseIp = Integer.parseInt(id.replace("R", "")) % 255;
                return "10.0.0." + (baseIp == 0 ? 1 : baseIp);
            } catch (NumberFormatException e) {
                // Fallback for IDs like "Rabc"
                return "10.0.0.100"; // Default IP for invalid numeric IDs
            }
        }
        // Default IP for other non-standard IDs
        return "10.0.0.100";
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
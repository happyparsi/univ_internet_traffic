package simulation;

public class UserRequest {
    private final RouterNode sourceRouter;
    private final String userType;
    private final String domain;
    private final String ipAddress;

    public UserRequest(RouterNode sourceRouter, String userType, String domain) {
        this.sourceRouter = sourceRouter;
        this.userType = userType;
        this.domain = domain;
        this.ipAddress = assignIpAddress(userType);
    }

    private String assignIpAddress(String userType) {
        if (userType.equals("Student")) {
            return "12.0.0." + (new java.util.Random().nextInt(254) + 1); // 12.0.0.1 to 12.0.0.254
        } else if (userType.equals("Staff")) {
            return "11.0.0." + (new java.util.Random().nextInt(254) + 1); // 11.0.0.1 to 11.0.0.254
        } else {
            return "0.0.0.0"; // Unregistered user
        }
    }

    public RouterNode getSourceRouter() {
        return sourceRouter;
    }

    public String getUserType() {
        return userType;
    }

    public String getDomain() {
        return domain;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
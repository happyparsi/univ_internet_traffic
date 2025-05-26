package simulation;

import java.util.HashSet;
import java.util.Set;

public class ProxySimulator {
    private final int maxCapacity;
    private int currentLoad;
    private final Set<String> cache;

    public ProxySimulator(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.currentLoad = 0;
        this.cache = new HashSet<>();
        this.cache.add("intranet.edu");
    }

    public boolean handleRequest(UserRequest request) {
        // Check for unregistered user
        if (request.getIpAddress().equals("0.0.0.0")) {
            System.out.println("[Proxy] User not registered: " + request.getUserType());
            return false;
        }

        String domain = request.getDomain();
        String userType = request.getUserType();
        String routerType = request.getSourceRouter().getType();

        // Logic for students in Classroom or Lab
        if (userType.equals("Student") && (routerType.equals("Classroom") || routerType.equals("Lab"))) {
            if (domain.contains(".edu")) {
                System.out.println("[Proxy] Cache HIT for: " + domain + " (Student in " + routerType + ", IP: " + request.getIpAddress() + ")");
                return true;
            } else {
                System.out.println("[Proxy] Cache MISS for: " + domain + " (Student in " + routerType + ", IP: " + request.getIpAddress() + ")");
                if (currentLoad >= maxCapacity) {
                    System.out.println("[Proxy] Proxy overloaded! Rerouting required. (IP: " + request.getIpAddress() + ")");
                    return false;
                }
                cache.add(domain);
                currentLoad++;
                return true;
            }
        }

        // Logic for staff in Classroom or Lab
        if (userType.equals("Staff") && (routerType.equals("Classroom") || routerType.equals("Lab"))) {
            if (!domain.contains(".edu")) {
                System.out.println("[Proxy] Cache MISS for: " + domain + " (Staff in " + routerType + ", IP: " + request.getIpAddress() + ")");
                if (currentLoad >= maxCapacity) {
                    System.out.println("[Proxy] Proxy overloaded! Rerouting required. (IP: " + request.getIpAddress() + ")");
                    return false;
                }
                cache.add(domain);
                currentLoad++;
                return true;
            }
        }

        // General cache logic for other cases
        if (cache.contains(domain)) {
            System.out.println("[Proxy] Cache HIT for: " + domain + " (IP: " + request.getIpAddress() + ")");
            return true;
        }

        if (currentLoad >= maxCapacity) {
            System.out.println("[Proxy] Proxy overloaded! Rerouting required. (IP: " + request.getIpAddress() + ")");
            return false;
        }

        cache.add(domain);
        currentLoad++;
        System.out.println("[Proxy] Cache MISS for: " + domain + " (IP: " + request.getIpAddress() + ")");
        return true;
    }

    public void decayLoad() {
        if (currentLoad > 0) {
            currentLoad--;
            System.out.println("[Proxy] Load decayed. Current load: " + currentLoad);
        }
    }
}
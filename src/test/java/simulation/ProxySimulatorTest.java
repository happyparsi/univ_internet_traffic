package simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

public class ProxySimulatorTest {
    private ProxySimulator proxy;
    private RouterNode classroomRouter;
    private RouterNode labRouter;
    private RouterNode adminRouter;

    @BeforeEach
    void setUp() {
        proxy = new ProxySimulator(2, 3); // maxCapacity=2, cacheCapacity=3
        classroomRouter = new RouterNode("R1", "Classroom", 1);
        labRouter = new RouterNode("R2", "Lab", 1);
        adminRouter = new RouterNode("R3", "Admin", 2);
    }

    @Test
    void testStudentInClassroomIntranetEduCacheHit() {
        UserRequest request = new UserRequest(classroomRouter, "Student", "intranet.edu");
        boolean result = proxy.handleRequest(request);
        assertTrue(result, "Student in Classroom accessing intranet.edu should be a cache hit");
        assertEquals(0, proxy.getCache().size() - 1, "No additional load for special case"); // intranet.edu is pre-cached
        assertTrue(proxy.getCache().contains("intranet.edu"), "intranet.edu should be in cache");
    }

    @Test
    void testStudentInLabIntranetEduCacheHit() {
        UserRequest request = new UserRequest(labRouter, "Student", "intranet.edu");
        boolean result = proxy.handleRequest(request);
        assertTrue(result, "Student in Lab accessing intranet.edu should be a cache hit");
        assertEquals(0, proxy.getCache().size() - 1, "No additional load for special case");
        assertTrue(proxy.getCache().contains("intranet.edu"), "intranet.edu should be in cache");
    }

    @Test
    void testCacheMissAndLoadIncrease() {
        UserRequest request = new UserRequest(adminRouter, "Staff", "example.com");
        boolean result = proxy.handleRequest(request);
        assertTrue(result, "Should handle request since load is below capacity");
        assertTrue(proxy.getCache().contains("example.com"), "example.com should be added to cache");
        assertEquals(2, proxy.getCache().size(), "Cache should contain intranet.edu and example.com");
    }

    @Test
    void testCacheEviction() {
        // Fill cache (capacity=3, already has intranet.edu)
        proxy.handleRequest(new UserRequest(adminRouter, "Staff", "example.com")); // Cache: [intranet.edu, example.com]
        proxy.handleRequest(new UserRequest(adminRouter, "Staff", "test.com"));   // Cache: [intranet.edu, example.com, test.com]
        // Cache is now full (3 entries)

        // Access intranet.edu to make it recently used
        proxy.handleRequest(new UserRequest(classroomRouter, "Student", "intranet.edu"));
        // Add a new domain, should evict the least recently used (example.com)
        proxy.handleRequest(new UserRequest(adminRouter, "Staff", "new.com"));

        Set<String> cache = proxy.getCache();
        assertEquals(3, cache.size(), "Cache should not exceed capacity of 3");
        assertFalse(cache.contains("example.com"), "Least recently used domain (example.com) should be evicted");
        assertTrue(cache.contains("intranet.edu"), "intranet.edu should still be in cache");
        assertTrue(cache.contains("test.com"), "test.com should still be in cache");
        assertTrue(cache.contains("new.com"), "new.com should be added to cache");
    }

    @Test
    void testProxyOverload() {
        // Max load capacity is 2
        proxy.handleRequest(new UserRequest(adminRouter, "Staff", "example.com")); // Load=1
        proxy.handleRequest(new UserRequest(adminRouter, "Staff", "test.com"));   // Load=2
        // Now at max capacity

        UserRequest request = new UserRequest(adminRouter, "Staff", "new.com");
        boolean result = proxy.handleRequest(request);
        assertFalse(result, "Should not handle request when proxy is overloaded");
        assertFalse(proxy.getCache().contains("new.com"), "new.com should not be added to cache when overloaded");
    }

    @Test
    void testLoadDecay() {
        proxy.handleRequest(new UserRequest(adminRouter, "Staff", "example.com")); // Load=1
        proxy.handleRequest(new UserRequest(adminRouter, "Staff", "test.com"));   // Load=2
        proxy.decayLoad();
        // Load should decrease to 1, allowing a new request
        boolean result = proxy.handleRequest(new UserRequest(adminRouter, "Staff", "new.com"));
        assertTrue(result, "Should handle request after load decay");
        assertTrue(proxy.getCache().contains("new.com"), "new.com should be added to cache after load decay");
    }
}
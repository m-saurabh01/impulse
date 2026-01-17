# ⚡ Impulse Optimization Guide

> Performance tuning, caching strategies, and production-ready configurations.

---

## 📊 Current Configuration Overview

| Component | Setting | Value | Purpose |
|-----------|---------|-------|---------|
| **HikariCP** | Max Pool Size | 50 | Connection limit |
| **HikariCP** | Min Idle | 10 | Warm connections |
| **Tomcat** | Max Threads | 200 | Concurrent requests |
| **Tomcat** | Max Connections | 8192 | TCP connections |
| **Hibernate** | Batch Size | 30 | Bulk operations |
| **Session** | Timeout | 30 min | User session |

---

## 🗄️ Database Optimization

### Connection Pool Tuning (HikariCP)

```properties
# application.properties

# Pool size = (core_count * 2) + spinning_disks
# For 8-core server: (8 * 2) + 0 = 16 (SSD)
spring.datasource.hikari.maximum-pool-size=50

# Keep connections warm for quick access
spring.datasource.hikari.minimum-idle=10

# Prevent connection leaks
spring.datasource.hikari.leak-detection-threshold=60000

# Connection timeout (30 seconds)
spring.datasource.hikari.connection-timeout=30000

# Idle connection timeout (10 minutes)
spring.datasource.hikari.idle-timeout=600000

# Maximum connection lifetime (30 minutes)
spring.datasource.hikari.max-lifetime=1800000
```

### Pool Size Recommendations

| Users | Max Pool | Min Idle | Notes |
|-------|----------|----------|-------|
| < 100 | 20 | 5 | Small deployment |
| 100-500 | 50 | 10 | Medium deployment |
| 500-2000 | 100 | 20 | Large deployment |
| 2000+ | 150+ | 30 | Enterprise (consider clustering) |

### Hibernate Optimizations

```properties
# Batch inserts/updates for bulk operations
spring.jpa.properties.hibernate.jdbc.batch_size=30
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.batch_versioned_data=true

# Statement caching
spring.jpa.properties.hibernate.jdbc.fetch_size=50

# Second-level cache (if using Ehcache/Redis)
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory
```

### Query Optimization

```java
// ❌ Bad: N+1 query problem
@Entity
public class Email {
    @ManyToOne
    private User sender;  // Lazy load causes N+1
}

// ✅ Good: Fetch join in repository
@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    
    @Query("SELECT e FROM Email e JOIN FETCH e.sender WHERE e.recipient.id = :userId")
    List<Email> findInboxWithSender(@Param("userId") Long userId);
}

// ✅ Good: Entity Graph for flexible loading
@EntityGraph(attributePaths = {"sender", "attachments"})
List<Email> findByRecipientId(Long recipientId);
```

### Index Optimization

```sql
-- Essential indexes for email queries
CREATE INDEX idx_email_recipient ON emails(recipient_id, is_trashed, created_at DESC);
CREATE INDEX idx_email_sender ON emails(sender_id, created_at DESC);
CREATE INDEX idx_email_draft ON emails(sender_id, is_draft);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_deleted ON users(deleted);

-- Composite index for inbox query
CREATE INDEX idx_email_inbox ON emails(recipient_id, is_trashed, is_read, created_at DESC);
```

---

## 🖥️ Tomcat Optimization

### Thread Pool Configuration

```properties
# Embedded Tomcat settings
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=20
server.tomcat.max-connections=8192
server.tomcat.accept-count=100

# Connection timeout
server.tomcat.connection-timeout=20000
```

### Thread Pool Sizing Guide

| Scenario | Max Threads | Min Spare | Max Connections |
|----------|-------------|-----------|-----------------|
| Development | 50 | 5 | 1000 |
| Small Prod | 100 | 10 | 4096 |
| Medium Prod | 200 | 20 | 8192 |
| Large Prod | 400 | 50 | 16384 |

### NIO vs NIO2

```properties
# Use NIO2 for better async I/O (Java 8+)
server.tomcat.protocol-handler-class-name=org.apache.coyote.http11.Http11Nio2Protocol
```

---

## 💾 Caching Strategies

### Spring Cache Setup

```java
// CacheConfig.java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats());
        return manager;
    }
}
```

### Cache Usage

```java
@Service
public class UserService {
    
    // Cache user by ID (frequently accessed)
    @Cacheable(value = "users", key = "#userId")
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    // Evict on update
    @CacheEvict(value = "users", key = "#user.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    // Cache email count (dashboard stat)
    @Cacheable(value = "emailCount", key = "#userId")
    public long getUnreadCount(Long userId) {
        return emailRepository.countUnreadByRecipientId(userId);
    }
}
```

### Cache Recommendations

| Data Type | TTL | Strategy |
|-----------|-----|----------|
| User profile | 10 min | Cache-aside |
| Email count | 1 min | Cache-aside + evict on new email |
| Settings | 30 min | Cache-aside |
| Attachments | N/A | Disk-based (large files) |

---

## 📁 Static Resource Optimization

### Enable Gzip Compression

```properties
# application.properties
server.compression.enabled=true
server.compression.mime-types=text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
server.compression.min-response-size=1024
```

### Browser Caching

```java
// WebConfig.java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/assets/**")
        .addResourceLocations("classpath:/static/assets/")
        .setCachePeriod(31536000)  // 1 year
        .resourceChain(true)
        .addResolver(new VersionResourceResolver()
            .addContentVersionStrategy("/**"));
}
```

### Minification Checklist

| File | Minified | Notes |
|------|----------|-------|
| `bootstrap.min.css` | ✅ | Pre-minified |
| `bootstrap.min.js` | ✅ | Pre-minified |
| `tinymce.min.js` | ✅ | Pre-minified |
| `mail.css` | ⚠️ | Minify for production |
| `mail.js` | ⚠️ | Minify for production |

---

## 🔒 Session Management

### Session Configuration

```properties
# Session timeout (30 minutes)
server.servlet.session.timeout=30m

# Session cookie settings
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true  # HTTPS only
server.servlet.session.cookie.same-site=strict
```

### Session Storage (Scaling)

For multi-server deployments, use Redis:

```properties
# Redis session storage
spring.session.store-type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

---

## 📈 Monitoring & Metrics

### Spring Actuator Setup

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# application.properties
management.endpoints.web.exposure.include=health,metrics,info,hikaricp
management.endpoint.health.show-details=when-authorized
```

### Key Metrics to Monitor

| Metric | Endpoint | Alert Threshold |
|--------|----------|-----------------|
| Connection pool | `/actuator/hikaricp` | Active > 80% |
| Heap memory | `/actuator/metrics/jvm.memory.used` | > 80% |
| Response time | `/actuator/metrics/http.server.requests` | p99 > 2s |
| Error rate | `/actuator/metrics/http.server.requests?tag=status:5xx` | > 1% |

### JVM Options for Production

```bash
java \
  -Xms1g -Xmx2g \                    # Heap size
  -XX:+UseG1GC \                      # G1 garbage collector
  -XX:MaxGCPauseMillis=200 \          # GC pause target
  -XX:+HeapDumpOnOutOfMemoryError \   # Dump on OOM
  -XX:HeapDumpPath=/var/log/impulse \ # Dump location
  -jar impulse.war
```

---

## 🚀 Production Checklist

### Pre-Deployment

- [ ] **Database indexes** created for email queries
- [ ] **Connection pool** sized for expected load
- [ ] **HTTPS enabled** with valid certificate
- [ ] **Static resources** minified and cached
- [ ] **Gzip compression** enabled
- [ ] **Session timeout** configured appropriately
- [ ] **Error pages** customized (403, 404, 500)

### Configuration

- [ ] **DEBUG logging** disabled
- [ ] **SQL logging** disabled (`show-sql=false`)
- [ ] **DDL auto** set to `validate` or `none`
- [ ] **Credentials** externalized to environment variables
- [ ] **CSRF protection** enabled
- [ ] **Security headers** configured (HSTS, X-Frame-Options)

### Monitoring

- [ ] **Actuator endpoints** secured
- [ ] **Health checks** configured
- [ ] **Log aggregation** set up
- [ ] **Alerting** configured for key metrics

### Scaling

- [ ] **Load balancer** configured (if multi-server)
- [ ] **Session storage** externalized (Redis)
- [ ] **Database replication** set up
- [ ] **CDN** for static assets (optional)

---

## 📉 Performance Anti-Patterns

### ❌ Avoid These

```java
// 1. N+1 Query Problem
for (Email email : emails) {
    User sender = email.getSender();  // Triggers query per email
}

// 2. Loading entire entity when not needed
User user = userRepository.findById(id).get();  // Loads all fields
// Use projection instead:
interface UserSummary {
    Long getId();
    String getEmail();
}

// 3. Unbounded queries
List<Email> all = emailRepository.findAll();  // Could be millions!
// Use pagination:
Page<Email> page = emailRepository.findAll(PageRequest.of(0, 50));

// 4. Business logic in transactions
@Transactional
public void sendEmail(Email email) {
    saveEmail(email);
    sendNotification(email);  // External call holds transaction!
}
```

### ✅ Better Approaches

```java
// 1. Fetch join
@Query("SELECT e FROM Email e JOIN FETCH e.sender")
List<Email> findAllWithSender();

// 2. Projections
@Query("SELECT u.id as id, u.email as email FROM User u WHERE u.id = :id")
UserSummary findSummaryById(@Param("id") Long id);

// 3. Pagination
Page<Email> findByRecipientId(Long recipientId, Pageable pageable);

// 4. Separate transactions
@Transactional
public void saveEmail(Email email) {
    emailRepository.save(email);
}

public void sendEmail(Email email) {
    saveEmail(email);
    asyncNotificationService.sendNotification(email);  // Async
}
```

---

## 📚 Related Documentation

- [Setup Guide](SETUP.md) - Installation and deployment
- [Developer Guide](DEVELOPER.md) - Architecture and conventions
- [README.md](../README.md) - Project overview

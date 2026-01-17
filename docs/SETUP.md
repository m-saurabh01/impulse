# 🚀 Impulse Setup Guide

> Complete installation and deployment guide for the Impulse email application.

---

## 📋 Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| **Java** | 8 or higher | OpenJDK or Oracle JDK |
| **Maven** | 3.6+ | For building the project |
| **MySQL** | 8.0+ | Primary database (PostgreSQL also supported) |
| **Tomcat** (optional) | 9.x | For WAR deployment |

---

## 🗄️ Database Setup

### 1. Create Database

```sql
-- Connect to MySQL as root
mysql -u root -p

-- Create database
CREATE DATABASE pulse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create application user (recommended for production)
CREATE USER 'impulse_user'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON pulse_db.* TO 'impulse_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Verify Connection

```bash
mysql -u impulse_user -p pulse_db
```

---

## ⚙️ Application Configuration

### Development Setup

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pulse_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### Production Setup (Environment Variables)

**Never hardcode credentials in production!**

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/pulse_db}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Set environment variables:

```bash
# Linux/macOS
export DB_URL=jdbc:mysql://your-server:3306/pulse_db
export DB_USERNAME=impulse_user
export DB_PASSWORD=your_secure_password

# Windows (PowerShell)
$env:DB_URL="jdbc:mysql://your-server:3306/pulse_db"
$env:DB_USERNAME="impulse_user"
$env:DB_PASSWORD="your_secure_password"
```

---

## 🏃 Running the Application

### Option 1: Development Mode (Embedded Tomcat)

```bash
# Clone and navigate to project
cd PulseMail

# Run with Maven
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

Access at: `http://localhost:8080`

### Option 2: JAR Deployment

```bash
# Build JAR
./mvnw clean package -DskipTests

# Run JAR
java -jar target/pulsemail-1.0.0.war
```

### Option 3: Tomcat WAR Deployment

```bash
# Build WAR
./mvnw clean package -DskipTests

# Copy to Tomcat
cp target/pulsemail-1.0.0.war $TOMCAT_HOME/webapps/impulse.war

# Start Tomcat
$TOMCAT_HOME/bin/startup.sh
```

Access at: `http://localhost:8080/impulse`

---

## 🔐 HTTPS Setup (Production)

### Step 1: Generate SSL Certificate

**Self-Signed (Development):**

```bash
keytool -genkeypair -alias impulse -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore src/main/resources/keystore.p12 \
  -validity 365 -storepass changeit \
  -dname "CN=localhost, OU=Impulse, O=YourOrg, L=City, ST=State, C=IN"
```

**Let's Encrypt (Production):**

```bash
# Install Certbot
sudo apt install certbot

# Get certificate
sudo certbot certonly --standalone -d impulse.yourdomain.com

# Convert to PKCS12
sudo openssl pkcs12 -export \
  -in /etc/letsencrypt/live/impulse.yourdomain.com/fullchain.pem \
  -inkey /etc/letsencrypt/live/impulse.yourdomain.com/privkey.pem \
  -out keystore.p12 -name impulse -passout pass:yourpassword
```

### Step 2: Enable in Configuration

Uncomment in `application.properties`:

```properties
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD:changeit}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=impulse
```

### Step 3: Enable in SecurityConfig.java

Uncomment the HTTPS redirect and HSTS configuration sections.

---

## 📂 File Storage Setup

### Attachments Directory

Create the attachments directory:

```bash
# Linux/macOS
mkdir -p /var/impulse/attachments
chmod 755 /var/impulse/attachments

# Update application.properties
mail.attachments.base-path=/var/impulse/attachments
```

---

## 🏢 Email Domain Configuration

Impulse restricts user registration to a specific email domain:

```properties
# Only users with @impulse.iaf.in can register
impulse.email.domain=@impulse.iaf.in
```

Change this to your organization's domain.

---

## ✅ Post-Installation Checklist

- [ ] Database created and accessible
- [ ] Application starts without errors
- [ ] Can access login page at `/login`
- [ ] Can register new user with correct email domain
- [ ] Attachments directory exists and is writable
- [ ] HTTPS configured (for production)
- [ ] Environment variables set (for production)

---

## 🐛 Troubleshooting

### Database Connection Failed

```
Check:
1. MySQL service is running: sudo service mysql status
2. Database exists: mysql -u root -p -e "SHOW DATABASES;"
3. Credentials are correct in application.properties
4. Port 3306 is not blocked by firewall
```

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Out of Memory Error

```bash
# Increase JVM memory
java -Xms512m -Xmx2048m -jar target/pulsemail-1.0.0.war
```

---

## 📞 Support

For issues and questions:
- Check the [Developer Guide](DEVELOPER.md)
- Review [Optimization Guide](OPTIMIZATION.md)
- Contact: saurabh.mishra@wipro.com

# 👨‍💻 Impulse Developer Guide

> Architecture, conventions, and contribution guidelines for Impulse development.

---

## 📐 Architecture Overview

### Technology Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| **Frontend** | JSP + Bootstrap 5 | Server-side rendered views |
| **Styling** | CSS3 + Bootstrap Icons | Responsive design system |
| **Rich Text** | TinyMCE | Email composition editor |
| **Backend** | Spring Boot 2.7.x | Application framework |
| **Security** | Spring Security | Authentication & authorization |
| **ORM** | Hibernate/JPA | Database abstraction |
| **Database** | MySQL 8+ | Primary data store |
| **Build** | Maven | Dependency management |

### Project Structure

```
PulseMail/
├── src/main/java/com/wipro/iaf/email/
│   ├── attachment/          # File attachment handling
│   │   ├── Attachment.java       # Entity
│   │   ├── AttachmentRepository.java
│   │   └── AttachmentService.java
│   │
│   ├── common/              # Shared utilities
│   │   ├── BaseEntity.java       # Common entity fields
│   │   └── GlobalExceptionHandler.java
│   │
│   ├── config/              # Configuration classes
│   │   └── WebConfig.java        # MVC configuration
│   │
│   ├── mail/                # Core email functionality
│   │   ├── Email.java            # Email entity
│   │   ├── EmailRepository.java
│   │   ├── EmailService.java     # Email CRUD operations
│   │   ├── EmailComposeService.java  # Send/Draft logic
│   │   └── MailController.java   # Request handling
│   │
│   ├── security/            # Authentication & authorization
│   │   ├── SecurityConfig.java   # Spring Security config
│   │   ├── CustomUserDetails.java
│   │   └── CustomUserDetailsService.java
│   │
│   └── user/                # User management
│       ├── User.java             # User entity (with soft-delete)
│       ├── UserRepository.java
│       ├── UserService.java
│       ├── ProfileService.java   # Profile/account operations
│       └── AchievementService.java  # Gamification
│
├── src/main/resources/
│   ├── application.properties    # Configuration
│   └── static/assets/           # Frontend assets
│       ├── css/                  # Stylesheets
│       ├── js/                   # JavaScript
│       └── tinymce/              # Rich text editor
│
├── src/main/webapp/WEB-INF/
│   ├── tags/layout/             # JSP tag files (layouts)
│   └── views/                   # JSP pages
│       ├── auth/                # Login, signup
│       ├── mail/                # Inbox, compose, etc.
│       └── error/               # Error pages
│
└── docs/                        # Documentation
    ├── SETUP.md
    ├── DEVELOPER.md
    └── OPTIMIZATION.md
```

---

## 🏗️ Design Patterns

### 1. Layered Architecture

```
┌─────────────────────────────────────┐
│           Controllers               │  ← HTTP Request Handling
├─────────────────────────────────────┤
│             Services                │  ← Business Logic
├─────────────────────────────────────┤
│           Repositories              │  ← Data Access (JPA)
├─────────────────────────────────────┤
│             Entities                │  ← Domain Models
└─────────────────────────────────────┘
```

### 2. Soft-Delete Pattern

Users are soft-deleted to preserve email history for recipients:

```java
// User.java
@Column(name = "deleted")
private boolean deleted = false;

public String getDisplayNameOrEmail() {
    return displayName != null ? displayName : email;
}
```

### 3. Service Layer Pattern

All business logic resides in services:

```java
// ProfileService.java
@Transactional
public boolean deleteAccount(Long userId, String password) {
    User user = userRepository.findById(userId).orElse(null);
    // Verify password, soft-delete user
    user.setDeleted(true);
    user.setPassword(null);
    return true;
}
```

---

## 📝 Coding Conventions

### Java Code Style

```java
// ✅ Good: Clear method names, proper annotations
@GetMapping("/inbox")
public String showInbox(@AuthenticationPrincipal CustomUserDetails user, Model model) {
    List<Email> emails = emailService.getInboxEmails(user.getId());
    model.addAttribute("emails", emails);
    return "mail/inbox";
}

// ❌ Bad: Cryptic names, business logic in controller
@GetMapping("/i")
public String i(@AuthenticationPrincipal CustomUserDetails u, Model m) {
    List<Email> e = repo.findAll(); // Direct repo access
    m.addAttribute("e", e);
    return "mail/inbox";
}
```

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `EmailService`, `UserRepository` |
| Methods | camelCase | `findByEmail()`, `sendEmail()` |
| Constants | UPPER_SNAKE | `MAX_ATTACHMENT_SIZE` |
| Database Tables | snake_case | `user_achievements` |
| JSP Views | kebab-case | `compose.jsp`, `preview.jsp` |

### Package Organization

- **One entity per package** with related service/repository
- **Cross-cutting concerns** in `common/` or `config/`
- **Security-related** classes in `security/`

---

## 🔐 Security Guidelines

### Password Handling

```java
// Always use BCrypt (configured in SecurityConfig)
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Verify password before sensitive operations
if (!passwordEncoder.matches(providedPassword, user.getPassword())) {
    return "Invalid password";
}
```

### CSRF Protection

All forms must include CSRF token:

```jsp
<form method="post" action="/profile/update">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <!-- form fields -->
</form>
```

### Input Validation

```java
// Entity-level validation
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
private String password;

// Controller-level validation
@PostMapping("/signup")
public String signup(@Valid @ModelAttribute User user, BindingResult result) {
    if (result.hasErrors()) {
        return "auth/signup";
    }
    // Process valid user
}
```

---

## 🎨 Frontend Guidelines

### CSS Structure

```css
/* mail.css - Organized by component */

/* =================================
   LAYOUT COMPONENTS
   ================================= */
.sidebar { ... }
.content-area { ... }

/* =================================
   EMAIL COMPONENTS
   ================================= */
.email-row { ... }
.email-preview { ... }

/* =================================
   BUTTON VARIANTS
   ================================= */
.action-btn { ... }
.action-btn-primary { ... }
.action-btn-disabled { ... }

/* =================================
   STATE MODIFIERS
   ================================= */
.deleted-user { ... }
.deleted-badge { ... }
```

### JavaScript Patterns

```javascript
// mail.js - Event delegation pattern
document.querySelector('.email-list').addEventListener('click', function(e) {
    if (e.target.classList.contains('email-row')) {
        openEmail(e.target.dataset.emailId);
    }
});

// CSRF token inclusion for AJAX
fetch('/api/emails', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
    },
    body: JSON.stringify(data)
});
```

---

## 📦 Adding New Features

### 1. Create Entity

```java
@Entity
@Table(name = "labels")
public class Label extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // getters, setters
}
```

### 2. Create Repository

```java
@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    List<Label> findByUserId(Long userId);
    Optional<Label> findByNameAndUserId(String name, Long userId);
}
```

### 3. Create Service

```java
@Service
@Transactional
public class LabelService {
    
    @Autowired
    private LabelRepository labelRepository;
    
    public List<Label> getUserLabels(Long userId) {
        return labelRepository.findByUserId(userId);
    }
    
    public Label createLabel(String name, Long userId) {
        // Business logic
    }
}
```

### 4. Create Controller

```java
@Controller
@RequestMapping("/labels")
public class LabelController {
    
    @Autowired
    private LabelService labelService;
    
    @GetMapping
    public String listLabels(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("labels", labelService.getUserLabels(user.getId()));
        return "mail/labels";
    }
}
```

### 5. Create View

```jsp
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>

<layout:mailLayout title="Labels">
    <div class="labels-container">
        <c:forEach items="${labels}" var="label">
            <div class="label-item">${label.name}</div>
        </c:forEach>
    </div>
</layout:mailLayout>
```

---

## 🧪 Testing Guidelines

### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    
    @Mock
    private EmailRepository emailRepository;
    
    @InjectMocks
    private EmailService emailService;
    
    @Test
    void shouldGetInboxEmails() {
        // Given
        when(emailRepository.findByRecipientId(1L))
            .thenReturn(Arrays.asList(new Email()));
        
        // When
        List<Email> result = emailService.getInboxEmails(1L);
        
        // Then
        assertThat(result).hasSize(1);
    }
}
```

### Integration Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
class MailControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser
    void shouldShowInbox() throws Exception {
        mockMvc.perform(get("/inbox"))
            .andExpect(status().isOk())
            .andExpect(view().name("mail/inbox"));
    }
}
```

---

## 🔄 Git Workflow

### Branch Naming

```
feature/add-labels
bugfix/fix-reply-button
hotfix/security-patch
refactor/optimize-queries
```

### Commit Messages

```
feat: add email labels feature
fix: disable reply button for deleted users
refactor: extract email sending logic to service
docs: update developer guide
style: format CSS according to guidelines
test: add unit tests for LabelService
```

---

## 🎮 Achievement System

To add new achievements:

```java
// AchievementService.java
public static final String POWER_USER = "Power User";
public static final String POWER_USER_DESC = "Send 500 emails";
public static final int POWER_USER_POINTS = 300;

public void checkPowerUser(User user) {
    long emailCount = emailRepository.countBySenderId(user.getId());
    if (emailCount >= 500) {
        grantAchievement(user, POWER_USER, POWER_USER_DESC, POWER_USER_POINTS);
    }
}
```

---

## 📚 Related Documentation

- [Setup Guide](SETUP.md) - Installation and deployment
- [Optimization Guide](OPTIMIZATION.md) - Performance tuning
- [README.md](../README.md) - Project overview

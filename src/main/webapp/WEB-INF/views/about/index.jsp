<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:mailLayout pageTitle="About Impulse">

<div class="about-container animate-fade-up">
    
    <!-- Admin Feedback Link -->
    <c:if test="${isAdmin}">
        <div class="admin-feedback-banner">
            <i class="bi bi-shield-check"></i>
            <span>You're logged in as Admin</span>
            <a href="${pageContext.request.contextPath}/about/feedback/admin" class="admin-feedback-link">
                <i class="bi bi-chat-square-text-fill"></i> View User Feedback
            </a>
        </div>
    </c:if>
    
    <!-- Success/Error Messages -->
    <c:if test="${not empty successMessage}">
        <div class="about-alert about-alert-success">
            <i class="bi bi-check-circle-fill"></i>
            ${successMessage}
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="about-alert about-alert-error">
            <i class="bi bi-exclamation-circle-fill"></i>
            ${errorMessage}
        </div>
    </c:if>

    <!-- Hero Section -->
    <div class="about-hero">
        <div class="about-logo">
            <img src="${pageContext.request.contextPath}/assets/icons/new_icon.png" alt="Impulse" class="about-logo-img">
        </div>
        <h1>Impulse</h1>
        <p class="about-tagline">A Modern Email Experience</p>
        <p class="about-version">Version 1.0.0</p>
    </div>

    <!-- Features Guide -->
    <div class="about-section">
        <h2><i class="bi bi-stars"></i> Features Guide</h2>
        <p class="section-intro">Discover all the powerful features Impulse has to offer</p>
        
        <div class="features-grid">
            <!-- Compose & Send -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #6c5ce7, #a29bfe);">
                    <i class="bi bi-pencil-square"></i>
                </div>
                <h3>Compose & Send</h3>
                <p>Write beautiful emails with our rich text editor. Format text, add colors, insert links, and attach files up to 10MB.</p>
            </div>

            <!-- Inbox Management -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #0984e3, #74b9ff);">
                    <i class="bi bi-inbox-fill"></i>
                </div>
                <h3>Smart Inbox</h3>
                <p>Your emails organized beautifully. Click any email to preview, use bulk actions for efficiency, and search instantly.</p>
            </div>

            <!-- Starred -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #fdcb6e, #ffeaa7);">
                    <i class="bi bi-star-fill"></i>
                </div>
                <h3>Starred Emails</h3>
                <p>Mark important emails with a star for quick access. Toggle star from the preview pane or the 3-dot menu.</p>
            </div>

            <!-- Labels -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #00b894, #55efc4);">
                    <i class="bi bi-tags-fill"></i>
                </div>
                <h3>Labels & Organization</h3>
                <p>Create custom labels with colors to organize your emails. Add labels from the 3-dot menu and find labeled emails in the sidebar.</p>
            </div>

            <!-- Contacts -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #e17055, #fab1a0);">
                    <i class="bi bi-people-fill"></i>
                </div>
                <h3>Contact Book</h3>
                <p>Save your frequent contacts. When composing, contacts appear as suggestions making it easy to add recipients.</p>
            </div>

            <!-- Read Receipts -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #636e72, #b2bec3);">
                    <i class="bi bi-check2-all"></i>
                </div>
                <h3>Read Receipts</h3>
                <p>Request read receipts when composing. Know when your important emails have been read by recipients.</p>
            </div>

            <!-- Signatures -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #d63031, #ff7675);">
                    <i class="bi bi-pen-fill"></i>
                </div>
                <h3>Rich Signatures</h3>
                <p>Create beautiful email signatures with rich text formatting. Go to Settings to customize your signature.</p>
            </div>

            <!-- Reply & Forward -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #6c5ce7, #a29bfe);">
                    <i class="bi bi-reply-all-fill"></i>
                </div>
                <h3>Reply & Forward</h3>
                <p>Reply, Reply All, or Forward emails with a single click. Original message is quoted automatically.</p>
            </div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="about-section">
        <h2><i class="bi bi-lightning-fill"></i> Quick Actions</h2>
        
        <div class="quick-actions-list">
            <div class="quick-action">
                <span class="action-key"><i class="bi bi-three-dots-vertical"></i></span>
                <span class="action-desc">Click the 3-dot menu on any email for quick actions: Reply, Star, Add Label, Delete</span>
            </div>
            <div class="quick-action">
                <span class="action-key"><i class="bi bi-check2-square"></i></span>
                <span class="action-desc">Select multiple emails using checkboxes for bulk operations</span>
            </div>
            <div class="quick-action">
                <span class="action-key"><i class="bi bi-search"></i></span>
                <span class="action-desc">Use the search bar to find emails by sender, subject, or content</span>
            </div>
        </div>
    </div>

    <!-- Developer Section -->
    <div class="about-section developer-section">
        <h2><i class="bi bi-code-slash"></i> Developer</h2>
        <div class="developer-card">
            <div class="developer-avatar">SM</div>
            <div class="developer-info">
                <h3>Saurabh Mishra</h3>
                <p>Full Stack Developer</p>
                <p class="developer-quote">"Building software that makes a difference"</p>
            </div>
        </div>
    </div>

    <!-- Feedback Form -->
    <div class="about-section feedback-section">
        <h2><i class="bi bi-chat-heart-fill"></i> Feedback & Suggestions</h2>
        <p class="section-intro">Your feedback helps make Impulse better for everyone</p>
        
        <form action="${pageContext.request.contextPath}/about/feedback" method="post" class="feedback-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="name">Your Name</label>
                    <input type="text" id="name" name="name" required 
                           value="${userName}" placeholder="Enter your name" class="form-control"/>
                </div>
                <div class="form-group">
                    <label for="email">Your Email</label>
                    <input type="email" id="email" name="email" required 
                           value="${userEmail}" placeholder="Enter your email" class="form-control"
                           <c:if test="${not empty userEmail}">readonly style="background-color: #f5f5f5; cursor: not-allowed;"</c:if>/>
                </div>
            </div>
            
            <div class="form-group">
                <label for="type">Feedback Type</label>
                <select id="type" name="type" required class="form-control">
                    <option value="feedback">General Feedback</option>
                    <option value="suggestion">Suggestion</option>
                    <option value="feature-request">Feature Request</option>
                    <option value="bug-report">Bug Report</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="subject">Subject</label>
                <input type="text" id="subject" name="subject" required 
                       placeholder="Brief summary of your feedback" class="form-control"/>
            </div>
            
            <div class="form-group">
                <label for="message">Message</label>
                <textarea id="message" name="message" required rows="5"
                          placeholder="Tell us what's on your mind..." class="form-control"></textarea>
            </div>
            
            <button type="submit" class="btn-primary-gradient">
                <i class="bi bi-send-fill"></i>
                Send Feedback
            </button>
        </form>
    </div>

    <!-- Footer -->
    <div class="about-footer">
        <p>Made with <i class="bi bi-heart-fill" style="color: #e74c3c;"></i> by Saurabh Mishra</p>
        <p class="copyright">&copy; 2026 Impulse. All rights reserved.</p>
    </div>
</div>

<style>
.about-container {
    max-width: 900px;
    margin: 0 auto;
    padding: 40px 24px;
}

/* Admin Feedback Banner */
.admin-feedback-banner {
    background: linear-gradient(135deg, #2d3436 0%, #636e72 100%);
    color: white;
    padding: 14px 20px;
    border-radius: 12px;
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    gap: 10px;
    font-weight: 500;
    font-size: 14px;
}

.admin-feedback-banner i:first-child {
    font-size: 18px;
    color: #00b894;
}

.admin-feedback-link {
    margin-left: auto;
    background: white;
    color: #2d3436;
    padding: 8px 16px;
    border-radius: 8px;
    text-decoration: none;
    font-weight: 600;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 6px;
    transition: all 0.2s;
}

.admin-feedback-link:hover {
    background: #6c5ce7;
    color: white;
    transform: translateY(-1px);
}

.about-alert {
    padding: 16px 20px;
    border-radius: 12px;
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    gap: 12px;
    font-weight: 500;
}

.about-alert-success {
    background: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
}

.about-alert-error {
    background: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
}

/* Hero */
.about-hero {
    text-align: center;
    padding: 48px 24px;
    background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 100%);
    border-radius: 24px;
    color: white;
    margin-bottom: 40px;
}

.about-logo {
    width: 100px;
    height: 100px;
    background: white;
    border-radius: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
    padding: 16px;
    box-shadow: 0 8px 32px rgba(0,0,0,0.2);
}

.about-logo-img {
    width: 100%;
    height: 100%;
    object-fit: contain;
}

.about-hero h1 {
    font-size: 42px;
    font-weight: 700;
    margin: 0 0 8px;
}

.about-tagline {
    font-size: 18px;
    opacity: 0.9;
    margin: 0 0 8px;
}

.about-version {
    font-size: 13px;
    opacity: 0.7;
    margin: 0;
}

/* Sections */
.about-section {
    margin-bottom: 48px;
}

.about-section h2 {
    font-size: 22px;
    font-weight: 600;
    color: #1a1a2e;
    margin: 0 0 8px;
    display: flex;
    align-items: center;
    gap: 10px;
}

.about-section h2 i {
    color: #6c5ce7;
}

.section-intro {
    color: #6c757d;
    margin: 0 0 24px;
}

/* Features Grid */
.features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
}

.feature-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.06);
    border: 1px solid #f0f0f0;
    transition: all 0.2s;
}

.feature-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(108, 92, 231, 0.15);
}

.feature-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 22px;
    margin-bottom: 16px;
}

.feature-card h3 {
    font-size: 16px;
    font-weight: 600;
    color: #1a1a2e;
    margin: 0 0 8px;
}

.feature-card p {
    font-size: 13px;
    color: #6c757d;
    margin: 0;
    line-height: 1.6;
}

/* Quick Actions */
.quick-actions-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.quick-action {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 12px;
}

.action-key {
    width: 40px;
    height: 40px;
    background: white;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    color: #6c5ce7;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    flex-shrink: 0;
}

.action-desc {
    font-size: 14px;
    color: #495057;
}

/* Developer Section */
.developer-card {
    display: flex;
    align-items: center;
    gap: 24px;
    background: linear-gradient(135deg, #1a1a2e 0%, #2d2d44 100%);
    border-radius: 16px;
    padding: 32px;
    color: white;
}

.developer-avatar {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #6c5ce7, #a29bfe);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
    font-weight: 700;
    flex-shrink: 0;
}

.developer-info h3 {
    font-size: 22px;
    font-weight: 600;
    margin: 0 0 4px;
}

.developer-info p {
    margin: 0;
    opacity: 0.8;
}

.developer-quote {
    font-style: italic;
    margin-top: 8px !important;
    opacity: 0.6 !important;
    font-size: 14px;
}

/* Feedback Form */
.feedback-form {
    background: white;
    border-radius: 16px;
    padding: 32px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.06);
    border: 1px solid #f0f0f0;
}

.form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
}

.feedback-form .form-group {
    margin-bottom: 20px;
}

.feedback-form label {
    display: block;
    font-size: 13px;
    font-weight: 600;
    color: #495057;
    margin-bottom: 6px;
}

.feedback-form .form-control {
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    font-size: 14px;
    transition: all 0.2s;
}

.feedback-form .form-control:focus {
    outline: none;
    border-color: #6c5ce7;
    box-shadow: 0 0 0 3px rgba(108, 92, 231, 0.1);
}

.feedback-form textarea {
    resize: vertical;
    min-height: 120px;
}

.feedback-form select {
    appearance: none;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' fill='%236c757d' viewBox='0 0 16 16'%3E%3Cpath d='M7.247 11.14 2.451 5.658C1.885 5.013 2.345 4 3.204 4h9.592a1 1 0 0 1 .753 1.659l-4.796 5.48a1 1 0 0 1-1.506 0z'/%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right 12px center;
    padding-right: 36px;
}

.feedback-form .btn-primary-gradient {
    width: 100%;
    padding: 14px;
    font-size: 15px;
}

/* Footer */
.about-footer {
    text-align: center;
    padding: 32px;
    border-top: 1px solid #f0f0f0;
    margin-top: 48px;
}

.about-footer p {
    margin: 0 0 8px;
    color: #6c757d;
}

.about-footer .copyright {
    font-size: 12px;
    color: #adb5bd;
}

@media (max-width: 768px) {
    .form-row {
        grid-template-columns: 1fr;
    }
    
    .developer-card {
        flex-direction: column;
        text-align: center;
    }
    
    .features-grid {
        grid-template-columns: 1fr;
    }
}
</style>

</layout:mailLayout>

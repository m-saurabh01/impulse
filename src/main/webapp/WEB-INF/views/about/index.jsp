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

            <!-- Snooze -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #00cec9, #81ecec);">
                    <i class="bi bi-clock-fill"></i>
                </div>
                <h3>Snooze Emails</h3>
                <p>Snooze emails to deal with later. Choose preset times or custom date. Snoozed emails return to inbox when time expires.</p>
            </div>

            <!-- Drafts -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #fdcb6e, #e17055);">
                    <i class="bi bi-file-earmark-text-fill"></i>
                </div>
                <h3>Auto-Save Drafts</h3>
                <p>Never lose your work. Emails are auto-saved as drafts. Access them anytime from the Drafts folder to continue editing.</p>
            </div>

            <!-- Trash Management -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #636e72, #2d3436);">
                    <i class="bi bi-trash3-fill"></i>
                </div>
                <h3>Trash & Restore</h3>
                <p>Deleted emails go to Trash. Restore them anytime or permanently delete. Empty trash to free up space.</p>
            </div>

            <!-- Attachments -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #0984e3, #00cec9);">
                    <i class="bi bi-paperclip"></i>
                </div>
                <h3>File Attachments</h3>
                <p>Attach multiple files up to 10MB each. Supports all common file types. Download attachments with a single click.</p>
            </div>

            <!-- Profile & Settings -->
            <div class="feature-card">
                <div class="feature-icon" style="background: linear-gradient(135deg, #a29bfe, #dfe6e9);">
                    <i class="bi bi-gear-fill"></i>
                </div>
                <h3>Profile & Settings</h3>
                <p>Customize your experience. Update profile picture, change password, manage signature, and configure preferences.</p>
            </div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="about-section">
        <h2><i class="bi bi-lightning-fill"></i> Quick Actions Guide</h2>
        <p class="section-intro">Master these shortcuts to boost your productivity</p>
        
        <div class="quick-actions-grid">
            <!-- Email Actions -->
            <div class="quick-action-card">
                <h4><i class="bi bi-envelope-fill"></i> Email Actions</h4>
                <div class="quick-actions-list">
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-three-dots-vertical"></i></span>
                        <span class="action-desc">3-dot menu for Reply, Star, Label, Snooze, Delete</span>
                    </div>
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-star"></i></span>
                        <span class="action-desc">Click star icon to toggle starred status</span>
                    </div>
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-clock"></i></span>
                        <span class="action-desc">Snooze to hide email and get reminded later</span>
                    </div>
                </div>
            </div>

            <!-- Bulk Operations -->
            <div class="quick-action-card">
                <h4><i class="bi bi-ui-checks"></i> Bulk Operations</h4>
                <div class="quick-actions-list">
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-check2-square"></i></span>
                        <span class="action-desc">Select multiple emails with checkboxes</span>
                    </div>
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-trash"></i></span>
                        <span class="action-desc">Delete selected emails at once</span>
                    </div>
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-envelope-open"></i></span>
                        <span class="action-desc">Mark all selected as read/unread</span>
                    </div>
                </div>
            </div>

            <!-- Search & Filter -->
            <div class="quick-action-card">
                <h4><i class="bi bi-funnel-fill"></i> Search & Filter</h4>
                <div class="quick-actions-list">
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-search"></i></span>
                        <span class="action-desc">Search by sender, subject, or content</span>
                    </div>
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-tag"></i></span>
                        <span class="action-desc">Click labels in sidebar to filter by label</span>
                    </div>
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-star-fill"></i></span>
                        <span class="action-desc">Use Starred folder to find important emails</span>
                    </div>
                </div>
            </div>

            <!-- Compose Tips -->
            <div class="quick-action-card">
                <h4><i class="bi bi-pencil-fill"></i> Compose Tips</h4>
                <div class="quick-actions-list">
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-at"></i></span>
                        <span class="action-desc">Type to see contact suggestions in To/CC fields</span>
                    </div>
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-check2-all"></i></span>
                        <span class="action-desc">Enable "Request Read Receipt" for important emails</span>
                    </div>
                    <div class="quick-action">
                        <span class="action-key"><i class="bi bi-save"></i></span>
                        <span class="action-desc">Click "Save Draft" to save and continue later</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Developer Section -->
    <div class="about-section developer-section">
        <h2><i class="bi bi-code-slash"></i> Developer</h2>
        <div class="developer-showcase">
            <div class="dev-card">
                <div class="dev-card-glow"></div>
                <div class="dev-card-content">
                    <div class="dev-avatar-container">
                        <div class="dev-avatar-ring"></div>
                        <div class="dev-avatar">
                            <!-- Animated Developer Character -->
                            <div class="dev-character">
                                <div class="char-head">
                                    <div class="char-hair"></div>
                                    <div class="char-face">
                                        <div class="char-eyes">
                                            <div class="char-eye left"></div>
                                            <div class="char-eye right"></div>
                                        </div>
                                        <div class="char-mouth"></div>
                                    </div>
                                </div>
                                <div class="char-body">
                                    <div class="char-shirt"></div>
                                    <div class="char-arm left-arm"></div>
                                    <div class="char-arm right-arm"></div>
                                </div>
                                <div class="char-laptop">
                                    <div class="laptop-screen">
                                        <div class="code-line"></div>
                                        <div class="code-line"></div>
                                        <div class="code-line short"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="dev-info">
                        <h3 class="dev-name">Saurabh Mishra</h3>
                        <p class="dev-role">
                            <i class="bi bi-braces"></i>
                            Full Stack Developer
                        </p>
                        <div class="dev-quote">
                            <i class="bi bi-quote"></i>
                            Building software that makes a difference
                        </div>
                        <div class="dev-tech-stack">
                            <span class="tech-tag"><i class="bi bi-cup-hot-fill"></i> Java</span>
                            <span class="tech-tag"><i class="bi bi-bootstrap-fill"></i> Spring Boot</span>
                            <span class="tech-tag"><i class="bi bi-database-fill"></i> MySQL</span>
                            <span class="tech-tag"><i class="bi bi-filetype-js"></i> JavaScript</span>
                        </div>
                    </div>
                </div>
                <div class="dev-card-particles">
                    <div class="particle"></div>
                    <div class="particle"></div>
                    <div class="particle"></div>
                    <div class="particle"></div>
                    <div class="particle"></div>
                </div>
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
.quick-actions-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 20px;
}

.quick-action-card {
    background: linear-gradient(135deg, #f8f9fa 0%, #fff 100%);
    border-radius: 16px;
    padding: 20px;
    border: 1px solid #e9ecef;
    transition: all 0.3s ease;
}

.quick-action-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(108, 92, 231, 0.12);
    border-color: #6c5ce7;
}

.quick-action-card h4 {
    font-size: 15px;
    font-weight: 600;
    color: #2d3436;
    margin: 0 0 16px 0;
    padding-bottom: 12px;
    border-bottom: 2px solid #6c5ce7;
    display: flex;
    align-items: center;
    gap: 8px;
}

.quick-action-card h4 i {
    color: #6c5ce7;
}

.quick-actions-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.quick-action {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 12px;
    background: white;
    border-radius: 10px;
    transition: all 0.2s ease;
}

.quick-action:hover {
    background: #f0f0ff;
    transform: translateX(4px);
}

.action-key {
    width: 32px;
    height: 32px;
    background: linear-gradient(135deg, #6c5ce7, #a29bfe);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: white;
    flex-shrink: 0;
}

.action-desc {
    font-size: 13px;
    color: #495057;
    line-height: 1.4;
}

/* ============================================
   DEVELOPER SECTION - Creative Animated Design
   ============================================ */
.developer-showcase {
    perspective: 1000px;
}

.dev-card {
    position: relative;
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
    border-radius: 24px;
    padding: 40px;
    overflow: hidden;
    transform-style: preserve-3d;
    transition: transform 0.4s ease, box-shadow 0.4s ease;
    box-shadow: 0 10px 40px rgba(108, 92, 231, 0.2);
}

.dev-card:hover {
    transform: translateY(-8px) rotateX(2deg);
    box-shadow: 0 20px 60px rgba(108, 92, 231, 0.35);
}

.dev-card-glow {
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: conic-gradient(
        from 0deg,
        transparent,
        rgba(108, 92, 231, 0.1),
        transparent,
        rgba(162, 155, 254, 0.1),
        transparent
    );
    animation: rotateGlow 8s linear infinite;
}

@keyframes rotateGlow {
    100% { transform: rotate(360deg); }
}

.dev-card-content {
    position: relative;
    z-index: 2;
    display: flex;
    align-items: center;
    gap: 32px;
}

.dev-avatar-container {
    position: relative;
    flex-shrink: 0;
}

.dev-avatar-ring {
    position: absolute;
    inset: -8px;
    border-radius: 50%;
    background: conic-gradient(
        from 0deg,
        #6c5ce7,
        #a29bfe,
        #fd79a8,
        #fdcb6e,
        #00b894,
        #0984e3,
        #6c5ce7
    );
    animation: spinRing 4s linear infinite;
    opacity: 0.7;
}

@keyframes spinRing {
    100% { transform: rotate(360deg); }
}

.dev-avatar-ring::before {
    content: '';
    position: absolute;
    inset: 3px;
    background: linear-gradient(135deg, #1a1a2e, #16213e);
    border-radius: 50%;
}

.dev-avatar {
    width: 100px;
    height: 100px;
    background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    z-index: 1;
    overflow: hidden;
}

/* ============================================
   ANIMATED DEVELOPER CHARACTER
   ============================================ */
.dev-character {
    position: relative;
    width: 70px;
    height: 80px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

/* Head */
.char-head {
    position: relative;
    width: 36px;
    height: 36px;
    z-index: 3;
}

.char-hair {
    position: absolute;
    top: -2px;
    left: 2px;
    right: 2px;
    height: 18px;
    background: #2d3436;
    border-radius: 50% 50% 0 0;
    z-index: 2;
}

.char-hair::before {
    content: '';
    position: absolute;
    top: 8px;
    left: -3px;
    width: 8px;
    height: 12px;
    background: #2d3436;
    border-radius: 50% 0 0 50%;
}

.char-face {
    position: absolute;
    top: 8px;
    left: 3px;
    right: 3px;
    bottom: 0;
    background: #ffeaa7;
    border-radius: 0 0 50% 50%;
    z-index: 1;
}

.char-eyes {
    display: flex;
    justify-content: center;
    gap: 8px;
    padding-top: 8px;
}

.char-eye {
    width: 5px;
    height: 5px;
    background: #2d3436;
    border-radius: 50%;
    animation: blink 4s ease-in-out infinite;
}

.char-eye.right {
    animation-delay: 0.1s;
}

@keyframes blink {
    0%, 45%, 55%, 100% { transform: scaleY(1); }
    50% { transform: scaleY(0.1); }
}

.char-mouth {
    width: 8px;
    height: 4px;
    background: #e17055;
    border-radius: 0 0 10px 10px;
    margin: 4px auto 0;
    animation: smile 3s ease-in-out infinite;
}

@keyframes smile {
    0%, 100% { width: 8px; }
    50% { width: 10px; }
}

/* Body */
.char-body {
    position: relative;
    margin-top: -4px;
    z-index: 2;
}

.char-shirt {
    width: 32px;
    height: 22px;
    background: linear-gradient(135deg, #0984e3, #74b9ff);
    border-radius: 8px 8px 0 0;
    position: relative;
}

.char-shirt::before {
    content: '';
    position: absolute;
    top: 4px;
    left: 50%;
    transform: translateX(-50%);
    width: 6px;
    height: 6px;
    background: white;
    border-radius: 50%;
}

/* Arms */
.char-arm {
    position: absolute;
    width: 8px;
    height: 18px;
    background: linear-gradient(135deg, #0984e3, #74b9ff);
    border-radius: 4px;
    top: 2px;
}

.left-arm {
    left: -6px;
    transform-origin: top center;
    animation: typeLeft 0.8s ease-in-out infinite;
}

.right-arm {
    right: -6px;
    transform-origin: top center;
    animation: typeRight 0.8s ease-in-out infinite;
    animation-delay: 0.4s;
}

@keyframes typeLeft {
    0%, 100% { transform: rotate(-15deg); }
    50% { transform: rotate(-25deg); }
}

@keyframes typeRight {
    0%, 100% { transform: rotate(15deg); }
    50% { transform: rotate(25deg); }
}

/* Laptop */
.char-laptop {
    position: absolute;
    bottom: 0;
    width: 44px;
    height: 12px;
    background: #636e72;
    border-radius: 2px 2px 4px 4px;
    z-index: 1;
}

.laptop-screen {
    position: absolute;
    bottom: 100%;
    left: 2px;
    right: 2px;
    height: 28px;
    background: #2d3436;
    border-radius: 3px 3px 0 0;
    padding: 4px;
    transform-origin: bottom center;
    animation: screenGlow 2s ease-in-out infinite;
}

@keyframes screenGlow {
    0%, 100% { box-shadow: 0 0 10px rgba(116, 185, 255, 0.3); }
    50% { box-shadow: 0 0 20px rgba(116, 185, 255, 0.6); }
}

.code-line {
    height: 3px;
    background: #00b894;
    border-radius: 1px;
    margin-bottom: 3px;
    animation: codeLine 1.5s ease-in-out infinite;
}

.code-line:nth-child(1) {
    width: 80%;
    animation-delay: 0s;
}

.code-line:nth-child(2) {
    width: 60%;
    background: #fdcb6e;
    animation-delay: 0.3s;
}

.code-line:nth-child(3) {
    width: 40%;
    background: #74b9ff;
    animation-delay: 0.6s;
}

@keyframes codeLine {
    0%, 100% { opacity: 0.6; transform: scaleX(1); }
    50% { opacity: 1; transform: scaleX(1.1); }
}

.dev-status-dot {
    position: absolute;
    bottom: 8px;
    right: 8px;
    width: 20px;
    height: 20px;
    background: #00b894;
    border-radius: 50%;
    border: 3px solid #1a1a2e;
    z-index: 3;
    animation: statusBlink 2s ease-in-out infinite;
}

@keyframes statusBlink {
    0%, 100% { opacity: 1; box-shadow: 0 0 0 0 rgba(0, 184, 148, 0.6); }
    50% { opacity: 0.8; box-shadow: 0 0 0 8px rgba(0, 184, 148, 0); }
}

.dev-info {
    color: white;
    flex: 1;
}

.dev-name {
    font-size: 28px;
    font-weight: 700;
    margin: 0 0 8px;
    background: linear-gradient(90deg, #ffffff, #a29bfe);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    animation: shimmer 3s ease-in-out infinite;
    background-size: 200% 100%;
}

@keyframes shimmer {
    0% { background-position: -200% 0; }
    100% { background-position: 200% 0; }
}

.dev-role {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    color: #a29bfe;
    margin: 0 0 16px;
    padding: 6px 14px;
    background: rgba(108, 92, 231, 0.15);
    border-radius: 20px;
    border: 1px solid rgba(108, 92, 231, 0.3);
}

.dev-role i {
    font-size: 14px;
}

.dev-quote {
    font-size: 15px;
    color: rgba(255, 255, 255, 0.7);
    font-style: italic;
    margin-bottom: 20px;
    display: flex;
    align-items: flex-start;
    gap: 8px;
}

.dev-quote i {
    color: #6c5ce7;
    font-size: 24px;
    opacity: 0.5;
}

.dev-tech-stack {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.tech-tag {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 14px;
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.85);
    transition: all 0.3s ease;
    cursor: default;
}

.tech-tag:hover {
    background: rgba(108, 92, 231, 0.3);
    border-color: rgba(108, 92, 231, 0.5);
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(108, 92, 231, 0.3);
}

.tech-tag i {
    font-size: 14px;
    color: #a29bfe;
}

/* Floating Particles */
.dev-card-particles {
    position: absolute;
    inset: 0;
    overflow: hidden;
    pointer-events: none;
}

.particle {
    position: absolute;
    width: 6px;
    height: 6px;
    background: rgba(108, 92, 231, 0.6);
    border-radius: 50%;
    animation: floatParticle 6s ease-in-out infinite;
}

.particle:nth-child(1) { left: 10%; top: 20%; animation-delay: 0s; }
.particle:nth-child(2) { left: 20%; top: 80%; animation-delay: 1s; }
.particle:nth-child(3) { left: 60%; top: 10%; animation-delay: 2s; }
.particle:nth-child(4) { left: 80%; top: 60%; animation-delay: 3s; }
.particle:nth-child(5) { left: 90%; top: 30%; animation-delay: 4s; }

@keyframes floatParticle {
    0%, 100% {
        transform: translateY(0) scale(1);
        opacity: 0.6;
    }
    50% {
        transform: translateY(-30px) scale(1.5);
        opacity: 1;
    }
}

/* Responsive adjustments */
@media (max-width: 600px) {
    .dev-card-content {
        flex-direction: column;
        text-align: center;
    }
    
    .dev-name {
        font-size: 24px;
    }
    
    .dev-quote {
        justify-content: center;
    }
    
    .dev-tech-stack {
        justify-content: center;
    }
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

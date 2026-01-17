<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<layout:authLayout pageTitle="Create account">

    <div class="auth-card animate-fade-up">
        
        <!-- Logo -->
        <div class="auth-logo">
            <img src="${pageContext.request.contextPath}/assets/icons/new_icon.png" alt="Impulse" class="auth-logo-img">
            <span>Impulse</span>
        </div>
        
        <h2 class="auth-title">Create your account</h2>
        <p class="auth-subtitle">Start using Impulse today</p>
        
        <c:if test="${not empty error}">
            <div class="auth-error">
                <i class="bi bi-exclamation-circle"></i>
                ${error}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/signup" class="auth-form" id="signupForm">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <div class="mb-3">
                <label class="form-label">Email address</label>
                <input class="form-control" 
                       type="email" 
                       name="email" 
                       id="emailInput"
                       placeholder="yourname@impulse.iaf.in"
                       required autofocus
                       pattern=".+@impulse\.iaf\.in$">
                <small class="form-hint">
                    <i class="bi bi-info-circle"></i>
                    Only @impulse.iaf.in emails are allowed
                </small>
            </div>

            <div class="mb-3">
                <label class="form-label">Password</label>
                <div class="password-wrapper">
                    <input class="form-control" 
                           type="password" 
                           name="password" 
                           id="passwordInput"
                           placeholder="Create a strong password"
                           required
                           minlength="8">
                    <button type="button" class="password-toggle" onclick="togglePassword('passwordInput', this)" aria-label="Show password">
                        <i class="bi bi-eye"></i>
                    </button>
                </div>
                <div class="password-requirements">
                    <small class="form-hint"><i class="bi bi-shield-lock"></i> Password Requirements:</small>
                    <ul class="requirements-list">
                        <li id="req-length" class="requirement"><i class="bi bi-circle"></i> At least 8 characters</li>
                        <li id="req-upper" class="requirement"><i class="bi bi-circle"></i> One uppercase letter (A-Z)</li>
                        <li id="req-lower" class="requirement"><i class="bi bi-circle"></i> One lowercase letter (a-z)</li>
                        <li id="req-digit" class="requirement"><i class="bi bi-circle"></i> One digit (0-9)</li>
                        <li id="req-special" class="requirement"><i class="bi bi-circle"></i> One special character (!@#$%^&*)</li>
                    </ul>
                </div>
            </div>

            <button type="submit" class="btn btn-success w-100" id="submitBtn" disabled>
                Create account
            </button>

            <div class="auth-footer">
                <span>Already have an account?</span>
                <a href="${pageContext.request.contextPath}/login">Sign in</a>
            </div>
        </form>
    </div>

    <style>
        .password-wrapper {
            position: relative;
        }
        .password-wrapper .form-control {
            padding-right: 45px;
        }
        .password-toggle {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            border: none;
            background: transparent;
            color: #6c757d;
            cursor: pointer;
            padding: 5px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .password-toggle:hover {
            color: #0078d4;
        }
        .password-toggle i {
            font-size: 18px;
        }
        .form-hint {
            color: #605e5c;
            font-size: 12px;
            margin-top: 6px;
            display: flex;
            align-items: center;
            gap: 4px;
        }
        .password-requirements {
            margin-top: 10px;
            padding: 12px;
            background: #f8f9fa;
            border-radius: 8px;
            border: 1px solid #e9ecef;
        }
        .requirements-list {
            margin: 8px 0 0 0;
            padding: 0;
            list-style: none;
        }
        .requirement {
            font-size: 12px;
            color: #6c757d;
            padding: 3px 0;
            display: flex;
            align-items: center;
            gap: 6px;
            transition: all 0.2s ease;
        }
        .requirement i {
            font-size: 10px;
        }
        .requirement.valid {
            color: #28a745;
        }
        .requirement.valid i::before {
            content: "\f26b"; /* bi-check-circle-fill */
        }
        .requirement.invalid {
            color: #dc3545;
        }
        .requirement.invalid i::before {
            content: "\f623"; /* bi-x-circle-fill */
        }
        #submitBtn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
    </style>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const emailInput = document.getElementById('emailInput');
            const passwordInput = document.getElementById('passwordInput');
            const submitBtn = document.getElementById('submitBtn');
            
            const requirements = {
                length: document.getElementById('req-length'),
                upper: document.getElementById('req-upper'),
                lower: document.getElementById('req-lower'),
                digit: document.getElementById('req-digit'),
                special: document.getElementById('req-special')
            };
            
            function validateEmail() {
                const email = emailInput.value.toLowerCase();
                return email.endsWith('@impulse.iaf.in');
            }
            
            function validatePassword() {
                const password = passwordInput.value;
                const checks = {
                    length: password.length >= 8,
                    upper: /[A-Z]/.test(password),
                    lower: /[a-z]/.test(password),
                    digit: /[0-9]/.test(password),
                    special: /[!@#$%^&*()_+\-=\[\]{}|;:,.<>?]/.test(password)
                };
                
                // Update visual indicators
                for (const [key, isValid] of Object.entries(checks)) {
                    if (requirements[key]) {
                        requirements[key].classList.toggle('valid', isValid);
                        requirements[key].classList.toggle('invalid', password.length > 0 && !isValid);
                    }
                }
                
                return Object.values(checks).every(Boolean);
            }
            
            function updateSubmitButton() {
                const emailValid = validateEmail();
                const passwordValid = validatePassword();
                submitBtn.disabled = !(emailValid && passwordValid);
            }
            
            emailInput.addEventListener('input', updateSubmitButton);
            passwordInput.addEventListener('input', updateSubmitButton);
            
            // Initial check
            updateSubmitButton();
        });
        
        // Password visibility toggle
        function togglePassword(inputId, button) {
            const input = document.getElementById(inputId);
            const icon = button.querySelector('i');
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('bi-eye');
                icon.classList.add('bi-eye-slash');
                button.setAttribute('aria-label', 'Hide password');
            } else {
                input.type = 'password';
                icon.classList.remove('bi-eye-slash');
                icon.classList.add('bi-eye');
                button.setAttribute('aria-label', 'Show password');
            }
        }
    </script>

</layout:authLayout>



function initUserValidation(form) {
    if (!form) return null;

    const username = form.querySelector('#username');
    const biography = form.querySelector('#biography');
    let currentUsernameCheck = 0;

    function validateUsername() {
        username.setCustomValidity("");
        username.removeAttribute('aria-invalid'); // Clear any previous state

        if (username.value.length < 3 || username.value.length > 20) {
            username.setCustomValidity("Username must be between 3 and 20 characters.");
            username.setAttribute('aria-invalid', 'true');
            username.reportValidity();
            return false;
        }

        if (!/^[a-zA-Z0-9_]+$/.test(username.value)) {
            username.setCustomValidity("Username can only contain letters, numbers, and underscores.");
            username.setAttribute('aria-invalid', 'true');
            username.reportValidity();
            return false;
        }

        return true;
    }

    function checkUsernameAvailability() {
        if (!validateUsername()) return;

        // Skip validation if in edit mode and username hasn't changed
        const originalUsername = username.getAttribute('data-original');
        if (originalUsername && originalUsername === username.value) {
            username.setCustomValidity("");
            username.removeAttribute('aria-invalid');
            return;
        }

        const requestId = ++currentUsernameCheck;

        // Include userId if in edit mode
        let url = window.contextPath + '/check-username?username=' + encodeURIComponent(username.value);
        const userId = username.getAttribute('data-user-id');
        if (userId) {
            url += '&userId=' + encodeURIComponent(userId);
        }
        
        fetch(url)
            .then(response => response.json())
            .then(data => {
                if (requestId !== currentUsernameCheck) return;

                if (data.exists) {
                    username.setCustomValidity("This username is already taken.");
                    username.setAttribute('aria-invalid', 'true');
                    username.reportValidity();
                } else {
                    username.setCustomValidity("");
                    username.removeAttribute('aria-invalid');
                    username.reportValidity();
                }
            })
            .catch(error => {
                console.error("Error checking username availability:", error);
            });
    }

    function debounce(fn, delay) {
        let timeoutId;
        return function (...args) {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => fn.apply(this, args), delay);
        };
    }

    const debouncedCheckUsername = debounce(checkUsernameAvailability, 300);

    function validateBiography() {
        biography.setCustomValidity("");

        if (biography.value.length > 500) {
            biography.setCustomValidity("Biography cannot exceed 500 characters.");
            return false;
        }

        return true;
    }

    // Event listeners
    username.addEventListener('input', () => {
        validateUsername();
        if (username.validity.valid) {
            debouncedCheckUsername();
        }
    });

    username.addEventListener('blur', () => {
        validateUsername();
        if (username.validity.valid) {
            checkUsernameAvailability();
        }
    });

    biography.addEventListener('input', validateBiography);
    biography.addEventListener('blur', validateBiography);

    return function validateUser() {
        let isValid = validateUsername();
        isValid = validateBiography() && isValid;

        // username availability check on submit
        if (username.validity.valid) {
            checkUsernameAvailability();
            isValid = !username.getAttribute('aria-invalid') && isValid;
        }

        if (!isValid) {
            const firstInvalid = form.querySelector(':invalid, [aria-invalid="true"]');
            if (firstInvalid) firstInvalid.focus();
        }

        return isValid;
    };
}

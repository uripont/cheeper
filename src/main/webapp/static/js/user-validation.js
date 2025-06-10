function validateUsername(username) {
    if (!username) return "Username is required";
    if (username.length < 3) return "Username must be at least 3 characters";
    if (username.length > 20) return "Username cannot exceed 20 characters";
    return "";
}

function validateBirthdate(birthdate) {
    if (!birthdate) return "Birthdate is required";
    const date = new Date(birthdate);
    const today = new Date();
    if (date > today) return "Birthdate cannot be in the future";
    return "";
}

function validateBiography(biography) {
    if (!biography) return "Biography is required";
    if (biography.length < 10) return "Biography must be at least 10 characters";
    if (biography.length > 500) return "Biography cannot exceed 500 characters";
    return "";
}

function validateSocialLink(platform, url) {
    if (!platform && url) return "Platform name is required when URL is provided";
    if (platform && !url) return "URL is required when platform is provided";
    if (url && !isValidUrl(url)) return "Invalid URL format";
    return "";
}

function validateDegree(type, field) {
    if (!type && field) return "Degree type is required when field is provided";
    if (type && !field) return "Field of study is required when type is provided";
    return "";
}

function validateSubject(code, name) {
    if (!code && name) return "Subject code is required when name is provided";
    if (code && !name) return "Subject name is required when code is provided";
    if (code && !/^\d{5}$/.test(code)) return "Subject code must be 5 digits";
    return "";
}

function isValidUrl(string) {
    try {
        new URL(string);
        return true;
    } catch (_) {
        return false;
    }
}

// Initialize validation for any user form
function initUserValidation(form) {
    if (!form) return null;

    return async function validate() {
        let isValid = true;

        // Validate username
        const username = form.querySelector('[name="username"]');
        if (username) {
            const usernameError = validateUsername(username.value);
            if (usernameError) {
                username.setCustomValidity(usernameError);
                username.reportValidity();
                isValid = false;
            }
        }

        // Validate birthdate
        const birthdate = form.querySelector('[name="birthdate"]');
        if (birthdate) {
            const birthdateError = validateBirthdate(birthdate.value);
            if (birthdateError) {
                birthdate.setCustomValidity(birthdateError);
                birthdate.reportValidity();
                isValid = false;
            }
        }

        // Validate biography
        const biography = form.querySelector('[name="biography"]');
        if (biography) {
            const biographyError = validateBiography(biography.value);
            if (biographyError) {
                biography.setCustomValidity(biographyError);
                biography.reportValidity();
                isValid = false;
            }
        }

        return isValid;
    };
}

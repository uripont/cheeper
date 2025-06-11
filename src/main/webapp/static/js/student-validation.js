function initStudentValidation(form) {
    if (!form) return null;

    // Initialize user validation (async)
    const validateUser = window.initUserValidation(form);

    const birthdate = form.querySelector('#birthdate');
    if (!birthdate) return null;

    // Validate birthdate: at least 13 years old 
    function validateBirthdate() {
        const birthDateValue = new Date(birthdate.value);
        const today = new Date();
        let age = today.getFullYear() - birthDateValue.getFullYear();
        const birthMonth = birthDateValue.getMonth();
        const birthDay = birthDateValue.getDate();

        if (isNaN(birthDateValue.getTime())) {
            birthdate.setCustomValidity("Invalid birthdate.");
            birthdate.reportValidity();
            return false;
        }

        if (today.getMonth() < birthMonth || (today.getMonth() === birthMonth && today.getDate() < birthDay)) {
            age--;
        }

        if (age < 13) {
            birthdate.setCustomValidity("You must be at least 13 years old to register.");
            birthdate.reportValidity();
            return false;
        }

        birthdate.setCustomValidity('');
        birthdate.reportValidity();
        return true;
    }

    birthdate.addEventListener('input', validateBirthdate);

    // Social network URL patterns
    const SOCIAL_NETWORK_PATTERNS = {
        'facebook': /^https:\/\/www\.facebook\.com\/[a-zA-Z0-9.]{5,}$/,
        'twitter': /^https:\/\/www\.twitter\.com\/[a-zA-Z0-9_]{1,15}$/,
        'instagram': /^https:\/\/www\.instagram\.com\/[a-zA-Z0-9._]{1,30}$/,
        'linkedin': /^https:\/\/www\.linkedin\.com\/in\/[a-zA-Z0-9-]{5,}$/,
        'youtube': /^https:\/\/www\.youtube\.com\/[a-zA-Z0-9_]{3,}$/,
        'github': /^https:\/\/www\.github\.com\/[a-zA-Z0-9-]{1,39}$/,
        'tiktok': /^https:\/\/www\.tiktok\.com\/@[a-zA-Z0-9._]{1,24}$/
    };

    function updateSocialLinkURL(platformInput, urlInput) {
        var platformName = platformInput.value.trim().toLowerCase();
        if (platformName && !urlInput.value) {
            urlInput.value = 'https://www.' + platformName + '.com/';
        }
    }

    function validateSocialLinks() {
        const socialLinkKeys = document.getElementsByName('socialLinkKey');
        const socialLinkValues = document.getElementsByName('socialLinkValue');
        let isValid = true;

        for (let i = 0; i < socialLinkKeys.length; i++) {
            const platform = socialLinkKeys[i].value.trim().toLowerCase();
            const url = socialLinkValues[i].value.trim();

            socialLinkKeys[i].setCustomValidity('');
            socialLinkValues[i].setCustomValidity('');

            if (platform && url) {
                updateSocialLinkURL(socialLinkKeys[i], socialLinkValues[i]);

                try {
                    const urlObj = new URL(url);
                    if (urlObj.protocol !== 'https:' || !url.startsWith('https://www.' + platform + '.com/')) {
                        socialLinkValues[i].setCustomValidity('URL must be exactly https://www.' + platform + '.com/');
                        isValid = false;
                        continue;
                    }

                    if (SOCIAL_NETWORK_PATTERNS[platform] && !SOCIAL_NETWORK_PATTERNS[platform].test(url)) {
                        socialLinkValues[i].setCustomValidity('Invalid ' + platform + ' URL format.');
                        isValid = false;
                    }
                } catch (err) {
                    socialLinkValues[i].setCustomValidity("Invalid URL.");
                    isValid = false;
                }
            } else if (platform || url) {
                if (!platform) socialLinkKeys[i].setCustomValidity("Platform name is required.");
                if (!url) socialLinkValues[i].setCustomValidity("URL is required.");
                isValid = false;
            }
        }
        return isValid;
    }

    function validateDegrees() {
        const degreeKeys = document.getElementsByName('degreeKey');
        const degreeValues = document.getElementsByName('degreeValue');
        let isValid = true;

        for (let i = 0; i < degreeKeys.length; i++) {
            const degreeType = degreeKeys[i].value.trim();
            const fieldOfStudy = degreeValues[i].value.trim();

            degreeKeys[i].setCustomValidity('');
            degreeValues[i].setCustomValidity('');

            if (degreeType && fieldOfStudy) {
                if (degreeType.length > 10) {
                    degreeKeys[i].setCustomValidity("Degree type should be abbreviated (e.g., BSc, PhD)");
                    isValid = false;
                }

                if (fieldOfStudy.length > 100) {
                    degreeValues[i].setCustomValidity("Field of study cannot exceed 100 characters");
                    isValid = false;
                }
            } else if (degreeType || fieldOfStudy) {
                if (!degreeType) degreeKeys[i].setCustomValidity("Degree type is required");
                if (!fieldOfStudy) degreeValues[i].setCustomValidity("Field of study is required");
                isValid = false;
            }
        }
        return isValid;
    }

    function validateSubjects() {
        const subjectKeys = document.getElementsByName('subjectKey');
        const subjectValues = document.getElementsByName('subjectValue');
        let isValid = true;

        for (let i = 0; i < subjectKeys.length; i++) {
            const subjectCode = subjectKeys[i].value.trim();
            const subjectName = subjectValues[i].value.trim();

            subjectKeys[i].setCustomValidity('');
            subjectValues[i].setCustomValidity('');

            if (subjectCode && subjectName) {
                if (!/^\d{5}$/.test(subjectCode)) {
                    subjectKeys[i].setCustomValidity("Subject code must be exactly 5 digits.");
                    isValid = false;
                }

                if (subjectName.length > 100) {
                    subjectValues[i].setCustomValidity("Subject name cannot exceed 100 characters.");
                    isValid = false;
                }
            } else if (subjectCode || subjectName) {
                if (!subjectCode) subjectKeys[i].setCustomValidity("Subject code is required.");
                if (!subjectName) subjectValues[i].setCustomValidity("Subject name is required.");
                isValid = false;
            }
        }
        return isValid;
    }

    function setupDynamicFieldValidation(containerId, keyName, valueName, validator) {
        const container = document.getElementById(containerId);
        if (container) {
            container.addEventListener('input', function(e) {
                if (e.target.name === keyName || e.target.name === valueName) {
                    validator();
                }
            });
			
			container.addEventListener('blur', function(e) {
                if (e.target.name === keyName || e.target.name === valueName) {
                    validator();
                }
            });
        }
    }

    setupDynamicFieldValidation('socialLinksContainer', 'socialLinkKey', 'socialLinkValue', validateSocialLinks);
    setupDynamicFieldValidation('degreesContainer', 'degreeKey', 'degreeValue', validateDegrees);
    setupDynamicFieldValidation('subjectsContainer', 'subjectKey', 'subjectValue', validateSubjects);

    // Return async validation function for student form
    
	return async function validateStudent() {
	        let isValid = true;

	        // Clear custom validity for all inputs
	        form.querySelectorAll('input, textarea').forEach(i => i.setCustomValidity(''));

	        // Await user validation
	        if (validateUser) {
	            const userValid = await validateUser();
	            if (!userValid) isValid = false;
	        }

	        // Run other validations
	        if (!validateSocialLinks()) isValid = false;
	        if (!validateDegrees()) isValid = false;
	        if (!validateSubjects()) isValid = false;
	        if (!validateBirthdate()) isValid = false;

	        return isValid;
	    };
}



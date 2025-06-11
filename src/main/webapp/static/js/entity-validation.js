function initEntityValidation(form) {
    if (!form) return null;

    var department = form.querySelector('#department');
    var validateUser = window.initUserValidation(form);

    function validateDepartment() {
        department.setCustomValidity("");
        
        if (department.value.length > 100) {
            department.setCustomValidity("Department name cannot exceed 100 characters.");
            return false;
        }
        return true;
    }

    // Basic validation during typing
    department.addEventListener('input', validateDepartment);

    // Validation on blur
    department.addEventListener('blur', function() {
        validateDepartment();
        department.dispatchEvent(new Event('invalid', { bubbles: false }));
    });

    // Return validation function for form submission
    return function validateEntity() {
        let isValid = true;
        
        // Validate user fields
        if (validateUser && !validateUser()) isValid = false;
        
        // Validate entity-specific fields
        if (!validateDepartment()) isValid = false;
        
        if (!isValid) {
            var firstInvalid = form.querySelector(':invalid');
            if (firstInvalid) {
                firstInvalid.focus();
            }
        }
        
        return isValid;
    };
}
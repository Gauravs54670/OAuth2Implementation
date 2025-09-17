document.querySelector("#registerForm").addEventListener("submit", async function(event) {
    event.preventDefault();
    let email = document.querySelector("#email").value.trim();
    let password = document.querySelector("#password").value.trim();
    let confirmPassword = document.querySelector("#confirmPassword").value.trim();

    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    const emailError = document.querySelector("#emailError");
    const passwordError = document.querySelector("#passwordError");
    const confirmPasswordError = document.querySelector("#confirmPasswordError");

    let valid = true;
     // Email validation
    if (!emailRegex.test(email)) {
        emailError.textContent = "Please enter a valid email address.";
        valid = false;
    } else {
        emailError.textContent = "";
    }

    // Password validation
    if (!passwordRegex.test(password)) {
        passwordError.textContent = "Password must be at least 8 characters, include upper, lower, number, and special character.";
        valid = false;
    } else {
        passwordError.textContent = "";
    }

    // Confirm password validation
    if (password !== confirmPassword || confirmPassword === "") {
        confirmPasswordError.textContent = "Passwords do not match.";
        valid = false;
    } else {
        confirmPasswordError.textContent = "";
    }
    if(valid) {
        try {
            let userData = {
                email : email,
                password : password
            }
            //registration logic here
            const backendResponse = await fetch("http://localhost:8080/api/public/register-user", {
                method : "POST",
                headers : {
                    "Content-Type" : "application/json"
                },
                credentials: "include",
                body : JSON.stringify(userData)
            });
            if(backendResponse.ok) {
                alert("Registration Successful!! Now sign-in into your account.");
                window.location.href = "SignIn.html";
            }
            else {
                const error = await backendResponse.json();
                alert("Error: " + (error.response || error.message));
                console.error(error);
            }

        } catch (error) {
            console.error(error);
            // Optionally display a user-friendly error message
            alert("An error occurred during registration. Please try again later.");
        }
    }

});
// Google login button
document.querySelector("#googleBtn").addEventListener("click", function(){
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
});
//Github login button
docuemt.querySelector("#githubBtn").addEventListener("click", function(){
    window.location.href = "http://localhost:8080/oauth2/authorization/github";
});
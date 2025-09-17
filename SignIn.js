document.addEventListener("DOMContentLoaded", function () {
    // Register form submit
    document.querySelector("#signInForm").addEventListener("submit", async function (event) {
        event.preventDefault();

        let email = document.querySelector("#email").value.trim();
        let password = document.querySelector("#password").value.trim();

        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

        const emailError = document.querySelector("#emailError");
        const passwordError = document.querySelector("#passwordError");

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

        if (valid) {
            try {
                let userData = { email, password };
                const backendResponse = await fetch("http://localhost:8080/api/auth/sign-in", {
                    method: "POST",
                    headers: {
                        "Authorization": "Bearer " + btoa(email + ":" + password),
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(userData),
                });

                if (backendResponse.ok) {
                    const data = await backendResponse.json();
                    localStorage.setItem("token", data.response);
                    console.log("Token Received");
                    alert("Sign-In Successful!!");
                    window.location.href = "Dashboard.html";
                } else {
                    const error = await backendResponse.json();
                    alert("Error: " + (error.response || error.message));
                    console.error("Backend Error:", error);
                }

            } catch (error) {
                console.error("Network/Server Error:", error);
                alert("⚠️ An error occurred during sign-in. Please try again later.");
            }
        }
    });

    // Forgot password button
    const btn2 = document.querySelector("#btn2");
    if (btn2) {
        btn2.addEventListener("click", function () {
            window.location.href = "ForgotPassword.html";
        });
    }
    //google login
    document.querySelector("#googleLogin").addEventListener("click", function(){
        window.location.href = "http://localhost:8080/oauth2/authorization/google";
    });
    //github login
    document.querySelector("#githubLogin").addEventListener("click", function(){
        window.location.href = "http://localhost:8080/oauth2/authorization/github";
    });
});

async function loadUserDetails(event) {
    console.log("Loading user details...");
    // Prevent default form submission if this is called from a form submit event
    if (event)
        event.preventDefault();
    try {
        let token = localStorage.getItem("token");
        if (!token) {
            alert("No token found, please sign in.");
            window.location.href = "SignIn.html";
        }
        const backendResponse = await fetch("http://localhost:8080/api/user/get-details", {
            method : "GET",
            headers :{
                "Authorization" : "Bearer " + token,
                "Content-Type" : "application/json"
            }
            // credentials: "include"
        });
        if(backendResponse.ok) {
            console.log("Authentication Successful");
            let userResponse = await backendResponse.json();
            let user = userResponse.response;
            console.log(user);
            document.querySelector("#firstName").value = user.firstName;
            document.querySelector("#lastName").value = user.lastName;
            document.querySelector("#phone").value = user.contact;
            document.querySelector("#email").value = user.email;
            document.querySelector("#bioText").value = user.bio;
        }
        else if(backendResponse.status === 401) {
            alert("Unautharized Access");
            localStorage.removeItem("token");
            window.location.href = "SignIn.html";
        }
        else console.error("Error fetching user details:", backendResponse.statusText);
    } catch (error) {
        console.error("Network error:", error);
    }
}

// Call this when page loads
window.addEventListener("DOMContentLoaded", loadUserDetails);
document.querySelector("#btn").addEventListener("click", function(){
    window.location.href = "MyProfile.html";
});
document.querySelector("#changePasswordBtn").addEventListener("click", function(){
    window.location.href = "ChangePassword.html";
});
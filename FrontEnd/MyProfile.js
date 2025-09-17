const firstName = document.querySelector('#firstName');
const lastName = document.querySelector('#lastName');
const phone = document.querySelector('#phone');
const bioText = document.querySelector('#bioText');
const profileForm = document.querySelector("#profileForm");

// 1. Load details when page loads
window.addEventListener("DOMContentLoaded", async function() {
    try {
        let token = this.localStorage.getItem("token");
        const response = await fetch("http://localhost:8080/api/user/get-details", {
            method : "GET",
            headers:{
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });
        if (response.ok) {
            let userResponse = await response.json();
            let details = userResponse.response;
            // Prefill form fields
            firstName.value = details.firstName || "";
            lastName.value  = details.lastName || "";
            phone.value     = details.contact || "";
            bioText.value   = details.bio || "";
        } else if (response.status === 401) {
            alert("Unauthorized Access");
            localStorage.removeItem("token");
            window.location.href = "SignIn.html";
        } else {
            console.error("Error fetching user details:", response.statusText);
        }
    } catch (error) {
        console.error("Error loading user details:", error);
    }
});

// 2. Update details on submit
profileForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    let userData = {
        firstName: firstName.value,
        lastName: lastName.value,
        contact: phone.value,
        bio: bioText.value
    };

    try {
        let token = localStorage.getItem("token");
        if (!token) {
            alert("You must be logged in to update your profile.");
            window.location.href = "SignIn.html";
            return;
        }
        const backEndResponse = await fetch("http://localhost:8080/api/user/update-details", {
            method : "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body : JSON.stringify(userData)
        });
        if (backEndResponse.ok) {
            console.log("Profile Updated Successfully");
            alert("Profile Updated Successfully");
            window.location.href = "Dashboard.html";
        } else if (backEndResponse.status === 401) {
            alert("Unauthorized Access");
            localStorage.removeItem("token");
            window.location.href = "SignIn.html";
        } else {
            console.error("Error updating profile:", backEndResponse.statusText);
        }
    } catch (error) {
        console.error("Exception while updating profile:", error);
        alert("An error occurred while updating your profile.");
    }
});
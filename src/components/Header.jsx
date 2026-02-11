import {assets} from "../assets/assets.js";
import {useContext} from "react";
import {AppContext} from "../context/AppContext.jsx";

const Header = () => {
    const {userData} = useContext(AppContext);

    return (
        <>
            <div className="text-center d-flex flex-column align-items-center justify-content-center py-5 px-3" style={{minHeight:"80vh"}}>
                <img src={assets.header} alt="header" width={120} className="mb-4"/>

                <h5 className="fw-semibold">
                    Hey {userData? userData.name : "Developer"}  <span role="img" aria-label="wave"></span>
                </h5>
               <h1 className="fw-bold display-5 mb-3">Welcome to our product</h1>
                <p className="text-muted 6 mb-4" style={{maxWidth:"500px"}}>
                    We’ll start with a quick product tour of this site built to demonstrate basic authentication.
                    It supports user registration and login with JWT, email OTP verification, and password reset via email.
                    The app uses a Spring Boot REST backend, a React frontend with protected routes, and Bootstrap 5.
                </p>


            </div>
        </>
    )
}
export default Header;
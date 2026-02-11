import {createContext, useEffect, useState} from "react";
import {AppConstants} from "../util/constants.js";
import axios from "axios";
import {toast} from "react-toastify";
import {useNavigate} from "react-router-dom";

export const AppContext = createContext();

export const AppContextProvider = (props) => {
    axios.defaults.withCredentials = true;
    const backendURL = AppConstants.BACKEND_URL;
    const [isLoggedIn,setIsLoggedIn] = useState(false);
    const [userData, setUserData] = useState(false);

    let navigate = useNavigate();

    const getUserData = async () => {
       try {
           const response = await axios.get(backendURL+"/profile");
           if(response.status === 200){
               setUserData(response.data);
           }else {
               toast.error("Unable to get retrieve profile");
           }
       }catch (error) {
           toast.error(error.message);
       }

    }

    const getAuthState = async () => {
        setIsLoggedIn(false);
        try {
            const response = await axios.get(backendURL+"/is-authenticated");
            if(response.status === 200 && response.data === true){
                 setIsLoggedIn(true);
                 await getUserData();
            } else {
                setIsLoggedIn(false);
            }
        } catch (error) {
        console.error(error);
        }
            setIsLoggedIn(false);

    }

    useEffect(() => {
        getAuthState();
    }, []);

    useEffect(() => {
        isLoggedIn && userData && userData.isAccountVerified && navigate("/");
    }, [isLoggedIn,userData]);

    const contextValue = {
           backendURL,
        isLoggedIn,setIsLoggedIn,
        userData,setUserData,getUserData
    }

    return (
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    )
}
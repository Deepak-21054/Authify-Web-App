import './App.css'
import {ToastContainer} from "react-toastify";
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";
import EmailVerify from "./pages/EmailVerify.jsx";
import ResetPassword from "./pages/ResetPassword.jsx";
import {Routes, Route} from "react-router-dom";

// import
function App() {

  return (
    <>
        <ToastContainer/>
            <Routes>
                <Route path="/" element={<Home/> } />
                <Route path="/login" element={<Login/>} />
                <Route path="/email-verify" element={<EmailVerify/>} />
                <Route path="/reset-password" element={<ResetPassword/>} />
            </Routes>

    </>
  )
}

export default App

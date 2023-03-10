import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Context } from '../../ColorsContext';
import { setLoggedIn, setUser, TrueOrFalse, UserInterface, UserType } from '../../types';
import './Login.css';

const Login = () => {
  const {setLoggedIn} = useContext(Context) as setLoggedIn
  const {setUser} = useContext(Context) as setUser
  const {TrueOrFalse} = useContext(Context) as TrueOrFalse

  const [EmailFocus, setEmailFocus] = useState(false)
  const [PassFocus, setPassFocus] = useState(false)
  const [EmailValue, setEmailValue] = useState('')
  const [PassValue, setPassValue] = useState('')
  const [ErrorMessage, setErrorMessage] = useState('')
  const navigate = useNavigate()

  const HandleFocus = (Index:number) => {
    switch(Index) {
        case 1:
            EmailFocus ? setEmailFocus(false) : setEmailFocus(true); setPassFocus(false)
            break;
        case 2:
            PassFocus ? setPassFocus(false) : setPassFocus(true); setEmailFocus(false)
            break;
    }
  }

  const LoginFunction = () => {
    if(EmailValue !== "" && PassValue !== "") {
      // If Email includes an @ send data.
      if(EmailValue.includes('@')) {
          SendData();
          setErrorMessage('')
      } else {
          // If email doesn't have an @.
          setErrorMessage("Introduce a proper email format.")
      }
    } else {
      // If email and password are empty.
      setErrorMessage("Please fill all the blank spaces.")
    }
  }

  const SendData = async () => {
    const post = await fetch("http://localhost:8080/api/v1/colors/users/login", {
      method: "POST",
      headers: {
        "Content-Type" : "application/json"
      },
      body: JSON.stringify({
        email: EmailValue,
        password: PassValue
      })
    })
    if(post.status === 200) {
      post.json().then(r => {
        LoggingIn(r)
      })
    } else {
      setErrorMessage("Incorrect Password")
    }
  }

  const LoggingIn = (r:UserType) => {
    localStorage.setItem('user', JSON.stringify(r))
    localStorage.setItem('loggedin', 'true')
      setUser(JSON.parse(localStorage.getItem('user')!))
      setLoggedIn(TrueOrFalse());
      navigate('/home')
  }

  return (
    <div className="loginContainer">
      <h1>Login to your account</h1>
      <div className="loginForm">
        <h1 style={{color: 'var(--errorcolor)'}}>{ErrorMessage}</h1>
        <section>
        <h1 className={EmailFocus ? 'loginFocused' : 'loginNotFocused'}>Email</h1>
        <input type="email" onClick={() => HandleFocus(1)} onChange={(e) => setEmailValue(e.target.value)}/>
        </section>
        <section>
        <h1 className={PassFocus ? 'loginFocused' : 'loginNotFocused'}>Password</h1>
        <input type="password" onClick={() => HandleFocus(2)} onChange={(e) => setPassValue(e.target.value)}/>
        </section>
      </div>
      <button onClick={() => LoginFunction()}>Submit</button>
    </div>
  )
}

export default Login
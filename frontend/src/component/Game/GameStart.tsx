import { FirebaseAuthProvider } from "@react-firebase/auth";
import firebase from "firebase";
import React, { useState } from "react";
import { useNavigate } from "react-router";
import firebaseConfig from "../../firebase/FirebaseIndex";
import '../../styles/Game/GameStart.css'
import { signOut } from '../../firebase/AuthMethods'

interface IGameStartProps {

}

function GameStart(props: IGameStartProps) {

	return (
			<div className="start-wrapper-flex">
				<div className="start-rect">
				<div className="start-section-heading">Hit the <br/> Midpoint Challenge</div>
				<div className="start-details">
				MidPoint is a platform where you can create custom surveys. 
				The statistics help you learn  about the ideas and opintions of the wider community.
				</div>
				<button className="start-button-submit"> Start</button>


				</div>
				<img className="start-picture" src="https://www.invaluable.com/blog/wp-content/uploads/2017/10/Invaluable-Roy-Lichtenstein-Hero.jpg"></img>
	

			</div>

	);
}

export default GameStart;
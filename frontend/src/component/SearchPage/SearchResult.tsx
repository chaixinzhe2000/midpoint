import axios from "axios";
import firebase from "firebase";
import React, { useEffect, useState } from "react";
import ReactLoading, { LoadingType } from 'react-loading';
import Masonry from "react-masonry-css";
import endpointUrl from "../../constants/Endpoint";
import IPoll from "../../interfaces/IPoll";
import '../../styles/Common/LoginModal.css'
import MasonryPoll from "../HomePage/Poll";

interface ISearchResultProps {
	searchString: string,
	setIsLoginModalOpen: any,
	isLoggedIn: boolean,

}

function SearchResult(props: ISearchResultProps) {
	
	const [validPolls, setValidPolls]: [IPoll[], any] = useState([]);

	useEffect(() => {
		requestPolls();
	}, [props.searchString])

	const requestPolls = async () => {
		let toSend;
		if (props.isLoggedIn) {
			const token = await firebase.auth().currentUser.getIdToken(true);
			toSend = {
				searchString: props.searchString,
				userIdToken: token,
				loggedIn: true
			}
		} else {
			toSend = {
				searchString: props.searchString,
				userIdToken: 'none',
				loggedIn: false
			}
		}
		console.log(toSend)
		const config = {
			headers: {
				"Content-Type": "application/json",
				'Access-Control-Allow-Origin': '*',
			}
		}
		axios.post(
			endpointUrl + '/poll/search', toSend, config)
			.then(response => {
				console.log(response.data.searchResults)
        console.log(response.data.answeredPollIds)
        console.log(response.data)
				const returnedPolls: IPoll[] = response.data.searchResults;
				setValidPolls(returnedPolls)
			})
			.catch(e => {
				console.log(e)
			});
	}

	const divItems = validPolls.map(function (poll) {
		return <MasonryPoll key={poll.id} id={poll.id} question={poll.question}
			emoji={poll.emoji} answerOption={poll.answerOptions} isLoggedIn={props.isLoggedIn}
			setIsLoginModalOpen={props.setIsLoginModalOpen} color={poll.color} imageUrl={poll.imageUrl} />
	});

	const breakpointColumnsObj = {
		default: 4,
		1700: 4,
		1300: 3,
		1000: 2,
		700: 2,
		600: 1
	};

	return (
		<div className="masonry-wrapper-wrapper">
			{console.log(props.searchString)}
			<Masonry
				breakpointCols={breakpointColumnsObj}
				className="my-masonry-grid"
				columnClassName="my-masonry-grid_column"
			>
				{divItems}
			</Masonry>
		</div>
	);
}

export default SearchResult;
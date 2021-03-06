import '../../styles/Common/OptionPanel.css'
import OptionItem from "./OptionItem";

interface IOptionPanelProps {
	numOfOptions: number,
	setNumOfOptions: any,
	pollEmojiArray: string[],
	setPollEmojiArray: any,
	isEmojiOpenArray: boolean[],
	setIsEmojiOpenArray: any,
	textFieldValue: string[],
	setTextFieldValue: any,
	optionHint: string[],
	setOptionHint: any,
	questionEmojiOpen: boolean,
	setQuestionEmojiOpen: any
}

function OptionPanel(props: IOptionPanelProps) {

	const range = (from: number, to: number, step: number) =>
		[...Array(Math.floor((to - from) / step) + 1)].map((_, i) => from + i * step);

	const itemProps = {
		pollEmojiArray: props.pollEmojiArray,
		setPollEmojiArray: props.setPollEmojiArray,
		isEmojiOpenArray: props.isEmojiOpenArray,
		setIsEmojiOpenArray: props.setIsEmojiOpenArray,
		textFieldValue: props.textFieldValue,
		setTextFieldValue: props.setTextFieldValue,
		optionHint: props.optionHint,
		setOptionHint: props.setOptionHint,
		questionEmojiOpen: props.questionEmojiOpen,
		setQuestionEmojiOpen: props.setQuestionEmojiOpen
	}

	return (
		<div>
			{range(0, props.numOfOptions - 1, 1).map((option) => (
				<OptionItem key={option} optionIndex={option} {...itemProps} />
			))}
		</div>
	);
}

export default OptionPanel;

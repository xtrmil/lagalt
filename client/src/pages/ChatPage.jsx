import React from 'react';
import { Container } from 'react-bootstrap';
import * as Chat from '../utils/Chat';
import * as Auth from '../utils/Auth';
import './ChatPage.css';

export default class ChatTest extends React.Component {
  state = { chatData: null, isLoading: true, endOfHistory: false };

  subscription;
  loggedInUser = null;
  chatDataArr = [];
  chatBox = React.createRef();
  chatMsgRef = React.createRef();
  emojis = new Map([
    [':)', '🙂'],
    [';)', '😉'],
    [':D', '😀'],
    [':(', '🙁'],
    [':P', '😛'],
    [':p', '😛'],
    [':/', '😕'],
    [':|', '😐'],
    ['xD', '😄'],
    ['XD', '😄'],
    ["x'D", '😂'],
    ["X'D", '🤣'],
    [';P', '😜'],
    [';p', '😜'],
    [':o', '😮'],
    [':O', '😲'],
    ['>(', '😠'],
    ['><', '😣'],
    ['o.O', '🤨'],
    ['oO', '🤨'],
    ['>.>', '🙄'],
    ['8)', '🤓'],
    [':s', '😧'],
    [':S', '🤢'],
    ['$)', '🤑'],
    ['<3', '🧡'],
    ['</3', '💔'],
    [':wave:', '🖐'],
    [':thumbsup:', '👍'],
    [':thumbsdown:', '👎'],
    [':ok:', '👌'],
    [':flex:', '💪'],
  ]);

  scrollCallback = async (e) => {
    if (e.target.scrollTop === 0 && !this.state.isLoading && !this.state.endOfHistory) {
      this.setState({ isLoading: true });
      const newMsgs = await Chat.getEarlierMessages(this.state.chatData[0]);
      if (newMsgs.length === 0) {
        this.setState({ isLoading: false, endOfHistory: true });
      } else {
        this.chatDataArr.unshift(...newMsgs);
        this.setState({ chatData: this.chatDataArr, isLoading: false });
        this.chatBox.current.children.item(newMsgs.length + 1).scrollIntoView(true);
      }
    }
  };

  componentDidMount = () => {
    this.subscribeToChat();
    this.chatBox.current.addEventListener('scroll', this.scrollCallback);
    Auth.loggedInUser().subscribe((user) => (this.loggedInUser = user.username));
  };

  componentDidUpdate = () => {
    // console.log('didUpdate');
    if (
      Chat.shouldTriggerUpdate(this.props.match.params.owner, this.props.match.params.projectName)
    ) {
      this.setState({ chatData: null, isLoading: true, endOfHistory: false });
      this.chatDataArr = [];
      this.subscribeToChat();
    }
  };

  subscribeToChat = async () => {
    const observable = await Chat.chatData(
      this.props.match.params.owner,
      this.props.match.params.projectName,
    );
    if (observable) {
      this.subscription = observable.subscribe((docs) => {
        this.chatDataArr.push(...docs);
        this.setState({ chatData: this.chatDataArr, isLoading: false });
        this.scrollToBottom();
      });
    }
  };

  componentWillUnmount = () => {
    // console.log('willUnmount');
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    Chat.unsubscribeAll();
    this.chatBox.current.removeEventListener('scroll', this.scrollCallback);
  };

  scrollToBottom = () => {
    const el = this.chatBox.current;
    el.scrollTop = el.scrollHeight - el.clientHeight;
  };

  handleSubmit = (e) => {
    e.preventDefault();
    const msg = this.chatMsgRef.current.value;
    if (msg.trim().length > 0) {
      Chat.sendMsg(msg, this.props.match.params.owner, this.props.match.params.projectName);
      this.chatMsgRef.current.value = '';
    }
  };

  getDate = (timestamp) => {
    if (!timestamp) {
      return;
    }
    let date;
    if (typeof timestamp === 'number') {
      date = new Date(timestamp);
    } else {
      date = timestamp.toDate();
    }
    return date.toUTCString();
  };

  handleSmileyFormatting = (e) => {
    if (e.nativeEvent.data === ' ') {
      this.chatMsgRef.current.value = this.chatMsgRef.current.value
        .split(' ')
        .map((item) => this.emojis.get(item) || item)
        .join(' ');
    }
  };

  render = () => {
    let chat = [];
    if (this.state.chatData) {
      chat = this.state.chatData.map((post, index) => {
        const fromSelf = post.user === this.loggedInUser;

        return (
          <div key={index} className={'post' + (fromSelf ? ' self' : '')}>
            <div className="user">{index}</div>
            <div className="msg" title={this.getDate(post.timestamp)}>
              {post.text}
            </div>
          </div>
        );
      });
    }

    return (
      <Container className="mt-3">
        <form onSubmit={this.handleSubmit}>
          <div ref={this.chatBox} id="chatBox">
            <small>
              {this.state.endOfHistory && 'end of history'}
              {this.state.isLoading && 'loading...'}
            </small>
            {chat.length ? chat : 'Chat is empty'}
          </div>
          <div className="inputs">
            <input
              className="form-control d-inline"
              type="text"
              ref={this.chatMsgRef}
              onChange={this.handleSmileyFormatting}
              autoFocus
            />
            <button className="btn btn-primary" type="submit">
              Submit
            </button>
          </div>
        </form>
      </Container>
    );
  };
}

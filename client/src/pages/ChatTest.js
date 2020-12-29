import React from 'react';
import { Container } from 'react-bootstrap';
import * as Chat from '../utils/Chat';
import * as Auth from '../utils/Auth';
import './ChatTest.css';

export default class ChatTest extends React.Component {
  state = { chatData: null, isLoading: true, endOfHistory: false };

  subscription;
  loggedInUser = null;
  chatDataArr = [];
  chatBox = React.createRef();
  chatMsgRef = React.createRef();

  scrollCallback = async (e) => {
    if (e.target.scrollTop === 0 && !this.state.isLoading && !this.state.endOfHistory) {
      this.setState({ isLoading: true });
      const lastNewElement = e.target.firstElementChild;
      const msgs = await Chat.getEarlierMessages(this.state.chatData[0]);
      if (msgs.length === 0) {
        this.setState({ isLoading: false, endOfHistory: true });
      } else {
        this.chatDataArr.unshift(...msgs);
        this.setState({ chatData: this.chatDataArr, isLoading: false });
        lastNewElement.scrollIntoView(true);
      }
    }
  };

  componentDidMount = () => {
    Chat.chatData(this.props.match.params.owner, this.props.match.params.projectName).then(
      (observable) =>
        (this.subscription = observable.subscribe((docs) => {
          this.chatDataArr.push(...docs);
          this.setState({ chatData: this.chatDataArr, isLoading: false });
          this.scrollToBottom();
        })),
    );
    this.chatBox.current.addEventListener('scroll', this.scrollCallback);
    Auth.loggedInUser().subscribe((user) => (this.loggedInUser = user.username));
  };

  componentWillUnmount = () => {
    Chat.unsubscribeAll();
    this.subscription.unsubscribe();
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

  render = () => {
    let chat = [];
    if (this.state.chatData) {
      chat = this.state.chatData.map((post) => {
        const fromSelf = post.user === this.loggedInUser;

        return (
          <div key={post.id} className={'post' + (fromSelf ? ' self' : '')}>
            <div className="user">{post.user}</div>
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
          <div className="input">
            <input className="form-control" type="text" ref={this.chatMsgRef} autoFocus />
            <button className="btn btn-primary" type="submit">
              Submit
            </button>
          </div>
        </form>
      </Container>
    );
  };
}

import React from 'react';
import * as Chat from '../utils/Chat.js';
import * as Auth from '../utils/Auth.js';
import './ChatTest.css';

export default class ChatTest extends React.Component {
  state = { chatData: null };

  loggedInUser = null;
  chatDataArr = [];
  chatBox = React.createRef();
  chatMsgRef = React.createRef();
  chatSubscription = null;

  async componentDidMount() {
    this.chatSubscription = Chat.chatData().subscribe((data) => {
      data.forEach((doc) => this.chatDataArr.push(doc));
      this.setState({ chatData: this.chatDataArr });
      setTimeout(() => this.scrollToBottom(), 0);
    });
    Auth.loggedInUser().subscribe((user) => (this.loggedInUser = 'some_user')); // user.username // TODO temp
  }

  componentWillUnmount() {
    console.log('unmounted');
    this.chatSubscription.unsubscribe();
  }

  scrollToBottom = () => {
    const el = this.chatBox.current;
    el.scrollTop = el.scrollHeight - el.clientHeight;
  };

  handleSubmit = (e) => {
    e.preventDefault();
    const msg = this.chatMsgRef.current.value;
    if (msg.trim().length > 0) {
      Chat.sendMsg(this.chatMsgRef.current.value.trim());
      this.chatMsgRef.current.value = '';
    }
  };

  getDate(timestamp) {
    const date = new Date(timestamp);
    return date.toUTCString();
  }

  render = () => {
    let chat;
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
      <form onSubmit={this.handleSubmit}>
        <div ref={this.chatBox} id="chatBox">
          {chat}
        </div>
        <input type="text" ref={this.chatMsgRef} autoFocus />
      </form>
    );
  };
}

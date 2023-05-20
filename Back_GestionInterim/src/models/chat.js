const mongoose = require('mongoose');

const chatSchema = new mongoose.Schema({
  sender: { type: mongoose.Schema.Types.ObjectId, ref: 'JobSeeker' },
  receiver: { type: mongoose.Schema.Types.ObjectId, ref: 'JobSeeker' },
  content: { type: String, required: true },
  timestamp: { type: Date, default: Date.now },
});

const Chat = mongoose.model('Message', chatSchema);

module.exports = Chat;
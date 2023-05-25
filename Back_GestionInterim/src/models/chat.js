const mongoose = require('mongoose');

const chatSchema = new mongoose.Schema({
  sender: { type: mongoose.Schema.Types.Mixed, required: true},
  receiver: { type: mongoose.Schema.Types.Mixed, required: true },
  content: { type: String, required: true },
  timestamp: { type: Date, default: Date.now },
});

const Chat = mongoose.model('Message', chatSchema);

module.exports = Chat;
const mongoose = require('mongoose');

const groupSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  creator: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'JobSeeker',
    required: true
  },
  members: [{
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'JobSeeker'
    },
    isAdmin: {
      type: Boolean,
      default: false
    }
  }]
});

module.exports = mongoose.model('Group', groupSchema);

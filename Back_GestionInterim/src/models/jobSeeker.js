const mongoose = require('mongoose');

const jobSeekerSchema = new mongoose.Schema({
  firstName: {
    type: String,
    required: true
  },
  lastName: {
    type: String,
    required: true
  },
  nationality: {
    type: String
  },
  dateOfBirth: {
    type: Date
  },
  phoneNumber: {
    type: String
  },
  email: {
    type: String
  },
  city: {
    type: String
  },
  cv: {
    type: String
  },
  comments: {
    type: String
  }
});

module.exports = mongoose.model('JobSeeker', jobSeekerSchema);

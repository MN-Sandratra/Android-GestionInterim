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
    type: String,
    unique: true
  },
  city: {
    type: String
  },
  cv: {
    type: String
  },
  comments: {
    type: String
  },
  password: {
    type: String,
    required: true
  },
  validationCode: {
    type: String,
  },
  isValidated: {
    type: Boolean,
    required:true,
    default: false
  }
});


module.exports = mongoose.model('JobSeeker', jobSeekerSchema);

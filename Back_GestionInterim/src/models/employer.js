const mongoose = require('mongoose');

const employerSchema = new mongoose.Schema({
    companyName: { 
        type: String, 
        required: true 
    },
    department: { 
        type: String 
    },
    subDepartment: { 
        type: String
    },
    nationalNumber: { 
        type: String, 
        required: true 
    },
    contactPerson1: { 
        type: String 
    },
    contactPerson2: { 
        type: String 
    },
    email1: { 
        type: String, 
        required: true 
    },
    email2: { 
        type: String 
    },
    phone1: { 
        type: String 
    },
    phone2: { 
        type: String 
    },
    address: { 
        type: String, 
        required: true 
    },
    publicLinks: {
      website: { type: String },
      linkedin: { type: String },
      facebook: { type: String },
    },
  });

  module.exports = mongoose.model('Employer', employerSchema);
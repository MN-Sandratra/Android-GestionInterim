const mongoose = require('mongoose');

const CandidatureSchema = new mongoose.Schema({
    jobSeeker: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'JobSeeker',
        required: true
    },
    firstName: {
        type: String,
        required: true
    },
    lastName: {
        type: String,
        required: true
    },
    nationality: {
        type: String,
        required: true
    },
    dateOfBirth: {
        type: Date,
        required: true
    },
    cv: {
        type: String,
        required: true
    },
    lm: {
        type: String
    }
});

module.exports = mongoose.model('Candidature', CandidatureSchema);

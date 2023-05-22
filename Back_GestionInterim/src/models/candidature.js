const mongoose = require('mongoose');

const CandidatureSchema = new mongoose.Schema({
    identifiant: {
        type : String
    },
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

CandidatureSchema.pre('save', function(next) {
    // Affecter l'ID généré par Mongoose à la propriété identifiant avant de sauvegarder le document
    this.identifiant = this._id;
    next();
});

module.exports = mongoose.model('Candidature', CandidatureSchema);

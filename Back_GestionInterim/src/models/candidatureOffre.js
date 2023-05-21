const mongoose = require('mongoose');

const candidatureOffreSchema = new mongoose.Schema({
  candidature: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Candidature',
    required: true
  },
  offre: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Offre',
    required: true
  }
});

module.exports = mongoose.model('CandidatureOffre', candidatureOffreSchema);

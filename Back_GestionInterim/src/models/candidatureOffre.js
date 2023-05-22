const mongoose = require('mongoose');

const etatsCandidaturesEnum = ['acceptée', 'en attente', 'refusée'];

const candidatureOffreSchema = new mongoose.Schema({
  candidature: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Candidature',
    required: true
  },
  offre: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Offer',
    required: true
  },
  status: {
    type: String,
    enum: etatsCandidaturesEnum,
    default:'en attente'
}
});

module.exports = mongoose.model('CandidatureOffre', candidatureOffreSchema);

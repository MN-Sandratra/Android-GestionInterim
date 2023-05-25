const mongoose = require('mongoose');
const employer = require('./employer');

const etatsEnum = ['en cours', 'terminé', 'en attente', 'annulé'];

const offerSchema = new mongoose.Schema({
    identifiant:{
        type: String
    },
    employeur: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Employer',
      required: true
    },
    intitule: {
      type: String,
      required: true
    },
    dateDebut: {
        type: Date, 
        required: true
    },
    dateFin: {
        type: Date, 
        required: true
    },
    experienceRequise:{
        type: String,
        required: true
    },
    description:{
        type: String,
        required: true
    },
    tauxHoraire:{
        type: Number,
        required: true
    },
    heureDebut:{
        type:String,
        required: true
    },
    disponibilite:{
        type: Boolean,
        default:true
    },
    etat:{
        type: String,
        enum: etatsEnum,
        default:'en cours'
    },
    ville:{
        type: String,
        required: true
    },
    adressePostale:{
        type: String,
        required: true
    },
    latitude:{
        type: Number
    },
    longitude:{
        type: Number
    }
});

offerSchema.pre('save', function(next) {
    // Affecter l'ID généré par Mongoose à la propriété identifiant avant de sauvegarder le document
    this.identifiant = this._id;
    next();
});


module.exports = mongoose.model('Offer', offerSchema);
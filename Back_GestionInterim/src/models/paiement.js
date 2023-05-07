const mongoose = require('mongoose');

const paiementSchema = new mongoose.Schema({
    type: { 
        type: String, 
        required: true 
    },
    paiementToken: { 
        type: String, 
        required: true 
    },
    paiementDate: { 
        type: Date, 
        default: Date.now 
    },
});

const Paiement = mongoose.model('Paiement', paiementSchema);

module.exports = { Paiement };
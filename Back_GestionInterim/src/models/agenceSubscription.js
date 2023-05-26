const mongoose = require('mongoose');

const agenceSubscriptionSchema = new mongoose.Schema({
    agence: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'agence', 
        required: true 
    },
    subscription: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'Subscription', 
        required: true 
    },
    paiement: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'Paiement', 
        required: true 
    },
    expirationDate: {
        type: Date,
        required: true
    }
  });

module.exports = mongoose.model('agenceSubscription', agenceSubscriptionSchema);
const mongoose = require('mongoose');

const employerSubscriptionSchema = new mongoose.Schema({
    employer: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'Employer', 
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

module.exports = mongoose.model('EmployerSubscription', employerSubscriptionSchema);
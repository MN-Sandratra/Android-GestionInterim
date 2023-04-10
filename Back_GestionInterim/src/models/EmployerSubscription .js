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
    payment: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'Payment', 
        required: true 
    },
  });

const EmployerSubscription = mongoose.model('EmployerSubscription', employerSubscriptionSchema);
module.exports = { EmployerSubscription };

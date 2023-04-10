const mongoose = require('mongoose');

const paymentSchema = new mongoose.Schema({
    type: { 
        type: String, 
        required: true 
    },
    paymentToken: { 
        type: String, 
        required: true 
    },
    paymentDate: { 
        type: Date, 
        default: Date.now 
    },
});

const Payment = mongoose.model('Payment', paymentSchema);

module.exports = { Payment };
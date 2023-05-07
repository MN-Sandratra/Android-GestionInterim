const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const subscriptionSchema = new Schema({
    type: {
        type: String,
        required: true
    },
    price: {
        type: Number,
        required: true
    },
    conditions: {
        type: String,
        required: true
    },
    cancellationConditions: {
        type: String,
        required: true
    }
});

module.exports = mongoose.model('Subscription', subscriptionSchema);
